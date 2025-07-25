# This property tells Spring Data JDBC to create the schema on startup for H2
spring.sql.init.mode=always
*/

// =================================================================
// File: src/test/resources/schema.sql (NEW FILE for tests)
// Description: Defines the schema for the H2 in-memory database during tests.
// Spring Data JDBC requires this for the `create-drop` equivalent.
// =================================================================

DROP TABLE IF EXISTS products;
DROP TABLE IF EXISTS taxes;
DROP TABLE IF EXISTS taxcategories;
DROP TABLE IF EXISTS categories;

CREATE TABLE categories (
    id VARCHAR(255) PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE taxcategories (
    id VARCHAR(255) PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE taxes (
    id VARCHAR(255) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    category VARCHAR(255) NOT NULL,
    rate DOUBLE PRECISION NOT NULL,
    FOREIGN KEY (category) REFERENCES taxcategories(id)
);

CREATE TABLE products (
    id VARCHAR(255) PRIMARY KEY,
    reference VARCHAR(255) NOT NULL UNIQUE,
    code VARCHAR(255) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    pricesell DOUBLE PRECISION NOT NULL,
    pricebuy DOUBLE PRECISION NOT NULL,
    category VARCHAR(255) NOT NULL,
    taxcat VARCHAR(255) NOT NULL,
    iscom BOOLEAN DEFAULT FALSE,
    isscale BOOLEAN DEFAULT FALSE,
    display VARCHAR(255),
    stockcost DOUBLE PRECISION DEFAULT 0,
    stockvolume DOUBLE PRECISION DEFAULT 0,
    stockunits DOUBLE PRECISION DEFAULT 0,
    isvprice BOOLEAN DEFAULT FALSE,
    codetype VARCHAR(255) DEFAULT 'EAN-13',
    FOREIGN KEY (category) REFERENCES categories(id),
    FOREIGN KEY (taxcat) REFERENCES taxcategories(id)
);

