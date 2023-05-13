package timesheet.model;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import timesheet.repository.WorkerRepository;

@Mapper(componentModel = "spring")
public abstract class DepartmentMapper {
    @Autowired
    WorkerRepository workerRepo;

    @Mapping(target = "managerId", source = "manager")
    @Mapping(target = "managerName", source = "manager")
    public abstract DepartmentDto toDto(Department entity);
    public abstract List<DepartmentDto> toDto(List<Department> entities);
       
    public abstract Department fromCreateCommand(DepartmentCreateCommand command);

    protected Worker fromLongToWorker(Long entityId) {
        if (entityId==null) return null;
        return workerRepo.getReferenceById(entityId);
    }

    protected Long fromWorkerToLong(Worker entity) {
        if (entity==null) return null;
        return entity.getId();
    }

    protected String fromWorkerToString(Worker entity) {
        if (entity==null) return null;
        return entity.getName();
    }

}
