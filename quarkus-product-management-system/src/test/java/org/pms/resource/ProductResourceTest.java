package org.pms.resource;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.Response;

@QuarkusTest
class ProductResourceTest {
    @Test
    void testGetAllEndpoint() {
    	Response response = given()
                .when()
                .get("/product")
                .then()
                .statusCode(200)
                .contentType("application/json")
                .extract().response();
    	assertEquals(response.jsonPath().getList("name").size(), 3);
        assert(response.jsonPath().getList("name")).contains("Apple");
    }
    
    @Test
    void testGetByIdEndpoint() {
    	Response response = given()
                .when()
                .get("/product/1")
                .then()
                .statusCode(200)
                .contentType("application/json")
                .extract().response();
    	assertEquals(response.jsonPath().get("name"), "Kiwi");
    }
    
    @Test
    void testGetProductByIdAndQuantity() {
    	Response response = given()
                .when()
                .get("/product/getProductByIdAndQuantity/1/1")
                .then()
                .statusCode(200)
                .extract().response();
    	assert(response.jsonPath().getList("name")).contains("Kiwi");
    	
    	response = given()
                .when()
                .get("/product/getProductByIdAndQuantity/2/10")
                .then()
                .statusCode(204)
                .extract().response();
    	assertEquals(response.statusCode(), 204);
    }
    
    
    @Test
    void testAddProduct() {
    	Response response = given()
                .when()
                .body("{\"name\":\"Watermelon\",\"description\":\"Fruit\",\"price\":100.1,\"quantity\":10}")
                .contentType("application/json")
                .post("/product")
                .then()
                .statusCode(201)
                .contentType("application/json")
                .extract().response();
    	assertEquals(response.jsonPath().get("name"), "Watermelon");
    }

    @Test
    void testUpdateProduct() {
    	Response response = given()
                .when()
                .get("/product/1")
                .then()
                .statusCode(200)
                .contentType("application/json")
                .extract().response();
    	String name=response.jsonPath().get("name");
    	response = given()
                .when()
                .body("{\"name\":\"banana\",\"description\":\"Fruit\",\"price\":100.1,\"quantity\":10}")
                .contentType("application/json")
                .put("/product/1")
                .then()
                .statusCode(200)
                .contentType("application/json")
                .extract().response();
    	assertNotEquals(response.jsonPath().get("name"), name);
    }

    
    @Test
    void testDeleteProduct() {
    	Response response = given()
                .when()
                .delete("/product/1")
                .then()
                .statusCode(204)
                .extract().response();
    	    	assertEquals(response.statusCode(), 204);
    	    	
    	    	response = given()
    	                .when()
    	                .get("/product/1")
    	                .then()
    	                .statusCode(204)
    	                .extract().response();
    	    	assertEquals(response.statusCode(), 204);
    }

}