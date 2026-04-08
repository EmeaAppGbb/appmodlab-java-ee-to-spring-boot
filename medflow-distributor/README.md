# MedFlow Distributors - Legacy Java EE Application

A Java EE 7 pharmaceutical distribution system for the "Java EE to Spring Boot" modernization lab.

## Business Domain
Wholesale pharmaceutical distribution and inventory management for "MedFlow Distributors".

## Technology Stack
- **Java EE 7** (EJB 3.2, JPA 2.1, JAX-RS 2.0, JSF 2.3, JMS 2.0)
- **WildFly 26** Application Server
- **PostgreSQL 14** Database
- **Apache ActiveMQ Artemis** for JMS messaging
- **Maven** build tool

## Architecture Highlights
This is a legacy application demonstrating typical Java EE patterns that need modernization:

### Legacy Patterns Used:
- **@Stateless EJBs** for all business logic with container-managed transactions
- **@Singleton EJB** for caching (CacheManagerBean) instead of modern cache libraries
- **JPA @NamedQueries** hard-coded in entity annotations (difficult to maintain)
- **JSF managed beans** with @ViewScoped and @ConversationScoped
- **@MessageDriven bean** for async JMS message processing
- **WildFly-specific JNDI** datasource: `java:jboss/datasources/MedFlowDS`
- **persistence.xml** with container-managed EntityManager
- **WAR deployment** to external application server

## Project Structure
```
medflow-distributor/
├── src/main/java/com/medflow/
│   ├── ejb/              # Stateless/Singleton EJBs for business logic
│   ├── entity/           # JPA entities with @NamedQueries
│   ├── rest/             # JAX-RS REST endpoints
│   ├── jms/              # JMS producer and @MessageDriven consumer
│   ├── jsf/              # JSF managed beans
│   └── security/         # JAAS security configuration
├── src/main/resources/
│   ├── META-INF/
│   │   ├── persistence.xml    # JPA configuration
│   │   └── ejb-jar.xml        # EJB deployment descriptor
│   └── import.sql             # Seed data
├── src/main/webapp/
│   ├── WEB-INF/
│   │   ├── web.xml            # Servlet 3.1 descriptor
│   │   ├── faces-config.xml   # JSF configuration
│   │   └── beans.xml          # CDI configuration
│   └── pages/                 # JSF XHTML pages
└── docker-compose.yml         # WildFly + PostgreSQL + ActiveMQ
```

## Building the Application

```bash
mvn clean package
```

This creates `target/medflow-distributor.war`

## Running with Docker Compose

```bash
docker-compose up --build
```

This starts:
- PostgreSQL on port 5432
- ActiveMQ Artemis on ports 8161 (console) and 61616 (broker)
- WildFly 26 on ports 8080 (app) and 9990 (management)

## Accessing the Application

- **Application**: http://localhost:8080/medflow-distributor
- **REST API**: http://localhost:8080/medflow-distributor/api
  - GET /api/products
  - GET /api/orders
  - GET /api/inventory/product/{id}
- **WildFly Console**: http://localhost:9990 (admin/admin123)
- **ActiveMQ Console**: http://localhost:8161 (admin/admin)

## Database Schema

**Products**: pharmaceutical products with SKU, pricing, cold chain requirements
**Inventory**: stock levels by warehouse and lot number
**Orders**: customer orders with line items
**OrderItems**: order line items with lot tracking
**PurchaseOrders**: supplier purchase orders
**LotTracking**: regulatory lot/batch tracking
**Customers**: hospitals and pharmacies
**Suppliers**: product suppliers

Seed data includes 15 products, 5 customers, 3 suppliers, and 10 sample orders.

## Key Features Demonstrating Legacy Patterns

1. **EJB Container-Managed Transactions**: All business logic uses @Stateless EJBs
2. **@Singleton Cache**: Product caching in CacheManagerBean (not using Redis/Hazelcast)
3. **JPA Named Queries**: Hard-coded JPQL in @NamedQuery annotations
4. **JMS MessageDriven Bean**: OrderMessageConsumer for async order processing
5. **JSF Conversation Scope**: OrderWizardBean uses @ConversationScoped for multi-step wizard
6. **JNDI DataSource**: `java:jboss/datasources/MedFlowDS` in persistence.xml
7. **WAR Deployment**: External application server required

## Modernization Challenges

This application exhibits typical challenges when migrating from Java EE to Spring Boot:

- EJB dependencies and container-managed services
- JNDI lookups for resources
- Application server-specific configurations
- JMS instead of modern messaging
- Monolithic WAR deployment
- JSF for UI (consider REST + modern frontend)
- Hard-coded named queries
- No embedded server option

## License

Sample code for educational purposes.
