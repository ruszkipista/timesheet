package timesheet.model;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import timesheet.repository.DepartmentRepository;

@Mapper(componentModel = "spring")
public abstract class WorkerMapper {
    @Autowired
    DepartmentRepository departmentRepo;

    @Mapping(target = "departmentId", source = "department")
    @Mapping(target = "departmentName", source = "department")
    public abstract WorkerDto toDto(Worker entity);
    public abstract List<WorkerDto> toDto(List<Worker> entities);

    @Mapping(target = "department", source = "departmentId")
    @Mapping(target = "contractType", expression = "java( ContractType.valueOf(command.getContractType()) )")
    public abstract Worker fromCreateCommand(WorkerCreateCommand command);
    
    protected Department fromLongToEntity(Long entityId) {
        if (entityId==null) return null;
        return departmentRepo.getReferenceById(entityId);
    }

    protected Long fromEntityToLong(Department entity) {
        if (entity==null) return null;
        return entity.getId();
    }

    protected String fromEntityToString(Department entity) {
        if (entity==null) return null;
        return entity.getName();
    }
}
