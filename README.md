# SpringBootApplication

API Documentation and Testing Workflow
Generate API Documentation with Swagger:

Configure Swagger in the Spring Boot application by adding dependencies (springdoc-openapi) and annotating controllers.
Access the generated Swagger UI at http://localhost:8080/swagger-ui.html or v3/api-docs to explore API endpoints.

Export API Specification:

From the Swagger UI, export the OpenAPI/Swagger specification in JSON or YAML format by clicking the Download button or copying the URL.

Import API Specification into Postman:

In Postman, click Import and upload the JSON/YAML file or paste the Swagger URL.
This action creates a collection with all your API endpoints ready to use in Postman.

Run Collection in Postman:

Add necessary request details like headers, body, or authentication (e.g., tokens) by selecting the request.
Use the Collection Runner to execute multiple API requests automatically, testing the flow of your APIs.

Add Tests in Postman:

Use the Tests tab in each request to validate responses using Postman’s JavaScript API (e.g., check status codes, response fields, or save tokens for reuse).
View test results (passed/failed) in the collection run summary.

Generate test automatically by using postbot or manually by writing js code in the scripts tab inside the pre-request tab
