package timesheet.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import io.swagger.v3.oas.annotations.Hidden;
import timesheet.service.CustomService;

@Controller
@RequestMapping("/api")
public class CustomController {
    @Autowired
    CustomService service;

    @PutMapping("clear-repository")
    @ResponseStatus(HttpStatus.OK)
    @Hidden
    public void clearRepository() {
        service.clearRepository();
    }

    @PostMapping("add-demo-data")
    @ResponseStatus(HttpStatus.OK)
    @Hidden
    public void addDemoData() {
        service.addDemoDataToRepository();
    }
}
