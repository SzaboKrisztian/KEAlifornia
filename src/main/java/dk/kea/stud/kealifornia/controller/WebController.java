package dk.kea.stud.kealifornia.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    @GetMapping("/index")
    public String showIndex() {
        return "/index";
    }

    @GetMapping("/")
    public String showIndex2() {
        return "/index";
    }

    @GetMapping("/login")
    public String login() {
        return "/login";
    }

    @GetMapping("/admin/index")
    public String showAdminMenu() {
        return "/admin-menu";
    }


}
