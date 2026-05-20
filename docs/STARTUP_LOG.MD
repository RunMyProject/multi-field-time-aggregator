# `STARTUP_LOG.MD`

## 📅 Session 1: Wednesday, May 20, 2026

**Objective:** Project Initialization, Authentication Security Setup, Core Architecture and Integration Testing Verification.

| Timestamp | Category | Action | Terminal Command / Notes |
| --- | --- | --- | --- |
| **10:00** | **Setup** | **Git Classic Token Configuration** | Created a GitHub Personal Access Token (Classic) with full `repo` scopes. Configured the global credential helper to securely store credentials on the OMEN PC. Switched the remote URL to HTTPS to ensure persistent authentication. |
| **10:30** | **Brainstorming** | **Project Architecture** | Defined the directory structure and foundational layout. Established the core layers for the Multi-Field Time Aggregator, ensuring separation of concerns between controllers, models, and integration tests. |
| **10:50** | **Development** | **Maven & POM Layout** | Built the definitive `pom.xml` configuration. Aligned core dependencies for Spring Boot, Lombok, and Web MVC, establishing a baseline compiled for modern Java enterprise standards. |
| **11:10** | **Development** | **First Controller & App Boot** | Engineered `BackendApplication.java` and `HelloController.java`. Implemented a robust `/api/hello` endpoint with custom header checking (`X-API-Version`) returning a standard text payload. |
| **11:25** | **Tooling** | **Bash Automation Script** | Programmed `ops/test_hello.sh` to execute rapid, lightweight terminal-based curl validations against the local server layer. |
| **11:40** | **Testing** | **Integration Test Architecture** | Engineered `HelloControllerIntegrationTest.java`. Fully eliminated legacy Field Injection (`@Autowired`) by implementing Spring 7 constructor injection mechanics via Lombok's `@RequiredArgsConstructor` and `@TestConstructor`. |
| **11:50** | **Verification** | **Test Execution Green Light** | Resolved `NoSuchBeanDefinitionException` by adding `@AutoConfigureRestTestClient`. Successfully executed the test suite inside VS Code. All assertions passed (`200 OK` and `400 Bad Request`). |
| **11:55** | **Docs** | **Documentation Updated** | Formalized the `STARTUP_LOG.MD` architecture record. Categorized and ordered setups, testing layers, and reference blocks for full team standard compliance. |

---

## 🛠️ Essential Setup: Git Authentication & Remotes

To bypass continuous password prompts and comply with modern security policies, the project has been migrated to standard token-based HTTPS authentication.

1. Enable the global credential helper to persist authentication tokens on disk:
```bash
git config --global credential.helper store

```


2. Reset the remote repository URL to the secure HTTPS path:
```bash
git remote set-url origin https://github.com/RunMyProject/multi-field-time-aggregator.git

```


3. Execute the initial upstream deployment (paste the `ghp_` Classic Token when prompted for a password):
```bash
git push -u origin main

```



---

## 💻 Technical Implementation & Method Documentation

### 1. `pom.xml` (Project Object Model)

The build file serves as the definitive anchor for the application framework lifecycle. It specifies proper compiler target baselines and pulls in required modules for enterprise Web MVC routing, auto-configuration testing, and compile-time boilerplate reduction.

### 2. Integration Testing Suite

Testing controller layer routing behaviors against a live running server container environment requires an immutable architecture setup.

* **`shouldReturn200AndHelloWorld()`**:
* **The Problem:** Hardcoding API versions or relying on insecure HTTP communication lines can lead to deployment vulnerabilities. Testing these layers without starting a real port often misses environment-specific network defects.
* **The Solution:** The method triggers a dynamic call via `RestTestClient` enforcing custom header injection. It strictly verifies that the endpoint safely returns the raw contract text format.



---

## ✅ Core Concepts (Don't Forget!)

> [!IMPORTANT]
> **Constructor Injection via TestConstructor**
> Legacy field injection using `@Autowired private RestTestClient...` is considered a major anti-pattern. By utilizing `@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)` coupled with Lombok's `@RequiredArgsConstructor`, Spring 7 is instructed to inject all `private final` components directly into the test constructor, guaranteeing immutability and complete isolation.

> [!NOTE]
> **Random Port Isolation**
> Using `web3Environment = SpringBootTest.WebEnvironment.RANDOM_PORT` ensures that the integration test suite boots up on a completely dynamic, non-blocked local port. This prevents port collision issues on the host machine when executing simultaneous parallel builds inside local terminal environments or remote CI/CD pipelines.

---

```bash
# Execute local bash script tool to verify live server endpoint behavior
./ops/test_hello.sh

```

```java
    @Test
    public void shouldReturn200AndHelloWorld() {
        // Tests successful response when the correct API version header is provided
        restTestClient.get()
                .uri("/api/hello")
                .header("X-API-Version", "1.0")
                .accept(MediaType.TEXT_PLAIN)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Hello World!");
    }

```

---

*Note: Initializing this foundation required careful verification of the testing dependencies and the authentication protocol parameters. Resolving the explicit injection requirement for the RestTestClient bean ensures a clean framework baseline. The project architecture is completely verified, under source control, and perfectly primed to begin domain model development.*
