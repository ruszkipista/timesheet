package timesheet.model;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkerDto {
    @Schema(description="worker ID", example = "2")    
    private long id;

    @Schema(description="worker name", example = "Teszt Elek")
    private String name;

    @Schema(description="date of birth", example = "2000-01-01")
    private LocalDate dateOfBirth;
    
    @Schema(description="worker's contract type", example = "FULL_TIME_EMPLOYEE")
    private ContractType contractType;

    @Schema(description="ID of department assigned", example = "1")
    private Long departmentId;

    @Schema(description="department name", example = "Finance")
    private String departmentName;

}
