# JENE-DFCMS

**Digital Forensic Case Management System** — A full-stack web application for managing digital forensic investigations, evidence tracking, chain of custody, and reporting.

Built with **Angular 17** (Frontend) + **Spring Boot 3.2.0** (Backend) + **H2 Database**.

---

## Features

- **Case Management** — Create, update, close, and track forensic cases with status workflows (Created → Pending → Open → Closed → Archived)
- **Task Management** — Assign tasks to investigators with deadlines, priorities, and QA review
- **Evidence Tracking** — Register digital/physical evidence with chain of custody, check-in/check-out, photo uploads, and document attachments
- **RBAC (Role-Based Access Control)** — 6 roles: Admin, Case Manager, Investigator, QA, Requester, Authoriser
- **File Management** — Upload case documents, evidence photos/documents, and final forensic reports with in-app viewer
- **Document Checklists** — 10-point checklist on case creation (COC form, authority letter, seizure memo, etc.)
- **Dashboard** — Real-time stats, charts, and daily forensic tips
- **Advanced Search** — Filter cases by type, priority, status, and date range with chart visualizations
- **Forensic Knowledge Center** — 538+ quiz questions across 12 categories for training
- **Chain of Custody** — Full audit trail of evidence handling with timestamps
- **Final Forensic Reports** — Upload and manage final investigation reports per case
- **JWT Authentication** — Secure token-based authentication with 1-hour expiry
- **Input Sanitization** — XSS prevention on all user inputs
- **File Upload Security** — Whitelist of 30+ allowed file types, blocklist of dangerous extensions

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Frontend | Angular 17 (NgModule), TypeScript, Angular Material Icons |
| Backend | Spring Boot 3.2.0, Spring Security, JPA/Hibernate |
| Database | H2 (file-based, auto-persist) |
| Auth | JWT (HS256, 1-hour expiry) |
| Build | Maven (backend), Angular CLI (frontend) |

---

## Prerequisites

- **Java 17+** — [Download](https://adoptium.net/)
- **Node.js 18+** and npm — [Download](https://nodejs.org/)
- **Maven 3.8+** — [Download](https://maven.apache.org/) (or use the included Maven wrapper `mvnw`)
- **Angular CLI** — Install globally: `npm install -g @angular/cli`

---

## Installation & Setup

### 1. Clone the Repository

```bash
git clone https://github.com/SukhvinderChhoker/JENE-DFCMS.git
cd JENE-DFCMS
```

### 2. Backend Setup

```bash
cd hwt-backend

# Install dependencies and build
mvn clean install

# Run the application
mvn spring-boot:run
```

The backend starts on **http://localhost:8080**.

On first run, the database is automatically seeded with:
- 8 users (admin, case managers, investigators, QA, etc.)
- 6 sample forensic cases
- 15 tasks
- 7 evidence items
- Lookup data (departments, teams, case types, priorities, etc.)

### 3. Frontend Setup

Open a new terminal:

```bash
cd hwt-frontend

# Install dependencies
npm install

# Run the development server
ng serve --host 0.0.0.0
```

The frontend starts on **http://localhost:4200**.

---

## Login Credentials

| Username | Password | Role |
|----------|----------|------|
| `admin` | `admin123` | Admin |
| `sarah.cm` | `pass1234` | Case Manager |
| `john.inv` | `pass1234` | Investigator |
| `priya.inv` | `pass1234` | Investigator |
| `mike.qa` | `pass1234` | QA |
| `lisa.req` | `pass1234` | Requester |
| `david.auth` | `pass1234` | Authoriser |
| `ankit.inv` | `pass1234` | Investigator |

---

## How to Use

### Login
1. Open http://localhost:4200
2. Enter username and password
3. Select your role from the dropdown (users may have multiple roles)
4. Click **Login**

### Create a Case
1. Click **Cases** in the navbar
2. Click **+ New Case**
3. Fill in case name, background, priority, classification, type
4. Complete the document checklist (COC form, authority letter, etc.)
5. Optionally attach documents during creation
6. Click **Save**

### Add Evidence
1. Open a case and go to the **Evidence** tab
2. Click **Add Evidence**
3. Fill in evidence details (type, location, originator, bag number)
4. Save — then open the evidence to upload photos and documents

### Upload Files
1. Open a case → **Files** tab (for case documents)
2. Or open an evidence item → **Evidence Photo** / **Evidence Documents** sections
3. Click the upload area, select file(s), add optional note
4. Click **Upload**
5. Click the **eye icon** to view files in-app, or **download icon** to download

### Track Chain of Custody
1. Open an evidence item
2. Click **Check In** or **Check Out**
3. Enter custodian name and comment
4. The timeline below shows the full custody history

### Manage Users (Admin only)
1. Click **Users** in the navbar
2. Click **+ New User** to create
3. Click a user → **Edit** to update roles, team, status

### Final Reports
1. Click **Reports** in the navbar
2. Select a case from the dropdown
3. Click **Upload Report**, select file and report type
4. View/download/delete reports from the list

### Quiz / Knowledge Center
1. Click **Quiz** in the navbar
2. Select category and difficulty level
3. Answer 10 questions
4. View your score and review answers

---

## Role Permissions

| Action | Admin | Case Manager | Investigator | QA | Requester | Authoriser |
|--------|:-----:|:------------:|:------------:|:--:|:---------:|:----------:|
| Create Cases | Yes | Yes | No | No | Yes | No |
| Manage Cases | Yes | Yes | No | No | No | No |
| Close/Authorize Cases | Yes | Yes | No | No | No | Yes |
| Create Tasks | Yes | Yes | No | No | No | No |
| Work on Tasks | Yes | Yes | Yes | Yes | No | No |
| Manage Evidence | Yes | Yes | Yes | No | No | No |
| Manage Users | Yes | No | No | No | No | No |
| View Reports | Yes | Yes | Yes | Yes | Yes | Yes |
| Upload Final Reports | Yes | Yes | No | No | No | No |

---

## API Endpoints (Key)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/login` | Login and get JWT token |
| GET | `/api/cases` | List all cases (filtered by role) |
| POST | `/api/cases` | Create a new case |
| PUT | `/api/cases/{id}` | Update a case |
| PUT | `/api/cases/{id}/status` | Change case status |
| GET | `/api/cases/{id}/tasks` | Get tasks for a case |
| GET | `/api/cases/{id}/evidence` | Get evidence for a case |
| POST | `/api/evidence/case/{caseId}` | Create evidence |
| POST | `/api/evidence/{id}/photo` | Upload evidence photo |
| POST | `/api/evidence/{id}/document` | Upload evidence document |
| POST | `/api/uploads/case/{caseId}` | Upload case document |
| POST | `/api/uploads/case/{caseId}/final-report` | Upload final report |
| GET | `/api/users` | List all users (admin) |
| POST | `/api/users` | Create user (admin) |

---

## Project Structure

```
JENE-DFCMS/
├── hwt-backend/                    # Spring Boot backend
│   ├── src/main/java/com/foreman/
│   │   ├── config/                 # Security, JWT, CORS, RoleHelper, DataInitializer
│   │   ├── controller/             # REST controllers
│   │   ├── dto/                    # Data Transfer Objects
│   │   ├── model/                  # JPA entities
│   │   ├── repository/             # Spring Data repositories
│   │   ├── security/               # UserDetailsService
│   │   ├── service/                # Business logic
│   │   └── util/                   # Input sanitization
│   ├── src/main/resources/
│   │   └── application.properties  # Configuration
│   └── pom.xml                     # Maven dependencies
│
├── hwt-frontend/                   # Angular 17 frontend
│   └── src/app/
│       ├── components/             # UI components
│       │   ├── login/              # Login page with role selector
│       │   ├── dashboard/          # Dashboard with stats and tips
│       │   ├── cases/              # Case list, detail, form
│       │   ├── tasks/              # Task list, detail, form
│       │   ├── evidence/           # Evidence list, detail, form
│       │   ├── users/              # User list, form (admin)
│       │   ├── search/             # Advanced search with charts
│       │   ├── quiz/               # Knowledge center quiz
│       │   ├── final-reports/      # Final report management
│       │   └── shared/             # Navbar, sidebar
│       ├── services/               # HTTP services
│       ├── models/                 # TypeScript interfaces
│       ├── guards/                 # Auth guard with role checking
│       └── interceptors/           # JWT interceptor
│
├── JENE-DFCMS-VAPT-Report.html            # Security assessment report
├── JENE-DFCMS-VAPT-PostPatch-Report.html  # Post-patch verification
├── JENE-DFCMS-Workflow-and-RBAC.html      # Workflow diagrams & RBAC guide
├── JENE-DFCMS-Roles-and-Access.html       # Roles & access document
├── JENE-DFCMS-Features-and-Templates.html # Features document
├── JENE-DFCMS-User-Manual.html            # User manual
├── Building-a-DFCMS-Book.html             # Complete teaching book (17 chapters)
└── README.md
```

---

## Configuration

Key settings in `hwt-backend/src/main/resources/application.properties`:

```properties
# Database (H2 file-based)
spring.datasource.url=jdbc:h2:file:./data/jene-dfcms-db
spring.datasource.username=sa
spring.datasource.password=changeme123

# JWT secret (override with JWT_SECRET env variable)
JWT_SECRET=your-secret-key-change-in-production

# Server
server.port=8080
```

---

## Security

- JWT tokens expire after **1 hour**
- Accounts lock after **5 failed login attempts**
- File uploads restricted to whitelisted extensions (PDF, DOC, images, etc.)
- Dangerous file types blocked (EXE, BAT, PHP, JSP, etc.)
- XSS prevention via input sanitization
- CORS restricted to `localhost:4200`
- H2 console disabled in production
- All API endpoints require authentication (except `/api/auth/login`)

---

## Building for Production

### Backend
```bash
cd hwt-backend
mvn clean package
java -jar target/hwt-backend-1.0.0.jar
```

### Frontend
```bash
cd hwt-frontend
ng build --configuration production
```

Output is in `hwt-frontend/dist/hwt-frontend/`. Serve with nginx or any static file server.

---

## Author

**Sukhvinder Chhoker**
- Email: chhokersukhvinder@gmail.com
- Phone: +91 75760 69781

---

## License

This project is for educational and professional use.
