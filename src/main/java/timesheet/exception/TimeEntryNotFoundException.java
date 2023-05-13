package timesheet.exception;

public class TimeEntryNotFoundException extends RuntimeException {
    private long id;

    public TimeEntryNotFoundException(long id) {
        this(id, null);
    }

    public TimeEntryNotFoundException(long id, Throwable cause) {
        super(String.format("TimeEntry not found with id %d",id), cause);
        this.id = id;
    }

    public long getId() {
        return this.id;
    }
}
