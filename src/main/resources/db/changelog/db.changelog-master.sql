--liquibase formatted sql

--changeset Bashir:1
--comment: 'Create attributes table to store product attributes like color, size, etc.'
CREATE TABLE attributes
(
    id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NULL
);

--changeset Bashir:2
--comment: 'Create brands table to store product brand information'
CREATE TABLE brands
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    name       VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT UK_brand_name UNIQUE (name)
);

--changeset Bashir:3
--comment: 'Create categories table to classify products into different groups'
CREATE TABLE categories
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    name       VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT UK_category_name UNIQUE (name)
);

--changeset Bashir:4
--comment: 'Create roles table for user and customer roles'
CREATE TABLE roles
(
    id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NULL UNIQUE
);

--changeset Bashir:5
--comment: 'Create suppliers table to store supplier details'
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
--comment: 'Create products table to store product information and references to brand, category, and supplier'
CREATE TABLE products
(
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    created_at     TIMESTAMP                                                      DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP                                                      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    cost_price     DECIMAL(10, 2)                                        NULL,
    description    TEXT                                                  NULL,
    name           VARCHAR(255)                                          NULL,
    selling_price  DECIMAL(10, 2)                                        NULL,
    sku            VARCHAR(255)                                          NULL,
    brand_id       BIGINT                                                NOT NULL,
    category_id    BIGINT                                                NOT NULL,
    supplier_id    BIGINT                                                NULL,
    product_status ENUM ('ACTIVE', 'INACTIVE', 'DELETED','DISCONNECTED') NOT NULL DEFAULT 'INACTIVE',
    CONSTRAINT UK_product_sku UNIQUE (sku),
    CONSTRAINT FK_product_supplier FOREIGN KEY (supplier_id) REFERENCES suppliers (id),
    CONSTRAINT FK_product_brand FOREIGN KEY (brand_id) REFERENCES brands (id),
    CONSTRAINT FK_product_category FOREIGN KEY (category_id) REFERENCES categories (id)
);

--changeset Bashir:7
--comment: 'Create images table to store product image URLs'
CREATE TABLE images
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    alt_text   VARCHAR(255) NULL,
    image_url  VARCHAR(255) NULL,
    product_id BIGINT       NULL,
    CONSTRAINT FK_image_product FOREIGN KEY (product_id) REFERENCES products (id)
);

--changeset Bashir:8
--comment: 'Create product_attributes table to map products to their attributes'
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
--comment: 'Create stock_movements table to track inventory movements'
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
--comment: 'Create users table to store application user details'
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
--comment: 'Create user_roles table to map users to their roles'
CREATE TABLE user_roles
(
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT FK_user_roles_role FOREIGN KEY (role_id) REFERENCES roles (id),
    CONSTRAINT FK_user_roles_user FOREIGN KEY (user_id) REFERENCES users (id)
);

--changeset Bashir:12
--comment: 'Create warehouses table to store warehouse locations'
CREATE TABLE warehouses
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    address    VARCHAR(255) NULL,
    name       VARCHAR(255) NULL
);

--changeset Bashir:13
--comment: 'Create stock table to track stock quantity per product and warehouse'
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
--comment: 'Create purchases table to store purchase orders from suppliers'
CREATE TABLE purchases
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    created_at  TIMESTAMP            DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP            DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    supplier_id BIGINT      NOT NULL,
    status      VARCHAR(20) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'COMPLETED', 'CANCELLED')),
    CONSTRAINT FK_purchase_supplier FOREIGN KEY (supplier_id) REFERENCES suppliers (id)
);


-- changeset Bashir:15
--comment: 'Create purchase_items table to store purchased product details'
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

-- changeset Bashir:16
--comment: 'Create indexes for purchase and purchase_items tables'
CREATE INDEX idx_purchase_supplier ON purchases (supplier_id);
CREATE INDEX idx_item_product ON purchase_items (product_id);

-- changeset Bashir:17
--comment: 'Add active flag to users table'
ALTER TABLE users
    ADD active BOOLEAN NOT NULL DEFAULT FALSE;

-- changeset Bashir:18
--comment: 'Create password_reset_token_users table for user password recovery'
CREATE TABLE password_reset_token_users
(
    id      BIGINT AUTO_INCREMENT PRIMARY KEY,
    token   VARCHAR(512) NOT NULL,
    user_id BIGINT       NOT NULL,
    used    BOOLEAN DEFAULT FALSE,
    CONSTRAINT fk_user_password_reset FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

-- changeset Bashir:19
--comment: 'Create error_logs table to log system errors'
CREATE TABLE error_logs
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    timestamp   VARCHAR(25) NOT NULL,
    status      INT,
    error       VARCHAR(255),
    message     TEXT,
    path        VARCHAR(512),
    stack_trace TEXT,
    resolved    BOOLEAN DEFAULT FALSE,
    resolved_at TIMESTAMP   NULL
);

-- changeset Bashir:20
--comment: 'Create customers table to store customer details'
CREATE TABLE customers
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    email      VARCHAR(255) NOT NULL UNIQUE,
    name       VARCHAR(255),
    password   VARCHAR(255) NOT NULL,
    phone      VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- changeset Bashir:21
--comment: 'Create password_reset_token_customers table for customer password recovery'
CREATE TABLE password_reset_token_customers
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    token       VARCHAR(512) NOT NULL,
    customer_id BIGINT       NOT NULL,
    used        BOOLEAN DEFAULT FALSE,
    CONSTRAINT fk_customer_password_reset FOREIGN KEY (customer_id) REFERENCES customers (id) ON DELETE CASCADE
);

-- changeset Bashir:22
--comment: 'Create customer_roles table to map customers to roles'
CREATE TABLE customer_roles
(
    customer_id BIGINT NOT NULL,
    role_id     BIGINT NOT NULL,
    PRIMARY KEY (customer_id, role_id),
    CONSTRAINT FK_customer_roles_role FOREIGN KEY (role_id) REFERENCES roles (id),
    CONSTRAINT FK_customer_roles_customer FOREIGN KEY (customer_id) REFERENCES customers (id)
);

-- changeset Bashir:23
--comment: 'Create cart table for shopping cart functionality'
CREATE TABLE cart
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id BIGINT,
    session_id  VARCHAR(255),
    status      ENUM ('ACTIVE', 'CONVERTED', 'EXPIRED') DEFAULT 'ACTIVE',
    created_at  TIMESTAMP                               DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP                               DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT FK_cart_customer FOREIGN KEY (customer_id) REFERENCES customers (id) ON DELETE SET NULL
);

-- changeset Bashir:24
--comment: 'Create cart_items table to store items in carts'
CREATE TABLE cart_items
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    cart_id    BIGINT         NOT NULL,
    product_id BIGINT         NOT NULL,
    quantity   INT            NOT NULL CHECK (quantity > 0),
    unit_price DECIMAL(10, 2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY unique_cart_product (cart_id, product_id),
    CONSTRAINT FK_cart_item_cart FOREIGN KEY (cart_id) REFERENCES cart (id) ON DELETE CASCADE,
    CONSTRAINT FK_cart_item_product FOREIGN KEY (product_id) REFERENCES products (id)
);

-- changeset Bashir:25
--comment: 'Create indexes for cart and cart_items tables'
CREATE INDEX idx_cart_customer_id ON cart (customer_id);
CREATE INDEX idx_cart_session_id ON cart (session_id);
CREATE INDEX idx_cart_items_cart_id ON cart_items (cart_id);
CREATE INDEX idx_cart_items_product_id ON cart_items (product_id);

-- changeset Bashir:26
--comment: 'Create wishlist table for customer wishlists'
CREATE TABLE wishlist
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id BIGINT,
    session_id  VARCHAR(255),
    status      ENUM ('ACTIVE', 'CONVERTED', 'EXPIRED') DEFAULT 'ACTIVE',
    created_at  TIMESTAMP                               DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP                               DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT FK_wishlist_customer FOREIGN KEY (customer_id) REFERENCES customers (id) ON DELETE SET NULL
);

-- changeset Bashir:27
--comment: 'Create wishlist_items table to store items in wishlists CREATE TABLE wishlist_items'
CREATE TABLE wishlist_items
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    wishlist_id BIGINT NOT NULL,
    product_id  BIGINT NOT NULL,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY unique_wishlist_product (wishlist_id, product_id),
    CONSTRAINT FK_wishlist_item_wishlist FOREIGN KEY (wishlist_id) REFERENCES wishlist (id) ON DELETE CASCADE,
    CONSTRAINT FK_wishlist_item_product FOREIGN KEY (product_id) REFERENCES products (id)
);

-- changeset Bashir:28
--comment: 'Create indexes for wishlist and wishlist_items tables'
CREATE INDEX idx_wishlist_customer_id ON wishlist (customer_id);
CREATE INDEX idx_wishlist_session_id ON wishlist (session_id);
CREATE INDEX idx_wishlist_items_wishlist_id ON wishlist_items (wishlist_id);
CREATE INDEX idx_wishlist_items_product_id ON wishlist_items (product_id);

-- changeset Bashir:29
--comment: 'Create customer_addresses table to store customer address details'
CREATE TABLE customer_addresses
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id  BIGINT                                     NOT NULL,
    address_line VARCHAR(255),
    city         VARCHAR(255),
    state        VARCHAR(255),
    postal_code  VARCHAR(50),
    country      VARCHAR(100),
    address_type ENUM ('SHIPPING', 'BILLING') DEFAULT 'BILLING',
    is_default   BOOLEAN                      DEFAULT FALSE NOT NULL,
    is_deleted   BOOLEAN                      DEFAULT FALSE NOT NULL,
    created_at   TIMESTAMP                    DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP                    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_customer_addresses_customer FOREIGN KEY (customer_id) REFERENCES customers (id) ON DELETE CASCADE
);
CREATE INDEX idx_customer_addresses_customer_id ON customer_addresses (customer_id);

-- changeset Bashir:30
--comment: 'Create orders table to store customer orders'
CREATE TABLE orders
(
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id         BIGINT,
    cart_id             BIGINT,
    status              ENUM ('PENDING', 'PROCESSING', 'SHIPPED', 'DELIVERED', 'CANCELLED') NOT NULL DEFAULT 'PENDING',
    total_amount        DECIMAL(19, 2)                                                      NOT NULL DEFAULT 0.00,
    shipping_address_id BIGINT,
    billing_address_id  BIGINT,
    created_at          TIMESTAMP                                                                    DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP                                                                    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_orders_customer FOREIGN KEY (customer_id) REFERENCES customers (id) ON DELETE CASCADE,
    CONSTRAINT fk_orders_cart FOREIGN KEY (cart_id) REFERENCES cart (id) ON DELETE SET NULL,
    CONSTRAINT fk_orders_shipping_address FOREIGN KEY (shipping_address_id) REFERENCES customer_addresses (id) ON DELETE SET NULL,
    CONSTRAINT fk_orders_billing_address FOREIGN KEY (billing_address_id) REFERENCES customer_addresses (id) ON DELETE SET NULL
);
CREATE INDEX idx_orders_customer_id ON orders (customer_id);
CREATE INDEX idx_orders_cart_id ON orders (cart_id);

-- changeset Bashir:31
--comment: 'Create order_items table to store items in orders'
CREATE TABLE order_items
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id    BIGINT NOT NULL,
    product_id  BIGINT NOT NULL,
    quantity    INT,
    unit_price  DECIMAL(19, 2),
    total_price DECIMAL(19, 2),
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_order_items_order FOREIGN KEY (order_id) REFERENCES orders (id) ON DELETE CASCADE,
    CONSTRAINT fk_order_items_product FOREIGN KEY (product_id) REFERENCES products (id) ON DELETE CASCADE
);
CREATE INDEX idx_order_items_order_id ON order_items (order_id);
CREATE INDEX idx_order_items_product_id ON order_items (product_id);

-- changeset Bashir:32
--comment: 'Create payments table to store payment transactions for orders'
CREATE TABLE payments
(
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id         BIGINT                                              NOT NULL,
    status           ENUM ('PENDING', 'COMPLETED', 'FAILED', 'REFUNDED') NOT NULL DEFAULT 'PENDING',
    payment_method   VARCHAR(50),
    amount           DECIMAL(19, 2),
    transaction_id   VARCHAR(100),
    response_message VARCHAR(255),
    created_at       TIMESTAMP                                                    DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP                                                    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_payments_order FOREIGN KEY (order_id) REFERENCES orders (id) ON DELETE CASCADE
);

CREATE INDEX idx_payments_order_id ON payments (order_id);


-- changeset Bashir:33
--comment: 'Add columns country_code and dial_code to store customer`s country and dialing code'
ALTER TABLE customers ADD COLUMN country_code VARCHAR(2) NULL, ADD COLUMN dial_code VARCHAR(10) NULL;
