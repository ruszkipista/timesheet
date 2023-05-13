package timesheet.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import timesheet.repository.CustomSqlExecutor;

@Service
public class CustomService {
    @Autowired
    CustomSqlExecutor repo;

    public void clearRepository() {
        repo.clearMariaDbTables();
    }

    public void addDemoDataToRepository() {
        repo.saveDemoDataIntoTables();
    }
}
