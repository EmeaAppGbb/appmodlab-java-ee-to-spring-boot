# Step 3 — MedFlow Distributor Modernization Specification

> **Document Type:** Migration Blueprint  
> **Source Platform:** Java EE 7 / WildFly 26.1.3 / WAR  
> **Target Platform:** Spring Boot 3.4.x / Embedded Tomcat / Executable JAR  
> **Java Version:** 11 → 17  
> **Generated from:** Step 01 Analysis + Step 02 Dependency Mapping  

---

## Table of Contents

1. [Migration Scope & Objectives](#1-migration-scope--objectives)
2. [Architecture Decisions](#2-architecture-decisions)
3. [New Project Structure](#3-new-project-structure)
4. [Detailed Migration Plan by Component Layer](#4-detailed-migration-plan-by-component-layer)
5. [Spring Boot Configuration](#5-spring-boot-configuration)
6. [Docker & Container Strategy](#6-docker--container-strategy)
7. [Testing Strategy](#7-testing-strategy)
8. [Risk Assessment](#8-risk-assessment)
9. [Migration Execution Order](#9-migration-execution-order)
10. [Acceptance Criteria](#10-acceptance-criteria)

---

## 1. Migration Scope & Objectives

### 1.1 Scope

Migrate the **MedFlow Distributors** wholesale pharmaceutical distribution system from a Java EE 7 WAR application deployed on WildFly 26 to a Spring Boot 3.4.x executable JAR application with embedded Tomcat.

**In scope:**

| Component | Count | From | To |
|---|---|---|---|
| JPA Entities | 8 | `javax.persistence` | `jakarta.persistence` (Spring Data JPA) |
| EJB Session Beans | 5 | `@Stateless`, `@Singleton` | `@Service`, `@Component` |
| JAX-RS Resources | 3 (+1 Application) | `@Path`, `@GET`/`@POST` | `@RestController`, `@GetMapping`/`@PostMapping` |
| JSF Managed Beans | 3 | `@Named`, `@ViewScoped` | `@Controller` (Spring MVC) |
| XHTML Views | 6 | JSF Facelets | Thymeleaf HTML templates |
| JMS Classes | 2 | `@MessageDriven`, manual JMS 1.1 | `@JmsListener`, `JmsTemplate` |
| Security | 1 + web.xml | BASIC auth via realm | Spring Security `SecurityFilterChain` |
| XML Descriptors | 5 | `persistence.xml`, `ejb-jar.xml`, `web.xml`, `faces-config.xml`, `beans.xml` | `application.properties` |
| Infrastructure | 3 | `Dockerfile` (WildFly), `docker-compose.yml`, `standalone.xml` | Multi-stage `Dockerfile`, updated `docker-compose.yml` |

**Out of scope:**

- Database schema changes (existing PostgreSQL schema is preserved)
- Business logic changes (functional parity is the goal)
- UI redesign (visual fidelity maintained; only the template engine changes)
- Data migration (schema managed by Hibernate DDL)

### 1.2 Objectives

| # | Objective | Success Metric |
|---|---|---|
| O1 | **Eliminate application server dependency** | Application runs as `java -jar` with no external WildFly |
| O2 | **Upgrade to Java 17 and Jakarta EE namespace** | All `javax.*` imports replaced with `jakarta.*` or Spring equivalents |
| O3 | **Spring Boot 3.4.x framework adoption** | All Spring Boot starters compile and auto-configure successfully |
| O4 | **Functional parity** | All 19 REST endpoints return identical responses; all 4 UI pages render correctly |
| O5 | **Simplified configuration** | Single `application.properties` replaces 5 XML descriptors + `standalone.xml` |
| O6 | **Container-ready deployment** | Multi-stage Docker build produces a minimal image |
| O7 | **Testable architecture** | Unit and integration tests cover service and controller layers |
| O8 | **Fix legacy bugs** | Resolve the 16 issues identified in Step 01 analysis |

### 1.3 Non-Goals

- Microservice decomposition (remains monolithic)
- Frontend SPA migration (server-side rendering retained)
- Cloud-native features (service mesh, circuit breakers) — may follow in future phases

---

## 2. Architecture Decisions

### AD-1: Monolithic WAR → Executable JAR

| Aspect | Decision |
|---|---|
| **Packaging** | `<packaging>jar</packaging>` — Spring Boot fat JAR |
| **Server** | Embedded Tomcat (via `spring-boot-starter-web`) |
| **Startup** | `java -jar medflow-springboot-1.0.0-SNAPSHOT.jar` |
| **Rationale** | Eliminates WildFly operational complexity; simplifies CI/CD; container-friendly |

### AD-2: Spring Data JPA Repositories

| Aspect | Decision |
|---|---|
| **Pattern** | One `JpaRepository<Entity, Long>` interface per entity (8 total) |
| **Query Strategy** | Derived query methods for simple queries; `@Query` for complex JPQL |
| **Named Queries** | Replaced by repository method signatures and `@Query` annotations |
| **EntityManager** | Not directly used; Spring Data handles all persistence operations |
| **Rationale** | Eliminates boilerplate CRUD code; compile-time query validation; pagination built-in |

### AD-3: Thymeleaf Server-Side Views

| Aspect | Decision |
|---|---|
| **Template Engine** | Thymeleaf 3.x (via `spring-boot-starter-thymeleaf`) |
| **Layout** | Thymeleaf Layout Dialect (`layout:decorate`, `layout:fragment`) |
| **Location** | `src/main/resources/templates/` |
| **Static Assets** | `src/main/resources/static/css/` |
| **Rationale** | Natural HTML templates; server-side rendering matches JSF paradigm; lowest migration friction |

### AD-4: Spring MVC REST Controllers

| Aspect | Decision |
|---|---|
| **REST API** | `@RestController` classes under `controller/` package |
| **View Controllers** | `@Controller` classes for Thymeleaf page rendering |
| **Base Path** | REST API endpoints prefixed with `/api/` (matching legacy paths) |
| **JSON Serialization** | Jackson (auto-configured by Spring Boot) |
| **Rationale** | Maintains API contract compatibility; clean separation of REST and view concerns |

### AD-5: Spring JMS with ActiveMQ Artemis

| Aspect | Decision |
|---|---|
| **Client** | `spring-boot-starter-activemq` (Classic ActiveMQ client compatible with Artemis via OpenWire) |
| **Producer** | `JmsTemplate.convertAndSend()` with `MessagePostProcessor` for event headers |
| **Consumer** | `@JmsListener(destination = "OrderQueue")` method |
| **Message Format** | `MapMessage` with `eventType`, `orderId`, `timestamp` properties (replacing `ObjectMessage`) |
| **Rationale** | Eliminates manual Connection/Session boilerplate; type-safe message handling |

### AD-6: Spring Security

| Aspect | Decision |
|---|---|
| **Authentication** | HTTP Basic (matching legacy behavior) |
| **User Store** | `InMemoryUserDetailsManager` (matching legacy WildFly realm; upgradeable to DB-backed) |
| **Authorization** | `SecurityFilterChain` with path-based rules |
| **Roles** | `ADMIN`, `USER`, `WAREHOUSE` (from existing `SecurityConfig` constants) |
| **API Security** | REST endpoints (`/api/**`) require authentication (fixing legacy gap) |
| **CSRF** | Disabled for REST API; enabled for form-based pages |
| **Rationale** | Programmatic security config; fixes existing security gaps; easy to extend |

### AD-7: New Project Directory (`medflow-springboot/`)

| Aspect | Decision |
|---|---|
| **Location** | `medflow-springboot/` alongside existing `medflow-distributor/` |
| **Rationale** | Preserves the legacy project for reference and side-by-side comparison during migration; allows incremental validation |

---

## 3. New Project Structure

```
medflow-springboot/
├── pom.xml
├── Dockerfile
├── docker-compose.yml
├── README.md
└── src/
    ├── main/
    │   ├── java/com/medflow/
    │   │   ├── MedflowApplication.java                  # @SpringBootApplication entry point
    │   │   │
    │   │   ├── entity/                                  # 8 JPA entities (javax → jakarta)
    │   │   │   ├── Product.java
    │   │   │   ├── Customer.java
    │   │   │   ├── Order.java
    │   │   │   ├── OrderItem.java
    │   │   │   ├── Inventory.java
    │   │   │   ├── Supplier.java
    │   │   │   ├── PurchaseOrder.java
    │   │   │   └── LotTracking.java
    │   │   │
    │   │   ├── repository/                              # 8 Spring Data JPA repositories ★ NEW
    │   │   │   ├── ProductRepository.java
    │   │   │   ├── CustomerRepository.java
    │   │   │   ├── OrderRepository.java
    │   │   │   ├── OrderItemRepository.java
    │   │   │   ├── InventoryRepository.java
    │   │   │   ├── SupplierRepository.java
    │   │   │   ├── PurchaseOrderRepository.java
    │   │   │   └── LotTrackingRepository.java
    │   │   │
    │   │   ├── service/                                 # 5 services (replaces EJBs)
    │   │   │   ├── OrderProcessingService.java          # ← OrderProcessingBean
    │   │   │   ├── ProductCatalogService.java           # ← ProductCatalogBean
    │   │   │   ├── InventoryService.java                # ← InventoryBean
    │   │   │   ├── PurchaseOrderService.java            # ← PurchaseOrderBean
    │   │   │   └── CacheManagerService.java             # ← CacheManagerBean (or Spring Cache)
    │   │   │
    │   │   ├── controller/                              # REST + MVC controllers
    │   │   │   ├── ProductController.java               # @RestController ← ProductResource
    │   │   │   ├── OrderController.java                 # @RestController ← OrderResource
    │   │   │   ├── InventoryController.java             # @RestController ← InventoryResource
    │   │   │   ├── DashboardController.java             # @Controller ← DashboardBean
    │   │   │   ├── ProductSearchController.java         # @Controller ← ProductSearchBean
    │   │   │   └── OrderWizardController.java           # @Controller ← OrderWizardBean
    │   │   │
    │   │   ├── jms/                                     # JMS producer + consumer
    │   │   │   ├── OrderMessageProducer.java            # JmsTemplate-based
    │   │   │   └── OrderMessageConsumer.java            # @JmsListener-based
    │   │   │
    │   │   ├── config/                                  # Spring configuration ★ NEW
    │   │   │   ├── SecurityConfig.java                  # SecurityFilterChain
    │   │   │   └── JmsConfig.java                       # JMS queue definitions (optional)
    │   │   │
    │   │   └── security/                                # Security constants
    │   │       └── SecurityRoles.java                   # Role constants (renamed for clarity)
    │   │
    │   └── resources/
    │       ├── application.properties                   # All configuration ★ NEW
    │       ├── application-dev.properties               # Dev profile overrides ★ NEW
    │       ├── application-prod.properties              # Production profile ★ NEW
    │       ├── data.sql                                 # Seed data (replaces missing import.sql) ★ NEW
    │       ├── templates/                               # Thymeleaf templates
    │       │   ├── layout.html                          # Master layout
    │       │   ├── index.html                           # Landing page
    │       │   └── pages/
    │       │       ├── dashboard.html
    │       │       ├── productSearch.html
    │       │       ├── orderWizard.html
    │       │       └── inventory.html
    │       └── static/
    │           └── css/
    │               └── styles.css                       # Migrated stylesheet
    │
    └── test/
        └── java/com/medflow/
            ├── MedflowApplicationTests.java             # Context load smoke test
            ├── repository/                              # Repository integration tests
            │   ├── ProductRepositoryTest.java
            │   └── OrderRepositoryTest.java
            ├── service/                                 # Service unit tests
            │   ├── OrderProcessingServiceTest.java
            │   ├── ProductCatalogServiceTest.java
            │   └── InventoryServiceTest.java
            └── controller/                              # Controller tests
                ├── ProductControllerTest.java
                ├── OrderControllerTest.java
                └── InventoryControllerTest.java
```

### File Count Summary

| Category | Legacy Count | Spring Boot Count | Delta |
|---|---|---|---|
| Java source files | 22 | 30 | +8 (repositories + config + main class) |
| View templates | 6 XHTML | 6 HTML | 0 |
| Config files (in-app) | 5 XML | 3 properties | −2 |
| Infra files | 3 | 3 | 0 |
| Test files | 0 | 9 | +9 |
| **Total** | **36** | **51** | **+15** |

---

## 4. Detailed Migration Plan by Component Layer

### 4.1 Entity Layer (8 entities)

**Goal:** Migrate JPA entities from `javax.persistence` to `jakarta.persistence` namespace; retain all field definitions, relationships, named queries, and business logic.

#### Changes per entity:

| Change | Description |
|---|---|
| **Import namespace** | `javax.persistence.*` → `jakarta.persistence.*` |
| **`@NamedQuery` annotations** | Retained on entities; repository methods will use them OR replace with derived queries |
| **`Serializable`** | Retained for compatibility |
| **`GenerationType.IDENTITY`** | No change (PostgreSQL-compatible) |
| **`@Temporal`** | Replace with `java.time` types (`LocalDate`, `LocalDateTime`) and remove `@Temporal` |
| **Business logic** | Retained in entities (`Order.addItem()`, `Order.recalculateTotal()`, `OrderItem.getLineTotal()`) |

#### Entity-by-entity details:

| Entity | Special Handling |
|---|---|
| `Product` | 5 named queries → repository derived methods. `deaSchedule` and `requiresColdChain` fields preserved. |
| `Customer` | 3 named queries → repository derived methods. `licenseNumber` unique constraint preserved. |
| `Order` | 4 named queries → repository derived methods. `@OneToMany(cascade=ALL, orphanRemoval=true)` to `OrderItem` preserved. Business methods `addItem()`, `recalculateTotal()` preserved. **Fix:** Ensure `totalAmount` getter is used consistently (not `total`). |
| `OrderItem` | Business method `getLineTotal()` preserved. |
| `Inventory` | 4 named queries → repository derived methods. `@Temporal(DATE)` → `LocalDate`. |
| `Supplier` | 2 named queries → repository derived methods. |
| `PurchaseOrder` | 3 named queries → repository derived methods. `@Temporal` → `java.time` types. |
| `LotTracking` | 3 named queries → repository derived methods. `@Temporal(DATE)` → `LocalDate`. |

### 4.2 Repository Layer (8 new interfaces) ★ NEW

Each repository extends `JpaRepository<Entity, Long>` and declares derived query methods replacing named queries.

**Example — `ProductRepository`:**

```java
public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findBySku(String sku);
    List<Product> findByCategory(String category);
    List<Product> findByManufacturer(String manufacturer);

    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Product> searchByName(@Param("name") String name);
}
```

**Repository derivation mapping (all named queries):**

| Entity | Named Query | Repository Method |
|---|---|---|
| Product | `Product.findAll` | `findAll()` (inherited) |
| Product | `Product.findBySku` | `Optional<Product> findBySku(String sku)` |
| Product | `Product.findByCategory` | `List<Product> findByCategory(String category)` |
| Product | `Product.findByManufacturer` | `List<Product> findByManufacturer(String manufacturer)` |
| Product | `Product.searchByName` | `@Query` with LIKE pattern |
| Customer | `Customer.findAll` | `findAll()` (inherited) |
| Customer | `Customer.findByType` | `List<Customer> findByType(String type)` |
| Customer | `Customer.findByLicenseNumber` | `Optional<Customer> findByLicenseNumber(String licenseNumber)` |
| Order | `Order.findAll` | `findAll()` (inherited) |
| Order | `Order.findByCustomer` | `List<Order> findByCustomerId(Long customerId)` |
| Order | `Order.findByStatus` | `List<Order> findByStatus(String status)` |
| Order | `Order.findByPriority` | `List<Order> findByPriority(String priority)` |
| Inventory | `Inventory.findByProduct` | `List<Inventory> findByProductId(Long productId)` |
| Inventory | `Inventory.findByWarehouse` | `List<Inventory> findByWarehouseId(String warehouseId)` |
| Inventory | `Inventory.findExpiringItems` | `@Query` with date comparison |
| Inventory | `Inventory.findByLotNumber` | `List<Inventory> findByLotNumber(String lotNumber)` |
| Supplier | `Supplier.findAll` | `findAll()` (inherited) |
| Supplier | `Supplier.findByRating` | `List<Supplier> findByRatingGreaterThanEqual(Double rating)` |
| PurchaseOrder | `PurchaseOrder.findAll` | `findAll()` (inherited) |
| PurchaseOrder | `PurchaseOrder.findBySupplier` | `List<PurchaseOrder> findBySupplierId(Long supplierId)` |
| PurchaseOrder | `PurchaseOrder.findByStatus` | `List<PurchaseOrder> findByStatus(String status)` |
| LotTracking | `LotTracking.findByProduct` | `List<LotTracking> findByProductId(Long productId)` |
| LotTracking | `LotTracking.findByLotNumber` | `List<LotTracking> findByLotNumber(String lotNumber)` |
| LotTracking | `LotTracking.findExpiring` | `@Query` with date comparison |

### 4.3 Service Layer (5 services replacing EJBs)

**General migration pattern:**

| Java EE | Spring Boot |
|---|---|
| `@Stateless` | `@Service` |
| `@Singleton @Startup` | `@Service` + `@PostConstruct` (or `@Cacheable`) |
| `@EJB SomeBean` | Constructor injection of `SomeService` / `SomeRepository` |
| `@PersistenceContext EntityManager em` | Constructor injection of `JpaRepository` |
| CMT `Required` (via `ejb-jar.xml`) | `@Transactional` on class |
| `em.persist(entity)` | `repository.save(entity)` |
| `em.find(Clazz, id)` | `repository.findById(id)` |
| `em.createNamedQuery(...)` | `repository.findByXxx(...)` |

#### 4.3.1 `OrderProcessingService` (← `OrderProcessingBean`)

```
@Service
@Transactional
public class OrderProcessingService {
    // Constructor injection: OrderRepository, InventoryService, OrderMessageProducer
    
    findById(Long id) → orderRepository.findById(id)
    findAll() → orderRepository.findAll()
    findByCustomer(Long customerId) → orderRepository.findByCustomerId(customerId)
    findByStatus(String status) → orderRepository.findByStatus(status)
    findByPriority(String priority) → orderRepository.findByPriority(priority)
    createOrder(Order order) → set defaults, orderRepository.save(), send JMS ORDER_CREATED
    processOrder(Long orderId) → check inventory, update status, send JMS
    shipOrder(Long orderId) → update status to SHIPPED, send JMS
    cancelOrder(Long orderId) → update status to CANCELLED
}
```

#### 4.3.2 `ProductCatalogService` (← `ProductCatalogBean`)

```
@Service
@Transactional
public class ProductCatalogService {
    // Constructor injection: ProductRepository
    
    Full CRUD via productRepository
    searchByName(String name) → productRepository.searchByName(name)
}
```

#### 4.3.3 `InventoryService` (← `InventoryBean`)

```
@Service
@Transactional
public class InventoryService {
    // Constructor injection: InventoryRepository
    
    checkAvailability(Long productId, String warehouseId, int quantity)
        → SUM quantity by product+warehouse, compare against required
    reduceStock(Long productId, String warehouseId, String lotNumber, int quantity)
        → FIFO reduction logic preserved
}
```

#### 4.3.4 `PurchaseOrderService` (← `PurchaseOrderBean`)

```
@Service
@Transactional
public class PurchaseOrderService {
    // Constructor injection: PurchaseOrderRepository
    
    approve(Long id) → status = "APPROVED"
    receive(Long id) → status = "RECEIVED"
}
```

#### 4.3.5 `CacheManagerService` (← `CacheManagerBean`)

**Option A (simple):** Direct port with `ConcurrentHashMap` (fixes thread-safety issue #13):

```
@Service
public class CacheManagerService {
    private final ConcurrentHashMap<Long, Product> productCache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Product> skuCache = new ConcurrentHashMap<>();
    
    @PostConstruct → load from ProductCatalogService
}
```

**Option B (recommended for future):** Replace with Spring Cache abstraction (`@Cacheable`, `@CacheEvict`). Option A is chosen for initial migration to preserve functional parity; Option B can follow.

### 4.4 Controller Layer (3 REST + 3 MVC)

#### 4.4.1 REST Controllers (`@RestController`)

**`ProductController`** (← `ProductResource`):

| Method | Path | Spring Annotation | Notes |
|---|---|---|---|
| `GET` | `/api/products` | `@GetMapping` | List all |
| `GET` | `/api/products/{id}` | `@GetMapping("/{id}")` | Cache-first lookup |
| `GET` | `/api/products/sku/{sku}` | `@GetMapping("/sku/{sku}")` | Cache-first lookup |
| `GET` | `/api/products/category/{category}` | `@GetMapping("/category/{category}")` | Filter by category |
| `GET` | `/api/products/search` | `@GetMapping("/search")` | `@RequestParam name` |
| `POST` | `/api/products` | `@PostMapping` | Create product |
| `PUT` | `/api/products/{id}` | `@PutMapping("/{id}")` | Update + cache evict |
| `DELETE` | `/api/products/{id}` | `@DeleteMapping("/{id}")` | Delete + cache evict |

**`OrderController`** (← `OrderResource`):

| Method | Path | Spring Annotation |
|---|---|---|
| `GET` | `/api/orders` | `@GetMapping` |
| `GET` | `/api/orders/{id}` | `@GetMapping("/{id}")` |
| `GET` | `/api/orders/customer/{customerId}` | `@GetMapping("/customer/{customerId}")` |
| `GET` | `/api/orders/status/{status}` | `@GetMapping("/status/{status}")` |
| `POST` | `/api/orders` | `@PostMapping` |
| `POST` | `/api/orders/{id}/process` | `@PostMapping("/{id}/process")` |
| `POST` | `/api/orders/{id}/ship` | `@PostMapping("/{id}/ship")` |
| `POST` | `/api/orders/{id}/cancel` | `@PostMapping("/{id}/cancel")` |

**`InventoryController`** (← `InventoryResource`):

| Method | Path | Spring Annotation |
|---|---|---|
| `GET` | `/api/inventory/product/{productId}` | `@GetMapping("/product/{productId}")` |
| `GET` | `/api/inventory/warehouse/{warehouseId}` | `@GetMapping("/warehouse/{warehouseId}")` |
| `GET` | `/api/inventory/expiring` | `@GetMapping("/expiring")` |
| `GET` | `/api/inventory/check-availability` | `@GetMapping("/check-availability")` |
| `POST` | `/api/inventory` | `@PostMapping` |
| `PUT` | `/api/inventory/{id}` | `@PutMapping("/{id}")` |

#### 4.4.2 View Controllers (`@Controller`)

**`DashboardController`** (← `DashboardBean`):

```java
@Controller
public class DashboardController {
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("totalProducts", productCatalogService.findAll().size());
        model.addAttribute("pendingOrders", orderProcessingService.findByStatus("PENDING").size());
        model.addAttribute("expiringItemsCount", inventoryService.findExpiringItems(...).size());
        model.addAttribute("recentOrders", orderProcessingService.findAll()); // top 10
        return "pages/dashboard";
    }
}
```

**`ProductSearchController`** (← `ProductSearchBean`):

```java
@Controller
public class ProductSearchController {
    @GetMapping("/products/search")
    public String searchForm(Model model) { ... return "pages/productSearch"; }

    @PostMapping("/products/search")
    public String search(@RequestParam String searchTerm,
                         @RequestParam(required=false) String category,
                         Model model) { ... return "pages/productSearch"; }
}
```

**`OrderWizardController`** (← `OrderWizardBean`):

```java
@Controller
@SessionAttributes("orderWizard")
public class OrderWizardController {
    // Multi-step wizard using session attributes to replace CDI ConversationScoped
    @GetMapping("/orders/wizard")    // Step 1: Customer info
    @PostMapping("/orders/wizard/step2")  // Step 2: Add products
    @PostMapping("/orders/wizard/step3")  // Step 3: Review
    @PostMapping("/orders/wizard/submit") // Submit order
    @GetMapping("/orders/wizard/cancel")  // Cancel wizard (clear session)
}
```

### 4.5 JMS / Messaging Layer

#### 4.5.1 `OrderMessageProducer`

```java
@Component
public class OrderMessageProducer {
    private final JmsTemplate jmsTemplate;

    // Constructor injection

    public void sendOrderCreatedMessage(Long orderId) {
        jmsTemplate.convertAndSend("OrderQueue", "ORDER_CREATED", message -> {
            message.setStringProperty("eventType", "ORDER_CREATED");
            message.setLongProperty("orderId", orderId);
            message.setLongProperty("timestamp", System.currentTimeMillis());
            return message;
        });
    }
    // Similar methods for CONFIRMED, BACKORDERED, SHIPPED
}
```

#### 4.5.2 `OrderMessageConsumer`

```java
@Component
public class OrderMessageConsumer {
    private static final Logger log = LoggerFactory.getLogger(OrderMessageConsumer.class);

    @JmsListener(destination = "OrderQueue")
    public void onMessage(Message message) throws JMSException {
        String eventType = message.getStringProperty("eventType");
        Long orderId = message.getLongProperty("orderId");
        // Dispatch to handler methods (same stub logic as legacy)
        switch (eventType) {
            case "ORDER_CREATED"    -> handleOrderCreated(orderId);
            case "ORDER_CONFIRMED"  -> handleOrderConfirmed(orderId);
            case "ORDER_BACKORDERED"-> handleOrderBackordered(orderId);
            case "ORDER_SHIPPED"    -> handleOrderShipped(orderId);
        }
    }
    // Stub handlers — log only (matching legacy behavior)
}
```

### 4.6 Security Configuration

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/index.html", "/css/**", "/js/**").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/**").authenticated()
                .requestMatchers("/dashboard", "/products/**", "/orders/**", "/inventory/**").authenticated()
                .anyRequest().authenticated()
            )
            .httpBasic(Customizer.withDefaults())
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/api/**")  // Disable CSRF for REST API
            );
        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        var admin = User.withDefaultPasswordEncoder()
            .username("admin").password("admin123").roles("ADMIN", "USER").build();
        var user = User.withDefaultPasswordEncoder()
            .username("user").password("user123").roles("USER").build();
        var warehouse = User.withDefaultPasswordEncoder()
            .username("warehouse").password("warehouse123").roles("WAREHOUSE", "USER").build();
        return new InMemoryUserDetailsManager(admin, user, warehouse);
    }
}
```

**Security improvements over legacy:**
- REST endpoints now require authentication (legacy had no security on `/api/*`)
- WAREHOUSE role added per existing `SecurityConfig` constants
- CSRF protection for form pages

### 4.7 Spring Boot Application Entry Point

```java
@SpringBootApplication
public class MedflowApplication {
    public static void main(String[] args) {
        SpringApplication.run(MedflowApplication.class, args);
    }
}
```

### 4.8 Java EE XML Descriptors — Deletion Checklist

| File | Replaced By |
|---|---|
| `src/main/resources/META-INF/persistence.xml` | `application.properties` (`spring.datasource.*`, `spring.jpa.*`) |
| `src/main/resources/META-INF/ejb-jar.xml` | `@Transactional` annotations on services |
| `src/main/webapp/WEB-INF/web.xml` | Spring Boot auto-configuration + `SecurityConfig.java` |
| `src/main/webapp/WEB-INF/faces-config.xml` | Spring MVC controller return values |
| `src/main/webapp/WEB-INF/beans.xml` | Spring component scanning (`@SpringBootApplication`) |
| `standalone.xml` | `application.properties` |

---

## 5. Spring Boot Configuration

### 5.1 `application.properties` (default profile)

```properties
# ──────────────────────────────────────────
# Server
# ──────────────────────────────────────────
server.port=8080

# ──────────────────────────────────────────
# PostgreSQL DataSource
# ──────────────────────────────────────────
spring.datasource.url=jdbc:postgresql://localhost:5432/medflowdb
spring.datasource.username=medflow
spring.datasource.password=medflow123
spring.datasource.driver-class-name=org.postgresql.Driver

# Connection pool (HikariCP — Spring Boot default)
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=5

# ──────────────────────────────────────────
# JPA / Hibernate
# ──────────────────────────────────────────
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.open-in-view=false
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.properties.hibernate.jdbc.batch_size=20
spring.jpa.properties.hibernate.jdbc.fetch_size=50

# ──────────────────────────────────────────
# Thymeleaf
# ──────────────────────────────────────────
spring.thymeleaf.cache=false
spring.thymeleaf.prefix=classpath:/templates/
spring.thymeleaf.suffix=.html

# ──────────────────────────────────────────
# ActiveMQ Artemis (JMS)
# ──────────────────────────────────────────
spring.activemq.broker-url=tcp://localhost:61616
spring.activemq.user=admin
spring.activemq.password=admin

# ──────────────────────────────────────────
# Jackson (JSON)
# ──────────────────────────────────────────
spring.jackson.serialization.write-dates-as-timestamps=false

# ──────────────────────────────────────────
# Logging
# ──────────────────────────────────────────
logging.level.com.medflow=DEBUG
logging.level.org.springframework=INFO
logging.level.org.hibernate.SQL=DEBUG
```

### 5.2 `application-dev.properties` (development overrides)

```properties
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true
spring.thymeleaf.cache=false
logging.level.com.medflow=TRACE

# Embedded H2 fallback (for quick local dev without PostgreSQL)
# spring.datasource.url=jdbc:h2:mem:medflowdb
# spring.datasource.driver-class-name=org.h2.Driver
# spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect
```

### 5.3 `application-prod.properties` (production overrides)

```properties
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
spring.thymeleaf.cache=true
logging.level.com.medflow=INFO
logging.level.org.hibernate.SQL=WARN

# Externalized via environment variables
# spring.datasource.url=${DATABASE_URL}
# spring.datasource.username=${DATABASE_USER}
# spring.datasource.password=${DATABASE_PASSWORD}
# spring.activemq.broker-url=${ACTIVEMQ_URL}
```

### 5.4 `pom.xml` (Spring Boot)

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
                             https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.4.4</version>
        <relativePath/>
    </parent>

    <groupId>com.medflow</groupId>
    <artifactId>medflow-springboot</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <packaging>jar</packaging>
    <name>MedFlow Distributor (Spring Boot)</name>

    <properties>
        <java.version>17</java.version>
    </properties>

    <dependencies>
        <!-- Core Spring Boot -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <!-- Spring Data JPA + Hibernate -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>

        <!-- Thymeleaf templates -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-thymeleaf</artifactId>
        </dependency>

        <!-- Thymeleaf Layout Dialect -->
        <dependency>
            <groupId>nz.net.ultraq.thymeleaf</groupId>
            <artifactId>thymeleaf-layout-dialect</artifactId>
        </dependency>

        <!-- Spring Security -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>

        <!-- ActiveMQ JMS -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-activemq</artifactId>
        </dependency>

        <!-- Bean Validation -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>

        <!-- PostgreSQL Driver -->
        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
            <scope>runtime</scope>
        </dependency>

        <!-- Dev Tools (optional, dev only) -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-devtools</artifactId>
            <scope>runtime</scope>
            <optional>true</optional>
        </dependency>

        <!-- Test -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.security</groupId>
            <artifactId>spring-security-test</artifactId>
            <scope>test</scope>
        </dependency>

        <!-- H2 for testing -->
        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

---

## 6. Docker & Container Strategy

### 6.1 Multi-Stage Dockerfile

```dockerfile
# ── Stage 1: Build ─────────────────────────
FROM eclipse-temurin:17-jdk AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src

# Install Maven and build
RUN apt-get update && apt-get install -y maven && \
    mvn clean package -DskipTests -B

# ── Stage 2: Runtime ───────────────────────
FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=builder /app/target/medflow-springboot-1.0.0-SNAPSHOT.jar app.jar

EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]
```

**Image size comparison:**

| Image | Base | Approximate Size |
|---|---|---|
| Legacy (WildFly 26) | `quay.io/wildfly/wildfly:26.1.3.Final` | ~750 MB |
| Spring Boot (JRE 17) | `eclipse-temurin:17-jre` | ~300 MB |

### 6.2 `docker-compose.yml`

```yaml
services:
  postgres:
    image: postgres:14
    container_name: medflow-postgres
    environment:
      POSTGRES_DB: medflowdb
      POSTGRES_USER: medflow
      POSTGRES_PASSWORD: medflow123
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data
    networks:
      - medflow-network
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U medflow -d medflowdb"]
      interval: 10s
      timeout: 5s
      retries: 5

  activemq:
    image: apache/activemq-artemis:2.28.0
    container_name: medflow-activemq
    environment:
      ARTEMIS_USER: admin
      ARTEMIS_PASSWORD: admin
    ports:
      - "8161:8161"    # Web console
      - "61616:61616"  # OpenWire
    networks:
      - medflow-network
    healthcheck:
      test: ["CMD-SHELL", "curl -f http://localhost:8161 || exit 1"]
      interval: 10s
      timeout: 5s
      retries: 5

  app:
    build: .
    container_name: medflow-app
    ports:
      - "8080:8080"
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/medflowdb
      SPRING_DATASOURCE_USERNAME: medflow
      SPRING_DATASOURCE_PASSWORD: medflow123
      SPRING_ACTIVEMQ_BROKER_URL: tcp://activemq:61616
      SPRING_ACTIVEMQ_USER: admin
      SPRING_ACTIVEMQ_PASSWORD: admin
      SPRING_PROFILES_ACTIVE: prod
    depends_on:
      postgres:
        condition: service_healthy
      activemq:
        condition: service_healthy
    networks:
      - medflow-network

networks:
  medflow-network:
    driver: bridge

volumes:
  postgres_data:
```

**Key changes from legacy `docker-compose.yml`:**

| Change | Legacy | Spring Boot |
|---|---|---|
| App service | `wildfly` (builds WildFly image, mounts WAR) | `app` (builds multi-stage JAR image) |
| Config | `standalone.xml` + env vars | Spring Boot env var overrides (`SPRING_*`) |
| Health checks | None | `pg_isready` for Postgres, HTTP for ActiveMQ, Actuator for app |
| Depends-on | Simple | `condition: service_healthy` |
| Compose version | `version: '3.8'` | Omitted (modern Compose spec) |

---

## 7. Testing Strategy

### 7.1 Test Framework Stack

| Tool | Purpose |
|---|---|
| JUnit 5 | Test runner and assertions |
| Mockito | Service mocking |
| Spring Boot Test (`@SpringBootTest`) | Integration test context |
| `@DataJpaTest` | Repository tests with embedded H2 |
| `@WebMvcTest` | Controller slice tests |
| `MockMvc` | HTTP endpoint testing |
| Spring Security Test (`@WithMockUser`) | Security-aware testing |
| H2 (in-memory) | Test database |

### 7.2 Test Categories

#### 7.2.1 Unit Tests — Service Layer

| Test Class | Coverage |
|---|---|
| `OrderProcessingServiceTest` | `createOrder()`, `processOrder()` (inventory check paths), `shipOrder()`, `cancelOrder()` |
| `ProductCatalogServiceTest` | CRUD operations, `searchByName()` |
| `InventoryServiceTest` | `checkAvailability()` (sufficient/insufficient stock), `reduceStock()` (FIFO logic) |
| `PurchaseOrderServiceTest` | `approve()`, `receive()` status transitions |
| `CacheManagerServiceTest` | Cache hit/miss, eviction, thread-safety |

**Approach:** Mock repository interfaces with Mockito; verify service logic in isolation.

#### 7.2.2 Integration Tests — Repository Layer

| Test Class | Coverage |
|---|---|
| `ProductRepositoryTest` | Derived queries: `findBySku()`, `findByCategory()`, `searchByName()` |
| `OrderRepositoryTest` | `findByStatus()`, `findByCustomerId()`, cascade persist of `OrderItem` |
| `InventoryRepositoryTest` | `findByProductId()`, `findExpiringItems()` date filtering |

**Approach:** `@DataJpaTest` with embedded H2; verify query correctness.

#### 7.2.3 Integration Tests — Controller Layer

| Test Class | Coverage |
|---|---|
| `ProductControllerTest` | All 8 REST endpoints; JSON request/response validation |
| `OrderControllerTest` | All 8 REST endpoints; order lifecycle (create → process → ship) |
| `InventoryControllerTest` | All 6 REST endpoints; availability check |
| `DashboardControllerTest` | `GET /dashboard` returns model attributes and view name |

**Approach:** `@WebMvcTest` with mocked services; `MockMvc` for HTTP assertions.

#### 7.2.4 Smoke / Sanity Tests

| Test | Description |
|---|---|
| `MedflowApplicationTests` | Verifies Spring context loads successfully |
| Security config test | Verifies `/api/**` requires auth; `/` is public |

### 7.3 Test Naming Convention

```
methodName_stateUnderTest_expectedBehavior
```

Example: `createOrder_validOrder_returnsPersistedOrderWithId`

---

## 8. Risk Assessment

### 8.1 High-Risk Items

| # | Risk | Impact | Likelihood | Mitigation |
|---|---|---|---|---|
| R1 | **JPA entity behavior change** after `javax` → `jakarta` migration and removal of `persistence.xml` | Data loss or incorrect queries | Low | Repository integration tests with H2; compare query results against legacy |
| R2 | **REST API contract drift** — response shape, status codes, or content types differ | Breaks API consumers | Medium | `MockMvc` tests validating exact JSON structure and status codes; side-by-side API comparison |
| R3 | **CDI ConversationScoped → SessionAttributes** — wizard state management change | Wizard data loss across steps | Medium | Manual end-to-end testing of order wizard; session timeout handling |
| R4 | **JMS message compatibility** — `ObjectMessage` → `convertAndSend` may change wire format | Messages lost or unreadable | Low | Use `MapMessage` or string-based format; consumer/producer tested together |
| R5 | **ActiveMQ Artemis client compatibility** — `spring-boot-starter-activemq` (Classic client) with Artemis broker | Connection failures | Medium | Verify OpenWire protocol compatibility; test in docker-compose environment |

### 8.2 Medium-Risk Items

| # | Risk | Impact | Likelihood | Mitigation |
|---|---|---|---|---|
| R6 | **Transaction boundary differences** — CMT `Required` vs. Spring `@Transactional` propagation | Incorrect rollback or commit scope | Low | `@Transactional` defaults to `REQUIRED`; functionally equivalent. Review multi-service calls. |
| R7 | **Thymeleaf template rendering** — JSF component behavior not exactly replicated | UI differences or broken forms | Medium | Manual visual comparison; priority on functional correctness over visual pixel-match |
| R8 | **Singleton cache thread-safety** — ConcurrentHashMap may have different contention characteristics | Performance regression | Low | Use ConcurrentHashMap (fixes existing HashMap issue); benchmark if needed |
| R9 | **Spring Security filter order** — CSRF + BASIC auth interaction on form pages | 403 errors on form submissions | Medium | Test form submissions with Spring Security Test; verify CSRF token in Thymeleaf forms |

### 8.3 Low-Risk Items

| # | Risk | Impact | Likelihood | Mitigation |
|---|---|---|---|---|
| R10 | **Named query removal** — derived queries may generate different SQL | Slight performance change | Low | Enable `spring.jpa.show-sql=true` in dev; compare SQL output |
| R11 | **Static content paths** — CSS/JS paths change from `resources/css/` to `static/css/` | Broken styles | Low | Verify `<link>` paths in Thymeleaf templates |
| R12 | **`data.sql` execution timing** — Spring Boot runs `data.sql` after Hibernate DDL | Seed data conflicts | Low | Use `spring.jpa.defer-datasource-initialization=true` |

### 8.4 Legacy Bugs Fixed During Migration

The following issues identified in Step 01 are resolved as part of the migration:

| Issue # | Description | Fix |
|---|---|---|
| 1 | Entity package mismatch in `persistence.xml` (`com.medflow.model` vs `com.medflow.entity`) | `persistence.xml` deleted; Spring Boot auto-scans `com.medflow.entity` |
| 2 | Non-existent Home/Remote interfaces in `ejb-jar.xml` | `ejb-jar.xml` deleted; no EJBs in Spring Boot |
| 3 | JSF EL bean name mismatches | Thymeleaf templates use `${model.attribute}` — no bean naming conflict |
| 4 | Property name mismatches (`total` vs `totalAmount`, `price` vs `unitPrice`) | Corrected in Thymeleaf templates |
| 5 | Java 11 build target with JDK 8 | New project targets Java 17 |
| 6 | Missing `import.sql` | Replaced by `data.sql` with actual seed data |
| 7 | `create-drop` DDL in production | Default `update`; production uses `validate` |
| 8 | Nested `<h:form>` | Standard HTML forms in Thymeleaf — no nesting issue |
| 9 | No REST/JSF security | Spring Security protects `/api/**` and view pages |
| 10 | JMS 1.1 boilerplate | Replaced by `JmsTemplate` |
| 13 | `HashMap` in `@Singleton` (thread-unsafe) | `ConcurrentHashMap` in `CacheManagerService` |
| 16 | `faces-config.xml` navigation mismatches | `faces-config.xml` deleted; Spring MVC routing |

---

## 9. Migration Execution Order

Migration should proceed in dependency order, where each step builds on the previous:

```
Phase 1: Foundation          Phase 2: Data           Phase 3: Business Logic
┌────────────────────┐      ┌──────────────────┐    ┌─────────────────────┐
│ 1. Project scaffold│ ──→  │ 3. Entities      │ →  │ 5. Services         │
│ 2. pom.xml + config│      │ 4. Repositories  │    │ 6. JMS messaging    │
└────────────────────┘      └──────────────────┘    └─────────────────────┘
                                                              │
Phase 4: API Layer           Phase 5: UI              Phase 6: Infrastructure
┌────────────────────┐      ┌──────────────────┐    ┌─────────────────────┐
│ 7. REST controllers│ ←──  │ 9. Thymeleaf     │    │ 11. Dockerfile      │
│ 8. View controllers│      │    templates     │    │ 12. docker-compose  │
│    + Security      │      │ 10. Static assets│    │ 13. Integration test│
└────────────────────┘      └──────────────────┘    └─────────────────────┘
```

| Step | Task | Depends On | Estimated Effort |
|---|---|---|---|
| 1 | Create `medflow-springboot/` project skeleton + `MedflowApplication.java` | — | Small |
| 2 | Create `pom.xml` with Spring Boot parent and starters | Step 1 | Small |
| 3 | Migrate 8 JPA entities (`javax` → `jakarta`, `@Temporal` → `java.time`) | Step 2 | Medium |
| 4 | Create 8 Spring Data JPA repository interfaces | Step 3 | Small |
| 5 | Migrate 5 EJB beans → `@Service` classes | Steps 3, 4 | Large |
| 6 | Migrate JMS producer (`JmsTemplate`) and consumer (`@JmsListener`) | Step 5 | Medium |
| 7 | Migrate 3 JAX-RS resources → `@RestController` classes | Step 5 | Medium |
| 8 | Migrate 3 JSF beans → `@Controller` classes + Spring Security config | Steps 5, 7 | Medium |
| 9 | Convert 6 XHTML views → Thymeleaf HTML templates | Step 8 | Medium |
| 10 | Migrate CSS and static assets | Step 9 | Small |
| 11 | Create multi-stage Dockerfile | Step 2 | Small |
| 12 | Update `docker-compose.yml` | Step 11 | Small |
| 13 | Write tests and validate end-to-end | Steps 7, 8, 9 | Large |
| 14 | Create `application.properties` (all profiles) | Step 2 | Small |

---

## 10. Acceptance Criteria

### 10.1 Build & Run

- [ ] `mvn clean package` succeeds with Java 17
- [ ] `java -jar target/medflow-springboot-1.0.0-SNAPSHOT.jar` starts without errors
- [ ] Application starts and connects to PostgreSQL (schema auto-created via `ddl-auto=update`)
- [ ] Application starts and connects to ActiveMQ Artemis

### 10.2 REST API Parity (19 endpoints)

- [ ] All `GET /api/products*` endpoints return correct JSON
- [ ] `POST /api/products` creates a product and returns 201
- [ ] `PUT /api/products/{id}` updates a product
- [ ] `DELETE /api/products/{id}` deletes a product
- [ ] All `GET /api/orders*` endpoints return correct JSON
- [ ] `POST /api/orders` creates an order with items
- [ ] `POST /api/orders/{id}/process` checks inventory and updates status
- [ ] `POST /api/orders/{id}/ship` and `cancel` update status
- [ ] All `GET /api/inventory*` endpoints return correct JSON
- [ ] `POST /api/inventory` and `PUT /api/inventory/{id}` work correctly

### 10.3 UI Parity

- [ ] Landing page (`/`) renders with navigation and feature cards
- [ ] Dashboard (`/dashboard`) displays product count, pending orders, expiring items, recent orders
- [ ] Product search (`/products/search`) — search by name and category works
- [ ] Order wizard (`/orders/wizard`) — multi-step wizard completes an order
- [ ] Inventory page (`/inventory`) renders informational content

### 10.4 Messaging

- [ ] Order creation sends JMS `ORDER_CREATED` message
- [ ] Consumer receives and logs messages for all 4 event types

### 10.5 Security

- [ ] `GET /` is accessible without authentication
- [ ] `GET /api/products` returns 401 without credentials
- [ ] `GET /api/products` returns 200 with valid Basic auth
- [ ] `GET /admin/**` requires ADMIN role

### 10.6 Docker

- [ ] `docker compose up --build` starts PostgreSQL, ActiveMQ, and app
- [ ] App container starts and is healthy within 60 seconds
- [ ] API endpoints accessible at `http://localhost:8080/api/*`

### 10.7 Tests

- [ ] All unit tests pass
- [ ] All integration tests pass
- [ ] Context load smoke test passes
- [ ] `mvn verify` exits with 0

---

*This specification serves as the authoritative blueprint for all subsequent migration steps. Each step should reference this document for architecture decisions, naming conventions, and target structure.*
