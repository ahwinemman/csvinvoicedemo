package co.petproject.billable.csvservice.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @author taiwo
 */
@Controller
public class IndexController {

    @RequestMapping("/")
    @ApiIgnore
    public String index() {
        return "index.html";
    }
}
