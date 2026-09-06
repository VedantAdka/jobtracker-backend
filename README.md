# Job Tracker — Backend

AI-powered job application tracker backend — Spring Boot, Spring Security (JWT), PostgreSQL, and Google's Gemini API for resume/job-description matching and interview prep.

**Frontend repo:** [jobtracker-frontend](https://github.com/VedantAdka/jobtracker-frontend)

## Features
- JWT-based authentication (register/login)
- Full CRUD for job applications, scoped per user
- Resume upload (PDF) with text extraction via Apache PDFBox
- AI-powered resume-to-job-description match analysis (Google Gemini)
- AI-generated interview question suggestions based on identified skill gaps, with a practiced/not-practiced checklist
- RESTful API secured end-to-end, with centralized exception handling

## Tech stack
- Java 17
- Spring Boot 3 (Web, Security, Data JPA)
- PostgreSQL
- JWT (jjwt)
- Apache PDFBox
- Google Gemini API
- Maven
- Docker

## Project structure
