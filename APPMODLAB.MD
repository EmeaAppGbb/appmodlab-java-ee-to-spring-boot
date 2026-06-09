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
thumbnail: "https://raw.githubusercontent.com/EmeaAppGbb/appmodlab-java-ee-to-spring-boot/main/assets/thumbnail-gpt-image.png"
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
