# MedFlow Distributor — Legacy Java EE Codebase Analysis

> **Application:** MedFlow Distributors — Wholesale Pharmaceutical Distribution System  
> **Platform:** Java EE 7 on WildFly 26.1.3.Final  
> **Database:** PostgreSQL 14 (via JPA/Hibernate)  
> **Messaging:** JMS (ActiveMQ Artemis 2.28.0)  
> **Packaging:** WAR (`medflow-distributor.war`)  
> **Build Tool:** Maven 3, Java source/target 11  

---

## 1. Project Structure

```
medflow-distributor/
├── pom.xml                          # Maven build (Java EE 7 BOM, WAR packaging)
├── Dockerfile                       # WildFly 26.1.3 image + PostgreSQL driver
├── docker-compose.yml               # PostgreSQL 14, ActiveMQ Artemis, WildFly
├── standalone.xml                   # WildFly server config (datasource + JMS queue)
└── src/main/
    ├── java/com/medflow/
    │   ├── entity/                  # 8 JPA entities
    │   ├── ejb/                     # 5 EJB session beans
    │   ├── rest/                    # 4 JAX-RS resource classes
    │   ├── jsf/                     # 3 JSF managed beans
    │   ├── jms/                     # 2 JMS classes (producer + MDB consumer)
    │   └── security/                # 1 security constants class
    ├── resources/META-INF/
    │   ├── persistence.xml          # JPA persistence unit (JTA, Hibernate)
    │   └── ejb-jar.xml              # EJB deployment descriptor
    └── webapp/
        ├── WEB-INF/
        │   ├── web.xml              # Servlet 3.1 config, JSF servlet, BASIC auth
        │   ├── faces-config.xml     # JSF 2.2 navigation rules
        │   └── beans.xml            # CDI 1.1 (bean-discovery-mode="all")
        ├── templates/layout.xhtml   # Facelets master template
        ├── index.xhtml              # Landing page
        ├── pages/
        │   ├── dashboard.xhtml      # Dashboard stats + recent orders
        │   ├── productSearch.xhtml  # Product search form + results table
        │   ├── orderWizard.xhtml    # Multi-step order creation wizard
        │   └── inventory.xhtml      # Inventory feature overview (static)
        └── resources/css/styles.css # Application stylesheet (656 lines)
```

---

## 2. Maven Build Configuration (`pom.xml`)

| Property | Value |
|---|---|
| `groupId` | `com.medflow` |
| `artifactId` | `medflow-distributor` |
| `version` | `1.0.0-SNAPSHOT` |
| `packaging` | `war` |
| `maven.compiler.source/target` | `11` |
| `javaee-api` | `7.0` (provided) |
| `postgresql` | `42.5.4` (provided — supplied by app server) |
| `slf4j-api` | `1.7.36` (provided) |

**BOM:** `org.jboss.spec:jboss-javaee-7.0:1.1.1.Final`

**Plugins:**
- `maven-compiler-plugin:3.11.0` — source/target 11
- `maven-war-plugin:3.3.2` — `failOnMissingWebXml=false`
- `wildfly-maven-plugin:2.1.0.Final` — deployment to WildFly

---

## 3. JPA Entities (8 classes)

All entities are in `com.medflow.entity`, use `javax.persistence` annotations, implement `Serializable`, and use `GenerationType.IDENTITY` for primary keys.

### 3.1 `Product`
| File | `entity/Product.java` |
|---|---|
| Table | `products` |
| Fields | `id`, `sku` (unique), `name`, `manufacturer`, `category`, `unitPrice` (BigDecimal 10,2), `requiresColdChain` (Boolean), `deaSchedule` |
| Named Queries | `Product.findAll`, `Product.findBySku`, `Product.findByCategory`, `Product.findByManufacturer`, `Product.searchByName` |
| Notes | Pharma-specific fields: cold chain flag, DEA schedule classification |

### 3.2 `Customer`
| File | `entity/Customer.java` |
|---|---|
| Table | `customer` |
| Fields | `id`, `name`, `type`, `licenseNumber` (unique), `address`, `creditLimit` (BigDecimal) |
| Named Queries | `Customer.findAll`, `Customer.findByType`, `Customer.findByLicenseNumber` |
| Notes | Customer types: Hospital, Pharmacy. License number for regulatory compliance. |

### 3.3 `Order`
| File | `entity/Order.java` |
|---|---|
| Table | `orders` |
| Fields | `id`, `customer` (ManyToOne → Customer), `orderDate` (Temporal TIMESTAMP), `status`, `totalAmount` (BigDecimal), `shippingAddress`, `priority` |
| Relationships | `@OneToMany(mappedBy="order", cascade=ALL, orphanRemoval=true)` → `List<OrderItem>` |
| Named Queries | `Order.findAll`, `Order.findByCustomer`, `Order.findByStatus`, `Order.findByPriority` |
| Business Logic | `addItem()` — adds item and recalculates total; `recalculateTotal()` — sums line totals |
| Status Values | `PENDING`, `CONFIRMED`, `BACKORDERED`, `SHIPPED`, `CANCELLED` |

### 3.4 `OrderItem`
| File | `entity/OrderItem.java` |
|---|---|
| Table | `order_item` |
| Fields | `id`, `order` (ManyToOne → Order), `product` (ManyToOne → Product), `lotNumber`, `quantity`, `unitPrice` (BigDecimal) |
| Business Logic | `getLineTotal()` — returns `unitPrice × quantity` |

### 3.5 `Inventory`
| File | `entity/Inventory.java` |
|---|---|
| Table | `inventory` |
| Fields | `id`, `product` (ManyToOne → Product), `warehouseId`, `lotNumber`, `quantity`, `expiryDate` (Temporal DATE), `receivedDate` (Temporal DATE) |
| Named Queries | `Inventory.findByProduct`, `Inventory.findByWarehouse`, `Inventory.findExpiringItems`, `Inventory.findByLotNumber` |

### 3.6 `Supplier`
| File | `entity/Supplier.java` |
|---|---|
| Table | `supplier` |
| Fields | `id`, `name`, `contactEmail`, `leadTimeDays`, `rating` (Double) |
| Named Queries | `Supplier.findAll`, `Supplier.findByRating` |

### 3.7 `PurchaseOrder`
| File | `entity/PurchaseOrder.java` |
|---|---|
| Table | `purchase_order` |
| Fields | `id`, `supplier` (ManyToOne → Supplier), `status`, `orderDate` (Temporal TIMESTAMP), `expectedDelivery` (Temporal DATE), `totalAmount` (BigDecimal) |
| Named Queries | `PurchaseOrder.findAll`, `PurchaseOrder.findBySupplier`, `PurchaseOrder.findByStatus` |

### 3.8 `LotTracking`
| File | `entity/LotTracking.java` |
|---|---|
| Table | `lot_tracking` |
| Fields | `id`, `product` (ManyToOne → Product), `lotNumber`, `manufacturerDate` (Temporal DATE), `expiryDate` (Temporal DATE), `quantityReceived`, `quantityDistributed` |
| Named Queries | `LotTracking.findByProduct`, `LotTracking.findByLotNumber`, `LotTracking.findExpiring` |

### Entity Relationship Diagram (Textual)

```
Customer 1──* Order 1──* OrderItem *──1 Product
Supplier 1──* PurchaseOrder
Product 1──* Inventory
Product 1──* LotTracking
```

---

## 4. EJB Session Beans (5 classes)

All in `com.medflow.ejb`, using `javax.ejb` annotations with `@PersistenceContext` EntityManager injection.

### 4.1 `OrderProcessingBean` — `@Stateless`
| File | `ejb/OrderProcessingBean.java` |
|---|---|
| Injections | `@PersistenceContext EntityManager`, `@EJB InventoryBean`, `@EJB OrderMessageProducer` |
| Methods | `findById()`, `findAll()`, `findByCustomer()`, `findByStatus()`, `findByPriority()`, `createOrder()`, `processOrder()`, `shipOrder()`, `cancelOrder()` |
| Business Logic | `createOrder()` — sets date/status/total, persists, sends JMS "ORDER_CREATED". `processOrder()` — checks inventory for all items, reserves stock if available (CONFIRMED) or marks BACKORDERED; sends appropriate JMS messages. `shipOrder()` — updates status, sends JMS. `cancelOrder()` — updates status. |
| Transaction | CMT Required (via ejb-jar.xml) |

### 4.2 `ProductCatalogBean` — `@Stateless`
| File | `ejb/ProductCatalogBean.java` |
|---|---|
| Injections | `@PersistenceContext EntityManager` |
| Methods | `findById()`, `findBySku()`, `findAll()`, `findByCategory()`, `findByManufacturer()`, `searchByName()`, `create()`, `update()`, `delete()` |
| Notes | Full CRUD. `searchByName()` uses inline JPQL (not named query) with LIKE pattern matching. |
| Transaction | CMT Required (via ejb-jar.xml) |

### 4.3 `InventoryBean` — `@Stateless`
| File | `ejb/InventoryBean.java` |
|---|---|
| Injections | `@PersistenceContext EntityManager` |
| Methods | `findById()`, `findByProduct()`, `findByWarehouse()`, `findExpiringItems()`, `findByLotNumber()`, `checkAvailability()`, `reduceStock()`, `create()`, `update()` |
| Business Logic | `checkAvailability()` — sums quantity by product+warehouse, compares against required. `reduceStock()` — iterates inventory records, reduces quantities (FIFO by iteration order), matches warehouse and optional lot number. |

### 4.4 `PurchaseOrderBean` — `@Stateless`
| File | `ejb/PurchaseOrderBean.java` |
|---|---|
| Injections | `@PersistenceContext EntityManager` |
| Methods | `findById()`, `findAll()`, `findBySupplier()`, `findByStatus()`, `create()`, `update()`, `approve()`, `receive()` |
| Business Logic | `approve()` → status "APPROVED"; `receive()` → status "RECEIVED" |

### 4.5 `CacheManagerBean` — `@Singleton @Startup`
| File | `ejb/CacheManagerBean.java` |
|---|---|
| Injections | `@EJB ProductCatalogBean` |
| State | `Map<Long, Product> productCache`, `Map<String, Product> skuCache` |
| Methods | `refreshCache()` (@PostConstruct), `getProductById()`, `getProductBySku()`, `evict()`, `evictBySku()`, `getCacheSize()`, `clear()` |
| Notes | Application-scoped in-memory cache. Populated at startup from DB. Manual eviction on product updates. No TTL or refresh mechanism. |

---

## 5. JAX-RS Resources (4 classes)

### 5.1 `JaxRsApplication`
| File | `rest/JaxRsApplication.java` |
|---|---|
| Base Path | `/api` |
| Notes | Extends `javax.ws.rs.core.Application`. No explicit resource registration (scan-based). |

### 5.2 `ProductResource` — `@Path("/products")`
| File | `rest/ProductResource.java` |
|---|---|
| Injections | `@EJB ProductCatalogBean`, `@EJB CacheManagerBean` |
| Endpoints | |

| Method | Path | Description |
|---|---|---|
| `GET` | `/api/products` | List all products |
| `GET` | `/api/products/{id}` | Get product by ID (cache-first) |
| `GET` | `/api/products/sku/{sku}` | Get product by SKU (cache-first) |
| `GET` | `/api/products/category/{category}` | List by category |
| `GET` | `/api/products/search?name=` | Search by name |
| `POST` | `/api/products` | Create product |
| `PUT` | `/api/products/{id}` | Update product (evicts cache) |
| `DELETE` | `/api/products/{id}` | Delete product (evicts cache) |

### 5.3 `OrderResource` — `@Path("/orders")`
| File | `rest/OrderResource.java` |
|---|---|
| Injections | `@EJB OrderProcessingBean` |
| Endpoints | |

| Method | Path | Description |
|---|---|---|
| `GET` | `/api/orders` | List all orders |
| `GET` | `/api/orders/{id}` | Get order by ID |
| `GET` | `/api/orders/customer/{customerId}` | List by customer |
| `GET` | `/api/orders/status/{status}` | List by status |
| `POST` | `/api/orders` | Create order |
| `POST` | `/api/orders/{id}/process` | Process order (check inventory, confirm/backorder) |
| `POST` | `/api/orders/{id}/ship` | Ship order |
| `POST` | `/api/orders/{id}/cancel` | Cancel order |

### 5.4 `InventoryResource` — `@Path("/inventory")`
| File | `rest/InventoryResource.java` |
|---|---|
| Injections | `@EJB InventoryBean` |
| Endpoints | |

| Method | Path | Description |
|---|---|---|
| `GET` | `/api/inventory/product/{productId}` | Get inventory by product |
| `GET` | `/api/inventory/warehouse/{warehouseId}` | Get inventory by warehouse |
| `GET` | `/api/inventory/expiring?date=` | Get expiring items (yyyy-MM-dd) |
| `GET` | `/api/inventory/check-availability?productId=&quantity=` | Check stock availability |
| `POST` | `/api/inventory` | Create inventory record |
| `PUT` | `/api/inventory/{id}` | Update inventory record |

---

## 6. JSF Managed Beans (3 classes)

All in `com.medflow.jsf`, using CDI `@Named` + JSF scopes.

### 6.1 `DashboardBean` — `@Named @ViewScoped`
| File | `jsf/DashboardBean.java` |
|---|---|
| Injections | `@EJB ProductCatalogBean`, `@EJB OrderProcessingBean`, `@EJB InventoryBean` |
| State | `totalProducts`, `pendingOrders`, `expiringItemsCount`, `recentOrders` (List<Order>) |
| Init | `@PostConstruct` — loads all products (count), pending orders (count), expiring items (within 30 days), recent orders (top 10) |
| Notes | Loads all data into memory on every view render. No pagination. |

### 6.2 `OrderWizardBean` — `@Named @ConversationScoped`
| File | `jsf/OrderWizardBean.java` |
|---|---|
| Injections | `@Inject Conversation`, `@EJB OrderProcessingBean`, `@EJB ProductCatalogBean` |
| State | `order` (Order), `availableProducts`, `selectedProductId`, `itemQuantity`, `lotNumber`, `step` (1-5) |
| Methods | `startOrder()` — begins CDI conversation, initializes order. `nextStep()`/`previousStep()` — wizard navigation. `addItem()` — adds product to order. `removeItem()` — removes item. `submitOrder()` — persists via EJB, ends conversation. |
| Notes | Uses CDI `Conversation` for stateful multi-step wizard. |

### 6.3 `ProductSearchBean` — `@Named @ViewScoped`
| File | `jsf/ProductSearchBean.java` |
|---|---|
| Injections | `@EJB ProductCatalogBean` |
| State | `searchTerm`, `selectedCategory`, `products` (List<Product>), `categories` (hardcoded list) |
| Methods | `search()` — searches by name, category, or returns all. `clear()` — resets form. |
| Categories | Prescription, Over-the-Counter, Medical Devices, Vaccines, Supplements |

---

## 7. JMS Classes (2 classes)

### 7.1 `OrderMessageProducer` — `@Stateless`
| File | `jms/OrderMessageProducer.java` |
|---|---|
| Resources | `@Resource(lookup="java:/ConnectionFactory") ConnectionFactory`, `@Resource(lookup="java:/jms/queue/OrderQueue") Queue` |
| Methods | `sendOrderCreatedMessage()`, `sendOrderConfirmedMessage()`, `sendOrderBackorderedMessage()`, `sendOrderShippedMessage()` |
| Message Format | `ObjectMessage` with properties: `eventType` (String), `orderId` (Long), `timestamp` (Long) |
| Notes | Uses JMS 1.1 API (manual Connection/Session management, not JMS 2.0 simplified API). No JMS context injection. |

### 7.2 `OrderMessageConsumer` — `@MessageDriven`
| File | `jms/OrderMessageConsumer.java` |
|---|---|
| Activation Config | `destinationType=javax.jms.Queue`, `destination=java:/jms/queue/OrderQueue`, `acknowledgeMode=Auto-acknowledge` |
| Handler | `onMessage()` — dispatches by `eventType` property to handler methods |
| Event Types | `ORDER_CREATED`, `ORDER_CONFIRMED`, `ORDER_BACKORDERED`, `ORDER_SHIPPED` |
| Notes | All handler methods are stubs — only log the event. No actual business logic in consumer. |

---

## 8. Security

### 8.1 `SecurityConfig` (`security/SecurityConfig.java`)
- Static constants class defining roles: `ADMIN`, `USER`, `WAREHOUSE`
- No actual security enforcement in EJBs or REST resources (no `@RolesAllowed`, `@DeclareRoles`)

### 8.2 `web.xml` Security Configuration
- **Auth Method:** BASIC authentication via `MedFlowRealm`
- **Protected Path:** `/admin/*` — requires `admin` role
- **Declared Roles:** `admin`, `user`
- **Transport Guarantee:** NONE (no SSL requirement)
- **Notes:** REST endpoints (`/api/*`) and JSF pages are NOT protected by security constraints

---

## 9. Configuration Files

### 9.1 `persistence.xml`
| Property | Value |
|---|---|
| Persistence Unit | `medflowPU` |
| Transaction Type | `JTA` |
| Data Source | `java:jboss/datasources/MedFlowDS` |
| Hibernate Dialect | `org.hibernate.dialect.PostgreSQLDialect` |
| DDL Strategy | `create-drop` (destroys data on redeploy) |
| SQL Logging | Enabled (`show_sql=true`, `format_sql=true`) |
| Import File | `import.sql` (referenced but **not present** in source) |
| Batch Size | 20 |
| Fetch Size | 50 |
| **Issue** | Entity classes listed as `com.medflow.model.*` but actual package is `com.medflow.entity.*` — **package mismatch** |

### 9.2 `ejb-jar.xml`
- EJB 3.2 deployment descriptor
- Declares `ProductCatalogBean` and `OrderProcessingBean` with **Home/Remote interfaces** that **do not exist** in the codebase (`ProductCatalogHome`, `ProductCatalog`, `OrderProcessingHome`, `OrderProcessing`)
- Container-Managed Transactions: `Required` for all methods on both beans
- Only 2 of 5 EJBs are declared (InventoryBean, PurchaseOrderBean, CacheManagerBean rely on annotation-only config)

### 9.3 `web.xml`
- Servlet 3.1
- JSF `FacesServlet` mapped to `*.xhtml`
- `PROJECT_STAGE=Development`
- Welcome file: `index.xhtml`
- BASIC auth for `/admin/*`

### 9.4 `faces-config.xml`
- JSF 2.2
- Default locale: `en`
- Navigation rules reference `#{searchController.goDashboard}` and `#{orderController.submit}` — **bean names do not match** actual `@Named` beans (`dashboardBean`, `productSearchBean`, `orderWizardBean`)

### 9.5 `beans.xml`
- CDI 1.1 with `bean-discovery-mode="all"` (all classes are CDI-managed)

### 9.6 `standalone.xml` (WildFly Server Config)
- **DataSource:** `java:jboss/datasources/MedFlowDS` → `jdbc:postgresql://postgres:5432/medflowdb`
- **JMS Queue:** `java:/jms/queue/OrderQueue`
- **Extensions:** JPA, EJB3, JAX-RS, Messaging-ActiveMQ

### 9.7 `docker-compose.yml`
- **PostgreSQL 14:** `medflowdb` database, user `medflow`
- **ActiveMQ Artemis 2.28.0:** JMS broker on port 61616
- **WildFly 26:** WAR deployed via volume mount
- Internal Docker network: `medflow-network`

---

## 10. JSF Views (6 XHTML files)

### 10.1 `index.xhtml` — Landing Page
- Standalone page (no template composition)
- Navigation header with links to all pages
- Feature cards grid: Product Catalog, Order Management, Inventory Tracking, Dashboard
- System info table: Java EE 7, WildFly 26, PostgreSQL, JMS (HornetQ), Hibernate 5.x

### 10.2 `templates/layout.xhtml` — Master Template
- Facelets template with `<ui:insert name="title"/>`, `<ui:insert name="head"/>`, `<ui:insert name="content"/>`
- Shared header navigation and footer

### 10.3 `pages/dashboard.xhtml`
- Uses `layout.xhtml` template
- Displays stats: total products, pending orders, expiring items
- Recent orders data table with customer, date, status, total
- **Issue:** References `#{dashboardController.*}` but bean is `@Named` as `dashboardBean`
- **Issue:** References `#{order.total}` but entity getter is `getTotalAmount()`

### 10.4 `pages/productSearch.xhtml`
- Search form with text input and category dropdown
- Results displayed in `<h:dataTable>`
- **Issue:** References `#{searchController.*}` but bean is `@Named` as `productSearchBean`
- **Issue:** References `#{product.price}` but entity getter is `getUnitPrice()`

### 10.5 `pages/orderWizard.xhtml`
- 3-step wizard: Customer Info → Add Products → Review Order
- Uses `<h:commandButton>` actions for navigation and item management
- Nested `<h:form>` elements inside outer form (invalid in JSF)
- **Issue:** References `#{orderController.*}` but bean is `@Named` as `orderWizardBean`
- **Issue:** References `#{item.product.price}` but entity getter is `getUnitPrice()`

### 10.6 `pages/inventory.xhtml`
- Static informational page (no dynamic data binding)
- Describes features: Stock Level Monitoring, Lot Tracking, Expiry Monitoring, Cold Chain, Multi-Warehouse, Supplier Management

---

## 11. Build Results

### Command
```bash
JAVA_HOME='C:\Program Files\Eclipse Adoptium\jdk-8.0.482.8-hotspot'
M2_HOME='C:\Tools\apache-maven-3.9.9'
cd medflow-distributor && mvn clean package -DskipTests
```

### Result: **BUILD FAILURE**

```
[ERROR] Failed to execute goal org.apache.maven.plugins:maven-compiler-plugin:3.11.0:compile
       (default-compile) on project medflow-distributor:
       Fatal error compiling: invalid target release: 11
```

### Root Cause
The `pom.xml` specifies `maven.compiler.source=11` and `maven.compiler.target=11`, but the build environment uses **JDK 8** (`jdk-8.0.482.8-hotspot`). JDK 8 cannot compile to Java 11 bytecode. A JDK 11+ is required to build this project as configured.

---

## 12. Issues & Inconsistencies Found

### Critical Issues (Prevent Correct Runtime Behavior)

| # | Issue | Location | Description |
|---|---|---|---|
| 1 | **Entity package mismatch in persistence.xml** | `persistence.xml` lines 12-19 | Lists classes as `com.medflow.model.*` but actual package is `com.medflow.entity.*`. JPA will not discover entities at runtime. |
| 2 | **Non-existent Home/Remote interfaces in ejb-jar.xml** | `ejb-jar.xml` lines 16-17, 28-29 | References `ProductCatalogHome`, `ProductCatalog`, `OrderProcessingHome`, `OrderProcessing` — none of these interfaces exist in the codebase. |
| 3 | **JSF EL bean name mismatches** | All XHTML pages | Views reference `#{dashboardController}`, `#{searchController}`, `#{orderController}` but beans are `@Named` as `dashboardBean`, `productSearchBean`, `orderWizardBean` respectively. |
| 4 | **Property name mismatches in views** | `dashboard.xhtml`, `productSearch.xhtml`, `orderWizard.xhtml` | `#{order.total}` should be `#{order.totalAmount}`; `#{product.price}` should be `#{product.unitPrice}` |
| 5 | **Build incompatibility** | `pom.xml` | Java 11 target with JDK 8 — build fails |

### Moderate Issues

| # | Issue | Location | Description |
|---|---|---|---|
| 6 | **Missing `import.sql`** | `persistence.xml` line 27 | `hibernate.hbm2ddl.import_files=import.sql` references a file that does not exist |
| 7 | **`create-drop` DDL strategy** | `persistence.xml` line 23 | Schema is dropped on undeploy — data loss on every redeploy |
| 8 | **Nested `<h:form>` elements** | `orderWizard.xhtml` lines 109, 179 | JSF does not support nested forms; inner form submissions will fail |
| 9 | **No REST/JSF security** | `web.xml`, REST resources | Only `/admin/*` is protected; all API endpoints and JSF pages are publicly accessible |
| 10 | **JMS 1.1 boilerplate** | `OrderMessageProducer.java` | Manual Connection/Session management instead of JMS 2.0 simplified API or `@JMSContext` |
| 11 | **Stub MDB handlers** | `OrderMessageConsumer.java` | All event handlers only log — no actual downstream processing |

### Minor Issues

| # | Issue | Location | Description |
|---|---|---|---|
| 12 | **No pagination** | `DashboardBean`, EJB queries | Loads all records into memory; `findAll()` returns unbounded result sets |
| 13 | **Thread-safety concern** | `CacheManagerBean` | Uses `HashMap` (not `ConcurrentHashMap`) in a `@Singleton` — concurrent read/write possible |
| 14 | **Hardcoded categories** | `ProductSearchBean.java` lines 33-37 | Categories hardcoded rather than derived from data |
| 15 | **Hardcoded warehouse ID** | `OrderProcessingBean.java` line 76, `InventoryResource.java` line 59 | Always uses warehouse ID `1L` |
| 16 | **faces-config.xml navigation mismatch** | `faces-config.xml` lines 17-34 | Navigation rules reference non-existent bean methods and outcomes |

---

## 13. Technology Inventory Summary

| Category | Technology | Java EE API | Count |
|---|---|---|---|
| Entities | JPA 2.1 | `javax.persistence.*` | 8 |
| Session Beans | EJB 3.2 | `javax.ejb.Stateless`, `Singleton`, `Startup` | 5 (4 Stateless + 1 Singleton) |
| Message-Driven Bean | EJB 3.2 / JMS 2.0 | `javax.ejb.MessageDriven`, `javax.jms.*` | 1 |
| JMS Producer | JMS 1.1 | `javax.jms.*`, `javax.annotation.Resource` | 1 |
| REST Resources | JAX-RS 2.0 | `javax.ws.rs.*` | 3 (+1 Application class) |
| JSF Beans | JSF 2.2 / CDI 1.1 | `javax.faces.*`, `javax.inject.*`, `javax.enterprise.context.*` | 3 |
| Views | Facelets | XHTML + JSF tags | 6 (1 template + 1 index + 4 pages) |
| Security | Servlet 3.1 | `web.xml` declarative | Constants class only |
| Config Files | XML descriptors | — | 5 (`persistence.xml`, `ejb-jar.xml`, `web.xml`, `faces-config.xml`, `beans.xml`) |

### REST API Endpoint Count: **19 endpoints** across 3 resources

### Total Java Source Files: **22**
### Total XHTML Files: **6**
### Total Configuration Files: **5** (in-WAR) + **3** (infra: Dockerfile, docker-compose.yml, standalone.xml)

---

## 14. Migration Considerations (Java EE → Spring Boot)

| Java EE Component | Spring Boot Equivalent |
|---|---|
| `@Stateless` EJBs | `@Service` / `@Component` with `@Transactional` |
| `@Singleton @Startup` | `@Component` with `@PostConstruct` (or Spring Cache) |
| `@MessageDriven` MDB | `@JmsListener` on a `@Component` method |
| JMS `@Resource` producer | `JmsTemplate` |
| `@PersistenceContext EntityManager` | Spring Data JPA `JpaRepository` interfaces |
| Named Queries | Spring Data derived queries or `@Query` |
| JAX-RS `@Path` resources | `@RestController` with `@RequestMapping` |
| JSF `@Named @ViewScoped` | Thymeleaf/Spring MVC `@Controller` (or REST + SPA) |
| CDI `@ConversationScoped` | Spring `@SessionScope` or wizard state in session |
| `persistence.xml` JTA | `application.properties` / `application.yml` |
| `web.xml` BASIC auth | Spring Security `SecurityFilterChain` |
| `ejb-jar.xml` CMT | `@Transactional` annotations |
| `faces-config.xml` navigation | Spring MVC view resolution / URL routing |
| `beans.xml` CDI | Spring component scanning (automatic) |
| JNDI DataSource | Spring Boot auto-configured `DataSource` |
| WildFly standalone.xml | `application.properties` |
