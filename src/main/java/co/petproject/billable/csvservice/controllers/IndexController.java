package co.petproject.billable.csvservice.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author taiwo
 */
@Controller
public class IndexController {

    @RequestMapping("/")
    public String index() {
        return "index.html";
    }
}
