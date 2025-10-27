package servlet;

import com.google.gson.*;
import dao.BookDAO;
import model.Book;
import model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

@WebServlet("/admin/import")
public class ImportBooksServlet extends HttpServlet {
    private final Gson gson = new Gson();
    // Simple in-memory background job tracker (per JVM). For production, persist to DB.
    private static volatile boolean jobRunning = false;
    private static volatile int jobTarget = 0;
    private static volatile int jobInserted = 0;
    private static volatile String jobSubjects = "";
    private static volatile String jobStatus = "IDLE";
    private BookDAO bookDAO;

    @Override
    public void init() {
        bookDAO = new BookDAO();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

    String subjectsParam = request.getParameter("subjects");
        String countParam = request.getParameter("count");
    String asyncParam = request.getParameter("async");
    int target = 1000;
    try { if (countParam != null) target = Math.max(50, Math.min(50000, Integer.parseInt(countParam.trim()))); } catch (NumberFormatException ignore) {}

        List<String> subjects = new ArrayList<>();
        if (subjectsParam != null && !subjectsParam.trim().isEmpty()) {
            for (String s : subjectsParam.split(",")) {
                String v = s.trim();
                if (!v.isEmpty()) subjects.add(v);
            }
        }
        if (subjects.isEmpty()) {
            subjects = Arrays.asList("fiction", "fantasy", "science", "engineering", "medicine", "history", "business", "technology");
        }

        boolean async = "true".equalsIgnoreCase(asyncParam) || "1".equals(asyncParam);
        if (async) {
            if (jobRunning) {
                request.setAttribute("message", "An import job is already running: " + jobInserted + "/" + jobTarget + " inserted for subjects: " + jobSubjects);
                request.getRequestDispatcher("/admin.jsp").forward(request, response);
                return;
            }
            final List<String> subjCopy = new ArrayList<>(subjects);
            jobRunning = true;
            jobTarget = target;
            jobInserted = 0;
            jobSubjects = String.join(", ", subjCopy);
            jobStatus = "RUNNING";
            final int targetFinal = target; // ensure effectively-final for lambda capture
            new Thread(() -> {
                try {
                    runImportBatches(subjCopy, targetFinal);
                    jobStatus = "DONE";
                } catch (Exception e) {
                    jobStatus = "ERROR: " + e.getMessage();
                } finally {
                    jobRunning = false;
                }
            }, "ImportBooksJob").start();
            request.setAttribute("message", "Background import started for subjects: " + jobSubjects + ". Target: " + jobTarget + ". Check status below.");
            request.getRequestDispatcher("/admin.jsp").forward(request, response);
        } else {
            int perSubject = Math.max(25, Math.max(1, target / subjects.size()));
            Set<String> seen = new HashSet<>();
            int inserted = 0;
            StringBuilder errors = new StringBuilder();

            for (String subj : subjects) {
                if (inserted >= target) break;
                int remaining = target - inserted;
                int toFetch = Math.min(perSubject, remaining);

                try {
                    inserted += importSubject(subj, toFetch, seen);
                } catch (Exception e) {
                    errors.append("Subject '").append(subj).append("' failed: ").append(e.getMessage()).append("\n");
                }
            }

            request.setAttribute("message", "Imported " + inserted + " real books. " + (errors.length() > 0 ? ("Some errors occurred: " + errors) : ""));
            request.getRequestDispatcher("/admin.jsp").forward(request, response);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if ("status".equalsIgnoreCase(action)) {
            response.setContentType("text/plain;charset=UTF-8");
            response.getWriter().println("status=" + jobStatus);
            response.getWriter().println("running=" + jobRunning);
            response.getWriter().println("inserted=" + jobInserted);
            response.getWriter().println("target=" + jobTarget);
            response.getWriter().println("subjects=" + jobSubjects);
            return;
        }
        super.doGet(request, response);
    }

    private void runImportBatches(List<String> subjects, int target) throws IOException {
        int batchSize = 250; // per iteration
        Set<String> seen = new HashSet<>();
        while (jobInserted < target && jobRunning) {
            int remaining = target - jobInserted;
            int perSubject = Math.max(25, Math.max(1, remaining / subjects.size()));
            for (String subj : subjects) {
                if (jobInserted >= target || !jobRunning) break;
                int toFetch = Math.min(perSubject, target - jobInserted);
                int got = importSubject(subj, toFetch, seen);
                jobInserted += got;
                try { Thread.sleep(400); } catch (InterruptedException ignore) {}
            }
        }
    }

    private int importSubject(String subject, int desired, Set<String> seen) throws IOException {
        int pageSize = Math.min(100, Math.max(25, desired));
        int inserted = 0;
        int page = 1;
        while (inserted < desired && page <= 20) { // cap pages defensively

            String url = "https://openlibrary.org/search.json?" +
                    "subject=" + URLEncoder.encode(subject, StandardCharsets.UTF_8.name()) +
                    "&limit=" + pageSize +
                    "&page=" + page +
                    "&fields=title,author_name,first_publish_year,publisher,isbn,cover_i";

            JsonObject root = getJson(url);
            if (root == null) break;
            JsonArray docs = root.has("docs") && root.get("docs").isJsonArray() ? root.getAsJsonArray("docs") : new JsonArray();
            if (docs.size() == 0) break;

            for (JsonElement el : docs) {
                if (inserted >= desired) break;
                if (!el.isJsonObject()) continue;
                JsonObject d = el.getAsJsonObject();

                String title = optString(d, "title");
                String author = firstFromArray(d, "author_name");
                if (title == null || title.trim().isEmpty()) continue;
                String key = (title.trim().toLowerCase()) + "|" + (author == null ? "" : author.trim().toLowerCase());
                if (seen.contains(key)) continue;

                String isbn = firstIsbn13OrAny(d);
                String publisher = firstFromArray(d, "publisher");
                Integer year = optInt(d, "first_publish_year");
                String coverUrl = coverFromDoc(d);

        // Skip if an exact title+author already exists
        if (existsByTitleAuthor(title, author)) {
            continue;
        }
        Book b = new Book(0, title, author, isbn, capitalize(subject), publisher, year, null,
            null, coverUrl, "Shelf OL", 10, 10, true);
        bookDAO.addBook(b);
                seen.add(key);
                inserted++;
            }
            page++;
        }
        return inserted;
    }

    private boolean existsByTitleAuthor(String title, String author) {
        String sql = "SELECT 1 FROM books WHERE LOWER(title)=? AND (LOWER(author)=? OR (author IS NULL AND ? IS NULL)) LIMIT 1";
        try (java.sql.Connection conn = util.DBConnection.getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, title == null ? null : title.toLowerCase());
            ps.setString(2, author == null ? null : author.toLowerCase());
            ps.setString(3, author == null ? null : author.toLowerCase());
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            return false;
        }
    }

    private JsonObject getJson(String urlStr) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(10000);
        conn.setReadTimeout(20000);
        int code = conn.getResponseCode();
        if (code != 200) return null;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) sb.append(line);
            return JsonParser.parseString(sb.toString()).getAsJsonObject();
        }
    }

    private static String optString(JsonObject o, String key) {
        return (o.has(key) && !o.get(key).isJsonNull()) ? o.get(key).getAsString() : null;
    }

    private static Integer optInt(JsonObject o, String key) {
        try {
            return (o.has(key) && !o.get(key).isJsonNull()) ? o.get(key).getAsInt() : null;
        } catch (Exception e) { return null; }
    }

    private static String firstFromArray(JsonObject o, String key) {
        if (!o.has(key) || !o.get(key).isJsonArray()) return null;
        JsonArray arr = o.get(key).getAsJsonArray();
        if (arr.size() == 0) return null;
        JsonElement el = arr.get(0);
        return el.isJsonNull() ? null : el.getAsString();
    }

    private static String firstIsbn13OrAny(JsonObject o) {
        if (!o.has("isbn") || !o.get("isbn").isJsonArray()) return null;
        JsonArray arr = o.get("isbn").getAsJsonArray();
        String any = null;
        for (JsonElement el : arr) {
            String v = el.getAsString();
            if (any == null) any = v;
            if (v != null && v.length() == 13 && v.matches("\\d+")) return v;
        }
        return any;
    }

    private static String coverFromDoc(JsonObject o) {
        try {
            if (o.has("cover_i") && !o.get("cover_i").isJsonNull()) {
                int id = o.get("cover_i").getAsInt();
                return "https://covers.openlibrary.org/b/id/" + id + "-L.jpg";
            }
        } catch (Exception ignore) {}
        return null;
    }

    private static String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.substring(0,1).toUpperCase() + s.substring(1);
    }
}
