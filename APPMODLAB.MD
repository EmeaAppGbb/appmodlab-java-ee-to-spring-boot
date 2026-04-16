---
title: "Java EE to Spring Boot"
description: "Migrate Java EE apps to Spring Boot with cloud-native patterns and microservices"
authors: ["marconsilva"]
category: "Code Modernization"
industry: "Healthcare & Life Sciences"
services: ["Azure Container Apps", "Azure Database for PostgreSQL", "Azure Service Bus"]
languages: ["Java"]
frameworks: ["Spring Boot", "Spring Data JPA", "Thymeleaf"]
modernizationTools: []
agenticTools: []
tags: ["ejb-migration", "jpa", "spring-security", "containerization"]
extensions: ["github.copilot"]
thumbnail: ""
video: ""
version: "1.0.0"
---

## Overview

This lab migrates a legacy Java EE 7 application running on WildFly to Spring Boot with cloud-native patterns. The target application is MedFlow Distributors, a pharmaceutical distribution management system.

## Understanding the Legacy Application

The following sections show the legacy Java EE 7 application running on WildFly 26 with PostgreSQL, prior to migration to Spring Boot.

### Application Server

The WildFly 26.1.3 application server welcome page confirms the server is running and ready to serve the MedFlow Distributors application:

![WildFly Welcome Page](assets/screenshots/08-wildfly-welcome.png)

### Homepage & Navigation

The main landing page features navigation to all major sections (Dashboard, Product Search, Inventory, Create Order), four feature cards highlighting core capabilities, and a system information table showing the legacy stack: Java EE 7, WildFly 26, PostgreSQL, JMS (HornetQ), and Hibernate 5.x.

![MedFlow Distributors Homepage](assets/screenshots/01-homepage.png)

### Dashboard

The Dashboard page displays three statistics cards (Total Products, Pending Orders, Expiring Items) and a Recent Orders table. The database is empty in this initial state, showing "No orders found."

![Dashboard](assets/screenshots/02-dashboard.png)

### Core Business Features

The Product Search page provides a search form with Product Name/SKU text input and Category dropdown filter. Results area shows "No products found" in the empty database state:

![Product Search](assets/screenshots/03-product-search.png)

The Inventory Management page showcases six feature sections: Stock Level Monitoring, Lot Tracking, Expiry Monitoring, Cold Chain Management, Multi-Warehouse Support, and Supplier Management:

![Inventory Management](assets/screenshots/04-inventory-management.png)

The order creation wizard demonstrates complex JSF form handling with three phases — Customer Information, Add Products, and Review Order. This will be migrated to Thymeleaf + htmx:

![Order Wizard](assets/screenshots/05-order-wizard.png)

### REST API Endpoints

The JAX-RS REST API endpoints (`/api/products` and `/api/orders`) return JSON responses. These will be migrated from JAX-RS to Spring MVC `@RestController` annotations:

![REST API - Products](assets/screenshots/06-api-products.png)

![REST API - Orders](assets/screenshots/07-api-orders.png)

## Screenshots Reference

| Screenshot | Description |
|---|---|
| ![WildFly](assets/screenshots/08-wildfly-welcome.png) | WildFly application server |
| ![Homepage](assets/screenshots/01-homepage.png) | MedFlow Distributors homepage |
| ![Dashboard](assets/screenshots/02-dashboard.png) | Dashboard with statistics |
| ![Product Search](assets/screenshots/03-product-search.png) | Product search page |
| ![Inventory](assets/screenshots/04-inventory-management.png) | Inventory management |
| ![Order Wizard](assets/screenshots/05-order-wizard.png) | Multi-step order wizard |
| ![API Products](assets/screenshots/06-api-products.png) | Products REST API |
| ![API Orders](assets/screenshots/07-api-orders.png) | Orders REST API |

## CLI Walkthrough

This section documents the 9-step migration process performed using **GitHub Copilot CLI** (`gh copilot`). Each step is tagged in the repository on the `solution-final` branch so you can check out any point in the migration.

### Step 1 — Explore Legacy App

| | |
|---|---|
| **Tag** | `step-01-explore-legacy` |
| **What was done** | Analyzed the Java EE codebase structure — identified EJBs, JPA entities, JSF views, JAX-RS endpoints, and WildFly configuration to build a complete inventory of components that need migration. |
| **Command pattern** | `gh copilot explain "Analyze this Java EE 7 project structure and list all EJBs, entities, REST endpoints, and JSF views"` |

### Step 2 — Analyze Dependencies

| | |
|---|---|
| **Tag** | `step-02-analyze-deps` |
| **What was done** | Mapped every Java EE 7 dependency in `pom.xml` to its Spring Boot 3.2 equivalent — javax.* → jakarta.*, EJB → Spring managed beans, JAX-RS → Spring MVC, JSF → Thymeleaf. |
| **Command pattern** | `gh copilot explain "Map each Java EE dependency in pom.xml to its Spring Boot 3.2 equivalent"` |

### Step 3 — Generate Modernization Spec

| | |
|---|---|
| **Tag** | `step-03-modernization-spec` |
| **What was done** | Created a comprehensive migration blueprint covering every component, the target architecture, conversion rules, and the order of operations for the remaining steps. |
| **Command pattern** | `gh copilot explain "Generate a modernization specification to migrate this Java EE app to Spring Boot 3.2"` |

### Step 4 — Scaffold Spring Boot

| | |
|---|---|
| **Tag** | `step-04-scaffold-springboot` |
| **What was done** | Created the Spring Boot 3.2 project skeleton — `pom.xml` with spring-boot-starter-web, spring-boot-starter-data-jpa, spring-boot-starter-thymeleaf, `application.properties`, and the main `@SpringBootApplication` class. |
| **Command pattern** | `gh copilot suggest "Create a Spring Boot 3.2 project with Web, JPA, Thymeleaf, Security, and PostgreSQL starters"` |

### Step 5 — Migrate EJBs

| | |
|---|---|
| **Tag** | `step-05-migrate-ejbs` |
| **What was done** | Converted 5 EJBs (`@Stateless` / `@Singleton`) to Spring `@Service` components, migrated 8 JPA entities from `javax.persistence` to `jakarta.persistence`, and generated 8 Spring Data JPA repositories. |
| **Command pattern** | `gh copilot suggest "Convert this @Stateless EJB to a Spring @Service and update javax.persistence imports to jakarta.persistence"` |

### Step 6 — Migrate Views

| | |
|---|---|
| **Tag** | `step-06-migrate-views` |
| **What was done** | Converted 6 JSF/XHTML pages to Thymeleaf HTML templates and created 7 Spring MVC `@Controller` classes to replace JSF backing beans. |
| **Command pattern** | `gh copilot suggest "Convert this JSF XHTML page to a Thymeleaf template and create a Spring MVC controller"` |

### Step 7 — Data Access

| | |
|---|---|
| **Tag** | `step-07-data-access` |
| **What was done** | Configured Spring Data JPA repositories, JMS messaging (replacing HornetQ with Spring JMS), Spring Security setup, and database seed data (`data.sql`). |
| **Command pattern** | `gh copilot suggest "Configure Spring Data JPA, JMS messaging, and Spring Security for this application"` |

### Step 8 — Containerize

| | |
|---|---|
| **Tag** | `step-08-containerize` |
| **What was done** | Created a multi-stage `Dockerfile` (Maven build → slim JRE runtime image) and a `docker-compose.yml` with PostgreSQL and ActiveMQ Artemis services. |
| **Command pattern** | `gh copilot suggest "Create a multi-stage Dockerfile and docker-compose.yml for a Spring Boot app with PostgreSQL and ActiveMQ"` |

### Step 9 — Build & Test

| | |
|---|---|
| **Tag** | `step-09-build-test` |
| **What was done** | Ran `mvn clean package` — **BUILD SUCCESS**. Started the containerized stack with `docker compose up` — application running on port **8080**. Verified the REST API returns **15 products** at `/api/products`. |
| **Command pattern** | `gh copilot suggest "Build and test this Spring Boot application with Maven and Docker Compose"` |

### Technology Migration Summary

| Area | Legacy (Before) | Modern (After) |
|---|---|---|
| **Framework** | Java EE 7 | Spring Boot 3.2 |
| **App Server** | WildFly 26 | Embedded Tomcat |
| **API Layer** | JAX-RS (`@Path`) | Spring MVC (`@RestController`) |
| **Business Logic** | EJB (`@Stateless` / `@Singleton`) | Spring `@Service` |
| **Persistence** | JPA + `javax.persistence` | Spring Data JPA + `jakarta.persistence` |
| **Views** | JSF / XHTML | Thymeleaf HTML |
| **Security** | JAAS / WildFly Realm | Spring Security |
| **Messaging** | HornetQ (JMS) | Spring JMS + ActiveMQ Artemis |
| **Packaging** | WAR deployed to WildFly | Executable JAR in Docker container |
| **Database** | PostgreSQL | PostgreSQL (unchanged) |
| **Branch** | `main` | `solution-final` |
