# Project Analysis: RPMS — Recruitment & Placement Management System

**Document Version:** 1.0  
**Project Base:** `RPMS_Java_Eclipse`  
**Target Architecture:** Production-Ready Full-Stack Web Application (Spring Boot + PostgreSQL + React + Vite + Tailwind CSS) deployable on Render.

---

## 1. Executive Summary

The existing project is a student starter project designed for Eclipse containing a Java console application, raw JDBC DAO implementations, and MySQL 8.x DDL/sample data scripts. 

While the database schema defines an extensive recruitment lifecycle (candidates, skills, companies, jobs, applications, interview rounds, feedback, offers, placements, activity logs, and roles/users), the Java starter implementation only partially wired Candidate, Job, and Application operations with a console-based interface (`RecruitmentApp.java`).

This document provides a comprehensive technical audit of the original codebase, documenting all business logic, database relationships, validation constraints, workflows, and missing pieces required for full enterprise web migration.

---

## 2. Existing Codebase Inventory

### 2.1 File & Package Structure
- **Package `com.kiranacademy.rpms.model`**:
  - `Candidate.java`: Core candidate fields (`candidateId`, `candidateCode`, `fullName`, `email`, `mobile`, `qualification`, `passingYear`, `percentage`, `backlogCount`, `primaryTechnology`, `profileStatus`, `placementStatus`).
  - `Job.java`: Job posting details (`jobId`, `jobCode`, `companyId`, `jobTitle`, `location`, `openings`, `minPackageLpa`, `maxPackageLpa`, `minPercentage`, `minPassingYear`, `maxPassingYear`, `maxBacklogs`, `applicationDeadline`, `status`).
  - `Application.java`: Job application entity (`applicationId`, `applicationCode`, `candidateId`, `jobId`, `appliedAt`, `eligibilityStatus`, `eligibilityReason`, `applicationStatus`).
  - `Company.java`: Corporate partner entity (`companyId`, `companyCode`, `companyName`, `industry`, `location`, `hrName`, `hrEmail`, `status`).
  - `Interview.java`: Round details (`interviewId`, `applicationId`, `roundNumber`, `roundType`, `mode`, `status`).
  - `Offer.java`: Offer record (`offerId`, `offerCode`, `applicationId`, `designation`, `offeredPackageLpa`, `offerStatus`).
  - `Placement.java`: Final placement tracking (`placementId`, `candidateId`, `companyId`, `jobId`, `finalPackageLpa`, `joiningStatus`).

- **Package `com.kiranacademy.rpms.dao`**:
  - `CandidateDAO.java` & `CandidateDAOImpl.java`: `save`, `findById`, `findByEmail`, `findByMobile`, `findAll`, `findByTechnology`, `update`, `deactivate`.
  - `JobDAO.java` & `JobDAOImpl.java`: `findById`, `findOpenJobs`.
  - `ApplicationDAO.java` & `ApplicationDAOImpl.java`: `save`, `exists` (duplicate check), `findById`, `findByCandidate`, `updateStatus`.

- **Package `com.kiranacademy.rpms.service`**:
  - `CandidateService.java`: Business validation and delegate calls to `CandidateDAO`.
  - `EligibilityService.java`: Comprehensive rule-based eligibility calculation.
  - `ApplicationService.java`: Orchestrates candidate/job existence check, duplicate verification, eligibility evaluation, code generation (`APP-XXXXXX`), and status tracking.
  - `ReportService.java`: Aggregates candidate counts by primary technology via raw JDBC.
  - `ActivityLogService.java`: Appends activity entries to a local flat file `activity_log.txt`.

- **Package `com.kiranacademy.rpms.util`**:
  - `DBConnection.java`: Raw JDBC `DriverManager.getConnection(...)` with hardcoded MySQL credentials.
  - `ValidationUtil.java`: Static validation helpers for percentage, backlogs, email regex/check, and 10-digit mobile check.

- **Package `com.kiranacademy.rpms.exception`**:
  - `CandidateNotEligibleException.java`: Thrown when a candidate violates job eligibility criteria.
  - `CandidateNotFoundException.java`: Thrown when a candidate record does not exist.
  - `DuplicateApplicationException.java`: Thrown when a candidate re-applies to the same job.
  - `JobNotFoundException.java`: Thrown when a target job does not exist.

- **Package `com.kiranacademy.rpms.app`**:
  - `RecruitmentApp.java`: Interactive command-line loop driving menus 1 through 6.

- **Database Scripts (`sql/`)**:
  - `rpms_schema.sql`: 15 tables with relational constraints, check constraints, indexes.
  - `rpms_sample_data.sql`: 20 candidates, 8 companies, 12 jobs, 40 applications, interview rounds, feedback, offers, placements, and initial activity logs.
  - `rpms_full_setup.sql`: Combined DDL and sample data runner.

---

## 3. Detailed Business Logic & Rules

### 3.1 Candidate Eligibility Engine (`EligibilityService.java`)
A candidate can only apply for a job if all of the following conditions are met:
1. **Candidate Profile Status**: Must be equal to `"ACTIVE"`. If inactive, hold, or blacklisted, fails with: `"Candidate profile is not ACTIVE"`.
2. **Job Status**: Must be equal to `"OPEN"`. If closed, draft, or cancelled, fails with: `"Job is not OPEN"`.
3. **Application Deadline**: If `job.applicationDeadline` is present, `LocalDate.now()` must NOT be after `applicationDeadline`. If expired, fails with: `"Application deadline has passed"`.
4. **Academic Percentage**: Candidate percentage must be greater than or equal to `job.minPercentage`. If lower, fails with: `"Percentage below minimum requirement"`.
5. **Minimum Passing Year**: If `job.minPassingYear > 0`, candidate passing year must be >= `job.minPassingYear`. If earlier, fails with: `"Passing year is below allowed range"`.
6. **Maximum Passing Year**: If `job.maxPassingYear > 0`, candidate passing year must be <= `job.maxPassingYear`. If later, fails with: `"Passing year is above allowed range"`.
7. **Backlog Limit**: Candidate backlog count must be <= `job.maxBacklogs`. If exceeded, fails with: `"Backlog count exceeds allowed limit"`.

### 3.2 Candidate Validation Rules (`ValidationUtil.java`, `CandidateService.java`)
- **Email Validation**: Must not be null, must contain `@` and `.`, and must be unique across the candidate table.
- **Mobile Validation**: Must not be null, must strictly match 10 digits (`\\d{10}`), and must be unique across the candidate table.
- **Percentage Range**: Must be between `0.00` and `100.00` inclusive.
- **Backlogs Count**: Must be non-negative (`>= 0`).
- **Profile Status Default**: Newly registered candidates default to `"ACTIVE"`.
- **Placement Status Default**: Defaults to `"NOT_PLACED"`.

### 3.3 Application Workflow & Integrity Rules
- **Duplicate Prevention**: A candidate cannot apply to the same job twice (`exists(candidateId, jobId)` verified via unique constraint `(candidate_id, job_id)`).
- **Application Code**: Generated with prefix `APP-` followed by an 8-character uppercase identifier.
- **Status Lifecycle**:
  `APPLIED` -> `ELIGIBILITY_VERIFIED` -> `SHORTLISTED` -> `INTERVIEW_SCHEDULED` -> `INTERVIEWED` -> `SELECTED` -> `OFFERED` -> `JOINED`  
  Alternative terminal paths: `REJECTED`, `WITHDRAWN`.
- **Audit History**: Status transitions are logged to `application_history` recording `old_status`, `new_status`, `remarks`, `changed_by`, and `changed_at`.

### 3.4 Interview, Offer & Placement Logic
- **Interview Rounds**: Associated with an application, numbered consecutively (`roundNumber >= 1`), mode in `ONLINE`, `OFFLINE`, `TELEPHONIC`, status in `SCHEDULED`, `COMPLETED`, `RESCHEDULED`, `CANCELLED`, `NO_SHOW`.
- **Interview Feedback**: 1-to-1 with interview round. Ratings (0.0 to 5.0) for technical, communication, problem solving, overall. Recommendation: `NEXT_ROUND`, `SELECTED`, `REJECTED`, `ON_HOLD`.
- **Offer Generation**: Bound to application; designation, package (LPA), offer date, expected joining date, status (`OFFERED`, `ACCEPTED`, `REJECTED`, `EXPIRED`, `WITHDRAWN`).
- **Placement Finalization**: Ties Candidate, Company, Job, and Offer together. When joining status is updated to `JOINED`, the candidate's `placement_status` updates from `NOT_PLACED` to `PLACED`.

---

## 4. Database Schema & Relational Model

The database comprises 15 tables categorized into core functional domains:

1. **Access & Security**:
   - `roles`: `role_id`, `role_code` (`ADMIN`, `RECRUITER`, `PLACEMENT_OFFICER`), `role_name`, `is_active`.
   - `users`: `user_id`, `username`, `password_hash`, `full_name`, `email`, `role_id` (FK), `status` (`ACTIVE`, `INACTIVE`, `LOCKED`).

2. **Master Lookup Tables**:
   - `qualifications`: Master list of degrees (BE, BTech, MCA, BCA, etc.).
   - `locations`: Cities, states, countries.
   - `skills`: Tech skills (Java, Python, SQL, Selenium, etc.).

3. **Candidate Domain**:
   - `candidates`: Demographics, academic scores, backlog counts, primary technology, placement status.
   - `candidate_skills`: Many-to-many junction between `candidates` and `skills` with proficiency levels (`BASIC`, `INTERMEDIATE`, `ADVANCED`).

4. **Job & Corporate Domain**:
   - `companies`: Company code, name, industry, website, HR contact, status.
   - `jobs`: Job code, company FK, title, package range, eligibility constraints (min %, year window, max backlogs), deadline, status (`DRAFT`, `OPEN`, `CLOSED`, `CANCELLED`).
   - `job_skills`: Required skills per job with minimum required proficiency and mandatory flag.

5. **Application & Hiring Domain**:
   - `applications`: Junction of candidate & job, application code, eligibility outcome, status.
   - `application_history`: Audit trail for status changes.
   - `interview_rounds`: Round schedule, type, mode, interviewer.
   - `interview_feedback`: Ratings, remarks, recommendations.
   - `offers`: Compensation, offer code, acceptance status.
   - `placements`: Placement records, package, joining dates, joining status.

6. **System Operations**:
   - `activity_logs`: User activity tracking (module, action, record ID, description, timestamp).

---

## 5. Menus and Workflows in `RecruitmentApp.java`

The console application exposes 6 options:
1. `View Candidates`: Fetches all candidate records from database and prints them to console.
2. `Add Candidate`: Solicits input for candidate code, full name, email, mobile, qualification, passing year, percentage, backlogs, and primary technology; defaults status to `ACTIVE` and `NOT_PLACED`; executes validation; saves to DB; appends to `activity_log.txt`.
3. `View Open Jobs`: Fetches all jobs where `status = 'OPEN'` and displays summary.
4. `Apply for Job`: Solicits `candidateId` and `jobId`; verifies candidate and job exist; checks duplicate application; runs `EligibilityService`; saves application with `APP-XXXXXX` code; logs activity.
5. `Candidate Technology Report`: Aggregates candidate counts grouped by `primary_technology`.
6. `Exit`: Terminates the console runner loop.

---

## 6. Current Technical Inconsistencies & Compatibility Issues

1. **Database Name Mismatch**:
   - `DBConnection.java` connects to `jdbc:mysql://localhost:3306/recruitment_portal`.
   - `sql/rpms_schema.sql` creates and targets `CREATE DATABASE rpms_db; USE rpms_db;`.
2. **Hardcoded Credentials**:
   - Database user `root` and password `root123` hardcoded in source code without environment variable fallback.
3. **Database Dialect Lock-in**:
   - Schema uses MySQL-specific types: `DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP`, `TINYINT(1)`, and functions like `DATE_ADD(..., INTERVAL 1 DAY)`.
   - Migration to PostgreSQL requires `TIMESTAMP WITH TIME ZONE`, `BOOLEAN`, trigger/function or Hibernate timestamp handling for `updated_at`, and standard interval syntax.
4. **Placeholder Password Hashes**:
   - `sql/rpms_sample_data.sql` inserts placeholder dummy hashes (`$2y$10$DEMO_ADMIN_HASH_ONLY`) which cannot be authenticated by standard BCrypt.
5. **Partial DAO Implementations**:
   - Only `CandidateDAO`, `JobDAO`, and `ApplicationDAO` have basic implementations.
   - `Company`, `Interview`, `Offer`, and `Placement` have Java model classes but zero DAOs or services.
6. **File Logging vs Database Logging**:
   - `ActivityLogService` writes to a text file `activity_log.txt` on the local disk instead of persisting into the existing `activity_logs` database table.
7. **No Web Interface, Security, or REST API**:
   - Entire application runs via console `Scanner(System.in)`, with no web server, session management, or endpoint abstraction.

---

## 7. Migration & Modernization Roadmap

To migrate RPMS to a production-ready, cloud-deployable architecture on Render:
1. **Repository Layout**:
   Organize into clean monorepo:
   - `backend/`: Spring Boot 3.3.x (Java 21), Maven, Spring Data JPA, Spring Security, Flyway, PostgreSQL, Global Exception Handling, REST API.
   - `frontend/`: React 18 / Vite, Tailwind CSS, Lucide icons, Axios, Responsive SaaS Admin UI.
   - `database/migrations/`: Flyway PostgreSQL migration scripts (V1 Schema, V2 Seed Data).
   - `render.yaml`, `Dockerfile`: Containerized production deployment configuration for Render.
2. **Database Engine**:
   PostgreSQL 15+ compatible migrations with complete referential integrity, indexes, and seeded records (with valid BCrypt passwords for `admin`, recruiters, placement officers).
3. **Security**:
   Spring Security with JWT authentication, BCrypt password hashing, role-based authorization (`ADMIN`, `RECRUITER`, `PLACEMENT_OFFICER`), secure CORS for frontend, and stateless session management.
4. **Feature Completeness**:
   Implement full CRUD, filtering, search, and pagination across all modules: Dashboard, Candidates, Jobs, Applications, Companies, Interviews, Offers, Placements, Activity Logs, and Reports (with CSV export).
5. **Preservation Guarantee**:
   All original business rules from `EligibilityService`, candidate validation rules from `ValidationUtil`, application workflow states, and duplicate prevention logic will be faithfully preserved and enforced at both API and database layers.
