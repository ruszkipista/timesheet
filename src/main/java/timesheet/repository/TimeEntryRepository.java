package timesheet.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import timesheet.model.TimeEntry;

@Repository
public interface TimeEntryRepository extends JpaRepository<TimeEntry, Long> {

    @Query("""
           SELECT t FROM TimeEntry t 
             LEFT JOIN Department d ON d.id = t.department
             LEFT JOIN Worker w ON w.id = t.worker
             WHERE 
             (:departmentId IS NULL OR d.id=:departmentId) AND
             (:workerId IS NULL OR w.id=:workerId) AND
             (:dateFrom IS NULL OR CAST(t.startDateTime AS date)>=:dateFrom) AND
             (:dateTo IS NULL OR CAST(t.startDateTime AS date)<=:dateTo) AND
             (:descriptionPart IS NULL OR LOCATE(UPPER(:descriptionPart),UPPER(t.description)) > 0)
           """)
    List<TimeEntry> findAllByParameters(
        Long departmentId,
        Long workerId,
        LocalDate dateFrom,
        LocalDate dateTo,
        String descriptionPart);
}
