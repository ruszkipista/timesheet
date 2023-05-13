package timesheet;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import timesheet.model.ContractType;
import timesheet.model.Department;
import timesheet.model.Worker;
import timesheet.model.WorkerDto;
import timesheet.model.WorkerMapperImpl;
import timesheet.repository.DepartmentRepository;
import timesheet.repository.WorkerRepository;
import timesheet.service.WorkerService;

@ExtendWith(MockitoExtension.class)
public class WorkerServiceTest {
    @Mock
    WorkerRepository workerRepo;
    @Mock
    DepartmentRepository departmentRepo;

    @InjectMocks
    WorkerService service = new WorkerService(workerRepo, new WorkerMapperImpl(), departmentRepo);

    List<Worker> workers;

    @BeforeEach
    void init() {
        workers = List.of(
            new Worker(1L,"Abc",LocalDate.parse("1970-01-01"),ContractType.FULL_TIME_EMPLOYEE,new Department()),
            new Worker(2L,"Bcd",LocalDate.parse("1975-01-01"),ContractType.PART_TIME_EMPLOYEE,null),
            new Worker(3L,"Cde",LocalDate.parse("1980-01-01"),ContractType.FIXED_AMOUNT,new Department()),
            new Worker(4L,"Def",LocalDate.parse("1985-01-01"),ContractType.TIME_AND_MATERIAL,null)
        );
    }

    @Test
    void getWorkers_returnsList() throws IOException {
        Mockito.when(workerRepo.findAll()).thenReturn(workers);

        Assertions.assertThat(service.getWorkers(Optional.empty()))
            .hasSize(4)
            .extracting(WorkerDto::getId, WorkerDto::getName)
            .containsExactlyInAnyOrder(
                Assertions.tuple(1L, "Abc"), 
                Assertions.tuple(2L, "Bcd"),
                Assertions.tuple(3L, "Cde"),
                Assertions.tuple(4L, "Def")
            );

        verify(workerRepo, times(1)).findAll();
    }

    @Test
    void getWorkersHavingCDinName_returns2() throws IOException {
        workers = List.of(
            new Worker(2L,"Bcd",LocalDate.parse("1975-01-01"),ContractType.PART_TIME_EMPLOYEE,null),
            new Worker(3L,"Cde",LocalDate.parse("1980-01-01"),ContractType.FIXED_AMOUNT,null)
        );        
        Mockito.when(workerRepo.findAllByNamePart(anyString())).thenReturn(workers);

        Assertions.assertThat(service.getWorkers(Optional.of("cd")))
            .hasSize(2)
            .extracting(WorkerDto::getId, WorkerDto::getName)
            .containsExactlyInAnyOrder(
                Assertions.tuple(2L, "Bcd"),
                Assertions.tuple(3L, "Cde")
            );

        verify(workerRepo, times(1)).findAllByNamePart("cd");
    }
   
}
