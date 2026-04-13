CREATE DATABASE lab7db;
GO

USE lab7db;
GO

CREATE TABLE category (
    id      VARCHAR(50)  NOT NULL PRIMARY KEY,
    name    nvarchar(100) NOT NULL
);

CREATE TABLE product (
    id          INT IDENTITY(1,1) PRIMARY KEY,
    name        nvarchar(150) NOT NULL,
    price       FLOAT        NOT NULL,
    create_date DATE,
    category_id VARCHAR(50)  NOT NULL,
    FOREIGN KEY (category_id) REFERENCES category(id)
);

SELECT * FROM category;
SELECT * FROM product;

INSERT INTO category (id, name) VALUES ('DIEN-TU',    N'Điện tử');
INSERT INTO category (id, name) VALUES ('THOI-TRANG', N'Thời trang');
INSERT INTO category (id, name) VALUES ('THUC-PHAM',  N'Thực phẩm');

-- Thêm Sản phẩm
INSERT INTO product (name, price, create_date, category_id)
VALUES (N'iPhone 15 Pro Max', 34990000, '2024-01-10', 'DIEN-TU');

INSERT INTO product (name, price, create_date, category_id)
VALUES (N'Samsung Galaxy S24', 22990000, '2024-02-01', 'DIEN-TU');

INSERT INTO product (name, price, create_date, category_id)
VALUES (N'Áo Polo Nam', 450000, '2024-02-14', 'THOI-TRANG');

SELECT * FROM category;
SELECT p.*, c.name AS category_name
FROM product p
JOIN category c ON p.category_id = c.id;