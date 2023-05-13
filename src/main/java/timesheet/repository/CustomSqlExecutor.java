package timesheet.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

@Repository
public class CustomSqlExecutor {
    @Autowired
    EntityManagerFactory emf;

    String[] clearTables = {
            "SET FOREIGN_KEY_CHECKS=0;",
            "TRUNCATE workers;",
            "TRUNCATE departments;",
            "TRUNCATE timesheetentries;",
            "SET FOREIGN_KEY_CHECKS=1;"
    };

    String[] saveDemoData = {
        "INSERT INTO departments (department_name) VALUES ('Finance'),('Marketing'),('Production'),('IT'),('HR'),('C-suite');",
        "INSERT INTO workers (worker_name, date_of_birth, contract_type, department_id) VALUES "
        +"('Gipsz Jakab','1970-01-01','FULL_TIME_EMPLOYEE',1),"
        +"('Teszt Elek','1975-01-01','PART_TIME_EMPLOYEE',1),"
        +"('Aranyos Juliska','1980-01-01','FIXED_AMOUNT',3),"
        +"('Takaros Katinka','1985-01-01','TIME_AND_MATERIAL',2),"
        +"('Okos Toni','1990-01-01','FULL_TIME_EMPLOYEE',6);",
        "UPDATE departments SET manager_id=5;",
        "INSERT INTO timesheetentries (department_id, worker_id, start_date_time, duration_in_minutes, description) VALUES "
        +"(1,1,'2023-04-17 09:00:00',480,'bookkeeping'),"
        +"(1,1,'2023-04-18 09:00:00',480,'reporting'),"
        +"(1,1,'2023-04-19 09:00:00',480,'cost analysis'),"
        +"(1,1,'2023-04-20 09:00:00',480,'revenue accounting'),"
        +"(1,1,'2023-04-21 09:00:00',240,'bookkeeping'),"
        +"(1,1,'2023-04-21 09:00:00',240,'administration'),"       
        +"(1,2,'2023-04-17 09:00:00',240,'bookkeeping'),"
        +"(1,2,'2023-04-18 09:00:00',240,'asset accounting'),"
        +"(1,2,'2023-04-19 09:00:00',240,'product costing'),"
        +"(1,2,'2023-04-20 09:00:00',240,'accrual postings'),"
        +"(1,2,'2023-04-21 09:00:00',240,'sick leave'),"
        +"(3,3,'2023-04-17 09:00:00',480,'project IV.'),"
        +"(3,3,'2023-04-18 09:00:00',480,'project IV.'),"
        +"(3,3,'2023-04-19 09:00:00',480,'project IV.'),"
        +"(3,3,'2023-04-20 09:00:00',480,'project IV.'),"
        +"(2,4,'2023-04-18 13:00:00',180,'maintenance on production line A'),"
        +"(2,4,'2023-04-20 09:00:00',300,'maintenance on production line B'),"
        +"(6,5,'2023-04-17 09:00:00',480,'project IV. negotiations'),"
        +"(6,5,'2023-04-18 09:00:00',480,'annual leave'),"
        +"(6,5,'2023-04-19 09:00:00',480,'employee evaluations'),"
        +"(6,5,'2023-04-20 09:00:00',480,'travel to customer'),"
        +"(6,5,'2023-04-21 09:00:00',480,'administration');"
    };

    public void clearMariaDbTables() {
        executeNativeSQLQueries(clearTables);
    }

    public void saveDemoDataIntoTables() {
        executeNativeSQLQueries(saveDemoData);
    }

    private void executeNativeSQLQueries(String[] sqlCommands) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            for (String command : sqlCommands) {
                em.createNativeQuery(command).executeUpdate();
            }
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }
}
