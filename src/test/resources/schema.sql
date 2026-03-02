-- src/test/resources/schema.sql
// =================================================================
// This property tells Spring Data JDBC to create the schema on startup for H2
//
// File: src/test/resources/schema.sql
// Description: Defines the schema for the H2 in-memory database during tests.
// Spring Data JDBC requires this for the `create-drop` equivalent.
// =================================================================

SET REFERENTIAL_INTEGRITY FALSE;

-- Drop tables with dependencies first
DROP TABLE IF EXISTS constraint_69;
DROP TABLE IF EXISTS taxcategories CASCADE;
DROP TABLE IF EXISTS taxes;
DROP TABLE IF EXISTS suppliers;
DROP TABLE IF EXISTS products_cat;
DROP TABLE IF EXISTS products;
DROP TABLE IF EXISTS categories;
DROP TABLE IF EXISTS ticketlines;
DROP TABLE IF EXISTS tickets;
DROP TABLE IF EXISTS stockcurrent;
DROP TABLE IF EXISTS stockdiary;

SET REFERENTIAL_INTEGRITY TRUE;

-- CATEGORIES
CREATE TABLE categories (
    id VARCHAR(255) PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

-- TAX CATEGORIES
CREATE TABLE taxcategories (
    id VARCHAR(255) PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

-- TAXES
CREATE TABLE taxes (
    id VARCHAR(255) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    category VARCHAR(255) NOT NULL,
    rate DOUBLE PRECISION NOT NULL,
    FOREIGN KEY (category) REFERENCES taxcategories(id) ON DELETE CASCADE
);

-- SUPPLIERS
CREATE TABLE suppliers (
    id VARCHAR(255) PRIMARY KEY,
    searchkey VARCHAR(255) NOT NULL UNIQUE,
    taxid VARCHAR(255),
    name VARCHAR(255) NOT NULL,
    maxdebt DOUBLE PRECISION NOT NULL DEFAULT 0,
    address VARCHAR(255),
    address2 VARCHAR(255),
    postal VARCHAR(255),
    city VARCHAR(255),
    region VARCHAR(255),
    country VARCHAR(255),
    firstname VARCHAR(255),
    lastname VARCHAR(255),
    email VARCHAR(255),
    phone VARCHAR(255),
    phone2 VARCHAR(255),
    fax VARCHAR(255),
    notes VARCHAR(255),
    visible BOOLEAN NOT NULL DEFAULT TRUE,
    curdate TIMESTAMP,
    curdebt DOUBLE PRECISION DEFAULT 0,
    vatid VARCHAR(255)
);

-- PRODUCTS
CREATE TABLE products (
    id VARCHAR(255) PRIMARY KEY,
    reference VARCHAR(255) NOT NULL UNIQUE,
    code VARCHAR(255) NOT NULL UNIQUE,
    codetype VARCHAR(255) DEFAULT 'EAN-13',
    name VARCHAR(255) NOT NULL,
    pricesell DOUBLE PRECISION NOT NULL,
    pricebuy DOUBLE PRECISION NOT NULL,
    category VARCHAR(255) NOT NULL,
    taxcat VARCHAR(255) NOT NULL,
    attributeset_id VARCHAR(255),
    attributes BLOB,
    iscom BOOLEAN DEFAULT FALSE,
    isscale BOOLEAN DEFAULT FALSE,
    isconstant BOOLEAN NOT NULL DEFAULT FALSE,
    printkb BOOLEAN NOT NULL DEFAULT FALSE,
    display VARCHAR(255),
    isvprice BOOLEAN NOT NULL DEFAULT FALSE,
    isverpatrib DOUBLE PRECISION,
    texttip VARCHAR(255),
    warranty DOUBLE PRECISION,
    stockcost DOUBLE PRECISION DEFAULT 0,
    stockvolume DOUBLE PRECISION DEFAULT 0,
    image BLOB,
    sendstatus BOOLEAN NOT NULL DEFAULT FALSE,
    isservice BOOLEAN NOT NULL DEFAULT FALSE,
    stockunits DOUBLE PRECISION DEFAULT 0,
    printto INTEGER NOT NULL DEFAULT 0,
    supplier VARCHAR(255),
    uom INTEGER NOT NULL DEFAULT 0,
    memodate TIMESTAMP(6),
    "value" BINARY(16) DEFAULT RANDOM_UUID(),
    price_buy DECIMAL(38,2),
    currency_buy VARCHAR(3),
    price_sell DECIMAL(38,2),
    currency_sell VARCHAR(3),
    category_id BINARY(16) DEFAULT RANDOM_UUID(),
    currency VARCHAR(255),
    tax_category_id VARCHAR(255) NOT NULL DEFAULT '',
    now TIMESTAMP(6),
    FOREIGN KEY (category) REFERENCES categories(id) ON DELETE CASCADE,
    FOREIGN KEY (taxcat) REFERENCES taxcategories(id) ON DELETE CASCADE
);

-- PRODUCTS_CAT (MUST come AFTER products)
CREATE TABLE products_cat (
    product VARCHAR(255) NOT NULL,
    catorder INTEGER DEFAULT NULL,
    PRIMARY KEY (product),
    CONSTRAINT products_cat_fk_1 FOREIGN KEY (product) REFERENCES products(id) ON DELETE CASCADE
);

-- TICKETS
CREATE TABLE tickets (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    user_id VARCHAR(255),
    status VARCHAR(50)
);

-- TICKET LINES
CREATE TABLE ticketlines (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    ticket_id BIGINT,
    product_id VARCHAR(255),
    quantity DECIMAL(19, 4) NOT NULL,
    price DECIMAL(19, 4) NOT NULL,
    FOREIGN KEY (ticket_id) REFERENCES tickets(id)
);

-- STOCK CURRENT
CREATE TABLE stockcurrent (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id VARCHAR(255),
    location_id VARCHAR(255),
    quantity DECIMAL(19, 4) NOT NULL,
    last_modified TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- STOCK DIARY
CREATE TABLE stockdiary (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id VARCHAR(255),
    location_id VARCHAR(255),
    transaction_type ENUM('IN', 'OUT') NOT NULL,
    quantity DECIMAL(19, 4) NOT NULL,
    created TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

SET REFERENTIAL_INTEGRITY TRUE;