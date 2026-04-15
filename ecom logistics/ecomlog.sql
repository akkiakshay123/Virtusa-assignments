CREATE DATABASE IF NOT EXISTS swiftship_db;
USE swiftship_db;

CREATE TABLE Partners (
    partner_id INT PRIMARY KEY AUTO_INCREMENT,
    partner_name VARCHAR(100) NOT NULL,
    contact_person VARCHAR(100),
    phone VARCHAR(20)
);

CREATE TABLE Shipments (
    shipment_id INT PRIMARY KEY AUTO_INCREMENT,
    partner_id INT NOT NULL,
    customer_name VARCHAR(100) NOT NULL,
    destination_city VARCHAR(100) NOT NULL,
    zone VARCHAR(50),
    promised_date DATE NOT NULL,
    actual_delivery_date DATE,
    status VARCHAR(20) NOT NULL,
    FOREIGN KEY (partner_id) REFERENCES Partners(partner_id)
);

CREATE TABLE DeliveryLogs (
    log_id INT PRIMARY KEY AUTO_INCREMENT,
    shipment_id INT NOT NULL,
    log_date DATE NOT NULL,
    event_status VARCHAR(30) NOT NULL,
    remarks VARCHAR(255),
    FOREIGN KEY (shipment_id) REFERENCES Shipments(shipment_id)
);

-- Changed partner names
INSERT INTO Partners (partner_name, contact_person, phone) VALUES
('RapidGo Transport', 'Suresh Reddy', '9123456780'),
('SpeedX Logistics', 'Priya Nair', '9123456781'),
('TrustLine Couriers', 'Karthik Iyer', '9123456782');

-- Changed customer names & cities
INSERT INTO Shipments (partner_id, customer_name, destination_city, zone, promised_date, actual_delivery_date, status) VALUES
(1, 'Manoj Verma', 'Pune', 'West', '2026-04-01', '2026-04-03', 'Delivered'),
(1, 'Divya Menon', 'Kolkata', 'East', '2026-04-02', '2026-04-05', 'Delivered'),
(2, 'Rohan Gupta', 'Jaipur', 'North', '2026-04-01', NULL, 'In Transit'),
(2, 'Simran Kaur', 'Ahmedabad', 'West', '2026-03-28', '2026-04-02', 'Returned'),
(3, 'Deepak Yadav', 'Coimbatore', 'South', '2026-03-25', '2026-03-26', 'Delivered'),
(3, 'Lakshmi Pillai', 'Vijayawada', 'South', '2026-04-05', '2026-04-09', 'Delivered'),
(1, 'Nikhil Joshi', 'Surat', 'West', '2026-04-10', '2026-04-12', 'Delivered'),
(2, 'Ayesha Khan', 'Lucknow', 'North', '2026-04-09', '2026-04-13', 'Delivered');

INSERT INTO DeliveryLogs (shipment_id, log_date, event_status, remarks) VALUES
(1, '2026-04-01', 'Picked Up', 'Collected from hub'),
(1, '2026-04-03', 'Delivered', 'Reached customer'),
(2, '2026-04-02', 'Picked Up', 'Shipment collected'),
(2, '2026-04-05', 'Delivered', 'Delivered with delay'),
(4, '2026-03-28', 'Picked Up', 'Pickup completed'),
(4, '2026-04-02', 'Returned', 'Address issue'),
(5, '2026-03-25', 'Delivered', 'On-time'),
(6, '2026-04-05', 'Picked Up', 'Shipment ready'),
(6, '2026-04-09', 'Delivered', 'Delayed delivery'),
(7, '2026-04-10', 'Picked Up', 'Collected'),
(7, '2026-04-12', 'Delivered', 'Successfully delivered'),
(8, '2026-04-09', 'Picked Up', 'Collected'),
(8, '2026-04-13', 'Delivered', 'Delivered properly');

-- 🔄 Query Order Changed

-- 1. Partner Performance Summary
SELECT
    p.partner_id,
    p.partner_name,
    COUNT(*) AS total_shipments,
    SUM(CASE
            WHEN s.actual_delivery_date IS NOT NULL
             AND s.actual_delivery_date > s.promised_date THEN 1
            ELSE 0
        END) AS delayed_shipments,
    SUM(CASE WHEN s.status = 'Delivered' THEN 1 ELSE 0 END) AS successful_deliveries,
    SUM(CASE WHEN s.status = 'Returned' THEN 1 ELSE 0 END) AS returned_deliveries,
    ROUND(
        (SUM(CASE WHEN s.status = 'Delivered' THEN 1 ELSE 0 END) * 100.0) / NULLIF(COUNT(*), 0),
        2
    ) AS success_rate_percent
FROM Partners p
LEFT JOIN Shipments s ON p.partner_id = s.partner_id
GROUP BY p.partner_id, p.partner_name
ORDER BY delayed_shipments ASC, success_rate_percent DESC;

-- 2. Most Active Destination City
SELECT
    s.destination_city,
    COUNT(*) AS shipment_count
FROM Shipments s
WHERE s.promised_date >= DATE_SUB(CURDATE(), INTERVAL 30 DAY)
GROUP BY s.destination_city
ORDER BY shipment_count DESC
LIMIT 1;

-- 3. Late Deliveries Analysis
SELECT
    s.shipment_id,
    p.partner_name,
    s.customer_name,
    s.destination_city,
    s.promised_date,
    s.actual_delivery_date,
    DATEDIFF(s.actual_delivery_date, s.promised_date) AS delay_days
FROM Shipments s
JOIN Partners p ON s.partner_id = p.partner_id
WHERE s.actual_delivery_date IS NOT NULL
  AND s.actual_delivery_date > s.promised_date
ORDER BY delay_days DESC;

-- 4. Partner Success Rate
SELECT
    p.partner_id,
    p.partner_name,
    SUM(CASE WHEN s.status = 'Delivered' THEN 1 ELSE 0 END) AS successful_deliveries,
    SUM(CASE WHEN s.status = 'Returned' THEN 1 ELSE 0 END) AS returned_deliveries,
    COUNT(s.shipment_id) AS total_shipments,
    ROUND(
        (SUM(CASE WHEN s.status = 'Delivered' THEN 1 ELSE 0 END) * 100.0) / COUNT(s.shipment_id),
        2
    ) AS success_rate_percent
FROM Partners p
LEFT JOIN Shipments s ON p.partner_id = s.partner_id
GROUP BY p.partner_id, p.partner_name
ORDER BY success_rate_percent DESC;