package dk.kea.stud.kealifornia.controller;

import dk.kea.stud.kealifornia.Helper;
import dk.kea.stud.kealifornia.model.ExchangeRate;
import dk.kea.stud.kealifornia.model.Hotel;
import dk.kea.stud.kealifornia.model.Preferences;
import dk.kea.stud.kealifornia.repository.ExchangeRateRepository;
import dk.kea.stud.kealifornia.repository.HotelRepository;
import dk.kea.stud.kealifornia.repository.PreferencesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@ControllerAdvice
public class WebController {

    @Autowired
    HotelRepository hotelRepo;

    @Autowired
    ExchangeRateRepository exchangeRateRepo;

    @Autowired
    PreferencesRepository preferencesRepository;

    @Autowired
    Helper helper;

    //Making hotels model global
    @ModelAttribute("hotels")
    public List<Hotel> getHotels(){
        List<Hotel> hotelList= hotelRepo.getAllHotels();
        return hotelList;
    }

    //Making currency model global
    @ModelAttribute("currencies")
    public List<ExchangeRate> getExchangeRates(){
        List<ExchangeRate> exchangeRateList= exchangeRateRepo.getAllExchangeRates();
        return exchangeRateList;
    }


    @PostMapping("/select-preferences")
    public String initializePreferences(HttpServletRequest req, @RequestParam("selectedHotel") int hotel_id, @RequestParam("selectedExchangeRate") int currency_id){
        HttpSession httpSession = req.getSession();
        Preferences preferences = new Preferences();

        preferences.setCurrencyId(currency_id);
        Hotel hotel = hotelRepo.getHotelById(hotel_id);
        preferences.setHotel(hotel);

        httpSession.setAttribute("preferences", preferences);
        return "redirect:/index";
    }

    @GetMapping("/index")
    public String showIndex(Model model, HttpServletRequest request) {
        Preferences preferences = helper.getPreferences(request);
        System.out.println(preferences);
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
