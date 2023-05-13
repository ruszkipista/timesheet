package timesheet.exception;

public class DepartmentNotFoundException extends RuntimeException {
    private long id;

    public DepartmentNotFoundException(long id) {
        this(id, null);
    }

    public DepartmentNotFoundException(long id, Throwable cause) {
        super(String.format("Department not found with id %d",id), cause);
        this.id = id;
    }

    public long getId() {
        return this.id;
    }
}
