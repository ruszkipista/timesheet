CREATE TABLE timesheetentries (
    id                  bigint  AUTO_INCREMENT,
    department_id       bigint,
    worker_id           bigint,
    start_date_time     datetime     NOT NULL,
    duration_in_minutes integer      NOT NULL,
    description         varchar(255) NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (department_id) REFERENCES departments (id),
    FOREIGN KEY (worker_id) REFERENCES workers (id)
);