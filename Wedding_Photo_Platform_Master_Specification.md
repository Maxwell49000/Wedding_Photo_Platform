# Architecture Specification -- Wedding Photo Platform

> **Purpose**
>
> This document is the single source of truth for AI development agents
> (GitHub Copilot, Claude Code, Cursor, OpenAI Codex...). Every
> generated file must comply with this specification.

------------------------------------------------------------------------

# 1. Project vision

## Goal

Build a modern web application allowing wedding guests to upload,
browse, search and download photos from before, during and after the
wedding.

The application must prioritize:

-   simplicity
-   performance
-   maintainability
-   clean architecture
-   scalability
-   mobile-first UX

The objective is **not** to create a social network but a private
collaborative gallery.

------------------------------------------------------------------------

# 2. Functional requirements

## Guests

Guests can:

-   access the application using the private wedding URL and wedding
    access code
-   upload one or several photos
-   browse the gallery
-   filter photos
-   download photos
-   view information about each photo

No account creation.

No authentication.

No password.

## Administrator

The administrator can:

-   manage guests
-   hide a photo
-   delete a photo
-   edit metadata
-   manage person tags

------------------------------------------------------------------------

# 3. User journeys

## Guest

Home → Enter wedding code → Gallery → Upload → Success

## Administrator

Admin login → Dashboard → Guests → Photos → Tags → Settings

------------------------------------------------------------------------

# 4. Wireframes (text)

## Home

Header

Welcome

\[Enter wedding code\]

\[Access gallery\]

\[Upload photos\]

------------------------------------------------------------------------

## Upload

Uploader (autocomplete)

Photographer (autocomplete or external)

People present (multi autocomplete)

Comment

Drag & Drop area

Progress bar

Upload button

------------------------------------------------------------------------

## Gallery

Responsive masonry/grid

Top filters

Infinite scroll (future)

Click photo → Viewer

------------------------------------------------------------------------

## Viewer

Large image

Photo metadata

Download

Previous / Next

------------------------------------------------------------------------

# 5. Technical stack

Frontend

-   Vue 3
-   Composition API
-   Vuetify
-   Pinia
-   Axios
-   Vue Router

Backend

-   Java 21
-   Spring Boot
-   Spring Data JPA
-   Validation
-   Lombok

Database

-   PostgreSQL

Infrastructure

-   Docker
-   Docker Compose
-   Nginx
-   VPS

------------------------------------------------------------------------

# 6. Clean architecture

Frontend responsibilities

Views: only orchestration

Components: UI only

Stores: business state

Services: HTTP only

Utils: helpers

Backend

Controller → Service → Repository

DTO Mapper Entity

No business logic inside controllers.

No SQL outside repositories.

------------------------------------------------------------------------

# 7. Project tree

## Frontend

``` text
src/
 components/
 layouts/
 views/
 router/
 stores/
 services/
 dto/
 composables/
 utils/
 assets/
 plugins/
```

## Backend

``` text
controller/
service/
repository/
entity/
dto/
mapper/
config/
validation/
exception/
security/
```

------------------------------------------------------------------------

# 8. Database model

## guest

id

firstname

lastname

display_name

category

table_number

is_active

## photo

id

filename

filepath

description

upload_date

uploader_guest_id

photographer_guest_id

is_visible

deleted_at

## photo_guest_tag

photo_id

guest_id

Relations

Guest 1..N Photo (uploader)

Guest 1..N Photo (photographer)

Photo N..N Guest

Images are NEVER stored in PostgreSQL.

------------------------------------------------------------------------

# 9. REST API

GET /api/photos

GET /api/photos/{id}

POST /api/photos/upload

PUT /api/photos/{id}

DELETE /api/photos/{id}

GET /api/guests

POST /api/guests

PUT /api/guests/{id}

DELETE /api/guests/{id}

GET /api/search

Response rules

200 OK

201 Created

204 No Content

400 Bad Request

404 Not Found

500 Internal Server Error

------------------------------------------------------------------------

# 10. DTO strategy

Every endpoint exchanges DTOs.

Never expose JPA entities.

Separate:

Request DTO

Response DTO

Mapper classes convert DTO ↔ Entity.

------------------------------------------------------------------------

# 11. Pinia stores

guestStore

photoStore

galleryStore

uploadStore

settingsStore

Stores never call fetch directly inside components.

------------------------------------------------------------------------

# 12. Axios

Single configured client.

Interceptors.

Global error handling.

One service per domain.

------------------------------------------------------------------------

# 13. Business rules

Guests selected through autocomplete only.

No free-text names.

Automatic publication.

No moderation workflow.

Administrator may hide or delete afterwards.

Generate:

original

optimized image

thumbnail

Gallery always loads thumbnails.

------------------------------------------------------------------------

# 14. Validation

Server-side validation mandatory.

Allowed MIME types only.

Configurable max size.

Unique server filename.

Reject invalid uploads.

------------------------------------------------------------------------

# 15. Security

No secrets in Git.

Use environment variables.

Protect against path traversal.

Validate every input.

Limit upload size.

------------------------------------------------------------------------

# 16. Coding conventions

Java

Constructor injection.

Services small.

Meaningful names.

No duplicated code.

Vue

Composition API.

No business logic in templates.

Reusable components.

------------------------------------------------------------------------

# 17. Docker

Services

frontend

backend

postgres

nginx

Volumes

photos_data

postgres_data

------------------------------------------------------------------------

# 18. Testing

Backend

JUnit

Mockito

Frontend

Vitest

Component tests

------------------------------------------------------------------------

# 19. Future extensions

Albums

ZIP download

QR Code

Favorites

Slideshows

Statistics

Face recognition module

Cloud storage abstraction

------------------------------------------------------------------------

# 20. AI agent instructions

Always respect this specification.

Never invent architecture.

Never bypass DTOs.

Never expose entities.

Never duplicate logic.

Prefer reusable components.

Keep code readable.

Keep responsibilities separated.

When uncertain, extend existing architecture instead of introducing a
new pattern.
