CREATE DATABASE IF NOT EXISTS online_retail_db;
USE online_retail_db;

CREATE TABLE Products (
    product_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    category VARCHAR(100) NOT NULL,
    price DECIMAL(10,2) NOT NULL CHECK (price > 0)
);

CREATE TABLE Customers (
    customer_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    city VARCHAR(100) NOT NULL
);

CREATE TABLE Orders (
    order_id INT PRIMARY KEY AUTO_INCREMENT,
    customer_id INT NOT NULL,
    order_date DATE NOT NULL,
    FOREIGN KEY (customer_id) REFERENCES Customers(customer_id)
);

CREATE TABLE Order_Items (
    order_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL CHECK (quantity > 0),
    PRIMARY KEY (order_id, product_id),
    FOREIGN KEY (product_id) REFERENCES Products(product_id),
    FOREIGN KEY (order_id) REFERENCES Orders(order_id)
);

INSERT INTO Customers (name, city) VALUES
('Arjun Reddy', 'Vijayawada'),
('Sneha Menon', 'Kochi'),
('Rahul Khanna', 'Jaipur'),
('Isha Kapoor', 'Indore'),
('Varun Das', 'Nagpur');

INSERT INTO Products (name, category, price) VALUES
('Tablet', 'Electronics', 30000),
('Keyboard', 'Electronics', 1200),
('Study Chair', 'Furniture', 5000),
('Diary', 'Stationery', 80),
('Marker Set', 'Stationery', 200),
('Smart Watch', 'Electronics', 15000),
('Work Desk', 'Furniture', 9000);

INSERT INTO Orders (customer_id, order_date) VALUES
(2, '2025-01-12'),
(1, '2025-01-18'),
(3, '2025-02-07'),
(1, '2025-02-20'),
(5, '2025-03-10'),
(4, '2025-03-22'),
(2, '2025-04-03');

INSERT INTO Order_Items (order_id, product_id, quantity) VALUES
(1, 2, 1),
(1, 4, 3),
(2, 1, 1),
(2, 5, 2),
(3, 6, 1),
(3, 2, 2),
(4, 3, 1),
(4, 4, 6),
(5, 7, 1),
(5, 5, 2),
(6, 1, 1),
(6, 4, 1),
(7, 6, 1),
(7, 2, 2);