# `FRONTEND_ANGULAR.MD`

## 📅 Session 4: Wednesday, May 20, 2026

**Objective:** Frontend bootstrapping — Angular 21 signal-first client for the multi-field time aggregator REST API, plus CORS hardening on the Spring Boot side.

| Timestamp | Category | Action | Notes |
| --- | --- | --- | --- |
| **18:00** | **Setup** | **Angular 21 Project Bootstrap** | New standalone Angular 21 project scaffolded via Angular CLI. `provideHttpClient()` and `provideBrowserGlobalErrorListeners()` configured in `app.config.ts`. Zoneless change detection confirmed as default. |
| **18:10** | **Design** | **Domain Model Layer** | Seven TypeScript interfaces/types created under `app/model/` mirroring every Java record and enum from the backend. |
| **18:20** | **Architecture** | **Signal-First Service** | `activity-service.ts` implemented with `httpResource`, reactive `groupBy` signal, and union response type. No Observables, no direct `HttpClient` subscription calls. |
| **18:35** | **Development** | **ActivityList Component** | `activity-list.ts` + `activity-list.html` built with `linkedSignal`, `computed`, `@if / @for / @switch` built-in control flow. Fully aligned with Angular 17+ reactive patterns. |
| **18:50** | **Styling** | **Editorial CSS Layer** | `activity-list.css` scoped with CSS custom properties — dark-slate palette, amber accents, sticky `<thead>`, responsive breakpoints. |
| **18:55** | **Bug Fix** | **`hasValue()` this-binding crash** | Root cause identified: aliasing a resource method detaches `this`. Fixed by wrapping all resource state reads in `computed()`. |
| **18:57** | **Bug Fix** | **CORS Preflight 400** | Custom `X-API-Version` header triggers OPTIONS preflight; Spring MVC version dispatcher intercepts it before CORS headers are written. Fixed via `CorsFilter` bean at servlet-filter level. |
| **19:00** | **Docs** | **Documentation Updated** | This file. Full per-file breakdown, Angular 17+ API guide, architectural rationale. |

---

## 🚀 Key Architectural Decisions

### 1. Fully Standalone Architecture (Angular 17+)

This project uses the **standalone component model** introduced and stabilized in Angular 17+. There is no `AppModule`, no `NgModule`, no `BrowserModule`. Every component declares its own dependencies directly in its `@Component` decorator's `imports` array, and the application is bootstrapped via `bootstrapApplication()` configured through `app.config.ts`.

This eliminates an entire category of boilerplate and makes dependency trees explicit and tree-shakeable by default.

### 2. Signal-First Reactivity (Angular 16+, matured in 17–21)

The entire state and data layer is built on Angular's **Signals API**. Signals are synchronous, fine-grained reactive primitives that integrate directly with Angular's change detection engine. When a signal changes, only the specific template expressions and computed values that depend on it are re-evaluated — no zone diffing, no component subtree scanning.

The three signal primitives used in this project:

| Primitive | Role in this project |
|---|---|
| `signal()` | Owns the mutable `groupBy` selection state in the service |
| `computed()` | Derives typed data projections and wraps resource state accessors |
| `linkedSignal()` | Synchronizes the local dropdown with the service signal |

### 3. `httpResource` for Reactive HTTP (Angular 19.2+)

Rather than manually wiring `HttpClient` with subscription management, this project uses `httpResource` — a reactive HTTP primitive that exposes request state as signals (`isLoading()`, `hasValue()`, `error()`, `value()`) and automatically re-fires the request whenever its reactive dependencies change.

### 4. Built-in Control Flow (Angular 17+)

All template branching and iteration uses Angular's native block syntax (`@if`, `@for`, `@switch`). No structural directives (`*ngIf`, `*ngFor`), no `CommonModule`, no `NgSwitch` imports.

---

## 🗂️ File-by-File Reference

### Spring Boot

---

#### `com/backend/config/GlobalCorsConfig.java`

**What it does:** Registers a `CorsFilter` Spring bean at `Ordered.HIGHEST_PRECEDENCE` so that every HTTP request — including OPTIONS preflight requests — passes through CORS handling at the **Servlet filter level**, before Spring MVC's `DispatcherServlet` and the custom API-version dispatcher touch it.

**Why it was needed:** The existing `WebMvcConfigurer.addCorsMappings()` approach (and the `@CrossOrigin` controller annotation) both hook into the Spring MVC interceptor chain, which runs *after* request dispatching. The custom `@GetMapping(version = "X.X")` dispatcher intercepted the browser's OPTIONS preflight, found no matching versioned handler for `OPTIONS`, returned HTTP 400, and CORS headers were never written — the browser therefore blocked the real `GET` request.

**Key design choices:**

- `@Order(Ordered.HIGHEST_PRECEDENCE)` — ensures the filter runs before any other filter (Spring Security, logging, etc.).
- `config.addAllowedHeader("*")` — allows the `X-API-Version` custom header that triggers the preflight.
- `config.addExposedHeader("X-API-Version")` — makes the header readable on the Angular side if needed in future.
- `allowCredentials(true)` with an explicit origin (not `"*"`) — required by the browser CORS spec when credentials are involved.

```
Browser OPTIONS preflight
        │
        ▼
  Servlet Filters
  [CorsFilter @HIGHEST_PRECEDENCE]  ← new, servlet level ✅ → 200 OK + CORS headers
        │
        ▼
  DispatcherServlet
        │
        ▼
  Version Dispatcher                ← WebMvcConfigurer tried to handle CORS here ❌
        │                              Returns 400 for OPTIONS → CORS headers never written
        ▼
  HandlerInterceptors
  (addCorsMappings / @CrossOrigin)
```

---

### Angular 21 Frontend

---

#### `app/app.config.ts`

**What it does:** The application root configuration — the Angular 17+ replacement for `AppModule`. All framework-level providers are registered here and composed into a single `ApplicationConfig` object passed to `bootstrapApplication()`.

```typescript
import { ApplicationConfig, provideBrowserGlobalErrorListeners } from '@angular/core';
import { provideHttpClient } from '@angular/common/http';

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideHttpClient()
  ]
};
```

**`provideBrowserGlobalErrorListeners()`** (Angular 19+) — registers a global unhandled-error listener in the browser environment. It captures uncaught exceptions and unhandled Promise rejections and routes them through Angular's `ErrorHandler` infrastructure, giving the framework visibility into errors that originate outside the change detection cycle (e.g. inside `setTimeout`, native event listeners, or third-party code).

**`provideHttpClient()`** — makes the `HttpClient` service available application-wide via the root injector. This is a prerequisite for `httpResource`, which is built on top of `HttpClient` internally. Without this provider, the framework throws `R3InjectorError` at runtime (not a descriptive message — easy to miss).

**Zoneless note:** Angular 21 ships with zoneless change detection as the default. No `provideZoneChangeDetection()` call is required. The signal graph drives all re-renders natively without `zone.js`.

---

#### `app/model/group-strategy.model.ts`

**What it does:** Declares the `GroupStrategy` union type and the `GROUP_STRATEGY_OPTIONS` constant array — the TypeScript mirror of the Java `GroupStrategy` enum.

```typescript
export type GroupStrategy = 'NONE' | 'PROJECT' | 'PROJECT_EMPLOYEE' | 'EMPLOYEE_PROJECT';

export const GROUP_STRATEGY_OPTIONS: GroupStrategy[] = [
  'NONE', 'PROJECT', 'PROJECT_EMPLOYEE', 'EMPLOYEE_PROJECT'
];
```

**Why a string union type and not a TypeScript `enum`:** TypeScript `enum` values at runtime are objects; using a string union type keeps values directly usable as HTTP parameter strings with no `.toString()` conversion, and they compare cleanly with `===` in `@switch` branches. `GROUP_STRATEGY_OPTIONS` is exported as a constant array so the template can iterate it with `@for` without hardcoding strings in HTML.

---

#### `app/model/employee.model.ts`

**What it does:** TypeScript interface mirroring the Java `Employee` record.

```typescript
export interface Employee { id: number; name: string; }
```

Java records are immutable value objects — TypeScript `interface` is the idiomatic equivalent. No class or constructor needed; Angular components work directly with plain object literals.

---

#### `app/model/project.model.ts`

**What it does:** TypeScript interface mirroring the Java `Project` record. Same rationale as `employee.model.ts`.

---

#### `app/model/activity-registry.model.ts`

**What it does:** Aggregate interface mirroring the Java `ActivityRegistry` record. Contains nested `Project` and `Employee` objects plus the `date` field.

**Date handling note:** The backend applies `@JsonFormat(pattern = "dd MMM yyyy", timezone = "Europe/Rome", locale = "en")` via Jackson, so the `date` field arrives pre-formatted as a plain string (e.g. `"20 May 2026"`). The TypeScript type is `string` — no `DatePipe` transformation or `Date.parse()` needed anywhere in the frontend.

---

#### `app/model/project-hours-row.model.ts`

**What it does:** Projection interface mirroring `ProjectHoursRow`. Returned by the backend when `groupBy=PROJECT`.

```typescript
export interface ProjectHoursRow { projectName: string; totalHours: number; }
```

---

#### `app/model/project-employee-hours-row.model.ts`

**What it does:** Projection interface mirroring `ProjectEmployeeHoursRow`. Returned when `groupBy=PROJECT_EMPLOYEE`.

---

#### `app/model/employee-project-hours-row.model.ts`

**What it does:** Projection interface mirroring `EmployeeProjectHoursRow`. Returned when `groupBy=EMPLOYEE_PROJECT`.

---

#### `app/service/activity-service.ts`

**What it does:** The single source of truth for all data and state in this feature. Owns the `groupBy` writable signal, the `httpResource` that reacts to it, and the `strategyOptions` constant used by the template's `@for` loop.

**`signal()` — mutable reactive state:**

```typescript
readonly groupBy = signal<GroupStrategy>('NONE');
```

`signal()` is Angular's primitive writable reactive container, introduced in Angular 16 and fully stable in Angular 17+. Reading a signal inside a reactive context (template, `computed`, `effect`) automatically registers a dependency — the consumer re-evaluates when the signal changes. Mutation is explicit and synchronous via `.set()` or `.update()`.

**`httpResource()` — reactive HTTP (Angular 19.2+):**

```typescript
readonly activitiesResource = httpResource<ActivityRow[]>(() => ({
  url: API_BASE,
  params: { groupBy: this.groupBy() },
  headers: { 'X-API-Version': '2.0' },
}));
```

The arrow function passed to `httpResource` is a **reactive computation**. Every time any signal it reads (here: `groupBy()`) changes, Angular re-evaluates the function, cancels any in-flight request, and issues a fresh one. The resource exposes its state as signals:

| Signal | Meaning |
|---|---|
| `isLoading()` | `true` while the request is pending |
| `hasValue()` | `true` when a valid response is cached |
| `value()` | the deserialized response body (throws if called in error state) |
| `error()` | the error object when the request failed |
| `status()` | full `ResourceStatus` enum: `idle`, `loading`, `resolved`, `error`, `reloading` |

`reload()` forces a re-fetch without changing params — used by the Refresh button.

**Why the `X-API-Version: 2.0` header lives in the service:** The component should not know about transport-level concerns (headers, URLs, versioning). The service owns the HTTP contract; the component receives typed signal data.

**`ActivityRow` union type:**

```typescript
export type ActivityRow =
  | ActivityRegistry
  | ProjectHoursRow
  | ProjectEmployeeHoursRow
  | EmployeeProjectHoursRow;
```

`httpResource` is typed with this union because different `groupBy` values produce structurally different response bodies. The component narrows to the concrete sub-type via `computed` signals after reading `groupBy()`.

---

#### `app/activity-list/activity-list.ts`

**What it does:** Standalone component class. Consumes the service and exposes computed projections and event handlers to the template. Contains zero HTTP logic.

**`linkedSignal()` — the dropdown synchronization (Angular 19+):**

```typescript
protected readonly selectedStrategy = linkedSignal<GroupStrategy>(
  () => this.svc.groupBy()
);
```

`linkedSignal` creates a **writable signal whose value is initialized and reset by a reactive computation**. This is the correct tool when local state must:

1. Start with a value derived from an external signal (the service's `groupBy`).
2. Be writable locally (the `<select>` element needs to call `.set()` on change).
3. Reset automatically if the external signal changes independently (e.g. from a future router integration).

A plain `computed()` cannot be written to. An independent `signal()` would diverge from the service state. `linkedSignal` solves this triangle — it is the idiomatic Angular 19+ solution for "editable local copy of external state."

**`computed()` — derived read-only reactive values (Angular 16+):**

`computed()` creates a lazy, cached, read-only signal derived from other signals. It only re-evaluates when one of its dependencies changes, and the result is memoized until the next invalidation.

In this component, `computed` serves two purposes:

*Purpose 1 — `this`-binding safety for resource accessors:*

```typescript
// ❌ Incorrect — strips `this` from the resource instance
protected readonly hasValue = this.svc.activitiesResource.hasValue;

// ✅ Correct — this is captured in the closure; result is a reactive Signal
protected readonly hasValue = computed(() => this.svc.activitiesResource.hasValue());
```

`hasValue()` is a plain method on the resource object — not a signal itself. Assigning it directly as a class property detaches it from its instance, causing `TypeError: this.isValueDefined is not a function` at runtime (strict mode, `this` is `undefined`). Wrapping in `computed` keeps `this` bound via the closure and also makes the result **trackable by Angular's reactive graph**, so templates re-render automatically on state transitions.

*Purpose 2 — type-narrowed data projections:*

```typescript
protected readonly rawActivities = computed<ActivityRegistry[]>(() =>
  this.hasValue() && this.svc.groupBy() === 'NONE'
    ? (this.svc.activitiesResource.value() as ActivityRegistry[])
    : []
);
```

Each projection reads both `hasValue()` and `groupBy()`, so it re-evaluates reactively whenever either changes. No manual wiring, no `combineLatest`, no subscription.

**`reload()` — manual refresh:**

```typescript
protected reload(): void {
  this.svc.activitiesResource.reload();
}
```

`reload()` is a method on `HttpResourceRef`. It forces a fresh HTTP request even if the params have not changed. The resource transitions to `isLoading() === true` during the refetch — the template uses this to display the spinner and disable the Refresh button simultaneously.

**Why no HTTP Interceptors:**

`HttpInterceptorFn` (the Angular 15+ functional interceptor API, registered via `provideHttpClient(withInterceptors([...]))`) is the correct tool for **cross-cutting** HTTP concerns: authentication tokens, global retry logic, telemetry, centralised error toasts.

In this project, interceptors were deliberately not used because:

- The `X-API-Version: 2.0` header is specific to one endpoint family — applying it globally via an interceptor would require conditional URL-matching logic (fragile, hard to maintain).
- There is no authentication layer at this stage.
- Error handling is intentionally local: the `error()` signal surfaces it directly in the template with a contextual Retry button.

If authentication is introduced in future, a functional interceptor added to `withInterceptors([authInterceptor])` in `app.config.ts` is the correct extension point — no changes to this component or service would be required.

---

#### `app/activity-list/activity-list.html`

**What it does:** The declarative template. Uses Angular 17+'s built-in block control flow exclusively.

**`@if` — conditional rendering:**

```html
@if (isLoading()) {
  <div class="al-state--loading">…</div>
}
@if (error()) {
  <div class="al-state--error">{{ error() }}</div>
}
@if (hasValue() && !isLoading()) {
  <!-- data table -->
}
```

Angular's built-in `@if` block requires no imports and no container element. It is compiled directly to optimized DOM instructions — more efficient than the structural directive it supersedes.

**`@switch` / `@case` — multi-branch rendering:**

```html
@switch (svc.groupBy()) {
  @case ('NONE')             { … 4-column table … }
  @case ('PROJECT')          { … 2-column table … }
  @case ('PROJECT_EMPLOYEE') { … 3-column table … }
  @case ('EMPLOYEE_PROJECT') { … 3-column table … }
}
```

Each `@case` renders a structurally different `<table>` — different column headers, different `track` expressions, different badge styles. This is the idiomatic Angular 17+ pattern for conditional structural variation. No container element, no directive imports.

**`@for` / `@empty` — iteration:**

```html
@for (row of rawActivities(); track row.date + row.employee.id) {
  <tr class="al-row">…</tr>
} @empty {
  <tr><td colspan="4" class="al-empty">No activity entries found.</td></tr>
}
```

`track` is mandatory and inlined (previously `trackBy: fn` as a method reference on the component). The `@empty` block handles the zero-item case inline — eliminating a separate conditional block.

---

#### `app/activity-list/activity-list.css`

**What it does:** Scoped component styles using CSS custom properties. No external UI library dependency.

Design direction: **editorial / utilitarian** — dark-slate background (`#0f1117`), amber accent (`#f5a623`), monospace font for numeric columns (`JetBrains Mono`, `Fira Mono`), sticky `<thead>` for long tables, responsive collapse at 600px.

All design tokens are declared on `:host` so they are scoped to the component boundary and cannot leak into or be polluted by the global stylesheet. The `al-spinning` keyframe animation drives both the refresh button icon and the loading spinner ring with a single `@keyframes` declaration.

---

## 🧠 Angular 17+ API Reference

> This section summarises the modern Angular APIs used in this project — all introduced or stabilized from Angular 17 onwards — with their rationale and how they fit together as a cohesive reactive architecture.

---

### `signal()` — Reactive Primitive (Angular 16+, stable 17+)

`signal()` is Angular's foundational reactive container. It holds a value, notifies dependents when that value changes, and integrates natively with the framework's change detection engine — with no `zone.js` involvement.

```typescript
const groupBy = signal<GroupStrategy>('NONE');
groupBy();              // read: 'NONE'
groupBy.set('PROJECT'); // write: triggers reactive update
groupBy.update(v => v); // write via transform function
```

Signals are **synchronous** — reads and writes happen immediately, with no async indirection. Angular's rendering engine subscribes to signals at compile time, so only the specific template expressions that depend on a changed signal are re-evaluated.

---

### `computed()` — Derived Read-Only Signal (Angular 16+, stable 17+)

`computed()` creates a lazy, cached, read-only signal derived from one or more other signals. It re-evaluates only when a dependency changes, and caches the result until the next invalidation.

```typescript
const total = computed(() =>
  rows().reduce((sum, r) => sum + r.totalHours, 0)
);
```

Key properties:
- **Lazy** — not evaluated until first read.
- **Cached** — subsequent reads return the memoized value without re-running the function.
- **Read-only** — cannot be `.set()`. If writable derived state is needed, use `linkedSignal`.
- **Composable** — computeds can depend on other computeds, building reactive graphs of arbitrary depth.

---

### `linkedSignal()` — Writable Synchronized Signal (Angular 19+)

`linkedSignal` extends `computed` with write capability. Its value is initialized and reset by a reactive computation, but can also be overwritten locally via `.set()`.

```typescript
const selectedStrategy = linkedSignal<GroupStrategy>(
  () => svc.groupBy()   // source: resets to this when svc.groupBy() changes
);
selectedStrategy.set('PROJECT'); // local override
```

This is the correct primitive for UI controls that must reflect external state while also being independently editable — dropdowns, toggles, paginator controls, form fields pre-populated from a store.

| | `signal` | `computed` | `linkedSignal` |
|---|---|---|---|
| Writable | ✅ | ❌ | ✅ |
| Derived from other signals | ❌ | ✅ | ✅ |
| Resets when source changes | N/A | N/A | ✅ |

---

### `httpResource()` — Reactive HTTP Primitive (Angular 19.2+)

`httpResource` is a reactive wrapper around `HttpClient` that manages the full lifecycle of an HTTP request — firing, cancellation, state tracking — as a set of signals.

```typescript
readonly resource = httpResource<T[]>(() => ({
  url: '/api/endpoint',
  params: { key: this.param() },   // reactive: re-fires when param() changes
  headers: { 'X-API-Version': '2.0' },
}));
```

The function argument is a **reactive computation**. Angular evaluates it inside a reactive context, tracking every signal read. When any tracked signal changes, the computation re-runs, the previous in-flight request is cancelled, and a new request is issued. This eliminates the entire category of race conditions that arise from manually managing concurrent subscriptions.

**State signals exposed:**

```typescript
resource.isLoading()  // boolean
resource.hasValue()   // boolean — safe guard before reading value()
resource.value()      // T[] — throws if called in error state
resource.error()      // unknown | undefined
resource.status()     // ResourceStatus enum
resource.reload()     // method: force re-fetch without param change
```

---

### Built-in Control Flow (Angular 17+)

Angular 17 introduced block-level template syntax for branching and iteration, compiled directly to optimized DOM operations.

| Construct | Syntax | Key feature |
|---|---|---|
| Conditional | `@if (expr) { } @else { }` | No imports, no container element |
| Switch | `@switch (expr) { @case (v) { } }` | Full structural variation per branch |
| Iteration | `@for (x of list(); track x.id) { }` | Mandatory `track`, inline `@empty` block |

The `track` expression replaces `trackBy` — it is inlined directly in the `@for` declaration rather than referencing a component method, making the tracking strategy immediately visible in the template.

---

### Zoneless Change Detection (Angular 18+, default in Angular 21)

Angular 21 removes the dependency on `zone.js` entirely as the default configuration. Change detection is driven exclusively by the signal graph: when a signal changes value, Angular schedules a re-render only for the components and computed values that directly depend on it.

Practical implications for this project:

- No `NgZone.run()` calls needed — signal mutations are automatically detected.
- No `ChangeDetectorRef.markForCheck()` — signals handle invalidation.
- `provideBrowserGlobalErrorListeners()` in `app.config.ts` replaces zone's unhandled-error capture in the browser environment.

---

### Why No HTTP Interceptors

`HttpInterceptorFn` (registered via `provideHttpClient(withInterceptors([...]))`) is the correct pattern for **application-wide** HTTP concerns: Authorization headers, retry policies, request telemetry, global error toasts.

In this project, the `X-API-Version: 2.0` header is scoped to one service's `httpResource` because it belongs to the service's HTTP contract — not to the application's cross-cutting infrastructure. A global interceptor would require URL-based conditional logic to avoid attaching the header to unrelated future endpoints. As the application grows, the correct approach is:

- Cross-cutting (auth, logging): interceptor registered in `app.config.ts`.
- Endpoint-specific (versioning, content-type negotiation): header on the `httpResource` request object in the responsible service.

---

## ✅ Validation Checklist

| Action | Expected result |
|---|---|
| Load page (default `NONE`) | Table with 4 columns: Project, Employee, Date, Hours |
| Select `PROJECT` | 2-column table sorted by total hours DESC |
| Select `PROJECT_EMPLOYEE` | 3-column table: Project → Employee → Total Hours |
| Select `EMPLOYEE_PROJECT` | 3-column table: Employee → Project → Total Hours |
| Click Refresh | Spinner shows, button disabled, data reloads |
| Kill backend → select any strategy | Error banner with Retry button appears |
| DevTools → Network | All requests carry `X-API-Version: 2.0` |
| DevTools → Network → OPTIONS | Preflight 200, `Access-Control-Allow-Origin: http://localhost:4200` |

---

## 📁 Final File Map

```
backend/
└── com/backend/config/
    └── GlobalCorsConfig.java                    ← Servlet-level CorsFilter (HIGHEST_PRECEDENCE)

frontend/src/
└── app/
    ├── model/
    │   ├── group-strategy.model.ts              ← String union type + options array
    │   ├── employee.model.ts                    ← Employee interface
    │   ├── project.model.ts                     ← Project interface
    │   ├── activity-registry.model.ts           ← Aggregate entry (nested Employee + Project)
    │   ├── project-hours-row.model.ts           ← Projection: groupBy=PROJECT
    │   ├── project-employee-hours-row.model.ts  ← Projection: groupBy=PROJECT_EMPLOYEE
    │   └── employee-project-hours-row.model.ts  ← Projection: groupBy=EMPLOYEE_PROJECT
    ├── service/
    │   └── activity-service.ts                  ← httpResource + groupBy signal (source of truth)
    ├── activity-list/
    │   ├── activity-list.ts                     ← Component: linkedSignal, computed, reload()
    │   ├── activity-list.html                   ← Template: @if @for @switch control flow
    │   └── activity-list.css                    ← Scoped styles: CSS vars, dark-slate, amber accent
    └── app.config.ts                            ← provideHttpClient(), provideBrowserGlobalErrorListeners()
```

---

*Session 4 finalized. Angular 21 signal architecture fully operational. CORS preflight resolved at servlet-filter level. All endpoints verified across all four `GroupStrategy` dimensions.*
