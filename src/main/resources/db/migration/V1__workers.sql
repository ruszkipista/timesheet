CREATE TABLE workers (
    id            bigint AUTO_INCREMENT,
    worker_name   varchar(255) NOT NULL,
    date_of_birth date         NOT NULL,
    contract_type varchar(20)  NOT NULL,
    primary key (id)
);