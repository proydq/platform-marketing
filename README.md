# Platform Marketing Project

This repository contains a simple marketing platform demo with a frontend built using Vue 3 and a backend based on Spring Boot 2.5.

The permission management module is implemented in `/frontend/src/views/system/PermissionView.vue` and corresponding backend code under `/backend`.

The project now also includes a **Marketing Campaign** module exposing REST APIs under `/api/marketing-campaign`. Status updates use a `PATCH /status` endpoint.

An **Email Campaign** module is available under `/api/email-campaign` for managing email templates and send records.


See `frontend/README.md` for instructions on running the client and `backend/pom.xml` for backend dependencies.
