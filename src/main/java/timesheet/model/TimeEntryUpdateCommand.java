package timesheet.model;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimeEntryUpdateCommand {

    @NotNull
    @Schema(description="Start date+time of work", example = "2023-04-19T09:00")
    private LocalDateTime startDateTime;

    @PositiveOrZero
    @Schema(description="Duration of work in minutes", example = "480")
    private int durationInMinutes;

    @NotBlank
    @Schema(description="Description of work", example = "machine maintenance")
    private String description;
    
}
