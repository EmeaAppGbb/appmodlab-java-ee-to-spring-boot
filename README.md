```
     ██╗ █████╗ ██╗   ██╗ █████╗     ███████╗███████╗    ═══>    ███████╗██████╗ ██████╗ ██╗███╗   ██╗ ██████╗     ██████╗  ██████╗  ██████╗ ████████╗    ██████╗ 
     ██║██╔══██╗██║   ██║██╔══██╗    ██╔════╝██╔════╝            ██╔════╝██╔══██╗██╔══██╗██║████╗  ██║██╔════╝     ██╔══██╗██╔═══██╗██╔═══██╗╚══██╔══╝    ╚════██╗
     ██║███████║██║   ██║███████║    █████╗  █████╗      ═══>    ███████╗██████╔╝██████╔╝██║██╔██╗ ██║██║  ███╗    ██████╔╝██║   ██║██║   ██║   ██║        █████╔╝
██   ██║██╔══██║╚██╗ ██╔╝██╔══██║    ██╔══╝  ██╔══╝              ╚════██║██╔═══╝ ██╔══██╗██║██║╚██╗██║██║   ██║    ██╔══██╗██║   ██║██║   ██║   ██║        ╚═══██╗
╚█████╔╝██║  ██║ ╚████╔╝ ██║  ██║    ███████╗███████╗    ═══>    ███████║██║     ██║  ██║██║██║ ╚████║╚██████╔╝    ██████╔╝╚██████╔╝╚██████╔╝   ██║       ██████╔╝
 ╚════╝ ╚═╝  ╚═╝  ╚═══╝  ╚═╝  ╚═╝    ╚══════╝╚══════╝            ╚══════╝╚═╝     ╚═╝  ╚═╝╚═╝╚═╝  ╚═══╝ ╚═════╝     ╚═════╝  ╚═════╝  ╚═════╝    ╚═╝       ╚═════╝ 
```

<div align="center">

# 🎮 MODERNIZATION ARCADE 🎮
### *The Ultimate Java EE Migration Quest*

[![Java](https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=openjdk&logoColor=white)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3-brightgreen?style=for-the-badge&logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Azure](https://img.shields.io/badge/Azure-Container%20Apps-0078D4?style=for-the-badge&logo=microsoftazure&logoColor=white)](https://azure.microsoft.com)

**🪙 INSERT COIN TO CONTINUE 🪙**

</div>

---

## 🌟 OVERVIEW

```
╔════════════════════════════════════════════════════════════════╗
║  🏥 MISSION BRIEFING: MEDFLOW DISTRIBUTORS MODERNIZATION 🏥   ║
╚════════════════════════════════════════════════════════════════╝
```

Welcome to the **ultimate Java EE modernization arcade**! 🕹️ You're about to embark on an epic journey migrating **MedFlow Distributors**, a pharmaceutical distribution powerhouse, from the ancient lands of **Java EE 7 / WildFly 26** to the modern realm of **Spring Boot 3.x**! ⚡

**🎯 THE TRANSFORMATION:**
- 🔄 **EJBs** → `@Service` & `@Repository` Spring beans
- 💾 **JPA 2.1** → Spring Data JPA with auto-configuration
- 🎨 **JSF 2.3 + PrimeFaces** → Thymeleaf + htmx (modern, reactive UI)
- 📨 **JMS 2.0 + ActiveMQ** → Spring Messaging (Kafka/Azure Service Bus ready)
- 🔐 **JAAS** → Spring Security OAuth2 / OIDC
- 🏗️ **WildFly 26** → Embedded Tomcat (fat JAR goodness)
- ☁️ **On-Prem** → Azure Container Apps

---

## 🎯 WHAT YOU'LL LEARN

<table>
<tr>
<td width="50%">

### 🔧 MIGRATION SKILLS
- ⚡ **EJB → Spring**: Convert Session Beans to Spring Services
- 🗄️ **JPA Config**: From `persistence.xml` to Spring Boot magic
- 🎨 **UI Modernization**: JSF/PrimeFaces → Thymeleaf + htmx
- 📬 **Messaging**: JMS → Spring Kafka/Azure Service Bus
- 🔒 **Security**: JAAS → OAuth2 / OIDC

</td>
<td width="50%">

### 🚀 CLOUD-NATIVE PATTERNS
- 🐳 **Containerization**: Docker + multi-stage builds
- ☁️ **Azure Deployment**: Container Apps, PostgreSQL Flexible
- 💨 **Caching**: Singleton EJB → Spring Cache + Caffeine
- 📊 **Observability**: Actuator, Metrics, Health Checks
- 🎮 **Copilot CLI**: AI-powered migration assistant

</td>
</tr>
</table>

---

## 🛠️ PREREQUISITES

```
╔═══════════════════════════════════════════════════════════╗
║  🎮 PLAYER 1 READY? CHECK YOUR INVENTORY! 🎮             ║
╚═══════════════════════════════════════════════════════════╝
```

Before entering the arcade, make sure you've got:

| Item | Version | Why You Need It |
|------|---------|-----------------|
| ☕ **Java JDK** | 21+ | Spring Boot 3.3 requires Java 17+ |
| 📦 **Maven** | 3.9+ | Build automation power-up |
| 🐳 **Docker Desktop** | Latest | Container runtime (WildFly, PostgreSQL, ActiveMQ) |
| ☁️ **Azure Subscription** | Active | Deploy to Azure Container Apps |
| 🐘 **PostgreSQL Knowledge** | Basic | Database migrations & Spring Data |
| 🤖 **GitHub Copilot CLI** | Latest | Your AI co-pilot for migration |

**⚡ POWER-UP:** Install Copilot CLI with `gh extension install github/gh-copilot`

---

## 🚀 QUICK START

```bash
# 🪙 INSERT COIN
git clone <repository-url>
cd appmodlab-java-ee-to-spring-boot

# 🎮 LEVEL 1: Start the Legacy App
docker-compose up -d

# 🌐 ACCESS THE GAME
open http://localhost:8080/medflow
```

**📺 LEGACY APP ENDPOINTS:**
- 🏠 **Home**: `http://localhost:8080/medflow`
- 📦 **Product Catalog**: `http://localhost:8080/medflow/products.jsf`
- 🏢 **Warehouse Inventory**: `http://localhost:8080/medflow/inventory.jsf`
- 📋 **Orders**: `http://localhost:8080/medflow/orders.jsf`

---

## 📁 PROJECT STRUCTURE

```
appmodlab-java-ee-to-spring-boot/
│
├── 🎮 legacy-app/                    # The Original Boss Battle
│   ├── src/main/java/
│   │   └── com/medflow/
│   │       ├── ejb/                  # EJB Session Beans
│   │       │   ├── ProductServiceBean.java
│   │       │   ├── OrderServiceBean.java
│   │       │   └── InventoryServiceBean.java
│   │       ├── entity/               # JPA Entities
│   │       │   ├── Product.java
│   │       │   ├── Order.java
│   │       │   ├── Warehouse.java
│   │       │   └── Supplier.java
│   │       ├── jsf/                  # JSF Managed Beans
│   │       │   ├── ProductBean.java
│   │       │   └── OrderBean.java
│   │       └── jms/                  # JMS Message Listeners
│   │           └── OrderNotificationListener.java
│   ├── src/main/resources/
│   │   └── META-INF/
│   │       ├── persistence.xml       # JPA Configuration
│   │       └── ejb-jar.xml           # EJB Descriptors
│   ├── src/main/webapp/
│   │   ├── WEB-INF/
│   │   │   ├── web.xml
│   │   │   └── faces-config.xml
│   │   └── *.xhtml                   # JSF/Facelets Views
│   └── pom.xml
│
├── 🌟 spring-boot-app/               # The Modernized Champion
│   ├── src/main/java/
│   │   └── com/medflow/
│   │       ├── service/              # @Service (was EJB)
│   │       ├── repository/           # Spring Data JPA
│   │       ├── controller/           # @RestController / @Controller
│   │       ├── config/               # Spring Configuration
│   │       └── messaging/            # Spring Kafka/Service Bus
│   ├── src/main/resources/
│   │   ├── application.yml           # Spring Boot Config (was persistence.xml)
│   │   ├── templates/                # Thymeleaf (was JSF .xhtml)
│   │   └── static/                   # CSS, JS, htmx
│   └── pom.xml
│
├── 🐳 docker-compose.yml             # WildFly + PostgreSQL + ActiveMQ
├── 📜 migration-guide.md             # Detailed migration notes
└── 📖 README.md                      # You are here! 🎯
```

---

## 🏥 MEDFLOW DISTRIBUTORS

```
╔═══════════════════════════════════════════════════════════╗
║     🏥 THE PHARMACEUTICAL DISTRIBUTION EMPIRE 🏥         ║
╚═══════════════════════════════════════════════════════════╝
```

**MedFlow Distributors** is a mission-critical pharmaceutical distribution system handling:

### 💊 Core Business Functions

🔹 **Product Catalog Management**
- Manage thousands of pharmaceutical SKUs
- Track drug classifications, dosages, and formulations
- Maintain pricing, discounts, and contract terms

🔹 **Warehouse Inventory Control**
- Multi-warehouse distributed inventory
- Real-time stock levels and reorder points
- Batch/lot tracking for regulatory compliance

🔹 **Customer Order Processing**
- Healthcare provider order management
- Real-time inventory allocation
- Automated order fulfillment workflows

🔹 **Lot Tracking & Compliance** 🎯
- FDA-mandated batch/lot traceability
- Expiration date management
- Recall readiness and audit trails

🔹 **Supplier Purchase Orders**
- Automated replenishment from manufacturers
- Purchase order tracking and receiving
- Supplier performance analytics

**⚡ POWER-UP:** Pharmaceutical distribution requires bulletproof data integrity and audit trails. Spring Boot + PostgreSQL delivers both!

---

## ⚡ THE LEGACY STACK

```
╔════════════════════════════════════════════════════════════╗
║  👾 FINAL BOSS: THE OLD GUARD (circa 2014) 👾            ║
╚════════════════════════════════════════════════════════════╝
```

| Technology | Version | What It Does | Pain Points |
|------------|---------|--------------|-------------|
| ☕ **EJB** | 3.2 | Session Beans (@Stateless, @Stateful) | Heavy, container-dependent, verbose |
| 💾 **JPA** | 2.1 | Persistence API | Manual config (`persistence.xml`) |
| 🎨 **JSF** | 2.3 + PrimeFaces | Server-side UI framework | Heavy page lifecycle, slow |
| 📨 **JMS** | 2.0 + ActiveMQ | Asynchronous messaging | Tightly coupled to Java EE |
| 🔐 **JAAS** | 1.0 | Java Authentication/Authorization | Complex, limited OAuth support |
| 💉 **CDI** | 1.2 | Dependency Injection | EJB-specific |
| 🏗️ **WildFly** | 26 | Application Server | Heavy footprint, slow startup |
| 📄 **XML Config** | - | `persistence.xml`, `ejb-jar.xml`, `web.xml` | Verbose, error-prone |

**🎮 GAME OVER SCREEN:** Slow startup, heavyweight deployment, cloud-unfriendly architecture!

---

## 🎯 TARGET ARCHITECTURE

```
╔════════════════════════════════════════════════════════════╗
║  🌟 THE NEW CHAMPION: SPRING BOOT 3.3 🌟                 ║
╚════════════════════════════════════════════════════════════╝
```

| Technology | Version | What It Does | Benefits |
|------------|---------|--------------|----------|
| 🍃 **Spring Boot** | 3.3 | Opinionated framework | Convention over config, fast startup |
| 🗄️ **Spring Data JPA** | 3.x | Repository abstraction | No boilerplate, auto-config |
| 🎨 **Thymeleaf** | 3.x | Modern template engine | Clean syntax, htmx integration |
| ⚡ **htmx** | Latest | Hypermedia-driven UI | SPA-like UX without heavy JS |
| 📬 **Spring Kafka** | 3.x | Event streaming | Cloud-native, scalable messaging |
| 🔐 **Spring Security** | 6.x | OAuth2 / OIDC | Modern auth, Azure AD integration |
| 🚀 **Embedded Tomcat** | 10.x | Servlet container | Fat JAR, no external server |
| 💨 **Caffeine Cache** | Latest | High-performance cache | Replaces Singleton EJB cache |
| ☁️ **Azure Container Apps** | - | Serverless containers | Auto-scaling, managed infrastructure |

**⚡ POWER-UP:** Spring Boot 3.3 starts in ~3 seconds vs. WildFly's 30+ seconds!

---

## 🕹️ LAB WALKTHROUGH

```
╔════════════════════════════════════════════════════════════╗
║          🎮 LEVEL SELECT: CHOOSE YOUR QUEST 🎮           ║
╚════════════════════════════════════════════════════════════╝
```

### 🎯 LEVEL 1: RUN THE LEGACY APP

**Mission**: Experience the pain of the old world! 🔥

```bash
# Start the full legacy stack
docker-compose up -d

# Check all services are running
docker ps

# Access the app
open http://localhost:8080/medflow/products.jsf
```

**🎮 GAMEPLAY:**
1. Browse the product catalog (JSF + PrimeFaces)
2. Check warehouse inventory
3. Create a test order
4. Observe the slow page loads and full-page refreshes 😱

**⚡ POWER-UP:** Use browser DevTools to see the heavyweight JSF ViewState!

---

### 🌟 LEVEL 2: SPRING BOOT SCAFFOLD

**Mission**: Bootstrap your Spring Boot 3 project! 🚀

```bash
# Use Spring Initializr (or Copilot CLI!)
gh copilot suggest "create a spring boot 3.3 project with web, data-jpa, thymeleaf, security, cache, actuator"

# Or manually:
curl https://start.spring.io/starter.zip \
  -d dependencies=web,data-jpa,thymeleaf,security,cache,actuator,postgresql \
  -d javaVersion=21 \
  -d bootVersion=3.3.0 \
  -d groupId=com.medflow \
  -d artifactId=medflow-spring \
  -o medflow-spring.zip

unzip medflow-spring.zip -d spring-boot-app
cd spring-boot-app
```

**🎯 MIGRATION CHECKLIST:**
- ✅ Copy JPA entities from `legacy-app/src/main/java/com/medflow/entity/` → `spring-boot-app/src/main/java/com/medflow/entity/`
- ✅ Convert `ProductServiceBean.java` (EJB) → `ProductService.java` (@Service)
- ✅ Convert `OrderServiceBean.java` (EJB) → `OrderService.java` (@Service)
- ✅ Remove `@Stateless`, `@EJB` annotations
- ✅ Add `@Service`, `@Autowired` (or constructor injection)

**Example Conversion:**

```java
// ❌ BEFORE (Java EE EJB)
@Stateless
public class ProductServiceBean {
    @PersistenceContext
    private EntityManager em;
    
    @EJB
    private InventoryServiceBean inventoryService;
    
    public List<Product> findAll() {
        return em.createQuery("SELECT p FROM Product p", Product.class)
                 .getResultList();
    }
}

// ✅ AFTER (Spring Boot)
@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final InventoryService inventoryService;
    
    public ProductService(ProductRepository repo, InventoryService inv) {
        this.productRepository = repo;
        this.inventoryService = inv;
    }
    
    public List<Product> findAll() {
        return productRepository.findAll();
    }
}
```

**🌀 WARP ZONE:** Use Copilot CLI to automate EJB → Spring conversion!

```bash
gh copilot suggest "convert this EJB session bean to a Spring @Service class"
```

---

### 💾 LEVEL 3: MIGRATE JPA

**Mission**: Say goodbye to `persistence.xml`! 👋

**🎯 BEFORE (Java EE):**
```xml
<!-- legacy-app/src/main/resources/META-INF/persistence.xml -->
<persistence-unit name="medflowPU" transaction-type="JTA">
    <jta-data-source>java:jboss/datasources/MedFlowDS</jta-data-source>
    <properties>
        <property name="hibernate.dialect" value="org.hibernate.dialect.PostgreSQLDialect"/>
        <property name="hibernate.hbm2ddl.auto" value="update"/>
    </properties>
</persistence-unit>
```

**🌟 AFTER (Spring Boot):**
```yaml
# spring-boot-app/src/main/resources/application.yml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/medflow
    username: medflow_user
    password: ${DB_PASSWORD}
  jpa:
    hibernate:
      ddl-auto: update
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
    show-sql: true
```

**🎯 CREATE SPRING DATA REPOSITORIES:**

```java
// legacy-app: Used EntityManager directly in EJB
// spring-boot-app/src/main/java/com/medflow/repository/ProductRepository.java

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // 🎮 NO BOILERPLATE! Spring Data magic! ✨
    List<Product> findByCategory(String category);
    List<Product> findByNameContainingIgnoreCase(String name);
    
    @Query("SELECT p FROM Product p WHERE p.stock > 0")
    List<Product> findInStock();
}
```

**⚡ POWER-UP:** Spring Data JPA eliminates 90% of your DAO boilerplate!

---

### 🎨 LEVEL 4: MIGRATE UI (JSF → THYMELEAF + HTMX)

**👾 FINAL BOSS ALERT:** This is the toughest migration! 🔥

**🎯 BEFORE (JSF + PrimeFaces):**
```xml
<!-- products.xhtml -->
<h:form>
    <p:dataTable value="#{productBean.products}" var="product">
        <p:column headerText="Product Name">
            <h:outputText value="#{product.name}"/>
        </p:column>
        <p:column headerText="Price">
            <h:outputText value="#{product.price}">
                <f:convertNumber type="currency" currencySymbol="$"/>
            </h:outputText>
        </p:column>
    </p:dataTable>
</h:form>
```

**🌟 AFTER (Thymeleaf + htmx):**
```html
<!-- templates/products.html -->
<div class="product-grid">
    <table class="retro-table" hx-get="/api/products" hx-trigger="load" hx-swap="innerHTML">
        <thead>
            <tr>
                <th>🏷️ Product Name</th>
                <th>💰 Price</th>
                <th>📦 Stock</th>
            </tr>
        </thead>
        <tbody>
            <tr th:each="product : ${products}">
                <td th:text="${product.name}">Product X</td>
                <td th:text="${#numbers.formatCurrency(product.price)}">$99.99</td>
                <td th:text="${product.stock}">100</td>
            </tr>
        </tbody>
    </table>
</div>
```

**🎮 SPRING CONTROLLER:**
```java
@Controller
public class ProductController {
    private final ProductService productService;
    
    @GetMapping("/products")
    public String showProducts(Model model) {
        model.addAttribute("products", productService.findAll());
        return "products";
    }
    
    // htmx endpoint for dynamic updates
    @GetMapping("/api/products")
    public String getProductsPartial(Model model) {
        model.addAttribute("products", productService.findAll());
        return "products :: product-table";
    }
}
```

**⚡ POWER-UP:** htmx gives you SPA-like UX with zero JavaScript frameworks! 🚀

---

### 📬 LEVEL 5: MIGRATE MESSAGING (JMS → SPRING)

**Mission**: From ActiveMQ JMS to Spring Kafka/Azure Service Bus! 📨

**🎯 BEFORE (Java EE JMS):**
```java
@MessageDriven(activationConfig = {
    @ActivationConfigProperty(propertyName = "destinationType", 
                              propertyValue = "javax.jms.Queue"),
    @ActivationConfigProperty(propertyName = "destination", 
                              propertyValue = "java:/jms/queue/OrderNotifications")
})
public class OrderNotificationListener implements MessageListener {
    @Override
    public void onMessage(Message message) {
        // Handle JMS message
    }
}
```

**🌟 AFTER (Spring Kafka):**
```java
@Service
public class OrderNotificationListener {
    
    @KafkaListener(topics = "order-notifications", groupId = "medflow-group")
    public void handleOrderNotification(@Payload OrderEvent event) {
        // Handle Kafka event
        log.info("📨 Order notification received: {}", event.getOrderId());
    }
}
```

**application.yml:**
```yaml
spring:
  kafka:
    bootstrap-servers: ${KAFKA_BOOTSTRAP_SERVERS:localhost:9092}
    consumer:
      group-id: medflow-group
      auto-offset-reset: earliest
```

**🌀 WARP ZONE:** For Azure, use Azure Service Bus Spring Boot Starter!

```xml
<dependency>
    <groupId>com.azure.spring</groupId>
    <artifactId>spring-cloud-azure-starter-servicebus-jms</artifactId>
</dependency>
```

---

### 🔐 LEVEL 6: MIGRATE SECURITY (JAAS → OAUTH2)

**Mission**: Modern authentication with Spring Security! 🔒

**🎯 BEFORE (JAAS):**
```xml
<!-- web.xml -->
<security-constraint>
    <web-resource-collection>
        <web-resource-name>Admin Area</web-resource-name>
        <url-pattern>/admin/*</url-pattern>
    </web-resource-collection>
    <auth-constraint>
        <role-name>admin</role-name>
    </auth-constraint>
</security-constraint>
<login-config>
    <auth-method>FORM</auth-method>
</login-config>
```

**🌟 AFTER (Spring Security OAuth2):**
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/", "/products").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .oauth2Login(oauth2 -> oauth2
                .defaultSuccessUrl("/dashboard", true)
            );
        return http.build();
    }
}
```

**application.yml (Azure AD OAuth2):**
```yaml
spring:
  security:
    oauth2:
      client:
        registration:
          azure:
            client-id: ${AZURE_CLIENT_ID}
            client-secret: ${AZURE_CLIENT_SECRET}
            scope: openid, profile, email
        provider:
          azure:
            issuer-uri: https://login.microsoftonline.com/${AZURE_TENANT_ID}/v2.0
```

**⚡ POWER-UP:** Azure AD integration = enterprise-grade SSO in minutes! 🚀

---

### 💨 LEVEL 7: ADD CACHING (SINGLETON EJB → SPRING CACHE)

**Mission**: Replace Singleton EJB cache with Caffeine! ⚡

**🎯 BEFORE (Singleton EJB Cache):**
```java
@Singleton
@Startup
public class ProductCacheBean {
    private Map<Long, Product> cache = new ConcurrentHashMap<>();
    
    @PostConstruct
    public void loadCache() {
        // Manually load cache
    }
    
    public Product getProduct(Long id) {
        return cache.get(id);
    }
}
```

**🌟 AFTER (Spring Cache + Caffeine):**
```java
@Configuration
@EnableCaching
public class CacheConfig {
    
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("products", "orders");
        cacheManager.setCaffeine(Caffeine.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .maximumSize(1000));
        return cacheManager;
    }
}

@Service
public class ProductService {
    
    @Cacheable("products")
    public Product findById(Long id) {
        return productRepository.findById(id).orElseThrow();
    }
    
    @CacheEvict(value = "products", key = "#product.id")
    public Product update(Product product) {
        return productRepository.save(product);
    }
}
```

**⚡ POWER-UP:** Declarative caching with `@Cacheable` - zero boilerplate! ✨

---

### 🐳 LEVEL 8: CONTAINERIZE & DEPLOY

**Mission**: Package as Docker container → Deploy to Azure! ☁️

**🎯 MULTI-STAGE DOCKERFILE:**
```dockerfile
# Stage 1: Build
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/medflow-spring-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

**🚀 BUILD & RUN:**
```bash
# Build the Docker image
docker build -t medflow-spring:latest .

# Run locally
docker run -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/medflow \
  medflow-spring:latest

# Test
curl http://localhost:8080/actuator/health
```

**☁️ DEPLOY TO AZURE CONTAINER APPS:**
```bash
# Login to Azure
az login

# Create resource group
az group create --name medflow-rg --location eastus

# Create Azure Container Registry
az acr create --resource-group medflow-rg \
  --name medflowacr --sku Basic

# Build and push to ACR
az acr build --registry medflowacr \
  --image medflow-spring:v1 .

# Create Container App
az containerapp create \
  --name medflow-app \
  --resource-group medflow-rg \
  --image medflowacr.azurecr.io/medflow-spring:v1 \
  --target-port 8080 \
  --ingress external \
  --env-vars \
    SPRING_DATASOURCE_URL=secretref:db-url \
    SPRING_DATASOURCE_PASSWORD=secretref:db-password
```

**⚡ POWER-UP:** Container Apps auto-scales from 0 to N based on load! 🚀

---

### ✅ LEVEL 9: VALIDATE END-TO-END

**Mission**: Prove the migration worked! 🎯

**🎮 FINAL BOSS BATTLE CHECKLIST:**
- ✅ Product catalog loads in Thymeleaf UI
- ✅ Create new order (triggers Spring messaging event)
- ✅ Check inventory updates in real-time (htmx magic)
- ✅ Login with OAuth2 (Azure AD)
- ✅ Admin functions protected by Spring Security
- ✅ Caching improves product lookup performance
- ✅ Actuator health endpoint returns `UP`
- ✅ Azure Container App handles 100+ concurrent requests

**🎯 PERFORMANCE COMPARISON:**

| Metric | Java EE / WildFly | Spring Boot 3 | Improvement |
|--------|-------------------|---------------|-------------|
| Startup Time | 30-45 seconds | 3-5 seconds | **90% faster** ⚡ |
| Memory (Idle) | 512 MB | 256 MB | **50% less** 💚 |
| Response Time | 200-300 ms | 50-100 ms | **60% faster** 🚀 |
| Container Image | 800 MB | 250 MB | **70% smaller** 📦 |

**🏆 ACHIEVEMENT UNLOCKED: MODERNIZATION MASTER! 🏆**

---

## ⏱️ ESTIMATED DURATION

```
╔════════════════════════════════════════════════════════════╗
║            ⏰ TIME TO COMPLETE THIS QUEST ⏰             ║
╚════════════════════════════════════════════════════════════╝
```

**Total Lab Time**: **6-8 hours** 🕹️

| Level | Task | Time | Difficulty |
|-------|------|------|------------|
| 1️⃣ | Run Legacy App | 30 min | ⭐ Easy |
| 2️⃣ | Spring Boot Scaffold | 1 hour | ⭐⭐ Medium |
| 3️⃣ | Migrate JPA | 1 hour | ⭐⭐ Medium |
| 4️⃣ | Migrate UI (JSF → Thymeleaf) | 2 hours | ⭐⭐⭐⭐ Hard |
| 5️⃣ | Migrate Messaging | 1 hour | ⭐⭐⭐ Medium |
| 6️⃣ | Migrate Security | 1 hour | ⭐⭐⭐ Medium |
| 7️⃣ | Add Caching | 30 min | ⭐⭐ Easy |
| 8️⃣ | Containerize & Deploy | 1.5 hours | ⭐⭐⭐ Medium |
| 9️⃣ | Validate E2E | 30 min | ⭐⭐ Medium |

**⚡ POWER-UP:** Use Copilot CLI to cut time by 30-40%! 🤖

---

## 📚 RESOURCES

```
╔════════════════════════════════════════════════════════════╗
║              📖 PLAYER'S STRATEGY GUIDE 📖              ║
╚════════════════════════════════════════════════════════════╝
```

### 🌟 Official Documentation
- 🍃 [Spring Boot 3.3 Reference](https://docs.spring.io/spring-boot/docs/3.3.x/reference/html/)
- 🗄️ [Spring Data JPA](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)
- 🔐 [Spring Security OAuth2](https://docs.spring.io/spring-security/reference/servlet/oauth2/index.html)
- 🎨 [Thymeleaf Docs](https://www.thymeleaf.org/documentation.html)
- ⚡ [htmx Documentation](https://htmx.org/docs/)
- ☁️ [Azure Container Apps](https://learn.microsoft.com/en-us/azure/container-apps/)

### 🎮 Migration Guides
- 📘 [Java EE to Spring Boot Migration](https://spring.io/guides/gs/spring-boot/)
- 🔄 [EJB to Spring Conversion](https://docs.spring.io/spring-framework/reference/integration/ejb.html)
- 🎨 [JSF to Thymeleaf Migration](https://www.thymeleaf.org/doc/articles/fromhtmltothymeleaf.html)

### 🤖 AI-Powered Tools
- 🚀 [GitHub Copilot CLI](https://githubnext.com/projects/copilot-cli/)
- 💬 [Copilot Chat in IDE](https://docs.github.com/en/copilot/using-github-copilot/asking-github-copilot-questions-in-your-ide)

---

<div align="center">

```
╔═══════════════════════════════════════════════════════════════╗
║                                                               ║
║    🎮 CONGRATULATIONS! YOU'VE COMPLETED THE QUEST! 🎮        ║
║                                                               ║
║         🏆 ACHIEVEMENTS UNLOCKED 🏆                          ║
║                                                               ║
║    ⭐ EJB Destroyer        ⭐ JPA Master                      ║
║    ⭐ UI Modernizer        ⭐ Cloud Native Champion           ║
║    ⭐ Security Guru        ⭐ Performance Optimizer           ║
║                                                               ║
║            🚀 READY FOR YOUR NEXT CHALLENGE? 🚀              ║
║                                                               ║
╚═══════════════════════════════════════════════════════════════╝
```

### 🌟 **HIGH SCORE**: Java EE ⟹ Spring Boot 3 **🌟**

**🪙 GAME OVER - PRESS START TO MODERNIZE AGAIN 🪙**

---

**Built with 💚 by the Azure App Modernization Team**

*© 2024 Microsoft Corporation. All rights reserved.*

</div>
