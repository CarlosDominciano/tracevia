DROP DATABASE tracevia;
CREATE DATABASE tracevia;
USE tracevia;

CREATE TABLE tb_products (
	id INT AUTO_INCREMENT PRIMARY KEY NOT NULL,
    name VARCHAR(50) NOT NULL,
    price DECIMAL(10, 2),
    description TEXT,
    image LONGBLOB
);

SELECT * FROM tb_products;