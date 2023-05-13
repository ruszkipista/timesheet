package timesheet.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import timesheet.exception.TimeEntryNotFoundException;
import timesheet.model.TimeEntry;
import timesheet.model.TimeEntryCreateCommand;
import timesheet.model.TimeEntryDto;
import timesheet.model.TimeEntryMapper;
import timesheet.model.TimeEntryUpdateCommand;
import timesheet.repository.TimeEntryRepository;

@Service
@AllArgsConstructor
public class TimeEntryService {
    private TimeEntryRepository timeEntryRepo;
    private TimeEntryMapper mapper;

    public List<TimeEntryDto> getTimeSheetEntries(
            Long      departmentId, 
            Long      workerId, 
            LocalDate dateFrom, 
            LocalDate dateTo, 
            String    descriptionPart) {
        if (departmentId ==null && workerId ==null && dateFrom ==null && dateTo ==null && descriptionPart ==null ){
            return this.mapper.toDto(timeEntryRepo.findAll());
        }
        return this.mapper.toDto(timeEntryRepo.findAllByParameters(departmentId, workerId, dateFrom, dateTo, descriptionPart));
    }

    public TimeEntryDto getTimeEntryById(long id) {
        return this.mapper.toDto(timeEntryRepo.findById(id)
            .orElseThrow(()->new TimeEntryNotFoundException(id)));
    }
    
    public TimeEntryDto createTimeEntry(TimeEntryCreateCommand command) {
        TimeEntry entry = timeEntryRepo.save(this.mapper.fromCreateCommand(command));
        return this.mapper.toDto(entry);
    }


    @Transactional
    public TimeEntryDto updateTimeEntryById(long id, TimeEntryUpdateCommand command) {
        TimeEntry entity = timeEntryRepo.findById(id)
                                    .orElseThrow(()->new TimeEntryNotFoundException(id));
        entity.setStartDateTime(command.getStartDateTime());
        entity.setDurationInMinutes(command.getDurationInMinutes());
        entity.setDescription(command.getDescription());
        return this.mapper.toDto(entity);
    }

    @Transactional
    public void removeTimeEntryById(long id) {
        timeEntryRepo.findById(id).orElseThrow(()->new TimeEntryNotFoundException(id));
        timeEntryRepo.deleteById(id);
    }

}
