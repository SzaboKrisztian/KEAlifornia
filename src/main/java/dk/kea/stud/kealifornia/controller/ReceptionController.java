package dk.kea.stud.kealifornia.controller;

import dk.kea.stud.kealifornia.model.Occupancy;
import dk.kea.stud.kealifornia.repository.BookingRepository;
import dk.kea.stud.kealifornia.repository.OccupancyRepository;
import dk.kea.stud.kealifornia.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class ReceptionController {
  @Autowired
  private BookingRepository bookingRepo;
  @Autowired
  private OccupancyRepository occupancyRepo;
  @Autowired
  private RoomRepository roomRepo;

  @GetMapping("/findBooking")
  public String findBooking() {
    return "reception/find-booking";
  }

  @ResponseBody
  @PostMapping("/findBooking")
  public String checkIn(@RequestParam String bookingNo, Model model) {
    model.addAttribute(bookingRepo.findBookingByRefNo(bookingNo));
    model.addAttribute("checkInForm", new Occupancy());
    model.addAttribute("rooms",roomRepo.findAllRooms());
    return "reception/check-in";
  }

  @PostMapping("/checkIn")
  public String updateDatabase(@ModelAttribute Occupancy checkInForm) {
    occupancyRepo.addOccupancy(checkInForm);
    return  "redirect:/index";
  }

}
