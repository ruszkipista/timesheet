package timesheet.model;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimeEntryDto {

    @Schema(description="ID of time sheet entry", example = "42")
    private long id;

    @Schema(description="ID of department", example = "3")
    private Long departmentId;

    @Schema(description="ID of worker", example = "2")    
    private Long workerId;

    @Schema(description="Start date+time of work", example = "2023-04-19T09:00")
    private LocalDateTime startDateTime;

    @Schema(description="Duration of work in minutes", example = "480")
    private int durationInMinutes;

    @Schema(description="Description of work", example = "machine maintenance")
    private String description;

}
