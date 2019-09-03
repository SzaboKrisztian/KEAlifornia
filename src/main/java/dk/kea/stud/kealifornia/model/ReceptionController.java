package dk.kea.stud.kealifornia.model;

import dk.kea.stud.kealifornia.repository.BookingRepository;
import dk.kea.stud.kealifornia.repository.OccupancyRepository;
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
  @Autowired
  private OccupancyRepository occupancyRepo;

  @GetMapping("/arrival")
  public String arrival() {
    return "templates/reception/find-booking";
  }

  @PostMapping("/findBooking")
  public String findBooking(@RequestParam String bookingNo, Model model) {
    model.addAttribute(bookingRepo.findBookingByRefNo(bookingNo));
    model.addAttribute("checkInForm", new Occupancy());
    //TODO model.addAttribute( list of available rooms for each category )

    return "templates/reception/check-in";
  }

  //TODO
  @PostMapping("/checkIn")
  public String checkIn(@ModelAttribute Occupancy checkInForm) {
    occupancyRepo.addOccupancy(checkInForm);
    return "/arrival";
  }

}
