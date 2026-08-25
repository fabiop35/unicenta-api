-- MySQL schema for the throwaway Testcontainers database used by
-- StockValuationControllerIntegrationTest. It mirrors the subset of the
-- uniCenta oPOS production schema that the new inventory-value module reads.
-- It is ONLY applied inside the ephemeral test container and never touches
-- the production database.
--
-- IMPORTANT: Table names must match the entity @Table mappings exactly,
-- because MySQL (unlike H2) is case-sensitive for table names on Linux:
--   Category  -> categories
--   TaxCategory -> taxcategories
--   Location  -> LOCATIONS
--   Product   -> products
--   StockCurrent -> STOCKCURRENT (only ever touched via lowercase raw SQL
--                  in the application queries)

CREATE TABLE categories (
    id VARCHAR(255) PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE taxcategories (
    id VARCHAR(255) PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE LOCATIONS (
    id VARCHAR(255) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    address VARCHAR(255)
);

CREATE TABLE products (
    id VARCHAR(255) PRIMARY KEY,
    reference VARCHAR(255) NOT NULL UNIQUE,
    code VARCHAR(255) NOT NULL UNIQUE,
    codetype VARCHAR(255) DEFAULT 'EAN-13',
    name VARCHAR(255) NOT NULL,
    pricesell DOUBLE NOT NULL,
    pricebuy DOUBLE NOT NULL,
    category VARCHAR(255) NOT NULL,
    taxcat VARCHAR(255) NOT NULL,
    iscom BOOLEAN DEFAULT FALSE,
    isscale BOOLEAN DEFAULT FALSE,
    display VARCHAR(255),
    stockcost DOUBLE DEFAULT 0,
    stockvolume DOUBLE DEFAULT 0,
    stockunits DOUBLE DEFAULT 0,
    isvprice BOOLEAN DEFAULT FALSE,
    warranty DOUBLE DEFAULT 0,
    isverpatrib DOUBLE DEFAULT 0,
    printto INT DEFAULT 0,
    uom INT DEFAULT 0,
    supplier VARCHAR(255),
    memodate TIMESTAMP NULL,
    CONSTRAINT fk_products_category FOREIGN KEY (category) REFERENCES categories(id),
    CONSTRAINT fk_products_taxcat FOREIGN KEY (taxcat) REFERENCES taxcategories(id)
);

CREATE TABLE stockcurrent (
    location VARCHAR(255) NOT NULL,
    product VARCHAR(255) NOT NULL,
    attributesetinstance_id VARCHAR(255),
    units DOUBLE NOT NULL DEFAULT 0,
    CONSTRAINT fk_stockcurrent_location FOREIGN KEY (location) REFERENCES LOCATIONS(id),
    CONSTRAINT fk_stockcurrent_product FOREIGN KEY (product) REFERENCES products(id)
);

CREATE TABLE stockdiary (
    id VARCHAR(255) PRIMARY KEY,
    datenew TIMESTAMP NOT NULL,
    reason INT NOT NULL,
    location VARCHAR(255) NOT NULL,
    product VARCHAR(255) NOT NULL,
    attributesetinstance_id VARCHAR(255),
    units DOUBLE NOT NULL DEFAULT 0,
    price DOUBLE NOT NULL DEFAULT 0,
    appuser VARCHAR(255),
    supplier VARCHAR(255),
    supplierdoc VARCHAR(255),
    CONSTRAINT fk_stockdiary_location FOREIGN KEY (location) REFERENCES LOCATIONS(id),
    CONSTRAINT fk_stockdiary_product FOREIGN KEY (product) REFERENCES products(id)
);
