package timesheet;

import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.equalTo;

import java.time.LocalDateTime;

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
import timesheet.model.TimeEntryCreateCommand;
import timesheet.model.TimeEntryUpdateCommand;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(statements = {
  "SET REFERENTIAL_INTEGRITY false; ",
  "TRUNCATE TABLE departments RESTART IDENTITY; ",
  "TRUNCATE TABLE workers RESTART IDENTITY; ",
  "TRUNCATE TABLE timesheetentries RESTART IDENTITY; ",
  "SET REFERENTIAL_INTEGRITY true;",
  "INSERT INTO departments (department_name) VALUES ('D1'),('D2'),('D3'),('D4');",
  "INSERT INTO workers (worker_name, date_of_birth, contract_type, department_id) VALUES "
  +"('Aaa','1970-01-01','FULL_TIME_EMPLOYEE',1),"
  +"('Bbb','1975-01-01','PART_TIME_EMPLOYEE',1),"
  +"('Ccc','1980-01-01','TIME_AND_MATERIAL', 2),"
  +"('Ddd','1985-01-01','PART_TIME_EMPLOYEE',3),"
  +"('Eee','1990-01-01','FULL_TIME_EMPLOYEE',1);",
  "UPDATE departments SET manager_id=5;",
  "INSERT INTO timesheetentries (department_id, worker_id, start_date_time, duration_in_minutes, description) VALUES "
  +"(1,1,'2023-04-17 09:00:00',480,'bookkeeping'),"
  +"(1,1,'2023-04-18 09:00:00',480,'reporting'),"
  +"(1,1,'2023-04-19 09:00:00',480,'cost analysis'),"
  +"(1,1,'2023-04-20 09:00:00',480,'revenue accounting'),"
  +"(1,1,'2023-04-21 09:00:00',240,'bookkeeping'),"
  +"(1,1,'2023-04-21 09:00:00',240,'administration'),"       
  +"(1,2,'2023-04-17 09:00:00',240,'bookkeeping'),"
  +"(1,2,'2023-04-18 09:00:00',240,'asset accounting'),"
  +"(1,2,'2023-04-19 09:00:00',240,'product costing'),"
  +"(1,2,'2023-04-20 09:00:00',240,'accrual postings'),"
  +"(1,2,'2023-04-21 09:00:00',240,'sick leave'),"
  +"(3,3,'2023-04-17 09:00:00',480,'project IV.'),"
  +"(3,3,'2023-04-18 09:00:00',480,'project IV.'),"
  +"(3,3,'2023-04-19 09:00:00',480,'project IV.'),"
  +"(3,3,'2023-04-20 09:00:00',480,'project IV.'),"
  +"(2,4,'2023-04-18 13:00:00',180,'maintenance on production line A'),"
  +"(2,4,'2023-04-20 09:00:00',300,'maintenance on production line B'),"
  +"(4,5,'2023-04-17 09:00:00',480,'project IV. negotiations'),"
  +"(4,5,'2023-04-18 09:00:00',480,'annual leave'),"
  +"(4,5,'2023-04-19 09:00:00',480,'employee evaluations'),"
  +"(4,5,'2023-04-20 09:00:00',480,'travel to customer'),"
  +"(4,5,'2023-04-21 09:00:00',480,'administration');"
})
public class TimeEntryControllerRestAssuredIT {
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
  void readAll_received22() {
    RestAssuredMockMvc.when()
        .get("/api/timesheetentries")
        .then()
          .statusCode(200)
          .body("[0].description", equalTo("bookkeeping"))
          .body("[21].description", equalTo("administration"));
  }

  @Test
  void filterByDescription_found3() {
    RestAssuredMockMvc
        .given()
          .queryParam("descriptionPart", "book")
        .when()
          .get("/api/timesheetentries")
        .then()
          .statusCode(200)
          .body("[0].startDateTime", equalTo("2023-04-17T09:00:00"))
          .body("[0].departmentId", equalTo(1))
          .body("[0].workerId", equalTo(1))
          .body("[2].startDateTime", equalTo("2023-04-17T09:00:00"))
          .body("[2].departmentId", equalTo(1))
          .body("[2].workerId", equalTo(2));
  }

  @Test
  void filterByMultipleQueryParameters_found1() {
    RestAssuredMockMvc
        .given()
          .queryParam("workerId", 2)
          .queryParam("dateFrom", "2023-04-18")
          .queryParam("dateTo", "2023-04-20")
          .queryParam("descriptionPart", "acc")
        .when()
          .get("/api/timesheetentries")
        .then()
          .statusCode(200)
          .body("[0].departmentId", equalTo(1))
          .body("[0].workerId", equalTo(2))
          .body("[0].startDateTime", equalTo("2023-04-18T09:00:00"))
          .body("[0].description", equalTo("asset accounting"));
  }

  @Test
  void getEntryById_found() {
    RestAssuredMockMvc
        .when()
          .get("/api/timesheetentries/{id}", 2L)
        .then()
          .statusCode(200)
          .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("timeentry-dto.json"))
          .body("description", equalTo("reporting"));
  }

  @Test
  void getEntryById_notFound() {
    RestAssuredMockMvc.when()
        .get("/api/timesheetentries/99999")
        .then()
          .statusCode(equalTo(HttpStatus.NOT_FOUND.value()));
  }

  @Test
  void createEntry_createdWithId() {
    RestAssuredMockMvc.with()
          .body(new TimeEntryCreateCommand(4L, 5L, LocalDateTime.parse("2023-04-22T10:00"), 120, "project IV contract"))
        .post("/api/timesheetentries")
        .then()
          .statusCode(201)
          .body(endsWith(",\"departmentId\":4,\"workerId\":5,\"startDateTime\":\"2023-04-22T10:00:00\",\"durationInMinutes\":120,\"description\":\"project IV contract\"}"));
  }

  @Test
  void createEntry_notValid() {
    RestAssuredMockMvc.with()
          .body(new TimeEntryCreateCommand(null, null, null, 0, null))
        .post("/api/timesheetentries")
        .then()
          .statusCode(HttpStatus.NOT_ACCEPTABLE.value())
          .body("status", equalTo(HttpStatus.NOT_ACCEPTABLE.value()))
          .body("type", equalTo("request/request-not-valid"))
          .body("title", equalTo("Not Acceptable"))
          .body("detail", Matchers.startsWith("Validation failed for "))
          .body("violations[0].message", Matchers.startsWith("must not be"));
  }

  @Test
  void updateEntryById_updatedContractType() {
    RestAssuredMockMvc.with() 
          .body(new TimeEntryUpdateCommand(LocalDateTime.parse("2023-04-21T09:00:00"), 480, "golfing"))
        .put("/api/timesheetentries/{id}", 22L)
        .then()
          .statusCode(200)
          .body(org.hamcrest.Matchers.endsWith(",\"departmentId\":4,\"workerId\":5,\"startDateTime\":\"2023-04-21T09:00:00\",\"durationInMinutes\":480,\"description\":\"golfing\"}"));

    RestAssuredMockMvc.when()
        .get("/api/timesheetentries/{id}", 22L)
        .then()
          .statusCode(200)
          .body("id", equalTo(22))
          .body("startDateTime", equalTo("2023-04-21T09:00:00"))
          .body("description", equalTo("golfing"));
  }

  @Test
  void updateEntryById_notFound() {
    RestAssuredMockMvc.with()
          .body(new TimeEntryUpdateCommand(LocalDateTime.parse("9999-12-31T00:00:00"), 1, "x"))
        .put("/api/timesheetentries/9999")
        .then()
          .statusCode(HttpStatus.NOT_FOUND.value())
          .body("status", equalTo(HttpStatus.NOT_FOUND.value()))
          .body("type", equalTo("timeentries/timeentry-not-found"))
          .body("title", equalTo("Not Found"))
          .body("detail", equalTo("TimeEntry not found with id 9999"));
  }

  @Test
  void deleteEntryById_deleted() {
    RestAssuredMockMvc
        .when()
          .delete("/api/timesheetentries/{id}", 3L)
        .then()
          .statusCode(204)
          .body(equalTo(""));

    RestAssuredMockMvc
        .when().get("/api/timesheetentries/{id}", 3L)
        .then().statusCode(404);
  }

  @Test
  void deleteEntryById_notFound() {
    RestAssuredMockMvc
        .when().delete("/api/timesheetentries/9999")
        .then().statusCode(HttpStatus.NOT_FOUND.value());
  }
}
