-- ============================================================
-- MedFlow Distributors - Seed Data
-- Table/column names match JPA entity mappings
-- ============================================================

-- Suppliers (table: supplier, cols: id, name, contact_email, lead_time_days, rating)
INSERT INTO supplier (id, name, rating) VALUES
(1, 'PharmaCorp', 4.5),
(2, 'MediSource', 4.2),
(3, 'BioHealth', 4.8)
ON CONFLICT (id) DO NOTHING;

-- Customers (table: customer, cols: id, name, type, license_number, address, credit_limit)
INSERT INTO customer (id, name, type, credit_limit) VALUES
(1, 'St. Mary''s Hospital', 'Hospital', 500000),
(2, 'Community Health Pharmacy', 'Pharmacy', 100000),
(3, 'Metro General Hospital', 'Hospital', 750000),
(4, 'HealthPlus Pharmacy Chain', 'Pharmacy', 250000),
(5, 'Regional Medical Center', 'Hospital', 600000)
ON CONFLICT (id) DO NOTHING;

-- Products (table: products, cols: id, sku, name, manufacturer, category, unit_price, requires_cold_chain, dea_schedule)
INSERT INTO products (id, sku, name, manufacturer, category, unit_price, requires_cold_chain, dea_schedule) VALUES
(1, 'SKU001', 'Amoxicillin', 'PharmaCorp', 'Antibiotics', 25.50, false, 'N'),
(2, 'SKU002', 'Azithromycin', 'MediSource', 'Antibiotics', 35.75, false, 'N'),
(3, 'SKU003', 'Ibuprofen', 'PharmaCorp', 'Pain Reliever', 12.00, false, 'N'),
(4, 'SKU004', 'Acetaminophen', 'BioHealth', 'Pain Reliever', 10.50, false, 'N'),
(5, 'SKU005', 'Lisinopril', 'MediSource', 'Cardiovascular', 18.25, false, 'N'),
(6, 'SKU006', 'Atorvastatin', 'PharmaCorp', 'Cardiovascular', 22.00, false, 'N'),
(7, 'SKU007', 'Metformin', 'BioHealth', 'Endocrine', 15.75, false, 'N'),
(8, 'SKU008', 'Insulin Glargine', 'MediSource', 'Endocrine', 65.00, true, 'N'),
(9, 'SKU009', 'Oxycodone', 'PharmaCorp', 'Pain Reliever', 85.50, false, 'II'),
(10, 'SKU010', 'Cisplatin', 'BioHealth', 'Oncology', 150.00, false, 'N'),
(11, 'SKU011', 'Doxorubicin', 'MediSource', 'Oncology', 125.00, false, 'N'),
(12, 'SKU012', 'Flu Vaccine', 'PharmaCorp', 'Vaccines', 18.50, true, 'N'),
(13, 'SKU013', 'Pneumococcal Vaccine', 'BioHealth', 'Vaccines', 28.75, true, 'N'),
(14, 'SKU014', 'Levofloxacin', 'MediSource', 'Antibiotics', 32.00, false, 'N'),
(15, 'SKU015', 'Prednisone', 'PharmaCorp', 'Immunosuppressant', 12.25, false, 'N')
ON CONFLICT (id) DO NOTHING;

-- Inventory (table: inventory, cols: id, product_id, warehouse_id, quantity, lot_number, expiry_date, received_date)
INSERT INTO inventory (id, product_id, warehouse_id, quantity, lot_number, expiry_date) VALUES
(1, 1, 1, 500, 'LOT-2024-001', '2026-01-15'),
(2, 2, 2, 300, 'LOT-2024-002', '2025-08-20'),
(3, 3, 1, 1000, 'LOT-2024-003', '2026-06-30'),
(4, 4, 2, 800, 'LOT-2024-004', '2026-03-15'),
(5, 5, 1, 450, 'LOT-2024-005', '2025-12-10'),
(6, 6, 2, 600, 'LOT-2024-006', '2026-09-20'),
(7, 7, 1, 700, 'LOT-2024-007', '2025-11-30'),
(8, 8, 3, 150, 'LOT-2024-008', '2025-02-28'),
(9, 9, 4, 100, 'LOT-2024-009', '2026-05-15'),
(10, 10, 1, 50, 'LOT-2024-010', '2025-10-20'),
(11, 11, 2, 45, 'LOT-2024-011', '2025-09-15'),
(12, 12, 3, 200, 'LOT-2024-012', '2025-01-31'),
(13, 13, 5, 180, 'LOT-2024-013', '2025-02-15'),
(14, 14, 1, 400, 'LOT-2024-014', '2026-04-10')
ON CONFLICT (id) DO NOTHING;

-- Lot Tracking (table: lot_tracking, cols: id, product_id, lot_number, manufacturer_date, expiry_date, quantity_received, quantity_distributed)
INSERT INTO lot_tracking (id, product_id, lot_number, quantity_received, quantity_distributed, expiry_date) VALUES
(1, 1, 'LOT-2024-001', 500, 0, '2026-01-15'),
(2, 2, 'LOT-2024-002', 300, 20, '2025-08-20'),
(3, 8, 'LOT-2024-008', 150, 10, '2025-02-28'),
(4, 12, 'LOT-2024-012', 200, 20, '2025-01-31'),
(5, 13, 'LOT-2024-013', 180, 20, '2025-02-15')
ON CONFLICT (id) DO NOTHING;

-- Orders (table: orders, cols: id, customer_id, order_date, status, total_amount, shipping_address, priority)
INSERT INTO orders (id, customer_id, order_date, status, priority, total_amount) VALUES
(1, 1, '2024-01-15 09:30:00', 'SHIPPED', 'URGENT', 2500.00),
(2, 2, '2024-01-16 11:20:00', 'CONFIRMED', 'STANDARD', 1850.50),
(3, 3, '2024-01-17 14:45:00', 'PENDING', 'URGENT', 3500.75),
(4, 4, '2024-01-18 10:15:00', 'SHIPPED', 'STANDARD', 950.25),
(5, 5, '2024-01-19 13:30:00', 'CONFIRMED', 'STANDARD', 2200.00),
(6, 1, '2024-01-20 08:45:00', 'PENDING', 'URGENT', 4100.50),
(7, 2, '2024-01-21 15:20:00', 'SHIPPED', 'STANDARD', 1200.75),
(8, 3, '2024-01-22 12:00:00', 'CONFIRMED', 'URGENT', 5500.00),
(9, 4, '2024-01-23 09:30:00', 'PENDING', 'STANDARD', 750.50),
(10, 5, '2024-01-24 16:10:00', 'SHIPPED', 'STANDARD', 1650.00)
ON CONFLICT (id) DO NOTHING;

-- Order Items (table: order_item, cols: id, order_id, product_id, quantity, lot_number, unit_price)
INSERT INTO order_item (id, order_id, product_id, quantity, lot_number, unit_price) VALUES
(1, 1, 1, 100, 'LOT-2024-001', 25.50),
(2, 1, 3, 50, 'LOT-2024-003', 12.00),
(3, 2, 2, 75, 'LOT-2024-002', 35.75),
(4, 2, 5, 25, 'LOT-2024-005', 18.25),
(5, 3, 6, 100, 'LOT-2024-006', 22.00),
(6, 4, 4, 90, 'LOT-2024-004', 10.50),
(7, 5, 7, 80, 'LOT-2024-007', 15.75),
(8, 6, 8, 20, 'LOT-2024-008', 65.00),
(9, 7, 14, 50, 'LOT-2024-014', 32.00)
ON CONFLICT (id) DO NOTHING;

-- Purchase Orders (table: purchase_order, cols: id, supplier_id, order_date, status, expected_delivery, total_amount)
INSERT INTO purchase_order (id, supplier_id, order_date, status, total_amount) VALUES
(1, 1, '2024-01-10 10:00:00', 'RECEIVED', 15000.00),
(2, 2, '2024-01-12 14:30:00', 'APPROVED', 18500.50),
(3, 3, '2024-01-14 09:15:00', 'PENDING', 22000.00)
ON CONFLICT (id) DO NOTHING;
