package timesheet.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import timesheet.exception.DepartmentNotFoundException;
import timesheet.exception.WorkerNotFoundException;
import timesheet.model.Department;
import timesheet.model.DepartmentCreateCommand;
import timesheet.model.DepartmentDto;
import timesheet.model.DepartmentMapper;
import timesheet.model.DepartmentUpdateCommand;
import timesheet.model.Worker;
import timesheet.repository.DepartmentRepository;
import timesheet.repository.WorkerRepository;

@Service
@AllArgsConstructor
public class DepartmentService {
    private DepartmentRepository departmentRepo;
    private DepartmentMapper mapper;
    private WorkerRepository workerRepo;

    public List<DepartmentDto> getDepartments() {
        return this.mapper.toDto(departmentRepo.findAll());
    }

    public DepartmentDto getDepartmentById(long id) {
        return this.mapper.toDto(departmentRepo.findById(id)
                                     .orElseThrow(()->new DepartmentNotFoundException(id)));
    }
    
    public DepartmentDto createDepartment(DepartmentCreateCommand command) {
        Department entity = departmentRepo.save(this.mapper.fromCreateCommand(command));
        return this.mapper.toDto(entity);
    }

    @Transactional
    public DepartmentDto updateDepartmentById(long id, DepartmentUpdateCommand command) {
        Department entity = departmentRepo.findById(id)
                                    .orElseThrow(()->new DepartmentNotFoundException(id));
        entity.setName(command.getName());
        return this.mapper.toDto(entity);
    }

    @Transactional
    public DepartmentDto appointManager(long departmentId, long workerId) {
        Department department = departmentRepo.getReferenceById(departmentId);
        Worker worker = workerRepo.getReferenceById(workerId);
        try {
            department.setManager(worker);
        } catch (Exception e) {
            throw new DepartmentNotFoundException(departmentId, e);
        }
        try {
            Department departmentUpdated = departmentRepo.save(department);
            return this.mapper.toDto(departmentUpdated);
        } catch (Exception e) {
            throw new WorkerNotFoundException(workerId, e);
        }
    }
}
