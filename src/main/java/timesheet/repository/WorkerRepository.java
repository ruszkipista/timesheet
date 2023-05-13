package timesheet.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import timesheet.model.Worker;

@Repository
public interface WorkerRepository extends JpaRepository<Worker, Long> {

    @Query("SELECT w FROM Worker w WHERE LOCATE(UPPER(:part), UPPER(w.name)) > 0")
    List<Worker> findAllByNamePart(String part);
 
}
