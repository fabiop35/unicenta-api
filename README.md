# unicenta oPos API REST

uniCenta oPos is an enterprise level point of sale system and it has the following feature set

* Sales
* Inventory
* Customers
* Suppliers
* Employees
* Reporting

#Run a specific test
./gradlew test --tests ProductControllerIntegrationTest

# Test the API
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

{

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