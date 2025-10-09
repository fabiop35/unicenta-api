# unicenta oPos API REST

uniCenta oPos is an enterprise level point of sale system and it has the following feature set

* Sales
* Inventory
* Customers
* Suppliers
* Employees
* Reporting

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



