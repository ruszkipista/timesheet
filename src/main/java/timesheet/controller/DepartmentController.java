package timesheet.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import timesheet.exception.DepartmentNotFoundException;
import timesheet.model.DepartmentCreateCommand;
import timesheet.model.DepartmentDto;
import timesheet.model.DepartmentUpdateCommand;
import timesheet.service.DepartmentService;

@RestController
@AllArgsConstructor
@RequestMapping("/api/departments")
@Tag(name = "Operations on Departments")
public class DepartmentController {
    private DepartmentService service;

    @GetMapping
    @Operation(summary = "Get all departments", description = "List all departments")
    public List<DepartmentDto> listDepartments() {
        return service.getDepartments();
    }

    @GetMapping(value = "{id}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @Operation(summary = "Find department by id", description = "Find department by id")
    @ApiResponse(responseCode = "404", description = "Department not found by ID")
    public ResponseEntity<DepartmentDto> getDepartmentById(
            @PathVariable("id") 
            @Parameter(description = "ID of the Department", example = "5")
            long id) {
        try {
            return ResponseEntity.ok(service.getDepartmentById(id));
        } catch (DepartmentNotFoundException nfe) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    @Operation(summary = "Create department from data", description = "Create department from sent data")
    @ApiResponse(responseCode = "201", description = "Department is created")
    @ApiResponse(responseCode = "406", description = "Provided data is not valid")
    public ResponseEntity<DepartmentDto> createDepartment(
            @Valid 
            @RequestBody 
            DepartmentCreateCommand command,
            UriComponentsBuilder uriComponentsBuilder ) {
        DepartmentDto entityDto = service.createDepartment(command);
        URI locationUri = uriComponentsBuilder
            .path("/api/departments/{id}")
            .buildAndExpand(entityDto.getId())
            .toUri();
        return ResponseEntity
            .created(locationUri)
            .body(entityDto);
    }

    @PutMapping("{id}")
    @Operation(summary = "Update department by ID", description = "Update department's attributes from sent data.")
    public ResponseEntity<DepartmentDto> modifyDepartmentById(
            @PathVariable("id") 
            @Parameter(description = "ID of the Department", example = "5")
            long departmentId,
            @Valid 
            @RequestBody 
            DepartmentUpdateCommand command) {
        DepartmentDto departmentDto = service.updateDepartmentById(departmentId, command);
        return ResponseEntity.ok(departmentDto);
    }

    @PutMapping("{departmentId}/appoint-department-manager/{workerId}")
    @Operation(summary = "Appoint department manager", description = "Update department's manager attribute.")
    public ResponseEntity<DepartmentDto> appointDepartmentManager(
            @PathVariable("departmentId")
            @Parameter(description = "ID of the Department", example = "5")
            long departmentId,
            @PathVariable("workerId")
            @Parameter(description = "ID of the Worker appointed to department manager", example = "2")
            long workerId) {
        DepartmentDto departmentDto = service.appointManager(departmentId, workerId);
        return ResponseEntity.ok(departmentDto);
    }
    
}