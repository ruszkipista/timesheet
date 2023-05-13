package timesheet.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "timesheetentries")
public class TimeEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;

    @ManyToOne
    @JoinColumn(name = "worker_id")
    private Worker worker;

    @Column(name = "start_date_time")
    private LocalDateTime startDateTime;

    @Column(name = "duration_in_minutes")
    private int durationInMinutes;
    
    private String description;

    public TimeEntry(Department department, 
                    Worker worker, 
                    LocalDateTime startDateTime, 
                    int durationInMinutes,
                    String description) {
        this(null, department, worker, startDateTime, durationInMinutes, description);
    }

    
}
