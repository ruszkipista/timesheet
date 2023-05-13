package timesheet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.util.UriComponentsBuilder;

import timesheet.controller.WorkerController;
import timesheet.exception.DepartmentNotFoundException;
import timesheet.exception.WorkerNotFoundException;
import timesheet.model.ContractType;
import timesheet.model.Department;
import timesheet.model.Worker;
import timesheet.model.WorkerCreateCommand;
import timesheet.model.WorkerDto;
import timesheet.model.WorkerUpdateCommand;
import timesheet.repository.DepartmentRepository;
import timesheet.repository.WorkerRepository;

@SpringBootTest
@Sql(statements = {
    "SET REFERENTIAL_INTEGRITY false; ",
    "TRUNCATE TABLE departments RESTART IDENTITY; ",
    "TRUNCATE TABLE workers RESTART IDENTITY; ",
    "TRUNCATE TABLE timesheetentries RESTART IDENTITY; ",
    "SET REFERENTIAL_INTEGRITY true;"
})
public class WorkerControllerIT {
    @Autowired
    WorkerController controller;

    @Autowired
    WorkerRepository repo;

    @Autowired
    DepartmentRepository departmentRepo;

    List<Worker> workers;

    @BeforeEach
    void init() {
        Department d1 = departmentRepo.save(new Department("D1", null));
        workers = repo.saveAllAndFlush(List.of(
            new Worker("Abc",LocalDate.parse("1970-01-01"),ContractType.FULL_TIME_EMPLOYEE,d1),
            new Worker("Bcd",LocalDate.parse("1975-01-01"),ContractType.PART_TIME_EMPLOYEE,d1),
            new Worker("Cde",LocalDate.parse("1980-01-01"),ContractType.FIXED_AMOUNT,d1),
            new Worker("Def",LocalDate.parse("1985-01-01"),ContractType.TIME_AND_MATERIAL,d1),
            new Worker("Efg",LocalDate.parse("1990-01-01"),ContractType.FULL_TIME_EMPLOYEE,d1)
        ));
    }

	@Test
	void contextLoads() {
	}

    @Test
    void readAllWorkers_received5() {
        Assertions.assertThat(controller.listWorkers(Optional.empty()))
            .hasSize(5)
            .extracting(WorkerDto::getName)
            .containsExactlyInAnyOrder("Abc", "Bcd", "Cde", "Def", "Efg");
    }

    @Test
    void findWorkerByExactName_found1() {
        Assertions.assertThat(controller.listWorkers(Optional.of("Bcd")))
            .hasSize(1)
            .extracting(WorkerDto::getName)
            .contains("Bcd");
    }

    @Test
    void findWorkerByNotExistingName_notFound() {
        Assertions.assertThat(controller.listWorkers(Optional.of("Stu")))
            .hasSize(0);
    }

    @Test
    public void getWorkerById_found() {
        var response = controller.getWorkerById(2L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("WorkerDto(id=2, name=Bcd, dateOfBirth=1975-01-01, contractType=PART_TIME_EMPLOYEE, departmentId=1, departmentName=D1)",
                     response.getBody().toString());
    }

    @Test
    public void getWorkerById_NotFound_404() {
        var response = controller.getWorkerById(9999L);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void createWorker_createdWithId() {      
        var command = new WorkerCreateCommand("Xyz", LocalDate.parse("2000-01-01"), "FIXED_AMOUNT", null);
        var response = controller.createWorker(command, UriComponentsBuilder.newInstance());

        Assertions.assertThat(List.of(response.getBody()))
            .extracting(WorkerDto::getName, WorkerDto::getDateOfBirth, WorkerDto::getContractType)
            .contains(Assertions.tuple("Xyz",LocalDate.parse("2000-01-01"),ContractType.FIXED_AMOUNT));

        assertTrue(response.getHeaders().getLocation().toString().startsWith("/api/workers/", 0));
    }

    @Test
    void updateWorkerById_modifiedContractType() {
        Long workerId = workers.get(1).getId();

        var workerUpdateCommand = new WorkerUpdateCommand("TIME_AND_MATERIAL");
        controller.modifyWorkerById(workerId, workerUpdateCommand);

        WorkerDto worker = controller.getWorkerById(workerId).getBody();

        Assertions.assertThat(List.of(worker))
            .extracting(WorkerDto::getName, WorkerDto::getDateOfBirth, WorkerDto::getContractType)
            .contains(Assertions.tuple("Bcd",LocalDate.parse("1975-01-01"),ContractType.TIME_AND_MATERIAL));
    }

    @Test
    void updateWorkerById_invalidContractType() {
        Long workerId = workers.get(1).getId();

        var workerUpdateCommand = new WorkerUpdateCommand("EMPLOYEE");
        var exception = assertThrows(IllegalArgumentException.class, ()-> controller.modifyWorkerById(workerId, workerUpdateCommand));
        assertEquals("No enum constant timesheet.model.ContractType.EMPLOYEE", exception.getMessage());
    }

    @Test
    void updateDepartmentOfWorker_modifiedDepartment() {
        Long workerId = workers.get(2).getId();
        
        Department departmentNew = departmentRepo.save(new Department("D2", null));
        assertNotNull(departmentNew.getId());

        WorkerDto workerUpdatedDto1 = controller.assignWorkerToDepartment(workerId, departmentNew.getId()).getBody();
        assertEquals(departmentNew.getId(), workerUpdatedDto1.getDepartmentId());

        WorkerDto workerUpdatedDto2 = controller.getWorkerById(workerId).getBody();
        assertEquals(departmentNew.getId(), workerUpdatedDto2.getDepartmentId());
    }

    @Test
    void updateDepartmentOfWorker_workerNotFound() {   
        Long badWorkerId = 42L;   
        Long departmentId = departmentRepo.save(new Department("D2", null)).getId();
        assertNotNull(departmentId);

        assertThrows(WorkerNotFoundException.class, ()->controller.assignWorkerToDepartment(badWorkerId, departmentId));
    }

    @Test
    void updateDepartmentOfWorker_departmentNotFound() {      
        Long badDepartmentId = 42L;
        Long workerId = workers.get(4).getId();

        assertThrows(DepartmentNotFoundException.class, ()->controller.assignWorkerToDepartment(workerId, badDepartmentId));
    }

    @Test
    void deleteWorkerById_deleted() {
        Long id = workers.get(0).getId();
        controller.deleteWorkerById(id);

        assertEquals(HttpStatus.NOT_FOUND, controller.getWorkerById(id).getStatusCode());
    }
}
