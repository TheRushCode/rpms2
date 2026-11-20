-- =========================================================
-- RPMS (Recruitment & Placement Management System)
-- PostgreSQL Database Schema (Flyway V1 Migration)
-- =========================================================

-- Optional master lookup tables
CREATE TABLE IF NOT EXISTS qualifications (
    qualification_id SERIAL PRIMARY KEY,
    qualification_name VARCHAR(100) NOT NULL UNIQUE,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS locations (
    location_id SERIAL PRIMARY KEY,
    city_name VARCHAR(100) NOT NULL,
    state_name VARCHAR(100) NOT NULL,
    country_name VARCHAR(100) NOT NULL DEFAULT 'India',
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT uq_location UNIQUE (city_name, state_name, country_name)
);

-- Roles and Users (Security)
CREATE TABLE IF NOT EXISTS roles (
    role_id SERIAL PRIMARY KEY,
    role_code VARCHAR(30) NOT NULL UNIQUE,
    role_name VARCHAR(80) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS users (
    user_id SERIAL PRIMARY KEY,
    username VARCHAR(80) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(120) NOT NULL,
    email VARCHAR(150) UNIQUE,
    role_id INT NOT NULL REFERENCES roles(role_id),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE','INACTIVE','LOCKED')),
    last_login_at TIMESTAMPTZ NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Candidates & Candidate Skills
CREATE TABLE IF NOT EXISTS candidates (
    candidate_id SERIAL PRIMARY KEY,
    candidate_code VARCHAR(30) NOT NULL UNIQUE,
    full_name VARCHAR(150) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    mobile VARCHAR(20) NOT NULL UNIQUE,
    date_of_birth DATE NULL,
    gender VARCHAR(20) NULL,
    qualification VARCHAR(80) NOT NULL,
    college VARCHAR(180) NULL,
    university VARCHAR(180) NULL,
    passing_year INT NOT NULL,
    percentage NUMERIC(5,2) NULL CHECK (percentage IS NULL OR (percentage >= 0 AND percentage <= 100)),
    cgpa NUMERIC(4,2) NULL,
    backlog_count INT NOT NULL DEFAULT 0 CHECK (backlog_count >= 0),
    primary_technology VARCHAR(80) NULL,
    current_location VARCHAR(100) NULL,
    preferred_location VARCHAR(100) NULL,
    profile_status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' CHECK (profile_status IN ('ACTIVE','INACTIVE','ON_HOLD','BLACKLISTED')),
    placement_status VARCHAR(20) NOT NULL DEFAULT 'NOT_PLACED' CHECK (placement_status IN ('NOT_PLACED','PLACED')),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_candidates_primary_technology ON candidates(primary_technology);
CREATE INDEX IF NOT EXISTS idx_candidates_passing_year ON candidates(passing_year);
CREATE INDEX IF NOT EXISTS idx_candidates_placement_status ON candidates(placement_status);

CREATE TABLE IF NOT EXISTS skills (
    skill_id SERIAL PRIMARY KEY,
    skill_name VARCHAR(100) NOT NULL UNIQUE,
    category VARCHAR(60) NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS candidate_skills (
    candidate_skill_id SERIAL PRIMARY KEY,
    candidate_id INT NOT NULL REFERENCES candidates(candidate_id) ON DELETE CASCADE,
    skill_id INT NOT NULL REFERENCES skills(skill_id) ON DELETE CASCADE,
    proficiency_level VARCHAR(20) NULL CHECK (proficiency_level IS NULL OR proficiency_level IN ('BASIC','INTERMEDIATE','ADVANCED')),
    years_experience NUMERIC(4,1) NOT NULL DEFAULT 0 CHECK (years_experience >= 0),
    is_primary BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT uq_candidate_skill UNIQUE (candidate_id, skill_id)
);

CREATE INDEX IF NOT EXISTS idx_candidate_skills_candidate ON candidate_skills(candidate_id);
CREATE INDEX IF NOT EXISTS idx_candidate_skills_skill ON candidate_skills(skill_id);

-- Companies and Jobs
CREATE TABLE IF NOT EXISTS companies (
    company_id SERIAL PRIMARY KEY,
    company_code VARCHAR(30) NOT NULL UNIQUE,
    company_name VARCHAR(180) NOT NULL,
    industry VARCHAR(100) NULL,
    website VARCHAR(200) NULL,
    location VARCHAR(120) NULL,
    company_type VARCHAR(30) NULL,
    hr_name VARCHAR(120) NULL,
    hr_email VARCHAR(150) NULL,
    hr_mobile VARCHAR(20) NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE','INACTIVE')),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_companies_name ON companies(company_name);
CREATE INDEX IF NOT EXISTS idx_companies_status ON companies(status);

CREATE TABLE IF NOT EXISTS jobs (
    job_id SERIAL PRIMARY KEY,
    job_code VARCHAR(40) NOT NULL UNIQUE,
    company_id INT NOT NULL REFERENCES companies(company_id) ON DELETE RESTRICT,
    job_title VARCHAR(150) NOT NULL,
    employment_type VARCHAR(30) NOT NULL DEFAULT 'FULL_TIME',
    location VARCHAR(120) NOT NULL,
    openings INT NOT NULL CHECK (openings > 0),
    min_package_lpa NUMERIC(6,2) NULL,
    max_package_lpa NUMERIC(6,2) NULL,
    min_percentage NUMERIC(5,2) NULL CHECK (min_percentage IS NULL OR (min_percentage >= 0 AND min_percentage <= 100)),
    min_passing_year INT NULL,
    max_passing_year INT NULL,
    max_backlogs INT NOT NULL DEFAULT 0 CHECK (max_backlogs >= 0),
    experience_min_years NUMERIC(4,1) NOT NULL DEFAULT 0 CHECK (experience_min_years >= 0),
    qualification_rule VARCHAR(255) NULL,
    posted_date DATE NOT NULL,
    application_deadline DATE NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'OPEN' CHECK (status IN ('DRAFT','OPEN','CLOSED','CANCELLED')),
    created_by INT NULL REFERENCES users(user_id),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_jobs_package CHECK (
        min_package_lpa IS NULL OR
        max_package_lpa IS NULL OR
        min_package_lpa <= max_package_lpa
    ),
    CONSTRAINT chk_jobs_dates CHECK (application_deadline >= posted_date)
);

CREATE INDEX IF NOT EXISTS idx_jobs_company ON jobs(company_id);
CREATE INDEX IF NOT EXISTS idx_jobs_status ON jobs(status);
CREATE INDEX IF NOT EXISTS idx_jobs_deadline ON jobs(application_deadline);
CREATE INDEX IF NOT EXISTS idx_jobs_location ON jobs(location);

CREATE TABLE IF NOT EXISTS job_skills (
    job_skill_id SERIAL PRIMARY KEY,
    job_id INT NOT NULL REFERENCES jobs(job_id) ON DELETE CASCADE,
    skill_id INT NOT NULL REFERENCES skills(skill_id) ON DELETE CASCADE,
    minimum_level VARCHAR(20) NULL CHECK (minimum_level IS NULL OR minimum_level IN ('BASIC','INTERMEDIATE','ADVANCED')),
    mandatory_flag BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT uq_job_skill UNIQUE (job_id, skill_id)
);

-- Applications & History
CREATE TABLE IF NOT EXISTS applications (
    application_id SERIAL PRIMARY KEY,
    application_code VARCHAR(40) NOT NULL UNIQUE,
    candidate_id INT NOT NULL REFERENCES candidates(candidate_id) ON DELETE RESTRICT,
    job_id INT NOT NULL REFERENCES jobs(job_id) ON DELETE RESTRICT,
    applied_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    eligibility_status VARCHAR(20) NOT NULL DEFAULT 'PENDING' CHECK (eligibility_status IN ('PENDING','ELIGIBLE','NOT_ELIGIBLE')),
    eligibility_reason VARCHAR(500) NULL,
    application_status VARCHAR(30) NOT NULL DEFAULT 'APPLIED' CHECK (application_status IN (
        'APPLIED',
        'ELIGIBILITY_VERIFIED',
        'SHORTLISTED',
        'INTERVIEW_SCHEDULED',
        'INTERVIEWED',
        'SELECTED',
        'REJECTED',
        'OFFERED',
        'JOINED',
        'WITHDRAWN'
    )),
    source VARCHAR(50) NULL,
    assigned_recruiter_id INT NULL REFERENCES users(user_id),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_candidate_job UNIQUE (candidate_id, job_id)
);

CREATE INDEX IF NOT EXISTS idx_applications_status ON applications(application_status);
CREATE INDEX IF NOT EXISTS idx_applications_recruiter ON applications(assigned_recruiter_id);

CREATE TABLE IF NOT EXISTS application_history (
    history_id BIGSERIAL PRIMARY KEY,
    application_id INT NOT NULL REFERENCES applications(application_id) ON DELETE CASCADE,
    old_status VARCHAR(30) NULL,
    new_status VARCHAR(30) NOT NULL,
    remarks VARCHAR(500) NULL,
    changed_by INT NULL REFERENCES users(user_id),
    changed_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_app_history_application ON application_history(application_id);
CREATE INDEX IF NOT EXISTS idx_app_history_changed_at ON application_history(changed_at);

-- Interview Rounds and Feedback
CREATE TABLE IF NOT EXISTS interview_rounds (
    interview_id SERIAL PRIMARY KEY,
    application_id INT NOT NULL REFERENCES applications(application_id) ON DELETE CASCADE,
    round_number INT NOT NULL CHECK (round_number > 0),
    round_type VARCHAR(50) NOT NULL,
    scheduled_date DATE NOT NULL,
    scheduled_time TIME NULL,
    mode VARCHAR(20) NOT NULL CHECK (mode IN ('ONLINE','OFFLINE','TELEPHONIC')),
    interviewer_name VARCHAR(120) NULL,
    meeting_or_location VARCHAR(255) NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'SCHEDULED' CHECK (status IN ('SCHEDULED','COMPLETED','RESCHEDULED','CANCELLED','NO_SHOW')),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_application_round UNIQUE (application_id, round_number)
);

CREATE INDEX IF NOT EXISTS idx_interviews_date ON interview_rounds(scheduled_date);
CREATE INDEX IF NOT EXISTS idx_interviews_status ON interview_rounds(status);

CREATE TABLE IF NOT EXISTS interview_feedback (
    feedback_id SERIAL PRIMARY KEY,
    interview_id INT NOT NULL UNIQUE REFERENCES interview_rounds(interview_id) ON DELETE CASCADE,
    technical_rating NUMERIC(3,1) NULL CHECK (technical_rating IS NULL OR (technical_rating >= 0 AND technical_rating <= 5)),
    communication_rating NUMERIC(3,1) NULL CHECK (communication_rating IS NULL OR (communication_rating >= 0 AND communication_rating <= 5)),
    problem_solving_rating NUMERIC(3,1) NULL CHECK (problem_solving_rating IS NULL OR (problem_solving_rating >= 0 AND problem_solving_rating <= 5)),
    overall_rating NUMERIC(3,1) NULL CHECK (overall_rating IS NULL OR (overall_rating >= 0 AND overall_rating <= 5)),
    recommendation VARCHAR(30) NOT NULL CHECK (recommendation IN ('NEXT_ROUND','SELECTED','REJECTED','ON_HOLD')),
    remarks TEXT NULL,
    submitted_by INT NULL REFERENCES users(user_id),
    submitted_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_feedback_recommendation ON interview_feedback(recommendation);

-- Offers and Placements
CREATE TABLE IF NOT EXISTS offers (
    offer_id SERIAL PRIMARY KEY,
    offer_code VARCHAR(40) NOT NULL UNIQUE,
    application_id INT NOT NULL UNIQUE REFERENCES applications(application_id) ON DELETE CASCADE,
    designation VARCHAR(120) NOT NULL,
    offered_package_lpa NUMERIC(6,2) NOT NULL CHECK (offered_package_lpa >= 0),
    offer_date DATE NOT NULL,
    expected_joining_date DATE NULL,
    offer_status VARCHAR(20) NOT NULL DEFAULT 'OFFERED' CHECK (offer_status IN ('OFFERED','ACCEPTED','REJECTED','EXPIRED','WITHDRAWN')),
    remarks VARCHAR(500) NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_offers_status ON offers(offer_status);

CREATE TABLE IF NOT EXISTS placements (
    placement_id SERIAL PRIMARY KEY,
    candidate_id INT NOT NULL REFERENCES candidates(candidate_id) ON DELETE RESTRICT,
    company_id INT NOT NULL REFERENCES companies(company_id) ON DELETE RESTRICT,
    job_id INT NOT NULL REFERENCES jobs(job_id) ON DELETE RESTRICT,
    offer_id INT NOT NULL UNIQUE REFERENCES offers(offer_id) ON DELETE CASCADE,
    designation VARCHAR(120) NOT NULL,
    final_package_lpa NUMERIC(6,2) NOT NULL CHECK (final_package_lpa >= 0),
    expected_joining_date DATE NULL,
    actual_joining_date DATE NULL,
    joining_status VARCHAR(20) NOT NULL DEFAULT 'PENDING' CHECK (joining_status IN ('PENDING','JOINED','NOT_JOINED','DEFERRED','NO_SHOW')),
    placement_status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' CHECK (placement_status IN ('ACTIVE','CANCELLED')),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_placements_candidate ON placements(candidate_id);
CREATE INDEX IF NOT EXISTS idx_placements_company ON placements(company_id);
CREATE INDEX IF NOT EXISTS idx_placements_joining_status ON placements(joining_status);

-- Activity Logs
CREATE TABLE IF NOT EXISTS activity_logs (
    log_id BIGSERIAL PRIMARY KEY,
    user_id INT NULL REFERENCES users(user_id),
    module_name VARCHAR(60) NOT NULL,
    action_name VARCHAR(80) NOT NULL,
    record_id VARCHAR(60) NULL,
    description VARCHAR(500) NULL,
    logged_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_activity_user ON activity_logs(user_id);
CREATE INDEX IF NOT EXISTS idx_activity_module ON activity_logs(module_name);
CREATE INDEX IF NOT EXISTS idx_activity_logged_at ON activity_logs(logged_at);
