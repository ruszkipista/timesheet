package timesheet.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentCreateCommand {
    @NotBlank(message = "name can not be blank")
    @Schema(description="department name", example = "Product Development")
    private String name;
}
