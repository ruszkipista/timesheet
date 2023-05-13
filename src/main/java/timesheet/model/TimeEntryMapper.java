package timesheet.model;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import timesheet.repository.DepartmentRepository;
import timesheet.repository.WorkerRepository;

@Mapper(componentModel = "spring")
public abstract class TimeEntryMapper {
    @Autowired
    DepartmentRepository departmentRepo;
    @Autowired
    WorkerRepository workerRepo;

    @Mapping(target = "departmentId", expression = "java( getDepartmentId(entity) )")
    @Mapping(target = "workerId", expression = "java( getWorkerId(entity) )")
    public abstract TimeEntryDto toDto(TimeEntry entity);
    public abstract List<TimeEntryDto> toDto(List<TimeEntry> entities);

    @Mapping(target = "department", expression = "java( departmentRepo.getReferenceById(command.getDepartmentId()) )")
    @Mapping(target = "worker", expression = "java( workerRepo.getReferenceById(command.getWorkerId()) )")
    public abstract TimeEntry fromCreateCommand(TimeEntryCreateCommand command);

    protected Long getDepartmentId(TimeEntry entity) {
        if (entity.getDepartment() == null) return null;
        return entity.getDepartment().getId();
    }

    protected Long getWorkerId(TimeEntry entity) {
        if (entity.getWorker() == null) return null;
        return entity.getWorker().getId();
    }
}
