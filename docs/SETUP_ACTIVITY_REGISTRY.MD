# `SETUP_ACTIVITY_REGISTRY.MD`

## 📅 Session 2: Wednesday, May 20, 2026

**Objective:** Domain Modeling, In-Memory DB Setup, Service Layer Wiring, Automation Script, and Integration Test validation.

| Timestamp | Category | Action | Terminal Command / Notes |
| --- | --- | --- | --- |
| **12:22** | **Design** | **Domain Entities Modeling** | Designed immutable records `Project.java`, `Employee.java`, and `ActivityRegistry.java`. |
| **12:40** | **Development** | **JPA Repository Strategy** | Created interface `ActivityRegistryRepository.java` to expose standard `findAll()` contract. |
| **12:55** | **Development** | **In-Memory Concretization** | Implemented `ActivityRegistryRepositoryInMemoryDBImpl.java` to seed evaluation dataset. |
| **13:10** | **Development** | **Business Logic Wiring** | Developed `ActivityRegistryService.java` using Lombok constructor injection. |
| **13:18** | **Development** | **Web Controller Engineering** | Programmed `ActivityRegistryController.java` with routing under `/api/activities`. |
| **13:21** | **Testing** | **Web Integration Validation** | Created `ActivityRegistryControllerIntegrationTest.java` verifying array payload length. |
| **13:23** | **Tooling** | **Bash Terminal Automation** | Wrote `ops/test_activities.sh` with automatic server-down safety check. |
| **13:30** | **Docs** | **Documentation Initialization** | Initialized system log records, logging class implementations line-by-line. |

---

## 🛠️ Essential Setup: Repository Workspace Target

```bash
cd ~/Projects/multi-field-time-aggregator/ops
./test_activities.sh

```

---

## 💻 Technical Implementation & Method Documentation

### 1. `Project.java`

Defines data holder for project entity using primitive fields: `Long id` and `String name`.

### 2. `Employee.java`

Models staff resource storing data attributes: `Long id` and `String name`.

### 3. `ActivityRegistry.java`

Encapsulates composite dataset holding reference pointers to `Project`, `Employee`, `ZonedDateTime date`, and `Integer hours`.

### 4. `ActivityRegistryRepository.java`

Declares abstract enterprise data access contract method signature: `List<ActivityRegistry> findAll()`.

### 5. `ActivityRegistryRepositoryInMemoryDBImpl.java`

Annotated with `@Component`. Initializes private tracking array (`List.of()`) inside default constructor to seed database table primary keys and structural relations.

### 6. `ActivityRegistryService.java`

Annotated with `@Service`. Uses Lombok `@RequiredArgsConstructor` to safely wire `private final ActivityRegistryRepositoryInMemoryDBImpl repository` without field reflection.

### 7. `ActivityRegistryController.java`

Annotated with `@RestController`. Maps traffic under `/api/activities` validating explicit enterprise parameters (`version = "1.0"`). Returns standard `ResponseEntity` envelope.

### 8. `ActivityRegistryControllerIntegrationTest.java`

Boots runtime context container (`RANDOM_PORT`) under `"test"` profile. Dispatches `GET` request injecting mandatory header verification sequence (`X-API-Version`, `"1.0"`) and asserts json path size matches target database.

### 9. Automation Bash Script: `ops/test_activities.sh`

POSIX shell tool executing automated checks. Pings port 8080. If missing, prints warning and breaks cleanly. If active, fetches metadata and pipes json body directly into Python tool.

---

## ✅ Core Concepts (Don't Forget!)

> [!IMPORTANT]
> **Enterprise In-Memory DB Mock Naming Convention**
> Explicit class naming `ActivityRegistryRepositoryInMemoryDBImpl` adheres to clean design patterns: interface query definition paired with open execution logging.

> [!NOTE]
> **Resilient Shell Exception Management**
> Pre-flight connection probe validation intercepts communication defects early, preventing standard script runtime crashes when local server layer is down.

---

```bash
# Check line inside test_activities.sh
if ! curl -s --connect-timeout 2 "$URL" &>/dev/null; then echo "Please start the server"; exit 1; fi

```

```java
// Target signature matching database setup
public record ActivityRegistry(Project project, Employee employee, ZonedDateTime date, Integer hours) {}

```

---

*Note: Session finalized. Core domain architecture decoupled via structured service adapters. Terminal validation tools execute cleanly on port 8080. Workspace primed for phase two aggregation logic development.*
