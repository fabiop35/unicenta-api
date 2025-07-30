INSERT INTO taxcategories (ID, NAME) VALUES ('00000000-taxc-0000-0000-000000000001', 'IVA General 19%');
INSERT INTO taxcategories (ID, NAME) VALUES ('00000000-taxc-0000-0000-000000000002', 'IVA Reducido 5%');
INSERT INTO taxcategories (ID, NAME) VALUES ('00000000-taxc-0000-0000-000000000003', 'Exento de IVA 0%');

-- -- Definición de Impuestos
INSERT INTO taxes (ID, NAME, CATEGORY, RATE) VALUES ('00000000-tax0-0000-0000-000000000001', 'IVA 19%', '00000000-taxc-0000-0000-000000000001', 0.19);
INSERT INTO taxes (ID, NAME, CATEGORY, RATE) VALUES ('00000000-tax0-0000-0000-000000000002', 'IVA 5%', '00000000-taxc-0000-0000-000000000002', 0.05);
INSERT INTO taxes (ID, NAME, CATEGORY, RATE) VALUES ('00000000-tax0-0000-0000-000000000003', 'IVA 0%', '00000000-taxc-0000-0000-000000000003', 0.00);

-- -- Definición de Categorías de Productos
INSERT INTO categories (ID, NAME) VALUES ('00000000-cat0-0000-0000-000000000001', 'Granos y Despensa');
INSERT INTO categories (ID, NAME) VALUES ('00000000-cat0-0000-0000-000000000002', 'Lácteos y Huevos');
INSERT INTO categories (ID, NAME) VALUES ('00000000-cat0-0000-0000-000000000003', 'Frutas y Verduras');
INSERT INTO categories (ID, NAME) VALUES ('00000000-cat0-0000-0000-000000000004', 'Carnes, Pollo y Pescado');
INSERT INTO categories (ID, NAME) VALUES ('00000000-cat0-0000-0000-000000000005', 'Bebidas');
INSERT INTO categories (ID, NAME) VALUES ('00000000-cat0-0000-0000-000000000006', 'Panadería y Pastelería');
INSERT INTO categories (ID, NAME) VALUES ('00000000-cat0-0000-0000-000000000007', 'Mecato y Dulces');
INSERT INTO categories (ID, NAME) VALUES ('00000000-cat0-0000-0000-000000000008', 'Aseo Hogar');
INSERT INTO categories (ID, NAME) VALUES ('00000000-cat0-0000-0000-000000000009', 'Cuidado Personal');
INSERT INTO categories (ID, NAME) VALUES ('00000000-cat0-0000-0000-000000000010', 'Mascotas');


--insert into products (id, reference, code, name, pricesell, pricebuy, category, taxcat, image, iscom, isscale, display, attributes, stockcost, stockvolume, stockunits, isvprice, codetype, printto, uom, value, category_id, tax_category_id) values ('00000000-prod-0000-0000-000000000001', 'REF-0001', '7702004001011', 'Arroz Diana Bolsa 1000g', 4900, 4200, '00000000-cat0-0000-0000-000000000001', '00000000-taxc-0000-0000-000000000002', null, false, false, 'Arroz Diana 1kg', null, 0, 0, 0, 0, 'EAN-13', 1,0, UNHEX(REPLACE(UUID(), '-', '')), UNHEX(REPLACE(UUID(), '-', '')), 'NULL');
--insert into products_cat (product, catorder) values ('00000000-prod-0000-0000-000000000001', 1);
--insert into stockdiary (id, datenew, reason, location, product, attributesetinstance_id, units, price, appuser, supplier, supplierdoc) values ('4602d718-6289-11f0-9538-ee78ebcc3137', now(), 1, '0', '00000000-prod-0000-0000-000000000001', null, 120.00, 4200, 'admin', null, null);
--insert into stockcurrent (product, location, units) values ('00000000-prod-0000-0000-000000000001', '0', 120.00);
--insert into stocklevel (id, product, location, stocksecurity, stockmaximum) values ('4602d718-6289-11f0-9538-ee78ebcc3137', '00000000-prod-0000-0000-000000000001', '0', 20.00, 200.00);
insert into products (
    id, reference, code, name, pricesell, pricebuy, category, taxcat,
    attributeset_id, stockcost, stockvolume, image, iscom, isscale,
    display, attributes, isvprice, isverpatrib, texttip, warranty,
    stockunits, printto, supplier, uom, memodate, "value",
    price_buy, currency_buy, price_sell, currency_sell,
    category_id, currency, tax_category_id, now
) values (
    '00000000-prod-0000-0000-000000000001', -- id (VARCHAR)
    'REF-0001', -- reference (VARCHAR)
    '7702004001011', -- code (VARCHAR)
    'Arroz Diana Bolsa 1000g', -- name (VARCHAR)
    4900, -- pricesell (DOUBLE)
    4200, -- pricebuy (DOUBLE)
    '00000000-cat0-0000-0000-000000000001', -- category (VARCHAR)
    '00000000-taxc-0000-0000-000000000002', -- taxcat (VARCHAR)
    null, -- attributeset_id (VARCHAR)
    0, -- stockcost (DOUBLE)
    0, -- stockvolume (DOUBLE)
    null, -- image (BLOB)
    false, -- iscom (BOOLEAN)
    false, -- isscale (BOOLEAN)
    'Arroz Diana 1kg', -- display (VARCHAR)
    null, -- attributes (BLOB)
    false, -- isvprice (BOOLEAN)
    null, -- isverpatrib (DOUBLE)
    null, -- texttip (VARCHAR)
    null, -- warranty (DOUBLE)
    0, -- stockunits (DOUBLE)
    1, -- printto (INTEGER)
    null, -- supplier (VARCHAR)
    0, -- uom (INTEGER)
    null, -- memodate (TIMESTAMP)
    RANDOM_UUID(), -- value (BINARY(16))
    null, -- price_buy (DECIMAL)
    null, -- currency_buy (VARCHAR)
    null, -- price_sell (DECIMAL)
    null, -- currency_sell (VARCHAR)
    RANDOM_UUID(), -- category_id (BINARY(16))
    null, -- currency (VARCHAR)
    UUID(), -- tax_category_id (VARCHAR) - Use UUID() for a string UUID
    NOW() -- now (TIMESTAMP)
);