package timesheet.exception;

public class WorkerNotFoundException extends RuntimeException {
    private long id;

    public WorkerNotFoundException(long id) {
        this(id, null);
    }

    public WorkerNotFoundException(long id, Throwable cause) {
        super(String.format("Worker not found with id %d",id), cause);
        this.id = id;
    }

    public long getId() {
        return this.id;
    }
}
