package timesheet.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class DepartmentDto {
    @Schema(description="department ID", example = "3")    
    private long id;

    @Schema(description="department name", example = "Production")
    private String name;

    @Schema(description="worker ID of manager", example = "5")
    private Long managerId;

    @Schema(description="name of manager", example = "Okos Toni")
    private String managerName;
}
