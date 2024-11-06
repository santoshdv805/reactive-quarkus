Here’s an example `README.md` file for your Quarkus Reactive application. This README provides an overview of the application, how to set it up, run it, and test it. It also includes details on running the application with PostgreSQL and testing the product-related REST endpoints.

---

# Quarkus Reactive Product API

This is a **Quarkus Reactive** application that manages products. The application exposes several REST endpoints to perform CRUD operations and check the stock availability of products. The backend uses **PostgreSQL** for data storage with **Quarkus Hibernate Reactive** and **Panache** for non-blocking database access.

## Prerequisites

To run this application, you will need the following:

- **Java 17+** (JDK 17 or higher)
- **Maven 3.9.6+**
- **PostgreSQL** (local or in a containerized environment using Podman)
- **Podman** (if running PostgreSQL in a container)

## Dependencies

The application uses the following key dependencies:

- **quarkus-resteasy-reactive** – To expose reactive RESTful endpoints.
- **quarkus-resteasy-reactive-jackson** – For JSON processing.
- **quarkus-reactive-pg-client** – For reactive PostgreSQL database connection.
- **quarkus-hibernate-reactive-panache** – For reactive Panache-based database access.
- **rest-assured** – For testing the REST endpoints.

## Setup Instructions

### 1. Start PostgreSQL Database (via Podman)

You can start a PostgreSQL container with Podman using the following command:

```bash
podman run -it --rm=true --name quarkus_test -e POSTGRES_USER=quarkus_test -e POSTGRES_PASSWORD=quarkus_test -e POSTGRES_DB=quarkus_test -p 5432:5432 postgres:13.3
```

This will start a PostgreSQL container with the database `quarkus_test`, user `quarkus_test`, and password `quarkus_test`.

### 2. Clone the Repository

If you haven't already, clone this repository to your local machine:

```bash
git clone <repository-url>
cd <repository-directory>
```

### 3. Configure Database Connection

Ensure that the `application.properties` or `application.yml` in the `src/main/resources` directory is configured to connect to the running PostgreSQL instance:

```properties
# src/main/resources/application.properties
quarkus.datasource.db-kind=postgresql
quarkus.datasource.username=quarkus_test
quarkus.datasource.password=quarkus_test
quarkus.datasource.jdbc.url=jdbc:postgresql://localhost:5432/quarkus_test
quarkus.hibernate-orm.database.generation=update
quarkus.hibernate-orm.sql-load-script=no-file
```

### 4. Run the Application in Dev Mode

Run the application in **dev mode** to enable live reloading and debugging:

```bash
mvn clean compile quarkus:dev
```

The application will be available at `http://localhost:8080`.

## API Endpoints

The application provides the following REST endpoints to interact with the products:

### 1. **Create a new product**

- **Endpoint**: `POST /product`
- **Request Body**:

```json
{
  "name": "Watermelon",
  "description": "Fruit",
  "price": 100.1,
  "quantity": 10
}
```

- **Example Command**:

```bash
curl -X POST -i http://localhost:8080/product -d '{"name":"Watermelon","description":"Fruit","price":100.1,"quantity":10}' -H 'content-type:application/json'
```

### 2. **Get all products**

- **Endpoint**: `GET /product`
- **Example Command**:

```bash
curl -GET -i http://localhost:8080/product
```

### 3. **Get a product by ID**

- **Endpoint**: `GET /product/{id}`
- **Example Command**:

```bash
curl -GET -i http://localhost:8080/product/1
```

### 4. **Update product by ID**

- **Endpoint**: `PUT /product/{id}`
- **Request Body**:

```json
{
  "name": "Updated Watermelon",
  "description": "Updated Fruit",
  "price": 150.1,
  "quantity": 15
}
```

- **Example Command**:

```bash
curl -X PUT -i http://localhost:8080/product/1 -d '{"name":"Updated Watermelon","description":"Updated Fruit","price":150.1,"quantity":15}' -H 'content-type:application/json'
```

### 5. **Delete a product by ID**

- **Endpoint**: `DELETE /product/{id}`
- **Example Command**:

```bash
curl -X DELETE -i http://localhost:8080/product/1
```

### 6. **Get products in ascending order by price**

- **Endpoint**: `GET /product/asc`
- **Example Command**:

```bash
curl -GET -i http://localhost:8080/product/asc
```

### 7. **Check stock availability for a product**

- **Endpoint**: `GET /product/getProductByIdAndQuantity/{id}/{count}`
- **Example Command**:

```bash
curl -GET -i http://localhost:8080/product/getProductByIdAndQuantity/1/10
```

### Response Example:

If the product has sufficient stock (i.e., quantity >= count), the response will be `true`. If not, it will be `false`.

## Running Tests

To run unit and integration tests for the application, use the following Maven command:

```bash
mvn test
```

This will run all test cases and validate the functionality of the REST endpoints.

## Testing with REST-assured

Test cases have been written using **REST-assured** to verify the correctness of the RESTful endpoints. These tests ensure that the CRUD operations and stock availability checks work as expected.

---

### Example Test Case:

**Test for creating a product**:

```java
@Test
public void testCreateProduct() {
    RestAssured.given()
        .body("{\"name\":\"Watermelon\",\"description\":\"Fruit\",\"price\":100.1,\"quantity\":10}")
        .header("Content-Type", "application/json")
        .when()
        .post("/product")
        .then()
        .statusCode(201)
        .body("name", equalTo("Watermelon"))
        .body("description", equalTo("Fruit"))
        .body("price", equalTo(100.1f))
        .body("quantity", equalTo(10));
}
```

This test verifies that when a `POST` request is made to `/product`, a new product is created with the specified details, and the response contains the correct fields.

---

## Troubleshooting

- If you encounter a **connection issue** with the PostgreSQL database, make sure the database container is running and accessible.
- If your tests fail, check the **database schema** to ensure it’s correctly set up, and confirm that your test data is valid.

---

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

This `README.md` file should provide a comprehensive guide for setting up, running, and testing your Quarkus Reactive application. It includes instructions on running the application, interacting with the product-related endpoints, and running unit tests.
