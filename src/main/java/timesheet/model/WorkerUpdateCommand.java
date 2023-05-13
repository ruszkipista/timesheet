package timesheet.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import timesheet.validation.ValidValueOfEnum;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkerUpdateCommand {
    @NotNull(message = "Contract type can not be null")
    @ValidValueOfEnum(enumClass = ContractType.class)
    @Schema(description="worker's contract type", example = "FULL_TIME_EMPLOYEE")
    private String contractType;
}