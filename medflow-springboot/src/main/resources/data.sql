-- ============================================================
-- MedFlow Distributors - Seed Data
-- ============================================================

-- ============================================================
-- Suppliers
-- ============================================================

INSERT INTO suppliers (id, name, rating, created_at) VALUES
(1, 'PharmaCorp', 4.5, NOW()),
(2, 'MediSource', 4.2, NOW()),
(3, 'BioHealth', 4.8, NOW());

SELECT setval('suppliers_id_seq', (SELECT MAX(id) FROM suppliers));

-- ============================================================
-- Customers
-- ============================================================

INSERT INTO customers (id, name, type, credit_limit, created_at) VALUES
(1, 'St. Mary''s Hospital', 'Hospital', 500000, NOW()),
(2, 'Community Health Pharmacy', 'Pharmacy', 100000, NOW()),
(3, 'Metro General Hospital', 'Hospital', 750000, NOW()),
(4, 'HealthPlus Pharmacy Chain', 'Pharmacy', 250000, NOW()),
(5, 'Regional Medical Center', 'Hospital', 600000, NOW());

SELECT setval('customers_id_seq', (SELECT MAX(id) FROM customers));

-- ============================================================
-- Products
-- ============================================================

INSERT INTO products (id, sku, name, manufacturer, category, price, requires_cold_chain, dea_schedule, created_at) VALUES
(1, 'SKU001', 'Amoxicillin', 'PharmaCorp', 'Antibiotics', 25.50, false, 'N', NOW()),
(2, 'SKU002', 'Azithromycin', 'MediSource', 'Antibiotics', 35.75, false, 'N', NOW()),
(3, 'SKU003', 'Ibuprofen', 'PharmaCorp', 'Pain Reliever', 12.00, false, 'N', NOW()),
(4, 'SKU004', 'Acetaminophen', 'BioHealth', 'Pain Reliever', 10.50, false, 'N', NOW()),
(5, 'SKU005', 'Lisinopril', 'MediSource', 'Cardiovascular', 18.25, false, 'N', NOW()),
(6, 'SKU006', 'Atorvastatin', 'PharmaCorp', 'Cardiovascular', 22.00, false, 'N', NOW()),
(7, 'SKU007', 'Metformin', 'BioHealth', 'Endocrine', 15.75, false, 'N', NOW()),
(8, 'SKU008', 'Insulin Glargine', 'MediSource', 'Endocrine', 65.00, true, 'N', NOW()),
(9, 'SKU009', 'Oxycodone', 'PharmaCorp', 'Pain Reliever', 85.50, false, 'II', NOW()),
(10, 'SKU010', 'Cisplatin', 'BioHealth', 'Oncology', 150.00, false, 'N', NOW()),
(11, 'SKU011', 'Doxorubicin', 'MediSource', 'Oncology', 125.00, false, 'N', NOW()),
(12, 'SKU012', 'Flu Vaccine', 'PharmaCorp', 'Vaccines', 18.50, true, 'N', NOW()),
(13, 'SKU013', 'Pneumococcal Vaccine', 'BioHealth', 'Vaccines', 28.75, true, 'N', NOW()),
(14, 'SKU014', 'Levofloxacin', 'MediSource', 'Antibiotics', 32.00, false, 'N', NOW()),
(15, 'SKU015', 'Prednisone', 'PharmaCorp', 'Immunosuppressant', 12.25, false, 'N', NOW());

SELECT setval('products_id_seq', (SELECT MAX(id) FROM products));

-- ============================================================
-- Inventory (Stock Levels)
-- ============================================================

INSERT INTO inventory (id, product_id, warehouse_location, quantity, lot_number, expiry_date, created_at) VALUES
(1, 1, 'Warehouse A', 500, 'LOT-2024-001', '2026-01-15', NOW()),
(2, 2, 'Warehouse B', 300, 'LOT-2024-002', '2025-08-20', NOW()),
(3, 3, 'Warehouse A', 1000, 'LOT-2024-003', '2026-06-30', NOW()),
(4, 4, 'Warehouse B', 800, 'LOT-2024-004', '2026-03-15', NOW()),
(5, 5, 'Warehouse A', 450, 'LOT-2024-005', '2025-12-10', NOW()),
(6, 6, 'Warehouse B', 600, 'LOT-2024-006', '2026-09-20', NOW()),
(7, 7, 'Warehouse A', 700, 'LOT-2024-007', '2025-11-30', NOW()),
(8, 8, 'Cold Storage A', 150, 'LOT-2024-008', '2025-02-28', NOW()),
(9, 9, 'Secure Storage', 100, 'LOT-2024-009', '2026-05-15', NOW()),
(10, 10, 'Warehouse A', 50, 'LOT-2024-010', '2025-10-20', NOW()),
(11, 11, 'Warehouse B', 45, 'LOT-2024-011', '2025-09-15', NOW()),
(12, 12, 'Cold Storage A', 200, 'LOT-2024-012', '2025-01-31', NOW()),
(13, 13, 'Cold Storage B', 180, 'LOT-2024-013', '2025-02-15', NOW()),
(14, 14, 'Warehouse A', 400, 'LOT-2024-014', '2026-04-10', NOW());

SELECT setval('inventory_id_seq', (SELECT MAX(id) FROM inventory));

-- ============================================================
-- Lot Tracking
-- ============================================================

INSERT INTO lot_tracking (id, product_id, lot_number, quantity_remaining, expiry_date, status, created_at) VALUES
(1, 1, 'LOT-2024-001', 500, '2026-01-15', 'Active', NOW()),
(2, 2, 'LOT-2024-002', 280, '2025-08-20', 'Active', NOW()),
(3, 8, 'LOT-2024-008', 140, '2025-02-28', 'Expiring Soon', NOW()),
(4, 12, 'LOT-2024-012', 180, '2025-01-31', 'Expiring Soon', NOW()),
(5, 13, 'LOT-2024-013', 160, '2025-02-15', 'Expiring Soon', NOW());

SELECT setval('lot_tracking_id_seq', (SELECT MAX(id) FROM lot_tracking));

-- ============================================================
-- Orders
-- ============================================================

INSERT INTO orders (id, customer_id, order_date, status, priority, total, created_at) VALUES
(1, 1, '2024-01-15 09:30:00', 'SHIPPED', 'URGENT', 2500.00, NOW()),
(2, 2, '2024-01-16 11:20:00', 'CONFIRMED', 'STANDARD', 1850.50, NOW()),
(3, 3, '2024-01-17 14:45:00', 'PENDING', 'URGENT', 3500.75, NOW()),
(4, 4, '2024-01-18 10:15:00', 'SHIPPED', 'STANDARD', 950.25, NOW()),
(5, 5, '2024-01-19 13:30:00', 'CONFIRMED', 'STANDARD', 2200.00, NOW()),
(6, 1, '2024-01-20 08:45:00', 'PENDING', 'URGENT', 4100.50, NOW()),
(7, 2, '2024-01-21 15:20:00', 'SHIPPED', 'STANDARD', 1200.75, NOW()),
(8, 3, '2024-01-22 12:00:00', 'CONFIRMED', 'URGENT', 5500.00, NOW()),
(9, 4, '2024-01-23 09:30:00', 'PENDING', 'STANDARD', 750.50, NOW()),
(10, 5, '2024-01-24 16:10:00', 'SHIPPED', 'STANDARD', 1650.00, NOW());

SELECT setval('orders_id_seq', (SELECT MAX(id) FROM orders));

-- ============================================================
-- Order Items
-- ============================================================

INSERT INTO order_items (id, order_id, product_id, quantity, lot_number, unit_price, created_at) VALUES
(1, 1, 1, 100, 'LOT-2024-001', 25.50, NOW()),
(2, 1, 3, 50, 'LOT-2024-003', 12.00, NOW()),
(3, 2, 2, 75, 'LOT-2024-002', 35.75, NOW()),
(4, 2, 5, 25, 'LOT-2024-005', 18.25, NOW()),
(5, 3, 6, 100, 'LOT-2024-006', 22.00, NOW()),
(6, 4, 4, 90, 'LOT-2024-004', 10.50, NOW()),
(7, 5, 7, 80, 'LOT-2024-007', 15.75, NOW()),
(8, 6, 8, 20, 'LOT-2024-008', 65.00, NOW()),
(9, 7, 14, 50, 'LOT-2024-014', 32.00, NOW());

SELECT setval('order_items_id_seq', (SELECT MAX(id) FROM order_items));

-- ============================================================
-- Purchase Orders
-- ============================================================

INSERT INTO purchase_orders (id, supplier_id, order_date, status, total_amount, created_at) VALUES
(1, 1, '2024-01-10 10:00:00', 'RECEIVED', 15000.00, NOW()),
(2, 2, '2024-01-12 14:30:00', 'APPROVED', 18500.50, NOW()),
(3, 3, '2024-01-14 09:15:00', 'PENDING', 22000.00, NOW());

SELECT setval('purchase_orders_id_seq', (SELECT MAX(id) FROM purchase_orders));
