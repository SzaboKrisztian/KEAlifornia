package dk.kea.stud.kealifornia;

import dk.kea.stud.kealifornia.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ReceptionController {
  @Autowired
  private BookingRepository bookingRepo;

  @GetMapping("/arrival")
  public String arrival() {
    return "templates/reception/find-booking";
  }

  @PostMapping("/findBooking")
  public String findBooking(@RequestParam String bookingNo, Model model) {
    model.addAttribute(bookingRepo.findBookingByRefNo(bookingNo));

    //TODO model.addAttribute( list of available rooms )

    return "templates/reception/check-in";
  }

  //TODO
  @PostMapping("/checkIn")
  public String checkIn(@ModelAttribute CheckInForm checkInForm) {

  }


}
