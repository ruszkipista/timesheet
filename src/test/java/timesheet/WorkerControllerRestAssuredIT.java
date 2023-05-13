package timesheet;

import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.equalTo;

import java.time.LocalDate;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import io.restassured.http.ContentType;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import timesheet.model.WorkerCreateCommand;
import timesheet.model.WorkerUpdateCommand;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(statements = {
  "SET REFERENTIAL_INTEGRITY false; ",
  "TRUNCATE TABLE departments RESTART IDENTITY; ",
  "TRUNCATE TABLE workers RESTART IDENTITY; ",
  "TRUNCATE TABLE timesheetentries RESTART IDENTITY; ",
  "SET REFERENTIAL_INTEGRITY true;",
  "INSERT INTO departments (department_name) VALUES ('D1'),('D2'),('D3');",
  "INSERT INTO workers (worker_name, date_of_birth, contract_type, department_id) VALUES "
  +"('Abc','1970-01-01','FULL_TIME_EMPLOYEE',1),"
  +"('Bcd','1975-01-01','PART_TIME_EMPLOYEE',1),"
  +"('Cde','1980-01-01','TIME_AND_MATERIAL', 2),"
  +"('Def','1985-01-01','PART_TIME_EMPLOYEE',3),"
  +"('Efg','1990-01-01','FULL_TIME_EMPLOYEE',1);"
})
public class WorkerControllerRestAssuredIT {
  @Autowired
  MockMvc mockMvc;

  @BeforeEach
  void init() {
    RestAssuredMockMvc.mockMvc(mockMvc);
    RestAssuredMockMvc.requestSpecification = RestAssuredMockMvc.given()
      .contentType(ContentType.JSON)
      .accept(ContentType.JSON);
  }

  @Test
  void readAll_received5() {
    RestAssuredMockMvc.when()
        .get("/api/workers")
        .then()
          .statusCode(200)
          .body("[0].name", equalTo("Abc"))
          .body("[4].name", equalTo("Efg"));
  }

  @Test
  void filterByName_found2() {
    RestAssuredMockMvc.when()
        .get("/api/workers?name=ef")
        .then()
          .statusCode(200)
          .body("[0].name", equalTo("Def"))
          .body("[1].name", equalTo("Efg"));
  }

  @Test
  void getWorkerById_found() {
    RestAssuredMockMvc.when()
        .get("/api/workers/{id}", 2L)
        .then()
          .statusCode(200)
          .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("worker-dto.json"))
          .body("name", equalTo("Bcd"));
  }

  @Test
  void getWorkerById_notFound() {
    RestAssuredMockMvc.when()
        .get("/api/workers/99999")
        .then()
          .statusCode(equalTo(HttpStatus.NOT_FOUND.value()));
  }

  @Test
  void createWorker_createdWithId() {
    RestAssuredMockMvc.with()
          .body(new WorkerCreateCommand("Xyz", LocalDate.parse("2000-01-01"), "FIXED_AMOUNT", null))
        .post("/api/workers")
        .then()
          .statusCode(201)
          .body(endsWith(",\"name\":\"Xyz\",\"dateOfBirth\":\"2000-01-01\",\"contractType\":\"FIXED_AMOUNT\",\"departmentId\":null,\"departmentName\":null}"));
  }

  @Test
  void createWorkerById_dateOfBirthIsInFuture_notValid() {
    RestAssuredMockMvc.with()
          .body(new WorkerCreateCommand("Xyz", LocalDate.parse("9999-12-31"), "FIXED_AMOUNT", null))
        .post("/api/workers")
        .then()
          .statusCode(HttpStatus.NOT_ACCEPTABLE.value())
          .body("status", equalTo(HttpStatus.NOT_ACCEPTABLE.value()))
          .body("type", equalTo("request/request-not-valid"))
          .body("title", equalTo("Not Acceptable"))
          .body("detail", Matchers.startsWith("Validation failed for "))
          .body("violations[0].field", equalTo("dateOfBirth"));
  }

  @Test
  void updateWorkerById_updatedContractType() {
    RestAssuredMockMvc.with() 
          .body(new WorkerUpdateCommand("TIME_AND_MATERIAL"))
        .put("/api/workers/{id}", 3L)
        .then()
          .statusCode(200)
          .body(endsWith(",\"name\":\"Cde\",\"dateOfBirth\":\"1980-01-01\",\"contractType\":\"TIME_AND_MATERIAL\",\"departmentId\":2,\"departmentName\":\"D2\"}"));

    RestAssuredMockMvc.when()
        .get("/api/workers/{id}", 3L)
        .then()
          .statusCode(200)
          .body("name", equalTo("Cde"))
          .body("dateOfBirth", equalTo("1980-01-01"))
          .body("contractType", equalTo("TIME_AND_MATERIAL"));
  }

  @Test
  void updateWorkerById_notFound() {
    RestAssuredMockMvc.with()
          .body(new WorkerUpdateCommand("TIME_AND_MATERIAL"))
        .put("/api/workers/9")
        .then()
          .statusCode(HttpStatus.NOT_FOUND.value())
          .body("status", equalTo(HttpStatus.NOT_FOUND.value()))
          .body("type", equalTo("workers/worker-not-found"))
          .body("title", equalTo("Not Found"))
          .body("detail", equalTo("Worker not found with id 9"));
  }

  @Test
  void updateWorkerById_updatedAssignedDepartment() {
    RestAssuredMockMvc.with() 
        .put("/api/workers/{workerId}/assign-to-department/{departmentId}", 3L, 1L)
        .then()
          .statusCode(200)
          .body(endsWith(",\"name\":\"Cde\",\"dateOfBirth\":\"1980-01-01\",\"contractType\":\"TIME_AND_MATERIAL\",\"departmentId\":1,\"departmentName\":\"D1\"}"));

    RestAssuredMockMvc.when()
        .get("/api/workers/{id}", 3L)
        .then()
          .statusCode(200)
          .body("name", equalTo("Cde"))
          .body("dateOfBirth", equalTo("1980-01-01"))
          .body("contractType", equalTo("TIME_AND_MATERIAL"))
          .body("departmentId", equalTo(1))
          .body("departmentName", equalTo("D1"));
  }  

  @Test
  void deleteWorkerById_deleted() {
    RestAssuredMockMvc.when()
        .delete("/api/workers/{id}", 3L)
        .then()
          .statusCode(204)
          .body(equalTo(""));

    RestAssuredMockMvc.when()
        .get("/api/workers/{id}", 3L)
        .then()
          .statusCode(404);
  }

  @Test
  void deleteWorkerById_notFound() {
    RestAssuredMockMvc.when()
        .delete("/api/workers/9")
        .then()
          .statusCode(HttpStatus.NOT_FOUND.value());
  }
}
