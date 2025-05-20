--liquibase formatted sql

--changeset Bashir:1
CREATE TABLE attributes
(
    id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NULL
);

--changeset Bashir:2
CREATE TABLE brands
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    name       VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT UK_brand_name UNIQUE (name)
);

--changeset Bashir:3
CREATE TABLE categories
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    name       VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT UK_category_name UNIQUE (name)
);

--changeset Bashir:4
CREATE TABLE roles
(
    id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NULL UNIQUE
);

--changeset Bashir:5
CREATE TABLE suppliers
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    address       VARCHAR(255) NULL,
    contact_email VARCHAR(255) NOT NULL,
    name          VARCHAR(255) NOT NULL,
    phone         VARCHAR(255) NULL,
    CONSTRAINT UK_supplier_name_email UNIQUE (name, contact_email)
);

--changeset Bashir:6
CREATE TABLE products
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    cost_price    DECIMAL(10, 2) NULL,
    description   TEXT           NULL,
    name          VARCHAR(255)   NULL,
    selling_price DECIMAL(10, 2) NULL,
    sku           VARCHAR(255)   NULL,
    brand_id      BIGINT         NOT NULL,
    category_id   BIGINT         NOT NULL,
    supplier_id   BIGINT         NULL,
    product_status  ENUM('ACTIVE', 'INACTIVE', 'DELETED','DISCONNECTED') NOT NULL DEFAULT 'INACTIVE',
    CONSTRAINT UK_product_sku UNIQUE (sku),
    CONSTRAINT FK_product_supplier FOREIGN KEY (supplier_id) REFERENCES suppliers (id),
    CONSTRAINT FK_product_brand FOREIGN KEY (brand_id) REFERENCES brands (id),
    CONSTRAINT FK_product_category FOREIGN KEY (category_id) REFERENCES categories (id)
);

--changeset Bashir:7
CREATE TABLE images
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    alt_text   VARCHAR(255) NULL,
    image_url  VARCHAR(255) NULL,
    product_id BIGINT       NULL,
    CONSTRAINT FK_image_product FOREIGN KEY (product_id) REFERENCES products (id)
);

--changeset Bashir:8
CREATE TABLE product_attributes
(
    attribute_value VARCHAR(255) NULL,
    product_id      BIGINT       NOT NULL,
    attribute_id    BIGINT       NOT NULL,
    PRIMARY KEY (attribute_id, product_id),
    CONSTRAINT FK_product_attribute FOREIGN KEY (attribute_id) REFERENCES attributes (id),
    CONSTRAINT FK_attribute_product FOREIGN KEY (product_id) REFERENCES products (id)
);

--changeset Bashir:9
CREATE TABLE stock_movements
(
    id                    BIGINT AUTO_INCREMENT PRIMARY KEY,
    created_at            TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at            TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    movement_type         ENUM ('IN', 'OUT', 'RETURN', 'TRANSFER', 'DAMAGED')                                    NOT NULL,
    product_id            BIGINT                                                                                 NULL,
    quantity              INT                                                                                    NOT NULL CHECK (quantity >= 0),
    reason                ENUM ('CREATED', 'DAMAGED', 'RETURNED', 'TRANSFERRED', 'RECEIVED_TRANSFER', 'UPDATED') NOT NULL,
    warehouse_id          BIGINT                                                                                 NOT NULL,
    username              VARCHAR(255)                                                                           NULL,
    product_name_snapshot VARCHAR(255),
    product_deleted       BOOLEAN   DEFAULT FALSE,
    CONSTRAINT FK_stockmovement_product FOREIGN KEY (product_id) REFERENCES products (id) ON DELETE SET NULL
);

--changeset Bashir:10
CREATE TABLE users
(
    id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    email    VARCHAR(255) NOT NULL,
    name     VARCHAR(255) NULL,
    password VARCHAR(255) NOT NULL,
    username VARCHAR(255) NOT NULL,
    CONSTRAINT UK_user_email UNIQUE (email),
    CONSTRAINT UK_user_username UNIQUE (username)
);

--changeset Bashir:11
CREATE TABLE user_roles
(
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT FK_user_roles_role FOREIGN KEY (role_id) REFERENCES roles (id),
    CONSTRAINT FK_user_roles_user FOREIGN KEY (user_id) REFERENCES users (id)
);

--changeset Bashir:12
CREATE TABLE warehouses
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    address    VARCHAR(255) NULL,
    name       VARCHAR(255) NULL
);

--changeset Bashir:13
CREATE TABLE stock
(
    product_id   BIGINT NOT NULL,
    warehouse_id BIGINT NOT NULL,
    quantity     INT    NOT NULL CHECK (quantity >= 0),
    PRIMARY KEY (product_id, warehouse_id),
    CONSTRAINT FK_stock_product FOREIGN KEY (product_id) REFERENCES products (id),
    CONSTRAINT FK_stock_warehouse FOREIGN KEY (warehouse_id) REFERENCES warehouses (id)
);

--changeset Bashir:14
CREATE TABLE purchases
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    created_at  TIMESTAMP            DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP            DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    supplier_id BIGINT      NOT NULL,
    status      VARCHAR(20) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'COMPLETED', 'CANCELLED')),
    CONSTRAINT FK_purchase_supplier FOREIGN KEY (supplier_id) REFERENCES suppliers (id)
);

--changeset Bashir:15
CREATE TABLE purchase_items
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    purchase_id  BIGINT         NOT NULL,
    product_id   BIGINT         NOT NULL,
    warehouse_id BIGINT         NOT NULL,
    quantity     INT            NOT NULL CHECK (quantity >= 0),
    unit_price   DECIMAL(10, 2) NOT NULL CHECK (unit_price >= 0),
    CONSTRAINT FK_item_purchase FOREIGN KEY (purchase_id) REFERENCES purchases (id) ON DELETE CASCADE,
    CONSTRAINT FK_item_product FOREIGN KEY (product_id) REFERENCES products (id),
    CONSTRAINT FK_item_warehouse FOREIGN KEY (warehouse_id) REFERENCES warehouses (id)
);

--changeset Bashir:16
CREATE INDEX idx_purchase_supplier ON purchases (supplier_id);
CREATE INDEX idx_item_product ON purchase_items (product_id);
