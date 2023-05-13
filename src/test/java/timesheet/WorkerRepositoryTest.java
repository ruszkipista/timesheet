package timesheet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import timesheet.model.ContractType;
import timesheet.model.Worker;
import timesheet.repository.WorkerRepository;

@DataJpaTest
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
    +"('Cde','1980-01-01','TIME_AND_MATERIAL',2),"
    +"('Def','1985-01-01','PART_TIME_EMPLOYEE',2),"
    +"('Efg','1990-01-01','FULL_TIME_EMPLOYEE',3);"
})
public class WorkerRepositoryTest {
    @Autowired
    WorkerRepository repo;
    
    @Test
    void readAllWorkers_returned5() {
        assertThat(repo.findAll())
            .hasSize(5)
            .extracting(Worker::getName)
            .contains("Abc", "Cde")
            .doesNotContain("Xxx");
    }

    @Test
    void findWorkersHavingCDinName_returns2() {
        assertThat(repo.findAllByNamePart("cd"))
            .hasSize(2)
            .extracting(Worker::getId, Worker::getName)
            .containsExactlyInAnyOrder(
                Assertions.tuple(2L, "Bcd"), 
                Assertions.tuple(3L, "Cde")
            );
    }

    @Test
    void findWorkerById_found() {
        assertEquals("Bcd", repo.findById(2L).get().getName());
    }

    @Test
    void findWorkerById_notFound() {
        assertTrue(repo.findById(9999L).isEmpty());
    }
    
    @Test
    void updateWorkerById_updated() {
        Worker worker = new Worker(2L,"Xyz", LocalDate.parse("1975-01-01"), ContractType.PART_TIME_EMPLOYEE,null);

        repo.save(worker);

        assertThat(repo.findAll())
            .hasSize(5)
            .extracting(Worker::getName)
            .containsOnly("Abc", "Xyz", "Cde", "Def", "Efg")
            .doesNotContain("Bcd");
    }

    @Test
    void deleteWorkerById() {
        repo.deleteById(2L);
        assertThat(repo.findAll())
            .hasSize(4)
            .extracting(Worker::getName)
            .containsOnly("Abc", "Cde", "Def", "Efg")
            .doesNotContain("Bcd");
    }
}
