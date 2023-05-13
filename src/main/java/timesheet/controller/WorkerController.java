package timesheet.controller;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import timesheet.exception.WorkerNotFoundException;
import timesheet.model.WorkerCreateCommand;
import timesheet.model.WorkerDto;
import timesheet.model.WorkerUpdateCommand;
import timesheet.service.WorkerService;

@RestController
@AllArgsConstructor
@RequestMapping("/api/workers")
@Tag(name = "Operations on Workers")
public class WorkerController {
    private WorkerService service;

    @GetMapping
    @Operation(summary = "Get all workers", description = "List all workers")
    public List<WorkerDto> listWorkers(@RequestParam Optional<String> name) {
        return service.getWorkers(name);
    }

    @GetMapping(value = "{id}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @Operation(summary = "Find worker by id", description = "Find worker by id")
    @ApiResponse(responseCode = "404", description = "Worker not found by ID")
    public ResponseEntity<WorkerDto> getWorkerById(
            @PathVariable("id") 
            @Parameter(description = "ID of the Worker", example = "2")
            long workerId) {
        try {
            return ResponseEntity.ok(service.getWorkerById(workerId));
        } catch (WorkerNotFoundException nfe) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    @Operation(summary = "Create worker from data", description = "Create worker from sent data")
    @ApiResponse(responseCode = "201", description = "Worker is created")
    @ApiResponse(responseCode = "406", description = "Provided data is not valid")
    public ResponseEntity<WorkerDto> createWorker(
            @Valid 
            @RequestBody 
            WorkerCreateCommand command,
            UriComponentsBuilder uriComponentsBuilder ) {
        WorkerDto entityDto = service.createWorker(command);
        URI locationUri = uriComponentsBuilder
            .path("/api/workers/{id}")
            .buildAndExpand(entityDto.getId())
            .toUri();
        return ResponseEntity
            .created(locationUri)
            .body(entityDto);
    }

    @PutMapping("{id}")
    @Operation(summary = "Update worker by ID", description = "Update worker's attributes from sent data.")
    public ResponseEntity<WorkerDto> modifyWorkerById(
            @PathVariable("id") 
            @Parameter(description = "ID of the Worker", example = "2")
            long id,
            @Valid 
            @RequestBody 
            WorkerUpdateCommand command) {
        WorkerDto entityDto = service.updateWorkerById(id, command);
        return ResponseEntity.ok(entityDto);
    }

    @PutMapping("{workerId}/assign-to-department/{departmentId}")
    @Operation(summary = "Assign worker to a department", description = "Update worker's department attribute.")
    public ResponseEntity<WorkerDto> assignWorkerToDepartment(
            @PathVariable("workerId")
            @Parameter(description = "ID of the Worker", example = "2")
            long workerId,
            @PathVariable("departmentId")
            @Parameter(description = "ID of the Department", example = "5")
            long departmentId) {
        WorkerDto workerDto = service.assignWorkerToDepartment(workerId, departmentId);
        return ResponseEntity.ok(workerDto);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete worker by ID", description = "Delete worker by ID")
    public void deleteWorkerById(
            @PathVariable("id") 
            @Parameter(description = "ID of the Worker", example = "2")
            long id) {
        service.removeWorkerById(id);
    }

}