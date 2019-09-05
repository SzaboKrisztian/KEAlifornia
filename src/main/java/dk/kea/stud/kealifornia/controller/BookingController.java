package dk.kea.stud.kealifornia.controller;

import dk.kea.stud.kealifornia.AppGlobals;
import dk.kea.stud.kealifornia.model.Booking;
import dk.kea.stud.kealifornia.model.Guest;
import dk.kea.stud.kealifornia.model.RoomCategory;
import dk.kea.stud.kealifornia.repository.BookingRepository;
import dk.kea.stud.kealifornia.repository.GuestRepository;
import dk.kea.stud.kealifornia.repository.RoomCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@SessionAttributes("booking")
public class BookingController {
  @Autowired
  BookingRepository bookingRepo;
  @Autowired
  GuestRepository guestRepo;
  @Autowired
  RoomCategoryRepository roomCategoryRepo;

  @GetMapping("/book")
  public String chooseDates() {
    return "/booking/dates.html";


  }

  @PostMapping("/book")
  public String processDates(@RequestParam(name = "checkin") String checkin,
                            @RequestParam(name = "checkout") String checkout,
                            Model model) {
      Booking booking = new Booking();
      try {
        booking.setCheckIn(LocalDate.parse(checkin, AppGlobals.DATE_FORMAT));
        booking.setCheckOut(LocalDate.parse(checkout, AppGlobals.DATE_FORMAT));
      } catch (DateTimeParseException e) {
        System.out.println("error parsing");
        model.addAttribute("error", "format");
        return "/booking/dates.html";
      }

      if (!booking.getCheckIn().isBefore(booking.getCheckOut())) {
        model.addAttribute("error", "dates");
        return "/booking/dates.html";
      }

      model.addAttribute("roomcatrepo", roomCategoryRepo);
      model.addAttribute("booking", booking);
      return "/booking/rooms.html";
  }

  @PostMapping("/rooms")
  public String addRooms(@ModelAttribute("booking") Booking booking,
                         @RequestParam(name = "roomtype") String typeId,
                         @RequestParam(name = "norooms") String noRooms,
                         @RequestParam(name = "action") String action,
                         Model model) {
    if (action.equals("add")) {
      int numberOfRooms;
      try {
        numberOfRooms = Integer.parseInt(noRooms);
      } catch (NumberFormatException e) {
        model.addAttribute("booking", booking);
        model.addAttribute("roomcatrepo", roomCategoryRepo);
        model.addAttribute("error", "invalid");
        return "/booking/rooms.html";
      }
      RoomCategory chosenCat = roomCategoryRepo.findRoomCategoryById(Integer.valueOf(typeId));
      if (booking.getBookedRooms().containsKey(chosenCat.getId())) {
        int oldAmount = booking.getBookedRooms().get(chosenCat.getId());
        booking.getBookedRooms().replace(chosenCat.getId(), oldAmount + numberOfRooms);
      } else {
        booking.getBookedRooms().put(chosenCat.getId(), numberOfRooms);
      }
      model.addAttribute("booking", booking);
      model.addAttribute("roomcatrepo", roomCategoryRepo);
      return "/booking/rooms.html";
    } else if (action.equals("reset")) {
      booking.setBookedRooms(new HashMap<>());
      model.addAttribute("booking", booking);
      model.addAttribute("roomcatrepo", roomCategoryRepo);
      return "/booking/rooms.html";
    } else {
      model.addAttribute("total", calculateTotalCost(booking));
      model.addAttribute("booking", booking);
      model.addAttribute("roomcatrepo", roomCategoryRepo);
      return "/booking/review.html";
    }
  }

  @PostMapping("/confirm")
  public String guestDetails(@ModelAttribute("booking") Booking booking,
                             Model model) {
    model.addAttribute("total", calculateTotalCost(booking));
    model.addAttribute("booking", booking);
    model.addAttribute("roomcatrepo", roomCategoryRepo);
    model.addAttribute("guest", new Guest());
    return "/booking/guestinfo.html";
  }

  @PostMapping("/review")
  public String finalReview(@ModelAttribute("booking") Booking booking,
                            @ModelAttribute("guest") Guest guest,
                            @ModelAttribute("dob") String dob,
                            Model model) {
    model.addAttribute("total", calculateTotalCost(booking));
    model.addAttribute("roomcatrepo", roomCategoryRepo);
    try {
      guest.setDateOfBirth(LocalDate.parse(dob, AppGlobals.DATE_FORMAT));
      booking.setGuest(guest);
      model.addAttribute("booking", booking);
      return "/booking/finalreview.html";
    } catch (DateTimeParseException e) {
      model.addAttribute("booking", booking);
      model.addAttribute("guest", guest);
      model.addAttribute("error", "format");
      return "/booking/guestinfo.html";
    }
  }

  @PostMapping("/summary")
  public String viewSummary(@ModelAttribute("booking") Booking booking,
                            Model model) {
    bookingRepo.addBooking(booking);
    model.addAttribute("total", calculateTotalCost(booking));
    model.addAttribute("booking", booking);
    model.addAttribute("roomcatrepo", roomCategoryRepo);
    return "/booking/summary.html";
  }

  private int calculateTotalCost(Booking booking) {
    int total = 0;
    for (Map.Entry<Integer, Integer> entry: booking.getBookedRooms().entrySet()) {
      total += entry.getValue() * roomCategoryRepo.
          findRoomCategoryById(entry.getKey()).getPricePerNight();
    }
    return total;
  }
}
