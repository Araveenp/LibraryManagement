# Library Management (JSP/Servlets + H2)

A simple Library Management web app built with Java Servlets/JSP, H2 database, and Maven (WAR). It supports modern UI, browsing/search, admin CRUD, borrowing/returns with quantities, renewals, and importing real books from Open Library.

## Features
- Modern dark UI with shared header/footer and responsive card grid
- Browse and Search
  - Empty search shows featured random books
  - Case‑insensitive search by title, author, and ISBN
- Inventory and Loans
  - Multi‑copy inventory (total_copies, available_copies)
  - Borrow with student roll number and quantity
  - Partial returns and renewals (extend due date)
  - Admin view of active loans by roll
- Admin
  - Add/Update/Delete books with extended metadata (ISBN, genre, publisher, year, pages, description, cover URL, location)
  - Import real books by subjects from Open Library (sync or async background)
- Startup seeding
  - Seeds from packaged CSV at `webapp/WEB-INF/books.csv` on first run

## Tech Stack
- Java 8, Servlets/JSP (Tomcat compatible)
- H2 database (MySQL mode)
- Maven (WAR packaging)
- Gson for JSON parsing

## Project Structure
- `src/servlet/` — Controllers (Search, Admin, Borrow, Return, Renew, Auth, Import, AdminLoans)
- `src/dao/` — Data access (BookDAO, UserDAO)
- `src/model/` — POJOs (Book, User, BorrowRecord)
- `src/util/` — DB connection and startup seeding (`DatabaseInitListener`)
- `webapp/` — JSPs and assets
- `WEB-INF/web.xml` — Deployment descriptor
- `Dockerfile` — Build and run via container

## How it works (high level)
- On startup, `DatabaseInitListener` creates/updates tables and seeds from CSV if empty.
- Requests hit servlets which read parameters, call DAOs, set attributes, and forward to JSPs.
- Admin CRUD modifies books; borrow/return/renew update loans and inventory consistently.
- ImportBooksServlet can fetch thousands of real books by subjects.

## Quick Start (Local)
1) Prerequisites: Java 8+, Maven
2) Build the WAR
```bash
mvn -DskipTests package
```
3) Deploy the WAR (`target/library-management-1.0-SNAPSHOT.war`) to Tomcat 9+ or run with your preferred servlet container.
4) Open in browser (context path may vary):
- Home: `/index.jsp`
- Search: `/search`
- Admin: `/admin`

H2 DB runs in file mode (configured in `DBConnection`). The app auto‑creates and seeds tables on first run.

## Import Real Books (Admin)
- Go to Admin → "Import Real Books"
- Enter subjects (comma‑separated) and count
- For large imports (e.g., 50,000), check "Run in background" and submit
- Monitor progress via the "View Import Status" link

## Common Routes
- `GET /search` — Featured or search results (card grid)
- `POST /borrow` — Borrow with roll and quantity
- `POST /return` — Return partial or all
- `POST /renew` — Extend due date
- `GET/POST /admin` — Admin list + Add/Update/Delete
- `GET /admin/loans` — View active loans by roll
- `POST /admin/import` — Import real books (supports async)

## Notes
- Links in the header use contextPath‑safe URLs to work under any WAR name.
- Duplicate protection in imports is by title+author.
- The seed CSV ensures the app isn’t empty on first launch.

## Development
- Build: `mvn -DskipTests package`
- Clean: `mvn clean`
- Dependencies are managed in `pom.xml`.

## Docker (optional)
- The included `Dockerfile` builds the WAR inside a builder image. If you have a Tomcat base image or compose setup, you can copy the WAR into webapps.

## License
This repository does not include a specific license. Add one if you plan to share or reuse code.
