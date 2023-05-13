package timesheet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import timesheet.model.Department;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
}
