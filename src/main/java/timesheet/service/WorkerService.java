package timesheet.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import timesheet.exception.DepartmentNotFoundException;
import timesheet.exception.WorkerNotFoundException;
import timesheet.model.ContractType;
import timesheet.model.Department;
import timesheet.model.Worker;
import timesheet.model.WorkerCreateCommand;
import timesheet.model.WorkerDto;
import timesheet.model.WorkerMapper;
import timesheet.model.WorkerUpdateCommand;
import timesheet.repository.DepartmentRepository;
import timesheet.repository.WorkerRepository;

@Service
@AllArgsConstructor
public class WorkerService {
    private WorkerRepository workerRepo;
    private WorkerMapper mapper;
    private DepartmentRepository departmentRepo;

    public List<WorkerDto> getWorkers(Optional<String> namePart) {
        if (namePart.isEmpty()){
            return this.mapper.toDto(workerRepo.findAll());
        }
        return this.mapper.toDto(workerRepo.findAllByNamePart(namePart.get()));
    }

    public WorkerDto getWorkerById(long id) {
        return this.mapper.toDto(workerRepo.findById(id)
            .orElseThrow(()->new WorkerNotFoundException(id)));
    }
    
    public WorkerDto createWorker(WorkerCreateCommand command) {
        Worker worker = workerRepo.save(this.mapper.fromCreateCommand(command));
        return this.mapper.toDto(worker);
    }

    @Transactional
    public WorkerDto updateWorkerById(long id, WorkerUpdateCommand command) {
        Worker entity = workerRepo.findById(id)
                                    .orElseThrow(()->new WorkerNotFoundException(id));
        entity.setContractType(ContractType.valueOf(command.getContractType()));
        return this.mapper.toDto(entity);
    }

    @Transactional
    public WorkerDto assignWorkerToDepartment(long workerId, long departmentId) {
        Worker worker = workerRepo.getReferenceById(workerId);
        Department department = departmentRepo.getReferenceById(departmentId);
        try {
            worker.setDepartment(department);
        } catch (Exception e) {
            throw new WorkerNotFoundException(workerId, e);
        }
        try {
            Worker workerUpdated = workerRepo.save(worker);
            return this.mapper.toDto(workerUpdated);
        } catch (Exception e) {
            throw new DepartmentNotFoundException(departmentId, e);
        }
    }

    public void removeWorkerById(long id) {
        workerRepo.findById(id).orElseThrow(()->new WorkerNotFoundException(id));
        workerRepo.deleteById(id);
    }
}
