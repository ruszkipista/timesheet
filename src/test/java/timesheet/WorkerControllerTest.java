package timesheet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import timesheet.controller.WorkerController;
import timesheet.exception.DepartmentNotFoundException;
import timesheet.exception.WorkerNotFoundException;
import timesheet.model.ContractType;
import timesheet.model.WorkerCreateCommand;
import timesheet.model.WorkerDto;
import timesheet.model.WorkerUpdateCommand;
import timesheet.service.WorkerService;

@ExtendWith(MockitoExtension.class)
public class WorkerControllerTest {

    @Mock
    WorkerService service;

    @InjectMocks
    WorkerController controller;

    @Mock
    UriComponentsBuilder builder;
    @Mock
    UriComponents components;

    List<WorkerDto> workers;

    @BeforeEach
    void intit(){
        workers = List.of(
            new WorkerDto(1L,"Abc",LocalDate.parse("1970-01-01"),ContractType.FULL_TIME_EMPLOYEE, 2L, "D2"),
            new WorkerDto(2L,"Bcd",LocalDate.parse("1975-01-01"),ContractType.PART_TIME_EMPLOYEE, null, null)
        );
    }
 
    @Test
    void listAllWorkers_listed() throws IOException{
        Mockito.when(service.getWorkers(Optional.empty())).thenReturn(workers);

        Assertions.assertEquals(workers, controller.listWorkers(Optional.empty()));

        Mockito.verify(service).getWorkers(any());
    }

    @Test
    public void createWorker_createdWithLocation() {
        // create a WorkerCreateCommand instance
        WorkerCreateCommand command = new WorkerCreateCommand("test worker", LocalDate.parse("1900-01-01"), "FULL_TIME_EMPLOYEE", 2L);       
        // create a WorkerDto instance
        WorkerDto workerDto = new WorkerDto(42L, "test worker", LocalDate.parse("1900-01-01"), ContractType.FULL_TIME_EMPLOYEE, 2L, "D2");

        // mock the service method to return the WorkerDto instance
        when(service.createWorker(command)).thenReturn(workerDto);

        URI expectedWorkerLocation = URI.create("http://localhost/api/workers/42");

        when(builder.path(any())).thenReturn(builder);
        when(builder.buildAndExpand(anyLong())).thenReturn(components);
        when(components.toUri()).thenReturn(expectedWorkerLocation);
              
        // call the createWorker method on the controller
        var response = controller.createWorker(command, builder);
        
        // verify that the service method was called with the command argument
        verify(service).createWorker(command);
        
        // verify that the response status is CREATED
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        
        // verify that the WorkerDto instance is returned in the response body
        assertEquals(workerDto, response.getBody());
        
        // verify that the response header contains a Location attribute with the URI of the newly created resource's location
        assertEquals(expectedWorkerLocation, response.getHeaders().getLocation());
    }

    @Test
    public void getWorkerById_found() {
        WorkerDto workerDto = new WorkerDto(42L, "test worker", LocalDate.parse("1900-01-01"), ContractType.FULL_TIME_EMPLOYEE, 2L, "D2");
        Mockito.when(service.getWorkerById(anyLong())).thenReturn(workerDto);

        assertEquals(HttpStatus.OK, controller.getWorkerById(42L).getStatusCode());
        Mockito.verify(service).getWorkerById(anyLong());
    }

    @Test
    public void getWorkerById_NotFound() {
        doThrow(new WorkerNotFoundException(0L)).when(service).getWorkerById(anyLong()); 

        assertEquals(HttpStatus.NOT_FOUND, controller.getWorkerById(42L).getStatusCode());
    }

    @Test
    public void updateById_workerNotFound() {
        doThrow(WorkerNotFoundException.class).when(service).updateWorkerById(anyLong(), any()); 

        assertThrows(WorkerNotFoundException.class, 
                    ()->controller.modifyWorkerById(42L, new WorkerUpdateCommand()));
    }

    @Test
    public void assignToDepartment_workerNotFound() {
        doThrow(WorkerNotFoundException.class).when(service).assignWorkerToDepartment(anyLong(),anyLong()); 

        assertThrows(WorkerNotFoundException.class, 
                    ()->controller.assignWorkerToDepartment(42L, 0L));
    }

    @Test
    public void assignToDepartment_departmentNotFound() {
        doThrow(DepartmentNotFoundException.class).when(service).assignWorkerToDepartment(anyLong(),anyLong()); 

        assertThrows(DepartmentNotFoundException.class, 
                    ()->controller.assignWorkerToDepartment(0L, 42L));
    }

    @Test
    public void deleteById_workerNotFound_404() {
        doThrow(new WorkerNotFoundException(0L)).when(service).removeWorkerById(anyLong()); 

        assertThrows(WorkerNotFoundException.class, ()->controller.deleteWorkerById(42L));
    }
}
