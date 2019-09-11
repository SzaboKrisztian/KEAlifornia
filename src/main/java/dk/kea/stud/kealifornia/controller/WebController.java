package dk.kea.stud.kealifornia.controller;

import dk.kea.stud.kealifornia.model.Preferences;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
public class WebController {

    @GetMapping("/index")
    public String showIndex(HttpServletRequest request, Model model) {
        HttpSession httpSession = request.getSession(false); //False because we do not want it to create a new session if it does not exist.

        Preferences preferences = new Preferences();
        if (httpSession != null) {
            preferences = (Preferences) httpSession.getAttribute("preferences");
        }
        else {
            System.out.println("nu exista");
        }

        model.addAttribute("preferences", preferences);
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

}
