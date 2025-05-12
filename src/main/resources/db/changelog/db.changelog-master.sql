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
    id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    CONSTRAINT UKoce3937d2f4mpfqrycbr0l93m UNIQUE (name)
);

--changeset Bashir:3
CREATE TABLE categories
(
    id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    CONSTRAINT UKt8o6pivur7nn124jehx7cygw5 UNIQUE (name)
);

--changeset Bashir:4
CREATE TABLE roles
(
    id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NULL
);

--changeset Bashir:5
CREATE TABLE suppliers
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    address       VARCHAR(255) NULL,
    contact_email VARCHAR(255) NOT NULL,
    name          VARCHAR(255) NOT NULL,
    phone         VARCHAR(255) NULL,
    CONSTRAINT UKlaeargh3fvypyj1sm5rhfbewm UNIQUE (name, contact_email)
);

--changeset Bashir:6
CREATE TABLE products
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    created_at    DATETIME(6) NULL,
    updated_at    DATETIME(6) NULL,
    cost_price    DECIMAL(38, 2) NULL,
    description   TEXT NULL,
    name          VARCHAR(255) NULL,
    selling_price DECIMAL(38, 2) NULL,
    sku           VARCHAR(255) NULL,
    brand_id      BIGINT NULL,
    category_id   BIGINT NULL,
    supplier_id   BIGINT NULL,
    CONSTRAINT UKfhmd06dsmj6k0n90swsh8ie9g UNIQUE (sku),
    CONSTRAINT FK6i174ixi9087gcvvut45em7fd FOREIGN KEY (supplier_id) REFERENCES suppliers (id),
    CONSTRAINT FKa3a4mpsfdf4d2y6r8ra3sc8mv FOREIGN KEY (brand_id) REFERENCES brands (id),
    CONSTRAINT FKog2rp4qthbtt2lfyhfo32lsw9 FOREIGN KEY (category_id) REFERENCES categories (id)
);

--changeset Bashir:7
CREATE TABLE images
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    alt_text   VARCHAR(255) NULL,
    image_url  VARCHAR(255) NULL,
    product_id BIGINT NULL,
    CONSTRAINT FKghwsjbjo7mg3iufxruvq6iu3q FOREIGN KEY (product_id) REFERENCES products (id)
);

--changeset Bashir:8
CREATE TABLE product_attributes
(
    attribute_value VARCHAR(255) NULL,
    product_id      BIGINT NOT NULL,
    attribute_id    BIGINT NOT NULL,
    PRIMARY KEY (attribute_id, product_id),
    CONSTRAINT FK6ksuorb5567jpa08ihcumumy1 FOREIGN KEY (attribute_id) REFERENCES attributes (id),
    CONSTRAINT FKcex46yvx4g18b2pn09p79h1mc FOREIGN KEY (product_id) REFERENCES products (id)
);

--changeset Bashir:9
CREATE TABLE stock_movements
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    created_at    DATETIME(6) NULL,
    updated_at    DATETIME(6) NULL,
    movement_type ENUM('ADJUST', 'IN', 'OUT', 'RETURN', 'TRANSFER','DAMAGED') NOT NULL,
    product_id    BIGINT NULL,
    quantity      INT    NOT NULL,
    reason        ENUM('CREATED', 'DAMAGED', 'RETURNED', 'TRANSFERRED', 'UPDATED') NOT NULL,
    warehouse_id  BIGINT NOT NULL,
    username      VARCHAR(255) NULL,
    CONSTRAINT FKjcaag8ogfjxpwmqypi1wfdaog
        FOREIGN KEY (product_id) REFERENCES products (id) ON DELETE SET NULL
);

--changeset Bashir:10
CREATE TABLE users
(
    id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    email    VARCHAR(255) NOT NULL,
    name     VARCHAR(255) NULL,
    password VARCHAR(255) NOT NULL,
    username VARCHAR(255) NOT NULL,
    CONSTRAINT UK6dotkott2kjsp8vw4d0m25fb7 UNIQUE (email),
    CONSTRAINT UKr43af9ap4edm43mmtq01oddj6 UNIQUE (username)
);

--changeset Bashir:11
CREATE TABLE user_roles
(
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT FKh8ciramu9cc9q3qcqiv4ue8a6 FOREIGN KEY (role_id) REFERENCES roles (id),
    CONSTRAINT FKhfh9dx7w3ubf1co1vdev94g3f FOREIGN KEY (user_id) REFERENCES users (id)
);

--changeset Bashir:12
CREATE TABLE warehouses
(
    id      BIGINT AUTO_INCREMENT PRIMARY KEY,
    address VARCHAR(255) NULL,
    name    VARCHAR(255) NULL
);

--changeset Bashir:13
CREATE TABLE stock
(
    product_id   BIGINT NOT NULL,
    warehouse_id BIGINT NOT NULL,
    quantity     INT    NOT NULL,
    PRIMARY KEY (product_id, warehouse_id),
    CONSTRAINT FKeuiihog7wq4cu7nvqu7jx57d2 FOREIGN KEY (product_id) REFERENCES products (id),
    CONSTRAINT FKpx2sjs5k0wdolrps3puo2skaw FOREIGN KEY (warehouse_id) REFERENCES warehouses (id)
);
