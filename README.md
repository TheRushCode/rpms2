# RPMS — Recruitment & Placement Management System

[![Build & Test](https://img.shields.io/badge/Build-Passing-emerald.svg)](backend)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](backend)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.5-brightgreen.svg)](backend)
[![React](https://img.shields.io/badge/React-18-blue.svg)](frontend)
[![Vite](https://img.shields.io/badge/Vite-5.4-purple.svg)](frontend)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15%2B-blue.svg)](database)
[![Deploy on Render](https://img.shields.io/badge/Backend-Render-46E3B7.svg)](render.yaml)
[![Deploy on Vercel](https://img.shields.io/badge/Frontend-Vercel-black.svg)](frontend)

A production-ready, cloud-native enterprise web application designed to manage the end-to-end campus recruitment and placement lifecycle. Fully migrated from an Eclipse Java console project to a modern Spring Boot + PostgreSQL + React full-stack application configured for split production deployment:
- **Repository**: GitHub
- **Backend & Database**: Render (Spring Boot Docker Web Service + Managed PostgreSQL)
- **Frontend**: Vercel (React 18 + Vite SPA)

---

## Table of Contents
1. [Project Overview](#project-overview)
2. [Key Features](#key-features)
3. [Technology Stack](#technology-stack)
4. [System Architecture](#system-architecture)
5. [Monorepo Directory Structure](#monorepo-directory-structure)
6. [Screenshots & UI Showcase](#screenshots--ui-showcase)
7. [Prerequisites](#prerequisites)
8. [Environment Variables](#environment-variables)
9. [Local Development Setup](#local-development-setup)
   - [Database Setup](#1-database-setup)
   - [Backend Execution](#2-backend-execution)
   - [Frontend Execution](#3-frontend-execution)
10. [Automated Testing](#automated-testing)
11. [REST API Overview](#rest-api-overview)
12. [Production Deployment Guide (GitHub + Render + Vercel)](#production-deployment-guide)
    - [Step 1: Push Repository to GitHub](#step-1-push-repository-to-github)
    - [Step 2: Deploy Backend & PostgreSQL on Render](#step-2-deploy-backend--postgresql-on-render)
    - [Step 3: Deploy Frontend on Vercel](#step-3-deploy-frontend-on-vercel)
    - [Step 4: Update CORS on Render with Live Vercel URL](#step-4-update-cors-on-render-with-live-vercel-url)
13. [Troubleshooting & FAQs](#troubleshooting--faqs)

---

## Project Overview

RPMS replaces manual placement tracking and console-based tools with a secure, responsive, multi-role web portal. It bridges colleges/training academies, candidates, corporate recruiters, and placement officers to orchestrate:
- Candidate registration, academic profiling, and technical skill matrices.
- Corporate partner profiles and job posting lifecycle.
- Automated, rule-based candidate eligibility evaluation.
- Duplicate application prevention and status workflows.
- Multi-round interview coordination and evaluator feedback.
- Formal offer management and placement recording with package tracking.
- Centralized audit trails (activity logs) and analytical reporting with CSV exports.

All core business logic, validations, and relational constraints from the original Java console implementation have been strictly preserved and enhanced with enterprise reliability.

---

## Key Features

### 1. Executive Dashboard
- Real-time KPI summary cards: Total Candidates, Active Job Openings, Total Applications, and Verified Placements.
- Visual charts: Candidate technology breakdown, application status funnels, and recruitment trends.
- Quick navigation to pending actions and live activity feed.

### 2. Candidate Management
- Full lifecycle management (Add, View, Edit, Search, Filter, Deactivate).
- Enforces academic validations (percentage range 0.00–100.00%, non-negative backlogs, 10-digit mobile, valid email format).
- Tracks candidate profiles (`ACTIVE`, `INACTIVE`, `ON_HOLD`, `BLACKLISTED`) and placement status (`NOT_PLACED`, `PLACED`).
- Referential safeguards preventing accidental deletion of candidates with existing applications.

### 3. Corporate & Job Management
- Company registry with industry categorization, locations, and HR contacts.
- Job opening management with detailed compensation brackets (min/max package in LPA) and application deadlines.
- Status transitions: `DRAFT` &rarr; `OPEN` &rarr; `CLOSED` &rarr; `CANCELLED`.

### 4. Automated Eligibility Verification Engine
Preserves the exact business logic from the original `EligibilityService.java`:
1. Candidate profile status must be `ACTIVE`.
2. Job posting status must be `OPEN`.
3. Application date must not exceed `applicationDeadline`.
4. Candidate percentage must satisfy minimum job percentage criteria (`percentage >= minPercentage`).
5. Candidate passing year must fall within `[minPassingYear, maxPassingYear]`.
6. Candidate active backlogs must not exceed job allowance (`backlogCount <= maxBacklogs`).
- Real-time eligibility preview modal before applying.

### 5. Application Management
- Prevents duplicate applications for the same candidate and job (`exists(candidateId, jobId)` constraint).
- Generates unique tracking codes (`APP-XXXXXXXX`).
- Complete lifecycle status tracking:
  `APPLIED` &rarr; `ELIGIBILITY_VERIFIED` &rarr; `SHORTLISTED` &rarr; `INTERVIEW_SCHEDULED` &rarr; `INTERVIEWED` &rarr; `SELECTED` &rarr; `OFFERED` &rarr; `JOINED` (or `REJECTED` / `WITHDRAWN`).
- Automated change audit logging in `application_history`.

### 6. Interviews, Offers & Placements
- **Interviews**: Multi-round scheduling (`ONLINE`, `OFFLINE`, `TELEPHONIC`), round status tracking (`SCHEDULED`, `COMPLETED`, `RESCHEDULED`), and evaluator scoring (0.0–5.0).
- **Offers**: Designation, offered package (LPA), offer issue date, and acceptance tracking.
- **Placements**: Records candidate joining, final accepted package, and automatically marks candidate status as `PLACED`.

### 7. Analytical Reports & CSV Export
- Aggregated reports by Primary Technology, Passing Year, Qualification, and Placement status.
- One-click CSV export for offline analysis and reporting to academic authorities.

### 8. Role-Based Security & Audit Logs
- Stateless authentication with JWT (JSON Web Tokens) and BCrypt password encryption.
- Roles: `ADMIN`, `RECRUITER`, `PLACEMENT_OFFICER`.
- Activity log tracking user actions, modules, record identifiers, and timestamps.
- Admin-only staff provisioning interface.

---

## Technology Stack

| Layer | Technologies |
| :--- | :--- |
| **Backend** | Java 21, Spring Boot 3.3.5, Spring Security, Spring Data JPA, Spring Validation |
| **Database** | PostgreSQL 15+, Flyway DB Migrations, HikariCP Connection Pooling |
| **Security** | JWT (jjwt 0.12.6), BCrypt Password Hashing, Dynamic Comma-Separated CORS |
| **Frontend** | React 18, Vite 5.4, Tailwind CSS, Lucide React Icons, Axios |
| **Build & Tooling** | Maven 3.9.9, Node.js 20+, Docker (Multi-stage build) |
| **Production Infrastructure** | GitHub (Repo) + Render (Spring Boot Docker Web Service + PostgreSQL) + Vercel (React Vite SPA) |

---

## System Architecture

```
┌────────────────────────────────────────────────────────┐
│             Vercel: React + Vite Frontend (SPA)        │
│    - Responsive Admin Dashboard (Tailwind CSS)         │
│    - Client-side Routing with vercel.json SPA rewrites │
│    - Axios Client configured via VITE_API_BASE_URL     │
└───────────────────────────┬────────────────────────────┘
                            │ HTTPS / REST (JSON) + CORS
                            ▼
┌────────────────────────────────────────────────────────┐
│       Render: Spring Boot 3.3 Docker Web Service       │
│  ┌───────────────────────┬──────────────────────────┐  │
│  │ Security & JWT Filter │ Global Exception Handler │  │
│  ├───────────────────────┴──────────────────────────┤  │
│  │ Controllers (Auth, Candidates, Jobs, Apps, ...)  │  │
│  ├──────────────────────────────────────────────────┤  │
│  │ Service Layer (EligibilityEngine, Workflows)     │  │
│  ├──────────────────────────────────────────────────┤  │
│  │ Spring Data JPA Repositories (Hibernate ORM)     │  │
│  └───────────────────────┬──────────────────────────┘  │
└───────────────────────────┼────────────────────────────┘
                            │ JDBC (HikariCP)
                            ▼
┌────────────────────────────────────────────────────────┐
│            Render Managed PostgreSQL Database          │
│    - Schema Migrations via Flyway (V1 Schema, V2 Data) │
│    - Referential Integrity, Indexes & Check Constraints│
└────────────────────────────────────────────────────────┘
```

---

## Monorepo Directory Structure

```
RPMS_Java_Eclipse/
├── backend/                       # Spring Boot 3 Backend
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/kiranacademy/rpms/
│   │   │   │   ├── config/        # DatabaseConfig (Render URL parser) & CORS
│   │   │   │   ├── controller/    # 13 REST Controllers
│   │   │   │   ├── dto/           # Request & Response DTOs
│   │   │   │   ├── entity/        # 15 JPA Entities
│   │   │   │   ├── exception/     # Custom Exceptions & Global Handler
│   │   │   │   ├── repository/    # Spring Data Repositories
│   │   │   │   ├── security/      # JWT, SecurityConfig, UserDetails
│   │   │   │   └── service/       # Business Logic & Eligibility Service
│   │   │   └── resources/
│   │   │       ├── application.yml
│   │   │       └── db/migration/  # Flyway PostgreSQL Migrations
│   │   └── test/                  # Unit & Integration Tests (14 passing)
│   ├── Dockerfile                 # Multi-stage production Dockerfile
│   └── pom.xml                    # Maven build configuration
├── frontend/                      # React + Vite Frontend
│   ├── src/
│   │   ├── components/common/     # Navbar, Sidebar, Modal, Badge, StatCard
│   │   ├── context/               # AuthContext
│   │   ├── pages/                 # Dashboard, Candidates, Jobs, Apps, etc.
│   │   └── services/              # Axios API client (handles base URLs safely)
│   ├── .env.example               # Template documenting VITE_API_BASE_URL
│   ├── vercel.json                # Vercel SPA rewrite configuration (all -> index.html)
│   ├── index.html
│   ├── package.json
│   ├── tailwind.config.js
│   └── vite.config.js
├── database/
│   └── migrations/                # Standalone DB Migration scripts
├── docs/                          # Original Student Specification PDF (Preserved)
├── sql/                           # Original MySQL Scripts (Preserved)
├── src/                           # Original Eclipse Console Source (Preserved)
├── .gitignore                     # Git ignore rules for Java, Node, env, IDE
├── PROJECT_ANALYSIS.md            # Comprehensive pre-migration technical audit
├── README.md                      # Complete project documentation
└── render.yaml                    # Render Blueprint (Backend + PostgreSQL only)
```

---

## Screenshots & UI Showcase

*(When running locally, access `http://localhost:5173` to explore these interfaces)*

1. **Dashboard**: KPI stat metrics, candidate skill distribution charts, and recruitment funnel analytics.
2. **Candidates Directory**: Tabular candidate grid with search, qualification/technology filter pills, and eligibility badges.
3. **Application Workflow**: Step-by-step modal checking eligibility rules with live visual passes/fails before submission.
4. **Interviews & Offers**: Evaluation scores and official offer packages tracking.
5. **Reports**: Technology-wise distribution with one-click `.csv` download.

---

## Prerequisites

- **Java**: OpenJDK 21 (Temurin / Oracle)
- **Maven**: 3.9.x (or use the provided tool in `tools/apache-maven-3.9.9`)
- **Node.js**: v18+ or v20+ with npm
- **Database**: PostgreSQL 15+ (or Docker PostgreSQL container)
- **Git**: Installed and configured

---

## Environment Variables

### Backend Configuration (Render)
| Variable | Required | Default | Description |
| :--- | :--- | :--- | :--- |
| `PORT` | No | `8080` | Web server listening port (Render assigns dynamically) |
| `DATABASE_URL` | Yes (Prod) | `jdbc:postgresql://localhost:5432/rpms_db` | Render PostgreSQL internal connection URL |
| `DB_USERNAME` | No | `postgres` | Database username (parsed automatically from DATABASE_URL) |
| `DB_PASSWORD` | No | `postgres` | Database password (parsed automatically from DATABASE_URL) |
| `JWT_SECRET` | Yes (Prod) | *(Generated by Render)* | 256-bit secret key for signing JSON Web Tokens |
| `CORS_ALLOWED_ORIGINS` | Yes (Prod) | `*` | Comma-separated allowed origins (e.g. `https://my-rpms.vercel.app,http://localhost:5173`) |

### Frontend Configuration (Vercel)
| Variable | Required | Default | Description |
| :--- | :--- | :--- | :--- |
| `VITE_API_BASE_URL` | Yes (Prod) | `http://localhost:8080/api` | Render backend API base URL (e.g. `https://rpms-backend.onrender.com/api`) |

---

## Local Development Setup

### 1. Database Setup

Create a local PostgreSQL database named `rpms_db`:

```sql
CREATE DATABASE rpms_db;
```

*(Note: Flyway automatically runs all migration scripts in `backend/src/main/resources/db/migration` upon backend launch, creating all 15 tables and seeding standard demo data.)*

### 2. Backend Execution

```bash
cd backend
mvn clean spring-boot:run
```

The backend starts on `http://localhost:8080`.
Verify the health check endpoint:
```bash
curl http://localhost:8080/api/health
```
Expected output:
```json
{"status":"UP","service":"RPMS Recruitment & Placement Management API","timestamp":"..."}
```

### 3. Frontend Execution

In a new terminal window:
```bash
cd frontend
npm install
npm run dev
```

The application opens at `http://localhost:5173`.

### Default Demo Credentials

| Role | Username | Password |
| :--- | :--- | :--- |
| **System Administrator** | `admin` | `Admin@123` |
| **Recruiter** | `recruiter1` | `Recruiter@123` |
| **Placement Officer** | `officer1` | `Officer@123` |

---

## Automated Testing

The backend includes a comprehensive suite of unit and integration tests verifying eligibility rules, candidate validation, and application workflows:

```bash
cd backend
mvn test
```

### Test Coverage Highlights:
- `EligibilityServiceTest`: Verifies all 7 eligibility business rules (minimum percentage, allowed passing year range, active backlog cutoff, job status, profile status, deadline checks).
- `CandidateServiceTest`: Validates 10-digit mobile check, percentage ranges, email format, and duplicate prevention.
- `ApplicationWorkflowTest`: Tests end-to-end application creation, unique tracking code generation, duplicate application blockage, and ineligible candidate rejection.

All 14 tests pass with zero failures.

---

## REST API Overview

All API endpoints are prefixed with `/api`. Protected routes require header:  
`Authorization: Bearer <jwt_token>`

### Authentication & Staff Provisioning
- `POST /api/auth/login` — Authenticate and obtain JWT token.
- `GET /api/auth/me` — Retrieve current authenticated user session profile.
- `POST /api/auth/register-staff` — Provision new recruiter/officer accounts (*Admin only*).

### Candidates
- `GET /api/candidates` — Paginated list with search, technology & status filters.
- `GET /api/candidates/{id}` — Candidate details and application history.
- `POST /api/candidates` — Register new candidate with validations.
- `PUT /api/candidates/{id}` — Update candidate profile details.
- `DELETE /api/candidates/{id}` — Deactivate candidate (soft delete / status update).

### Jobs & Companies
- `GET /api/companies` & `POST /api/companies` — Company corporate partner directory.
- `GET /api/jobs` — View jobs with filters for status, title, and location.
- `GET /api/jobs/open` — List only open jobs available for campus drives.
- `POST /api/jobs` & `PUT /api/jobs/{id}` — Create and update job postings.

### Applications & Eligibility
- `POST /api/applications` — Submit application (enforces duplicate and eligibility rules).
- `GET /api/applications` — List applications with candidate and job details.
- `GET /api/applications/check-eligibility?candidateId={c}&jobId={j}` — Real-time eligibility evaluation without submitting.
- `PATCH /api/applications/{id}/status` — Progress application stage (`SHORTLISTED`, `INTERVIEW_SCHEDULED`, `SELECTED`, etc.).

### Interviews, Offers & Placements
- `GET /api/interviews` & `POST /api/interviews` — Schedule and track interview rounds.
- `GET /api/offers` & `POST /api/offers` — Manage compensation packages and offers.
- `GET /api/placements` & `POST /api/placements` — Finalize placements and update candidate status.

### Analytics & System
- `GET /api/dashboard/summary` — Key KPI counters and recruitment metrics.
- `GET /api/dashboard/technology-distribution` — Technology breakdown.
- `GET /api/reports/technology` — Technology report.
- `GET /api/reports/technology/export` — Download CSV report.
- `GET /api/activity-logs` — Full system activity audit trail.
- `GET /api/health` — Public health check endpoint for container and uptime monitors.

---

## Production Deployment Guide

Follow these 4 sequential steps to deploy RPMS in production using GitHub, Render, and Vercel.

### Step 1: Push Repository to GitHub

```bash
# 1. Create a new GitHub repository (e.g. rpms-recruitment-placement-system)
# 2. Add remote origin (if not already added)
git remote add origin https://github.com/<your-username>/rpms-recruitment-placement-system.git

# 3. Rename branch to main and push
git branch -M main
git push -u origin main
```

---

### Step 2: Deploy Backend & PostgreSQL on Render

Render provisions the PostgreSQL database and Spring Boot backend container automatically from `render.yaml`.

1. Go to **[Render Dashboard](https://dashboard.render.com/)**.
2. Click **New +** &rarr; Select **Blueprint**.
3. Connect your GitHub repository (`rpms-recruitment-placement-system`).
4. Render detects `render.yaml` and lists the 2 resources:
   - **`rpms-postgres`**: Managed PostgreSQL Database (`rpms_db`).
   - **`rpms-backend`**: Docker Web Service running Spring Boot on Java 21.
5. In the blueprint parameters prompt:
   - For `CORS_ALLOWED_ORIGINS`, you can temporarily leave it as `*` or enter your planned Vercel domain.
6. Click **Apply**.
7. Wait 3–4 minutes while Render:
   - Creates the database.
   - Builds the Docker container via `backend/Dockerfile`.
   - Executes Flyway migrations (creates 15 tables + seeds initial records).
   - Verifies the health check at `/api/health`.
8. Once deployed, note down your Render Backend URL (e.g., `https://rpms-backend-xxxx.onrender.com`).
9. Test health in your browser:
   `https://rpms-backend-xxxx.onrender.com/api/health` &rarr; returns `{"status":"UP", ...}`.

---

### Step 3: Deploy Frontend on Vercel

1. Go to **[Vercel Dashboard](https://vercel.com/dashboard)**.
2. Click **Add New...** &rarr; Select **Project**.
3. Import your GitHub repository (`rpms-recruitment-placement-system`).
4. In the **Configure Project** screen:
   - **Project Name**: `rpms-frontend` (or your choice).
   - **Framework Preset**: Select **Vite**.
   - **Root Directory**: Click **Edit** &rarr; select **`frontend`** &rarr; click **Continue**.
   - **Build and Output Settings**: Defaults are pre-configured:
     - Build Command: `npm run build`
     - Output Directory: `dist`
5. Expand **Environment Variables**:
   - Key: `VITE_API_BASE_URL`
   - Value: `https://<your-render-backend-name>.onrender.com/api`  
     *(Be sure to include `/api` at the end)*
6. Click **Deploy**.
7. Vercel builds the SPA and deploys it in ~30 seconds.
8. When deployment finishes, note your live Vercel URL (e.g., `https://rpms-frontend-xxxx.vercel.app`).
9. Thanks to `frontend/vercel.json`, refreshing any sub-route (such as `/candidates` or `/dashboard`) works seamlessly without 404 errors.

---

### Step 4: Update CORS on Render with Live Vercel URL

To lock down backend access to only your live Vercel frontend:

1. Return to the **[Render Dashboard](https://dashboard.render.com/)**.
2. Click on the **`rpms-backend`** web service.
3. In the left navigation, click **Environment**.
4. Locate the `CORS_ALLOWED_ORIGINS` variable (or add it if not present).
5. Set the value to your live Vercel frontend URL plus local dev if desired:
   ```text
   https://rpms-frontend-xxxx.vercel.app,http://localhost:5173
   ```
6. Click **Save Changes**.
7. Render will automatically redeploy the backend with the new CORS configuration.
8. Open your live Vercel app in the browser and log in with `admin` / `Admin@123`!

---

## Troubleshooting & FAQs

#### 1. Why does Render PostgreSQL connection use `postgres://`?
Render passes `postgres://username:password@host:port/database`. Our custom `DatabaseConfig.java` automatically detects this URL format and converts it into a standard JDBC connection string (`jdbc:postgresql://host:port/database`) with separate username and password parameters for HikariCP.

#### 2. Are original Eclipse project files preserved?
Yes! The original Eclipse console starter files remain completely intact in `src/`, `sql/`, `.classpath`, and `.project`. You can still import and run the original Eclipse project whenever needed.

#### 3. Why is `frontend/vercel.json` required?
Because React is a Single Page Application (SPA) using client-side routing. When a user navigates to `/candidates` and refreshes, Vercel looks for a static file at `/candidates.html`. The rewrite rule in `frontend/vercel.json` directs all unknown routes to `/index.html`, allowing React Router to handle page rendering.

#### 4. How do I change default administrator credentials in production?
In production, log in with the initial `admin` account and change the password immediately via the database or staff provisioning panel.

---

## License

This project was developed as part of the Kiran Academy advanced placement training curriculum and modernized into an enterprise-grade web application. Free for educational and commercial recruitment management use.
