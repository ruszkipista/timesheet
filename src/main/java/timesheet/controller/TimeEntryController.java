package timesheet.controller;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

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
import timesheet.exception.TimeEntryNotFoundException;
import timesheet.model.TimeEntryCreateCommand;
import timesheet.model.TimeEntryDto;
import timesheet.model.TimeEntryUpdateCommand;
import timesheet.service.TimeEntryService;

@RestController
@AllArgsConstructor
@RequestMapping("/api/timesheetentries")
@Tag(name = "Operations on Time Sheet Entries")
public class TimeEntryController {
    private TimeEntryService service;

    @GetMapping
    @Operation(summary = "Get all time sheet entries", description = "List all time sheet entries")
    public List<TimeEntryDto> listTimeSheetEntries(
            @RequestParam(required=false) Long      departmentId,
            @RequestParam(required=false) Long      workerId,
            @RequestParam(required=false) LocalDate dateFrom,
            @RequestParam(required=false) LocalDate dateTo,
            @RequestParam(required=false) String    descriptionPart) {
        return service.getTimeSheetEntries(departmentId, workerId, dateFrom, dateTo, descriptionPart);
    }

    @GetMapping(value = "{id}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @Operation(summary = "Find time sheet entry by id", description = "Find time sheet entry by id")
    @ApiResponse(responseCode = "404", description = "Time entry not found by ID")
    public ResponseEntity<TimeEntryDto> getTimeEntryById(
            @PathVariable("id") 
            @Parameter(description = "ID of the Time sheet entry", example = "2")
            long id) {
        try {
            return ResponseEntity.ok(service.getTimeEntryById(id));
        } catch (TimeEntryNotFoundException nfe) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    @Operation(summary = "Create time sheet entry from data", description = "Create time sheet entry from sent data")
    @ApiResponse(responseCode = "201", description = "TimeEntry is created")
    @ApiResponse(responseCode = "406", description = "Provided data is not valid")
    public ResponseEntity<TimeEntryDto> createTimeEntry(
            @Valid 
            @RequestBody 
            TimeEntryCreateCommand command,
            UriComponentsBuilder uriComponentsBuilder ) {
        TimeEntryDto entityDto = service.createTimeEntry(command);
        URI locationUri = uriComponentsBuilder
            .path("/api/timesheetentries/{id}")
            .buildAndExpand(entityDto.getId())
            .toUri();
        return ResponseEntity
            .created(locationUri)
            .body(entityDto);
    }

    @PutMapping("{id}")
    @Operation(summary = "Update time sheet entry by ID", description = "Update time sheet entry's attributes from sent data.")
    public ResponseEntity<TimeEntryDto> modifyTimeEntryById(
            @PathVariable("id") 
            @Parameter(description = "ID of the TimeEntry", example = "2")
            long id,
            @Valid 
            @RequestBody 
            TimeEntryUpdateCommand command) {
        TimeEntryDto entityDto = service.updateTimeEntryById(id, command);
        return ResponseEntity.ok(entityDto);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete time sheet entry by ID", description = "Delete time sheet entry by ID")
    public void deleteTimeEntryById(
            @PathVariable("id") 
            @Parameter(description = "ID of the TimeEntry", example = "2")
            long id) {
        service.removeTimeEntryById(id);
    }

}