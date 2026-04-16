# Step 2 — Java EE to Spring Boot Dependency & API Mapping

> **Source:** `medflow-distributor/pom.xml` (Java EE 7 WAR on WildFly 26)
> **Target:** Spring Boot 3.x (JAR, embedded Tomcat, Java 17+)
> **Generated from:** Step 01 analysis (`step-01-analysis.md`)

---

## 1. Maven Coordinates — Old → New

### 1.1 Parent / BOM

| # | Legacy (Java EE 7) | Spring Boot 3.x Equivalent | Notes |
|---|---|---|---|
| 1 | `org.jboss.spec:jboss-javaee-7.0:1.1.1.Final` (BOM, `<dependencyManagement>`) | `org.springframework.boot:spring-boot-starter-parent:3.4.x` (`<parent>`) | Replaces the JBoss Java EE BOM. Spring Boot parent manages all transitive dependency versions. |

### 1.2 Dependencies

| # | Legacy GAV | Scope | Spring Boot GAV | Scope | Notes |
|---|---|---|---|---|---|
| 2 | `javax:javaee-api:7.0` | provided | **Remove entirely** — replaced by individual starters below | — | The umbrella Java EE API jar is replaced by targeted Spring Boot starters. |
| 3 | `org.postgresql:postgresql:42.5.4` | provided | `org.postgresql:postgresql` | **runtime** | Version managed by Spring Boot parent. No longer provided by an app server; bundled in the fat JAR. |
| 4 | `org.slf4j:slf4j-api:1.7.36` | provided | **Remove** — included transitively by `spring-boot-starter-logging` | — | Spring Boot ships SLF4J + Logback by default. |
| 5 | *(implicit via javaee-api)* EJB 3.2 | provided | `org.springframework.boot:spring-boot-starter` | compile | Core Spring DI, component scanning, `@Service`, `@Component`, `@Transactional`. |
| 6 | *(implicit via javaee-api)* JPA 2.1 | provided | `org.springframework.boot:spring-boot-starter-data-jpa` | compile | Spring Data JPA + Hibernate 6. Replaces `persistence.xml`, `EntityManager` usage, named queries. |
| 7 | *(implicit via javaee-api)* JAX-RS 2.0 | provided | `org.springframework.boot:spring-boot-starter-web` | compile | Spring MVC, embedded Tomcat, `@RestController`. Replaces `JaxRsApplication`, `@Path`, `@GET`/`@POST`, etc. |
| 8 | *(implicit via javaee-api)* JSF 2.2 | provided | `org.springframework.boot:spring-boot-starter-thymeleaf` | compile | Thymeleaf server-side templates replace Facelets/XHTML views. |
| 9 | *(implicit via javaee-api)* JMS 2.0 | provided | `org.springframework.boot:spring-boot-starter-activemq` | compile | Spring JMS + ActiveMQ client. `JmsTemplate` replaces manual `ConnectionFactory`/`Session` management. |
| 10 | *(implicit via javaee-api)* Bean Validation 1.1 | provided | `org.springframework.boot:spring-boot-starter-validation` | compile | Hibernate Validator. `jakarta.validation` annotations (`@NotNull`, `@Size`, etc.). |
| 11 | *(implicit via javaee-api)* CDI 1.1 | provided | *(included in spring-boot-starter)* | — | Spring DI replaces `@Inject`, `@Named`, `beans.xml`. No separate dependency needed. |
| 12 | *(implicit via javaee-api)* Servlet 3.1 | provided | *(included in spring-boot-starter-web)* — embedded Tomcat | — | No `web.xml` needed. Spring Boot auto-configures the embedded container. |
| 13 | — | — | `org.springframework.boot:spring-boot-starter-security` | compile | **New.** Replaces `web.xml` BASIC auth and `MedFlowRealm` with Spring Security `SecurityFilterChain`. |
| 14 | — | — | `org.springframework.boot:spring-boot-starter-test` | test | **New.** JUnit 5 + Mockito + Spring test utilities. |
| 15 | — | — | `org.springframework.boot:spring-boot-devtools` | runtime (optional) | **New.** Hot-reload during development. |

### 1.3 Plugins

| # | Legacy Plugin | Spring Boot Equivalent | Notes |
|---|---|---|---|
| 16 | `maven-compiler-plugin:3.11.0` (source/target 11) | `maven-compiler-plugin` (source/target 17) | Spring Boot 3.x requires Java 17+. Version managed by parent. |
| 17 | `maven-war-plugin:3.3.2` | **Remove** | Spring Boot produces a fat JAR, not a WAR. |
| 18 | `wildfly-maven-plugin:2.1.0.Final` | `spring-boot-maven-plugin` | Replaces WildFly deployment with executable JAR packaging (`mvn package` → `java -jar`). |

### 1.4 Packaging

| Aspect | Legacy | Spring Boot |
|---|---|---|
| Packaging type | `<packaging>war</packaging>` | `<packaging>jar</packaging>` |
| Output | `medflow-distributor.war` deployed to WildFly | `medflow-distributor-1.0.0-SNAPSHOT.jar` (self-contained) |
| Server | External WildFly 26 application server | Embedded Tomcat (included in starter-web) |
| Java version | 11 | 17+ |

---

## 2. API & Annotation Mapping — Old → New

### 2.1 EJB → Spring Service Layer (5 beans)

| # | Java EE Annotation / Pattern | Spring Boot Equivalent | Affected Classes |
|---|---|---|---|
| 1 | `@Stateless` | `@Service` + `@Transactional` | `OrderProcessingBean`, `ProductCatalogBean`, `InventoryBean`, `PurchaseOrderBean` |
| 2 | `@Singleton @Startup` | `@Component` + `@PostConstruct` (or `@Cacheable` with Spring Cache) | `CacheManagerBean` |
| 3 | `@EJB` (injection) | `@Autowired` or constructor injection | All EJB cross-references |
| 4 | `@PersistenceContext EntityManager` | `@Autowired JpaRepository<Entity, Long>` (Spring Data) | All 5 EJBs |
| 5 | Container-Managed Transactions (CMT via `ejb-jar.xml`) | `@Transactional` on class or method | `OrderProcessingBean`, `ProductCatalogBean` (explicit in ejb-jar.xml); others via annotation defaults |
| 6 | `@MessageDriven` (MDB) | `@Component` + `@JmsListener(destination = "...")` | `OrderMessageConsumer` |
| 7 | `@Stateless` (JMS producer) | `@Component` + `JmsTemplate` injection | `OrderMessageProducer` |
| 8 | `ejb-jar.xml` deployment descriptor | **Delete** — no equivalent needed | Config file removed entirely |

### 2.2 JPA → Spring Data JPA (8 entities)

| # | Java EE Pattern | Spring Boot Equivalent | Notes |
|---|---|---|---|
| 1 | `javax.persistence.*` annotations | `jakarta.persistence.*` annotations | Spring Boot 3.x uses Jakarta namespace. All entity annotations (`@Entity`, `@Table`, `@Column`, `@ManyToOne`, `@OneToMany`, `@NamedQuery`, etc.) move from `javax.persistence` → `jakarta.persistence`. |
| 2 | `persistence.xml` (JTA datasource, Hibernate properties) | `application.properties` / `application.yml` | See §4 Configuration Mapping below. |
| 3 | JNDI DataSource `java:jboss/datasources/MedFlowDS` | Spring Boot auto-configured `DataSource` via `spring.datasource.*` properties | No JNDI lookup needed. |
| 4 | `@NamedQuery` + `em.createNamedQuery()` | Spring Data JPA `JpaRepository` derived query methods or `@Query` | Example: `Product.findBySku` → `Optional<Product> findBySku(String sku)` |
| 5 | `em.persist(entity)` | `repository.save(entity)` | Spring Data `save()` handles both insert and update. |
| 6 | `em.find(Entity.class, id)` | `repository.findById(id)` | Returns `Optional<Entity>`. |
| 7 | `em.merge(entity)` | `repository.save(entity)` | Same method for both insert and update in Spring Data. |
| 8 | `em.remove(entity)` | `repository.deleteById(id)` or `repository.delete(entity)` | — |
| 9 | Inline JPQL `em.createQuery(...)` | `@Query("SELECT p FROM Product p WHERE ...")` on repository method | Used in `ProductCatalogBean.searchByName()`. |
| 10 | `GenerationType.IDENTITY` | `GenerationType.IDENTITY` | No change — same strategy works with PostgreSQL. |
| 11 | `Serializable` on entities | Optional (still valid, but not required for Spring Data) | Can keep for compatibility. |

**New Repository Interfaces Required (one per entity):**

| Entity | New Spring Data Repository |
|---|---|
| `Product` | `ProductRepository extends JpaRepository<Product, Long>` |
| `Customer` | `CustomerRepository extends JpaRepository<Customer, Long>` |
| `Order` | `OrderRepository extends JpaRepository<Order, Long>` |
| `OrderItem` | `OrderItemRepository extends JpaRepository<OrderItem, Long>` |
| `Inventory` | `InventoryRepository extends JpaRepository<Inventory, Long>` |
| `Supplier` | `SupplierRepository extends JpaRepository<Supplier, Long>` |
| `PurchaseOrder` | `PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long>` |
| `LotTracking` | `LotTrackingRepository extends JpaRepository<LotTracking, Long>` |

### 2.3 JAX-RS → Spring MVC REST (3 resource classes + 1 Application class)

| # | JAX-RS Annotation | Spring MVC Equivalent | Notes |
|---|---|---|---|
| 1 | `javax.ws.rs.core.Application` (extends) + `@ApplicationPath("/api")` | **Delete** — not needed | Spring Boot serves from context root; use `@RequestMapping("/api")` on controllers or set `server.servlet.context-path=/api`. |
| 2 | `@Path("/products")` | `@RequestMapping("/api/products")` on `@RestController` | Or use `@RestController` + `@RequestMapping`. |
| 3 | `@GET` | `@GetMapping` | — |
| 4 | `@POST` | `@PostMapping` | — |
| 5 | `@PUT` | `@PutMapping` | — |
| 6 | `@DELETE` | `@DeleteMapping` | — |
| 7 | `@Path("{id}")` | `@GetMapping("/{id}")` (path in mapping annotation) | — |
| 8 | `@PathParam("id")` | `@PathVariable("id")` | — |
| 9 | `@QueryParam("name")` | `@RequestParam("name")` | — |
| 10 | `@Produces(MediaType.APPLICATION_JSON)` | `produces = MediaType.APPLICATION_JSON_VALUE` (or default — Spring defaults to JSON with Jackson) | Spring auto-configures Jackson; usually no explicit annotation needed. |
| 11 | `@Consumes(MediaType.APPLICATION_JSON)` | `@RequestBody` on method parameter | Spring handles JSON deserialization via Jackson automatically. |
| 12 | `javax.ws.rs.core.Response` | `org.springframework.http.ResponseEntity<T>` | Provides status codes and headers. |
| 13 | `Response.ok(entity).build()` | `ResponseEntity.ok(entity)` | — |
| 14 | `Response.status(404).build()` | `ResponseEntity.notFound().build()` | — |
| 15 | `Response.status(201).entity(obj).build()` | `ResponseEntity.status(HttpStatus.CREATED).body(obj)` | — |

**Controller Mapping (resource → controller):**

| Legacy Resource | New Spring Controller |
|---|---|
| `rest/JaxRsApplication.java` | **Delete** (not needed) |
| `rest/ProductResource.java` | `controller/ProductController.java` (`@RestController`) |
| `rest/OrderResource.java` | `controller/OrderController.java` (`@RestController`) |
| `rest/InventoryResource.java` | `controller/InventoryController.java` (`@RestController`) |

### 2.4 JSF → Thymeleaf + Spring MVC (3 managed beans → controllers, 6 XHTML → HTML)

| # | JSF Pattern | Spring Boot Equivalent | Notes |
|---|---|---|---|
| 1 | `@Named` + `@ViewScoped` | `@Controller` + `@GetMapping` / `@PostMapping` | Each JSF bean becomes a Spring MVC controller returning Thymeleaf view names. |
| 2 | `@Named` + `@ConversationScoped` (`Conversation` API) | `@Controller` + `@SessionAttributes` or wizard state in `HttpSession` | `OrderWizardBean` multi-step wizard. |
| 3 | `#{beanName.property}` (JSF EL) | `${variable}` (Thymeleaf expression) | Thymeleaf uses `${...}` syntax with model attributes. |
| 4 | `<h:form>` | `<form th:action="@{/path}" method="post">` | Standard HTML with Thymeleaf attributes. |
| 5 | `<h:inputText value="#{bean.field}">` | `<input type="text" th:field="*{field}">` | Thymeleaf form binding. |
| 6 | `<h:commandButton action="#{bean.method}">` | `<button type="submit">` (form posts to controller) | Spring MVC handles form submission via `@PostMapping`. |
| 7 | `<h:dataTable value="#{bean.list}" var="item">` | `<tr th:each="item : ${list}">` | Thymeleaf iteration. |
| 8 | `<ui:composition template="/templates/layout.xhtml">` | Thymeleaf layout dialect (`layout:decorate`) or fragment includes | `<div layout:decorate="~{layout}">`. |
| 9 | `<ui:insert name="content"/>` / `<ui:define name="content">` | `layout:fragment="content"` | Thymeleaf layout fragments. |
| 10 | `faces-config.xml` navigation rules | Spring MVC controller return values (`"redirect:/path"`, view names) | **Delete** `faces-config.xml`. |
| 11 | `FacesServlet` mapping (`*.xhtml`) | **Delete** — Thymeleaf auto-configured by Spring Boot | Templates in `src/main/resources/templates/`. |

**View File Mapping:**

| Legacy XHTML (webapp/) | New Thymeleaf Template (resources/templates/) |
|---|---|
| `index.xhtml` | `index.html` |
| `templates/layout.xhtml` | `layout.html` (Thymeleaf layout) |
| `pages/dashboard.xhtml` | `pages/dashboard.html` |
| `pages/productSearch.xhtml` | `pages/productSearch.html` |
| `pages/orderWizard.xhtml` | `pages/orderWizard.html` |
| `pages/inventory.xhtml` | `pages/inventory.html` |
| `resources/css/styles.css` | `static/css/styles.css` |

**Controller Mapping (JSF bean → Spring MVC controller):**

| Legacy JSF Bean | New Spring Controller |
|---|---|
| `jsf/DashboardBean.java` | `controller/DashboardController.java` (`@Controller`) |
| `jsf/ProductSearchBean.java` | `controller/ProductSearchController.java` (`@Controller`) |
| `jsf/OrderWizardBean.java` | `controller/OrderWizardController.java` (`@Controller`) |

### 2.5 JMS → Spring JMS (2 classes)

| # | Java EE JMS Pattern | Spring Boot Equivalent | Affected Class |
|---|---|---|---|
| 1 | `@Resource(lookup="java:/ConnectionFactory") ConnectionFactory` | Auto-configured by `spring-boot-starter-activemq` | `OrderMessageProducer` |
| 2 | `@Resource(lookup="java:/jms/queue/OrderQueue") Queue` | Queue name in `@JmsListener(destination="OrderQueue")` / `jmsTemplate.convertAndSend("OrderQueue", ...)` | Both JMS classes |
| 3 | Manual `Connection` / `Session` / `MessageProducer` management | `@Autowired JmsTemplate` + `jmsTemplate.convertAndSend()` | `OrderMessageProducer` |
| 4 | `ObjectMessage` with properties | `JmsTemplate.convertAndSend()` with `MessagePostProcessor` for headers, or plain Java object serialization | `OrderMessageProducer` |
| 5 | `@MessageDriven(activationConfig={...})` | `@Component` class with `@JmsListener(destination="OrderQueue")` method | `OrderMessageConsumer` |
| 6 | `implements MessageListener` + `onMessage(Message)` | `@JmsListener` method with typed parameter (e.g., `String`, `Map`, or custom DTO) | `OrderMessageConsumer` |
| 7 | `message.getStringProperty("eventType")` | JMS headers via `@Header("eventType")` parameter or `MessageHeaders` | `OrderMessageConsumer` |

### 2.6 CDI → Spring Dependency Injection

| # | CDI Annotation | Spring Equivalent | Notes |
|---|---|---|---|
| 1 | `@Inject` | `@Autowired` (or constructor injection — preferred) | Spring favors constructor injection for mandatory dependencies. |
| 2 | `@Named` | `@Component` / `@Service` / `@Controller` | Spring stereotype annotations. `@Named` value becomes unnecessary with type-based injection. |
| 3 | `@Named("beanName")` | `@Component("beanName")` or `@Qualifier("beanName")` | Only if explicit naming is needed. |
| 4 | `beans.xml` (`bean-discovery-mode="all"`) | **Delete** — Spring component scanning is automatic | `@SpringBootApplication` enables scanning. |
| 5 | `@PostConstruct` | `@PostConstruct` (same — from `jakarta.annotation`) | Works identically in Spring. |
| 6 | `@PreDestroy` | `@PreDestroy` (same — from `jakarta.annotation`) | Works identically in Spring. |
| 7 | `javax.enterprise.context.ConversationScoped` | `@SessionScope` or `@Scope("session")` | For `OrderWizardBean` wizard state. |
| 8 | `javax.faces.view.ViewScoped` | `@RequestScope` or model attributes per request | For `DashboardBean`, `ProductSearchBean`. |

### 2.7 Security

| # | Java EE Pattern | Spring Boot Equivalent | Notes |
|---|---|---|---|
| 1 | `web.xml` `<login-config>` BASIC auth | `SecurityFilterChain` bean with `httpBasic()` | Programmatic configuration via `@Configuration` class. |
| 2 | `web.xml` `<security-constraint>` on `/admin/*` | `.requestMatchers("/admin/**").hasRole("ADMIN")` in `SecurityFilterChain` | — |
| 3 | `web.xml` `<security-role>` declarations | Defined in `UserDetailsService` or `application.properties` | Roles: `ADMIN`, `USER`, `WAREHOUSE`. |
| 4 | `MedFlowRealm` (WildFly security realm) | `InMemoryUserDetailsManager` or database-backed `UserDetailsService` | — |
| 5 | `SecurityConfig.java` (static constants) | Keep as-is or integrate into Spring Security config | Role constants remain useful. |

### 2.8 javax → jakarta Namespace Migration

Spring Boot 3.x requires the Jakarta EE 9+ namespace. **Every** `javax.*` import must change:

| javax (Java EE 7) | jakarta (Spring Boot 3.x) |
|---|---|
| `javax.persistence.*` | `jakarta.persistence.*` |
| `javax.ejb.*` | **Remove** — no EJB in Spring |
| `javax.ws.rs.*` | **Remove** — replaced by Spring MVC annotations |
| `javax.faces.*` | **Remove** — replaced by Thymeleaf |
| `javax.jms.*` | `jakarta.jms.*` (used internally by Spring JMS) |
| `javax.inject.*` | **Remove** — replaced by Spring DI (`@Autowired`, constructor injection) |
| `javax.enterprise.context.*` | **Remove** — replaced by Spring scopes |
| `javax.annotation.Resource` | **Remove** — replaced by `@Autowired` / `@Value` |
| `javax.annotation.PostConstruct` | `jakarta.annotation.PostConstruct` |
| `javax.annotation.PreDestroy` | `jakarta.annotation.PreDestroy` |
| `javax.validation.*` | `jakarta.validation.*` |
| `javax.servlet.*` | `jakarta.servlet.*` (if needed — usually not directly used in Spring Boot) |

---

## 3. Configuration File Mapping

### 3.1 Files to Delete (Java EE XML descriptors)

| File | Reason |
|---|---|
| `src/main/resources/META-INF/persistence.xml` | Replaced by `application.properties` + Spring Data JPA auto-configuration |
| `src/main/resources/META-INF/ejb-jar.xml` | No EJBs in Spring Boot |
| `src/main/webapp/WEB-INF/web.xml` | Replaced by Spring Boot auto-configuration + Spring Security |
| `src/main/webapp/WEB-INF/faces-config.xml` | No JSF in Spring Boot |
| `src/main/webapp/WEB-INF/beans.xml` | Spring component scanning is automatic |
| `standalone.xml` | WildFly config replaced by `application.properties` |
| `Dockerfile` | Rewrite for Spring Boot fat JAR (no WildFly) |
| `docker-compose.yml` | Update — remove WildFly service, add Spring Boot app |

### 3.2 Files to Create

| File | Purpose |
|---|---|
| `src/main/resources/application.properties` | All configuration (datasource, JPA, JMS, server port, logging) |
| `src/main/java/.../MedflowApplication.java` | `@SpringBootApplication` main class |
| `src/main/java/.../config/SecurityConfig.java` | `@Configuration` with `SecurityFilterChain` bean |
| `src/main/java/.../config/JmsConfig.java` | JMS queue/topic configuration (optional) |
| `src/main/java/.../repository/*.java` | 8 Spring Data JPA repository interfaces |

---

## 4. Configuration Property Mapping

### 4.1 persistence.xml → application.properties

| persistence.xml Property | application.properties Equivalent |
|---|---|
| `<jta-data-source>java:jboss/datasources/MedFlowDS</jta-data-source>` | `spring.datasource.url=jdbc:postgresql://localhost:5432/medflowdb` |
| *(from standalone.xml)* PostgreSQL user `medflow` | `spring.datasource.username=medflow` |
| *(from standalone.xml)* PostgreSQL password | `spring.datasource.password=medflow` |
| — | `spring.datasource.driver-class-name=org.postgresql.Driver` |
| `hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect` | `spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect` (auto-detected by Spring Boot) |
| `hibernate.hbm2ddl.auto=create-drop` | `spring.jpa.hibernate.ddl-auto=update` (use `validate` in production) |
| `hibernate.show_sql=true` | `spring.jpa.show-sql=true` |
| `hibernate.format_sql=true` | `spring.jpa.properties.hibernate.format_sql=true` |
| `hibernate.jdbc.batch_size=20` | `spring.jpa.properties.hibernate.jdbc.batch_size=20` |
| `hibernate.jdbc.fetch_size=50` | `spring.jpa.properties.hibernate.jdbc.fetch_size=50` |
| `hibernate.hbm2ddl.import_files=import.sql` | Place `data.sql` in `src/main/resources/` (Spring Boot convention) |

### 4.2 standalone.xml JMS → application.properties

| standalone.xml Config | application.properties Equivalent |
|---|---|
| ActiveMQ Artemis broker on port 61616 | `spring.activemq.broker-url=tcp://localhost:61616` |
| — | `spring.activemq.user=admin` |
| — | `spring.activemq.password=admin` |
| `java:/jms/queue/OrderQueue` | Queue name `OrderQueue` used in `@JmsListener` / `JmsTemplate` |

### 4.3 web.xml → application.properties / Spring Security

| web.xml Config | Spring Boot Equivalent |
|---|---|
| `PROJECT_STAGE=Development` | `logging.level.root=DEBUG` (or Spring profiles) |
| `FacesServlet *.xhtml` | Not needed — Thymeleaf auto-configured |
| Welcome file `index.xhtml` | Controller mapping `/` → `index.html` view |
| BASIC auth `MedFlowRealm` | `SecurityFilterChain` with `.httpBasic()` |
| `/admin/*` security constraint | `.requestMatchers("/admin/**").hasRole("ADMIN")` |

### 4.4 Additional Spring Boot Properties

```properties
# Server
server.port=8080

# Logging
logging.level.com.medflow=DEBUG
logging.level.org.springframework=INFO

# Thymeleaf
spring.thymeleaf.cache=false
spring.thymeleaf.prefix=classpath:/templates/
spring.thymeleaf.suffix=.html

# Jackson (JSON serialization)
spring.jackson.serialization.write-dates-as-timestamps=false
```

---

## 5. Project Structure — Old → New

```
# LEGACY (Java EE WAR)                    # SPRING BOOT (fat JAR)
medflow-distributor/                       medflow-distributor/
├── pom.xml (war, javaee-api)              ├── pom.xml (jar, spring-boot-starter-parent)
├── standalone.xml                         ├── ✗ DELETED
├── Dockerfile (WildFly)                   ├── Dockerfile (java -jar)
├── docker-compose.yml                     ├── docker-compose.yml (updated)
└── src/main/                              └── src/main/
    ├── java/com/medflow/                      ├── java/com/medflow/
    │   ├── entity/ (8 JPA entities)           │   ├── MedflowApplication.java ★ NEW
    │   ├── ejb/ (5 EJBs)                      │   ├── entity/ (8 entities, javax→jakarta)
    │   ├── rest/ (4 JAX-RS)                   │   ├── service/ (replaces ejb/)
    │   ├── jsf/ (3 managed beans)             │   ├── repository/ ★ NEW (8 interfaces)
    │   ├── jms/ (2 JMS classes)               │   ├── controller/ (replaces rest/ + jsf/)
    │   └── security/                          │   ├── jms/ (refactored with JmsTemplate)
    ├── resources/META-INF/                    │   ├── config/ ★ NEW (Security, JMS)
    │   ├── persistence.xml                    │   └── security/
    │   └── ejb-jar.xml                        ├── resources/
    └── webapp/                                │   ├── application.properties ★ NEW
        ├── WEB-INF/                           │   ├── templates/ (Thymeleaf HTML)
        │   ├── web.xml                        │   │   ├── layout.html
        │   ├── faces-config.xml               │   │   ├── index.html
        │   └── beans.xml                      │   │   └── pages/
        ├── templates/layout.xhtml             │   │       ├── dashboard.html
        ├── index.xhtml                        │   │       ├── productSearch.html
        ├── pages/*.xhtml                      │   │       ├── orderWizard.html
        └── resources/css/styles.css           │   │       └── inventory.html
                                               │   └── static/css/styles.css
                                               └── test/java/com/medflow/ ★ NEW
```

---

## 6. Migration Checklist

| # | Task | Status |
|---|---|---|
| 1 | Replace `pom.xml` — new parent, starters, plugins, JAR packaging | ☐ |
| 2 | Create `MedflowApplication.java` (`@SpringBootApplication`) | ☐ |
| 3 | Create `application.properties` (datasource, JPA, JMS, server) | ☐ |
| 4 | Migrate 8 entities: `javax.persistence` → `jakarta.persistence` | ☐ |
| 5 | Create 8 `JpaRepository` interfaces in `repository/` package | ☐ |
| 6 | Migrate 5 EJBs → `@Service` + `@Transactional` in `service/` package | ☐ |
| 7 | Migrate 3 JAX-RS resources → `@RestController` in `controller/` package | ☐ |
| 8 | Migrate 3 JSF beans → `@Controller` in `controller/` package | ☐ |
| 9 | Convert 6 XHTML views → Thymeleaf HTML templates | ☐ |
| 10 | Migrate JMS producer → `JmsTemplate` | ☐ |
| 11 | Migrate MDB consumer → `@JmsListener` | ☐ |
| 12 | Create Spring Security config (replace `web.xml` auth) | ☐ |
| 13 | Delete all Java EE XML descriptors | ☐ |
| 14 | Update Dockerfile for Spring Boot fat JAR | ☐ |
| 15 | Update `docker-compose.yml` (remove WildFly service) | ☐ |
| 16 | Write and run tests | ☐ |
