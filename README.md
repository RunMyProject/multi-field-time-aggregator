# multi-field-time-aggregator

A web application to dynamically aggregate and sum employee project working hours by multiple fields.

> [!CAUTION]
> **Project Overview:**
> This project was developed to address the need for a web-based data analysis tool capable of dynamic, multi-field aggregation of employee project hours. We have engineered a full-stack solution utilizing Java 25 (Spring Boot) for the backend and Angular 21 for the frontend, ensuring high-performance data processing and a highly reactive user experience. The architecture leverages a zoneless, signal-first paradigm to optimize state management and rendering efficiency.

## 📚 Project Documentation (`/docs`)

* **[`docs/STARTUP_LOG.MD`](./docs/STARTUP_LOG.MD)**: Session logs, project initialization, Git token setup and integration testing architecture.
* **[`docs/SETUP_ACTIVITY_REGISTRY.MD`](./docs/SETUP_ACTIVITY_REGISTRY.MD)**: Session logs, domain modeling, In-Memory DB setup, service wiring, automation script and integration testing validation.
* **[`docs/SOLUTION_PROPOSED.MD`](./docs/SOLUTION_PROPOSED.MD)**: Session logs, architectural refactoring, multi-field aggregation logic (3-step Stream pipeline), sorting strategy implementation and API v2.0 integration.
* **[`docs/FRONTEND_ANGULAR.MD`](./docs/FRONTEND_ANGULAR.MD)**: Angular 21 application initialization, zoneless signal-first architecture (signal, computed, linkedSignal) and servlet-level CorsFilter implementation.
