ALTER TABLE IF EXISTS workers 
  ADD department_id bigint;

ALTER TABLE IF EXISTS workers
  ADD CONSTRAINT fk_department FOREIGN KEY (department_id) REFERENCES departments (id);

ALTER TABLE departments
  ADD manager_id bigint;

ALTER TABLE departments
ADD CONSTRAINT fk_manager FOREIGN KEY (manager_id) REFERENCES workers (id);