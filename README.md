# unicenta oPos API REST

uniCenta oPos is an enterprise level point of sale system and it has the following feature set

* Sales
* Inventory
* Customers
* Suppliers
* Employees
* Reporting

## Tech Stack

* **Language / Runtime:** Java 21
* **Framework:** Spring Boot 3.5.3 (Web, Data JDBC, Validation, Cache)
* **Database:** MySQL (H2 for tests)
* **Caching:** Caffeine
* **PDF Generation:** iText 7 (`com.itextpdf:itext7-core:8.0.4`)
* **Code Generation:** Lombok
* **API Documentation:** Spring REST Docs (`spring-restdocs-mockmvc`) + Asciidoctor
* **Build Tool:** Gradle (with dependency management plugin)

## Getting Started

### Prerequisites

* JDK 21
* MySQL database configured for the application
* Gradle (wrapped: `./gradlew`)

### Build

```bash
./gradlew build
```

### Run

```bash
./gradlew bootRun
```

The API is served on port `8081` (see examples below).

### Run a specific test

```bash
./gradlew test --tests ProductControllerIntegrationTest
```

> Note: `StockValuationControllerIntegrationTest` uses Testcontainers and needs a
> running Docker daemon (the app's MySQL DB runs in a Docker container, so the
> test shares the same MySQL configuration as production). If Docker is not
> available the test is skipped automatically (`disabledWithoutDocker = true`).

#REST API
|:-------------------------------------------:|:------:|:--------------------------------------------------------:|:-------:|:------------------------------:|:------------------------------------------------------------------------------------------------------|
|  Endpoint                                  | Method | Req. body                                                 | Status   | Resp. body                     | Description                                                                                            |
|:-------------------------------------------:|:------:|:--------------------------------------------------------:|:-------:|:------------------------------:|:------------------------------------------------------------------------------------------------------|
| /api/v1/products                            | POST   | ProductRequestDto                                        | 201     | ProductResponseDto             | Create a new product.                                                                                 |
|---------------------------------------------|--------|----------------------------------------------------------|---------|--------------------------------|-------------------------------------------------------------------------------------------------------|
| /api/v1/products                            | GET    |                                                          | 200     | Page<ProductResponseDto>       | Retrieve all products, paginated.                                                                     |
|---------------------------------------------|--------|----------------------------------------------------------|---------|--------------------------------|-------------------------------------------------------------------------------------------------------|
| /api/v1/products/{id}                       | GET    |                                                          | 200     | ProductResponseDto             | Get a specific product by ID.                                                                         |
|---------------------------------------------|--------|----------------------------------------------------------|---------|--------------------------------|-------------------------------------------------------------------------------------------------------|
| /api/v1/products/{id}                       | PUT    | ProductDto                                               | 200     | Product                        | Update a specific product by ID.                                                                      |
|---------------------------------------------|--------|----------------------------------------------------------|---------|--------------------------------|-------------------------------------------------------------------------------------------------------|
| /api/v1/products/{id}                       | DELETE |                                                          | 204     |                                | Delete a specific product by ID.                                                                      |
|---------------------------------------------|--------|----------------------------------------------------------|---------|--------------------------------|-------------------------------------------------------------------------------------------------------|
| /api/v1/products/search?name=...            | GET    |                                                          | 200     | List<ProductResponseDto>       | Search products by name.                                                                              |
|---------------------------------------------|--------|----------------------------------------------------------|---------|--------------------------------|-------------------------------------------------------------------------------------------------------|
| /api/v1/products/searchByCode?code=...      | GET    |                                                          | 200     | List<ProductResponseDto>       | Search products by code.                                                                              |
|---------------------------------------------|--------|----------------------------------------------------------|---------|--------------------------------|-------------------------------------------------------------------------------------------------------|
| /api/v1/taxes                               | POST   | TaxDto                          				            | 201     | Tax                            | Create a new tax.                                                                                   |
|---------------------------------------------|--------|----------------------------------------------------------|---------|--------------------------------|-------------------------------------------------------------------------------------------------------|
| /api/v1/taxes                               | GET    |                                                          | 200     | List<Tax>                      | Retrieve all taxes.                                                                                   |
|---------------------------------------------|--------|----------------------------------------------------------|---------|--------------------------------|-------------------------------------------------------------------------------------------------------|
| /api/v1/taxes/{id}                          | PUT    | TaxDto                                                   | 200     | Tax                            | Update a specific tax by ID.                                                                          |
|---------------------------------------------|--------|----------------------------------------------------------|---------|--------------------------------|-------------------------------------------------------------------------------------------------------|
| /api/v1/categories                          | POST   | NameDto                                                  | 201     | Category                       | Create a new category.                                                                                |
|---------------------------------------------|--------|----------------------------------------------------------|---------|--------------------------------|-------------------------------------------------------------------------------------------------------|
| /api/v1/categories                          | GET    |                                                          | 200     | List<Category>                 | Retrieve all categories.                                                                              |
|---------------------------------------------|--------|----------------------------------------------------------|---------|--------------------------------|-------------------------------------------------------------------------------------------------------|
| /api/v1/categories/{id}                     | PUT    | NameDto                                                  | 200     | Category                       | Update a specific category by ID.                                                                     |
|---------------------------------------------|--------|----------------------------------------------------------|---------|--------------------------------|-------------------------------------------------------------------------------------------------------|
| /api/v1/categories/{id}                     | DELETE |                                                          | 204     |                                | Delete a specific category by ID.                                                                     |
|---------------------------------------------|--------|----------------------------------------------------------|---------|--------------------------------|-------------------------------------------------------------------------------------------------------|
| /api/v1/categories/{id}                     | GET    |                                                          | 200     | Category                       | Get a specific category by ID.                                                                        |
|---------------------------------------------|--------|----------------------------------------------------------|---------|--------------------------------|-------------------------------------------------------------------------------------------------------|
| /api/v1/suppliers 					      | POST   | SupplierDto                                              | 201     | Supplier                       | Create a new supplier.                                                                                |
|---------------------------------------------|--------|----------------------------------------------------------|---------|--------------------------------|-------------------------------------------------------------------------------------------------------|
| /api/v1/suppliers                           | GET    |                                                          | 200     | Page<Supplier>                 | Retrieve all suppliers, paginated.                                                                    |
|---------------------------------------------|--------|----------------------------------------------------------|---------|--------------------------------|-------------------------------------------------------------------------------------------------------|
| /api/v1/suppliers/{id}                      | GET    |                                                          | 200     | Supplier                       | Get a specific supplier by ID.                                                                        |
|---------------------------------------------|--------|----------------------------------------------------------|---------|--------------------------------|-------------------------------------------------------------------------------------------------------|
| /api/v1/suppliers/{id}                      | PUT    | SupplierDto                                              | 200     | Supplier                       | Update a specific supplier by ID.                                                                     |
|---------------------------------------------|--------|----------------------------------------------------------|---------|--------------------------------|-------------------------------------------------------------------------------------------------------|
| /api/v1/suppliers/{id}                      | DELETE |                                                          | 204     |                                | Delete a specific supplier by ID.                                                                     |
|---------------------------------------------|--------|----------------------------------------------------------|---------|--------------------------------|-------------------------------------------------------------------------------------------------------|
| /api/v1/suppliers/search?term=...           | GET    |                                                          | 200     | List<Supplier>                 | Search suppliers by term.                                                                             |
|---------------------------------------------|--------|----------------------------------------------------------|---------|--------------------------------|-------------------------------------------------------------------------------------------------------|
| /api/v1/suppliers/{id}/stockdiary           | GET    |                                                          | 200     | List<StockDiary>               | Retrieve all StockDiary records for a specific supplier.                                              |
|---------------------------------------------|--------|----------------------------------------------------------|---------|--------------------------------|-------------------------------------------------------------------------------------------------------|
| /api/v1/sales/sales-closed-pos              | GET    | startDate, endDate (optional)                            | 200     | List<SalesClosedPosReportItem> | Get sales closed pos report data within the specified date range.                                     |
|---------------------------------------------|--------|----------------------------------------------------------|---------|--------------------------------|-------------------------------------------------------------------------------------------------------|
| /api/v1/sales/sales-closed-pos/pdf          | GET    | startDate, endDate (optional)                            | 200     | PDF file                       | Get sales closed pos report data as a PDF within the specified date range.                            |
|---------------------------------------------|--------|----------------------------------------------------------|---------|--------------------------------|-------------------------------------------------------------------------------------------------------|
| /api/v1/stock/current                       | GET    | page, size, search (optional), locationId (optional)     | 200     | Page<StockCurrentDto>          | Retrieve current stock paginated.                                                                     |
|---------------------------------------------|--------|----------------------------------------------------------|---------|--------------------------------|-------------------------------------------------------------------------------------------------------|
| /api/v1/stock/current/location/{locationId} | GET    |                                                          | 200     | List<StockCurrentDto>          | Retrieve current stock by location ID.                                                                |
|---------------------------------------------|--------|----------------------------------------------------------|---------|--------------------------------|-------------------------------------------------------------------------------------------------------|
| /api/v1/stock/adjust                        | POST   | StockAdjustmentRequest                                   | 200     | StockDiary                     | Adjust stock levels based on a request.                                                               |
|---------------------------------------------|--------|----------------------------------------------------------|---------|--------------------------------|-------------------------------------------------------------------------------------------------------|
| /api/v1/stock/low-stock                     | GET    | threshold (optional)                                     | 200     | List<StockCurrentDto>          | Get list of products that are low in stock.                                                           |
|---------------------------------------------|--------|----------------------------------------------------------|---------|--------------------------------|-------------------------------------------------------------------------------------------------------|
| /api/v1/stock/history/{productId}           | GET    |                                                          | 200     | List<StockHistoryDto>          | Retrieve stock history for a specific product.                                                        |
|---------------------------------------------|--------|----------------------------------------------------------|---------|--------------------------------|-------------------------------------------------------------------------------------------------------|
| /api/v1/stock/valuation                     | GET    |                                                          | 200     | InventoryValuationDto          | Get inventory valuation: cost side (WAC from stockdiary history, fallback products.pricebuy) plus retail side (catalog pricesell): itemValue, invested, retailValue and margins per item, with grand totals. |
|---------------------------------------------|--------|----------------------------------------------------------|---------|--------------------------------|-------------------------------------------------------------------------------------------------------|
| /api/v1/stock/valuation/summary             | GET    | locationId (optional)                                    | 200     | InventoryValueDto              | Get the total value of the inventory at cost price (single aggregate query), optionally filtered by location, with item/product counts and a per-location breakdown.  |
|---------------------------------------------|--------|----------------------------------------------------------|---------|--------------------------------|-------------------------------------------------------------------------------------------------------|
| /api/v1/stock/valuation/by-location         | GET    |                                                          | 200     | List<InventoryValueByLocationDto> | Get the inventory value at cost price grouped by location.                                          |
|---------------------------------------------|--------|----------------------------------------------------------|---------|--------------------------------|-------------------------------------------------------------------------------------------------------|
| /api/v1/stock/locations                     | GET    |                                                          | 200     | List<Location>                 | Retrieve list of stock locations.                                                                     |
|---------------------------------------------|--------|----------------------------------------------------------|---------|--------------------------------|-------------------------------------------------------------------------------------------------------|
| /api/v1/stock/current/product/{productId}   | GET    |                                                          | 200     | List<StockCurrentDto>          | Retrieve current stock by product ID.                                                                 |
|---------------------------------------------|--------|----------------------------------------------------------|---------|--------------------------------|-------------------------------------------------------------------------------------------------------|
| /api/v1/stock/history/item                  | GET    | locationId, productId, attributeSetInstanceId (optional) | 200     | List<StockHistoryDto>          | Retrieve stock history for a specific item (location, product, AttributeSetInstance).                 |
|---------------------------------------------|--------|----------------------------------------------------------|---------|--------------------------------|-------------------------------------------------------------------------------------------------------|
| /api/v1/stock/current/byCode                | GET    | code, locationId (optional)                              | 200     | List<StockCurrentDto>          | Retrieve current stock by product code.                                                               |
|---------------------------------------------|--------|----------------------------------------------------------|---------|--------------------------------|-------------------------------------------------------------------------------------------------------|
| /api/v1/stock/entry                         | POST   | StockEntryRequest                                        | 200     | StockDiary                     | Create a new stock entry based on the request.                                                        |
|---------------------------------------------|--------|----------------------------------------------------------|---------|--------------------------------|-------------------------------------------------------------------------------------------------------|
| /api/v1/tax-categories                      | POST   | NameDto                                                  | 201     | TaxCategory                    | Create a new tax category.                                                                            |
|---------------------------------------------|--------|----------------------------------------------------------|---------|--------------------------------|-------------------------------------------------------------------------------------------------------|
| /api/v1/tax-categories                      | GET    |                                                          | 200     | List<TaxCategory>              | Retrieve all tax categories.                                                                          |
|---------------------------------------------|--------|----------------------------------------------------------|---------|--------------------------------|-------------------------------------------------------------------------------------------------------|
| /api/v1/tax-categories/{id}                 | PUT    | NameDto                                                  | 200     | TaxCategory                    | Update a specific tax category by ID.                                                                 |
|:-------------------------------------------:|:------:|:--------------------------------------------------------:|:-------:|:------------------------------:|:------------------------------------------------------------------------------------------------------|

# Inventory Valuation — Concepts & Implementation

This section documents how `GET /api/v1/stock/valuation` computes its numbers,
the accounting concepts behind them, and the changes that were applied to make
the report reflect **real inventory value** instead of catalog prices.

## Where the numbers come from (data model)

| Table | Role in valuation |
|---|---|
| `stockdiary` | The movement ledger and **source of truth**. Every stock change is a row: signed `units` (+ = stock entering, − = stock leaving) and `price`. Stock-in rows carry the **unit price actually paid**; out rows carry retail prices, so they are excluded from cost calculations. |
| `stockcurrent` | Materialized on-hand quantities per (location, product, attribute set instance). Quantities only — never costs. |
| `products.pricebuy` | Catalog purchase price. Used **only as a fallback** when a product has no priced stock-in history. In this database many values are stale placeholders (`1`), which is why it cannot be trusted as the primary source. |
| `products.pricesell` | Current catalog selling price (**tax-exclusive**, uniCenta convention). Source for the retail side of the report. |

> Important convention discovered in this database: the movement direction is
> determined by the **sign of `units`**, not by the `reason` code (reason codes
> are unreliable here because entries are written through different flows with
> mixed conventions). All costing logic is therefore sign-based.

## Costing method: Weighted Average Cost (WAC)

For each product, over all priced stock-in movements
(`units > 0 AND price > 0`):

```
WAC   = SUM(price * units) / SUM(units)
invested = SUM(price * units)          -- lifetime cash paid
```

WAC smooths out purchase-price changes over time and is the standard
method for valuing stock at cost.

## Response fields

### Per item (`InventoryItemValuationDto`)

| Field | Meaning | Source |
|---|---|---|
| `costPrice` | Weighted average unit cost of what is on the shelf | WAC from `stockdiary`, else `products.pricebuy` fallback |
| `costSource` | Provenance of `costPrice`: `STOCKDIARY_WAC` or `PRICEBUY_FALLBACK` | computed |
| `itemValue` | Value of remaining stock at cost: `units × costPrice` | computed |
| `invested` | Total cash ever paid for this product (lifetime, regardless of units already sold) | `SUM(price×units)` of stock-ins |
| `priceSell` | Current catalog selling price (tax-exclusive) | `products.pricesell` |
| `retailValue` | Potential revenue if all current stock sells: `units × priceSell` | computed |
| `potentialMargin` | `retailValue − itemValue`: gross profit expected if the shelf sells out at current catalog price | computed |

### Totals (`InventoryValuationDto`)

`totalValue`, `totalInvested`, `totalRetailValue`, `totalPotentialMargin` —
sums of the item-level figures above.

## Key invariant

```
itemValue <= invested      (when data is healthy)
```

You can never have more value on the shelf than you ever paid for the product.
The difference is the cost already consumed by sold/removed units. If you ever
see `itemValue > invested`, it means stock quantities do not reconcile with
purchase history (e.g., missing opening-stock entries) — treat it as a data
quality alert.

Worked example (LAVALOZA OXI X500CC): bought 9 units for 10,048 total →
`costPrice = 1116.44`; 8 remain → `itemValue = 8931.56`; `invested = 10048`;
gap of ~1116 = cost of the unit that left.

## Files changed

| File | Change |
|---|---|
| `domain/ProductCostBasis.java` | **New.** Query projection: `productId`, `avgCost`, `invested`. |
| `domain/StockDiaryRepository.java` | New query `findWeightedAverageCostByProduct()`: sign-based cost basis + lifetime invested, single grouped query. |
| `application/StockService.java` | `getInventoryValuation()` reworked: builds the cost map once, falls back to `pricebuy`, accumulates cost/invested/retail/margin totals. |
| `interfaces/dto/InventoryItemValuationDto.java` | Added `costSource`, `invested`, `priceSell`, `retailValue`, `potentialMargin`. |
| `interfaces/dto/InventoryValuationDto.java` | Added `totalInvested`, `totalRetailValue`, `totalPotentialMargin`. |
| `test/resources/mysql-test-schema.sql` | Added the `stockdiary` table (was missing from the Testcontainers schema). |
| `StockValuationControllerIntegrationTest.java` | New end-to-end test covering WAC, out-movement exclusion, pricebuy fallback, invested and retail/margin figures. |

## Example

```bash
curl -s http://localhost:8081/api/v1/stock/valuation | python -m json.tool
```

```json
{
  "totalValue": 62134.56,
  "totalInvested": 63251,
  "totalRetailValue": 77200,
  "totalPotentialMargin": 15065.44,
  "items": [
    {
      "productName": "LAVALOZA OXI X500CC",
      "units": 8,
      "costPrice": 1116.44,
      "costSource": "STOCKDIARY_WAC",
      "invested": 10048,
      "itemValue": 8931.56,
      "priceSell": 2550,
      "retailValue": 20400,
      "potentialMargin": 11468.44
    }
  ]
}
```

## Known limitations

* Only products with priced stock-in rows get true WAC; everything else falls
  back to `products.pricebuy` (mostly placeholder `1` today), understating
  totals until real purchase prices are recorded.
* Items with negative `stockcurrent.units` (sold more than ever recorded as
  purchased — missing opening stock) contribute negative values and margins.
* Retail figures are tax-exclusive (catalog prices as stored).
* `/api/v1/stock/valuation/summary` and `/api/v1/stock/valuation/by-location`
  still value at catalog `pricebuy`, so they can disagree with this endpoint.

# ToDo — Inventory control & data quality roadmap

Analysis of what can be improved next to get reliable, decision-grade
inventory information. Ordered roughly by impact.

## High priority (correctness first)

1. **Data cleanup — real purchase prices.** Backfill `products.pricebuy` (or
   create stockdiary stock-in entries) for products currently carrying the
   placeholder `1`. Without this, any valuation is understated for most of the
   catalog. Candidate: an admin endpoint that flags every product whose
   `costSource` resolves to `PRICEBUY_FALLBACK`.
2. **Opening stock / negative-units reconciliation.** Many items show negative
   `stockcurrent.units`. Run a one-off physical count and record opening-stock
   entries so quantities become trustworthy; add a validation warning when a
   sale would drive stock below zero.
3. **Consistency across endpoints.** Port the WAC cost basis to
   `/valuation/summary` and `/valuation/by-location` so every surface reports
   the same value for the same question.
4. **Ledger-vs-stock drift detector.** Periodic check that
   `stockcurrent.units == SUM(stockdiary.units)` per item; expose mismatches
   via a small report endpoint before they silently corrupt valuations.

## Medium priority (better analysis)

5. **Realized margin reporting (COGS).** Once POS sales are reliably written
   to `stockdiary`, compute actual gross margin (revenue − COGS at WAC) per
   period and per product, complementing today's *potential* margin. This is
   the bridge from "what is my stock worth" to "is my shop profitable".
6. **Reorder management.** Replace the single global threshold in
   `/stock/low-stock` with per-product/per-location min/max levels
   (`products.stockunits`/`stockcost` exist but are unused), suggested order
   quantities and a supplier-grouped purchase suggestion report.
7. **Historical ("as-of") valuation.** Replay `stockdiary` up to a date to
   answer "what was my inventory worth on Dec 31?" — essential for account
   reconciliation; requires nothing beyond a date filter on existing queries.
8. **ABC classification + dead stock.** Pareto-rank products by revenue/value
   contribution (A/B/C), and flag slow movers/dead stock using last-movement
   dates. Directly actionable: discount, relocate or stop reordering C items.
9. **Valuation endpoint ergonomics.** Add location filtering, pagination/sort
   for `items`, CSV/PDF export (pattern already exists in the sales report),
   and an optional tax-inclusive mode (join `taxes/taxcategories`).

## Low priority / hygiene

10. **Test schema drift.** `src/test/resources/schema.sql` (H2) has an older
    divergent shape of `stockcurrent`/`stockdiary` (no `price` column). Align
    it with the real mappings so non-Testcontainers tests stay meaningful.
11. **Rounding policy.** Decide currency precision (e.g., round WAC to 2–4
    decimals) consistently across endpoints instead of exposing raw doubles.
12. **Shrinkage & cycle counts.** Support periodic physical-count workflows
    (count vs expected, variance report) to quantify losses systematically.
13. **Batch/expiry tracking.** Use attribute set instances to track lots and
    expiry dates where relevant (FEFO picking, waste reporting).
14. **Caching.** The valuation aggregates are read-heavy; consider caching the
    totals with short TTL consistent with the existing Caffeine setup.

# Test the API

# Get Inventory Valuation (JSON) — cost (WAC), invested, retail value and margins
curl -s "http://localhost:8081/api/v1/stock/valuation"
curl -s "http://localhost:8081/api/v1/stock/valuation" | python -m json.tool

# Compact per-item summary
curl -s http://localhost:8081/api/v1/stock/valuation \
  | python -c "import json,sys; d=json.load(sys.stdin); [print(f\"{i['productName']:<30} units={i['units']:>5} cost={i['costPrice']:>10} value={i['itemValue']:>12} invested={i['invested']:>10} retail={i['retailValue']:>12} margin={i['potentialMargin']:>10} [{i['costSource']}]\") for i in d['items']]; print('TOTAL value:', d['totalValue'], ' invested:', d['totalInvested'], ' retail:', d['totalRetailValue'], ' margin:', d['totalPotentialMargin'])"

# Get Report Data (JSON)
#Without Date Range (Get All Data)
curl -X GET "http://localhost:8081/api/v1/sales/sales-closed-pos" -H "Accept: application/json"
curl "http://localhost:8081/api/v1/sales/sales-closed-pos"

#With Date Range Filter:  ISO 8601 format (YYYY-MM-DDTHH:MM:SS).
curl "http://localhost:8081/api/v1/sales/sales-closed-pos?startDate=10-09-2023T00:00:00&endDate=10-31-2023T23:59:59"

# Get Report as PDF
#Without Date Range (Get All Data as PDF):
curl -X GET "http://localhost:8081/api/v1/sales/sales-closed-pos/pdf" -H "Accept: application/pdf" -o "sales_report_all.pdf"

#With Date Range Filter (Get Filtered Data as PDF):
curl -X GET "http://localhost:8081/api/v1/sales/sales-closed-pos/pdf?startDate=2023-10-01T00:00:00&endDate=2023-10-31T23:59:59" -H "Accept: application/pdf" -o "sales_report_filtered.pdf"

#Curl request to create a category
curl -X POST http://localhost:8081/api/v1/categories -H "Content-Type: application/json" -d '{"name": "Electronics"}'
Response →
{"id":"1c41f685-cc92-4a20-b54d-d6c668809652","name":"Electronics33","newProduct":true}
> ./gradlew test --tests "*CategoryControllerIntegrationTest.createCategory*"


#Find a specific category
curl -X GET http://localhost:8081/api/v1/categories/00000000-cat0-0000-0000-000000000008 -H "Content-Type: application/json"

#curl request to create a new product
curl -X POST http://localhost:8081/api/v1/products \
  -H "Content-Type: application/json" \
  -d '{
    "reference": "REF12345",
    "code": "PCODE001",
    "codetype": "EAN13",
    "name": "Wireless Bluetooth Headphones",
    "pricesell": 59.99,
    "pricebuy": 30.00,
    "categoryId": "00000000-cat0-0000-0000-000000000008",
    "taxcatId": "00000000-taxc-0000-0000-000000000003",
    "display": "Headphones (Wireless)",
    "idSupplier": "sup-789",
    "supplierName": "TechGadgets Inc."
  }'

Response → 
{
  "id": "e26079c4-11d3-4fb3-9975-3ad6cdf6b2c2",
  "reference": "REF12345888",
  "code": "PCODE001888",
  "name": "Wireless Bluetooth Headphones888",
  "pricesell": 59.99,
  "pricebuy": 30,
  "categoryId": "00000000-cat0-0000-0000-000000000008",
  "taxcatId": "00000000-taxc-0000-0000-000000000003",
  "iscom": false,
  "isscale": false,
  "display": "Wireless Bluetooth Headphones888",
  "stockcost": 0,
  "stockvolume": 0,
  "stockunits": 0,
  "isvprice": false,
  "codetype": "EAN-13",
  "warranty": 0,
  "isverpatrib": 0,
  "printto": 1,
  "uom": 0,
  "now": "2026-01-29T17:18:27.379+00:00",
  "memodate": "2026-01-29T17:18:27.379+00:00",
  "value": "dbefc4ff-3f74-4312-9cb3-4db3acd7585c",
  "currency": "NDF",
  "idSupplier": "sup-789",
  "new": true,
  "newProduct": true
}

#get all the Products
curl -X GET http://localhost:8081/api/v1/products?page=1&size=5&sort=name,desc
RESPONSE → :
{
  "content": [
    {
      "id": "8c1f6525-d23f-48f8-9d08-e8a4bfd9aaa1",
      "reference": "REF-0000003251",
      "code": "7707860027590",
      "codetype": "EAN-13",
      "name": "\"KIT ORAL PLUS (2 CEPILLOS, 1 CREMA 30G)\"",
      "pricesell": 5300,
      "pricebuy": 5300,
      "categoryId": "00000000-cat0-0000-0000-000000000009",
      "categoryName": "Cuidado Personal",
      "taxcatId": "00000000-taxc-0000-0000-000000000003",
      "display": "KIT ORAL PLUS (2 CEPILLOS, 1 CREMA 30G)",
      "taxRate": 0,
      "taxName": "IVA 0%",
      "idSupplier": "No Supplier",
      "supplierName": "No Supplier"
    },
    {
      "id": "be577358-76a8-4f91-8bfa-2a4d6a3e9f53",
      "reference": "REF-0000028509",
      "code": "7702047040898",
      "codetype": "EAN-13",
      "name": "|MAYONESA FRUCO 40 GMS",
      "pricesell": 800,
      "pricebuy": 1,
      "categoryId": "000",
      "categoryName": "Category Standard",
      "taxcatId": "00000000-taxc-0000-0000-000000000003",
      "display": "|MAYONESA FRUCO 40 GMS",
      "taxRate": 0,
      "taxName": "IVA 0%",
      "idSupplier": "No Supplier",
      "supplierName": "No Supplier"
    },
 ],
  "pageable": {
    "pageNumber": 1,
    "pageSize": 5,
    "sort": {
      "unsorted": false,
      "empty": false,
      "sorted": true
    },
    "offset": 5,
    "unpaged": false,
    "paged": true
  },
  "last": false,
  "totalPages": 8559,
  "totalElements": 42791,
  "first": false,
  "size": 5,
  "number": 1,
  "sort": {
    "unsorted": false,
    "empty": false,
    "sorted": true
  },
  "numberOfElements": 5,
  "empty": false
}
