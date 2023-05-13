package timesheet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.util.UriComponentsBuilder;

import timesheet.controller.DepartmentController;
import timesheet.exception.DepartmentNotFoundException;
import timesheet.exception.WorkerNotFoundException;
import timesheet.model.ContractType;
import timesheet.model.Department;
import timesheet.model.DepartmentCreateCommand;
import timesheet.model.DepartmentDto;
import timesheet.model.DepartmentUpdateCommand;
import timesheet.model.Worker;
import timesheet.repository.DepartmentRepository;
import timesheet.repository.WorkerRepository;

@SpringBootTest
@Sql(statements = {
    "SET REFERENTIAL_INTEGRITY false; ",
    "TRUNCATE TABLE departments; ",
    "TRUNCATE TABLE workers; ",
    "TRUNCATE TABLE timesheetentries; ",
    "SET REFERENTIAL_INTEGRITY true;"
})
public class DepartmentControllerIT {
    @Autowired
    DepartmentController controller;

    @Autowired
    DepartmentRepository departmentRepo;

    @Autowired
    WorkerRepository workerRepo;

    List<Department> departments;

    @BeforeEach
    void init(){
        Worker m1 = workerRepo.save(new Worker("M1", LocalDate.parse("2001-01-01"), ContractType.FULL_TIME_EMPLOYEE, null));
        Worker m2 = workerRepo.save(new Worker("M2", LocalDate.parse("2002-01-01"), ContractType.FULL_TIME_EMPLOYEE, null));
        departments = departmentRepo.saveAllAndFlush(List.of(
            new Department("D1", m1),
            new Department("D2", m2),
            new Department("D3", null)
        ));
    }

	@Test
	void contextLoads() {
	}

    @Test
    void readAllDepartment_received5() {
        Assertions.assertThat(controller.listDepartments())
            .hasSize(3)
            .extracting(DepartmentDto::getName)
            .containsExactlyInAnyOrder("D1", "D2", "D3");
    }

    @Test
    public void getDepartmentById_found() {
        Long idToBeFound = departments.get(1).getId();

        var response = controller.getDepartmentById(idToBeFound);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        DepartmentDto department = response.getBody();
        assertEquals("D2", department.getName());
        assertEquals("M2", department.getManagerName());
    }

    @Test
    public void getDepartmentById_NotFound_404() {
        var response = controller.getDepartmentById(9999L);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void createDepartment_createdWithId() {      
        var command = new DepartmentCreateCommand("D4");
        var response = controller.createDepartment(command, UriComponentsBuilder.newInstance());

        Assertions.assertThat(List.of(response.getBody()))
            .extracting(DepartmentDto::getName, DepartmentDto::getManagerId)
            .contains(Assertions.tuple("D4", null));

        assertTrue(response.getHeaders().getLocation().toString().startsWith("/api/departments/", 0));
    }

    @Test
    void updateDepartmentById_modifiedName() {
        Long departmentId = departments.get(1).getId();
        Long managerId =  departments.get(1).getManager().getId();

        var updateCommand = new DepartmentUpdateCommand("X2");
        controller.modifyDepartmentById(departmentId, updateCommand);

        DepartmentDto departmentUpdated = controller.getDepartmentById(departmentId).getBody();

        Assertions.assertThat(List.of(departmentUpdated))
            .extracting(DepartmentDto::getName, DepartmentDto::getManagerId)
            .contains(Assertions.tuple("X2", managerId));
    }

    @Test
    void updateDepartmentManager_modifiedManager() {
        Long idToBeUpdated = departments.get(2).getId();
        
        Worker managerNew = workerRepo.save(new Worker("M4", LocalDate.parse("2004-01-01"), ContractType.FULL_TIME_EMPLOYEE, null));
        assertNotNull(managerNew.getId());

        DepartmentDto departmentUpdatedDto1 = controller.appointDepartmentManager(idToBeUpdated, managerNew.getId()).getBody();
        assertEquals(managerNew.getId(), departmentUpdatedDto1.getManagerId());

        DepartmentDto departmentUpdatedDto2 = controller.getDepartmentById(idToBeUpdated).getBody();
        assertEquals(managerNew.getId(), departmentUpdatedDto2.getManagerId());
    }

    @Test
    void updateDepartmentManager_workerNotFound() {   
        Long badWorkerId = 42L;
        Long departmentId = departments.get(2).getId();

        assertThrows(WorkerNotFoundException.class, ()->controller.appointDepartmentManager(departmentId, badWorkerId));
    }

    @Test
    void updateDepartmentManager_departmentNotFound() {      
        Long wrongDepartmentId = 42L;
        Long workerId = workerRepo.save(new Worker("M4", LocalDate.parse("2003-01-01"), ContractType.FULL_TIME_EMPLOYEE, null)).getId();

        assertThrows(DepartmentNotFoundException.class, ()->controller.appointDepartmentManager(wrongDepartmentId, workerId));
    }
}
