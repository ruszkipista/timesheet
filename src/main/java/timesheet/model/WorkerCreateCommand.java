package timesheet.model;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import timesheet.validation.ValidValueOfEnum;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkerCreateCommand {
    @NotBlank(message = "name can not be blank")
    @Schema(description="name of the worker", example = "Balga Todor")
    private String name;

    @NotNull
    @Past
    @Schema(description="date of birth", example = "2003-01-01")
    private LocalDate dateOfBirth;
    
    @NotNull
    @ValidValueOfEnum(enumClass = ContractType.class)
    @Schema(description="worker's contract type", example = "FULL_TIME_EMPLOYEE")
    private String contractType;

    @Schema(description="ID of assigned department", example = "5")
    private Long departmentId;
}
