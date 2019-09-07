package dk.kea.stud.kealifornia.controller;

import dk.kea.stud.kealifornia.AppGlobals;
import dk.kea.stud.kealifornia.model.CheckInForm;
import dk.kea.stud.kealifornia.model.Guest;
import dk.kea.stud.kealifornia.model.Occupancy;
import dk.kea.stud.kealifornia.model.Room;
import dk.kea.stud.kealifornia.repository.BookingRepository;
import dk.kea.stud.kealifornia.repository.OccupancyRepository;
import dk.kea.stud.kealifornia.repository.RoomCategoryRepository;
import dk.kea.stud.kealifornia.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;


@Controller
@SessionAttributes({"occupancy", "data"})
public class ReceptionController {
  @Autowired
  private BookingRepository bookingRepo;
  @Autowired
  private OccupancyRepository occupancyRepo;
  @Autowired
  private RoomRepository roomRepo;
  @Autowired
  private RoomCategoryRepository roomCategoryRepo;

  @GetMapping("/findBooking")
  public String findBooking() {
    return "reception/findBooking.html";
  }

  @PostMapping("/findBooking")
  public String checkIn(@RequestParam String bookingRefNo, Model model) {
    CheckInForm data = new CheckInForm();

    data.setAvailableRoomsForEachCategory(occupancyRepo.getAvailableRoomsForAllCategories());
    data.setSelectedRooms(new ArrayList<>());

    model.addAttribute("data", data);
    model.addAttribute("roomRepo", roomRepo);
    model.addAttribute("roomCatRepo", roomCategoryRepo);
    model.addAttribute("booking", bookingRepo.findBookingByRefNo(bookingRefNo));

    return "reception/bookingCheckIn.html";
  }


  @PostMapping("/checkIn/{bookingId}")
  public String updateDatabase(@ModelAttribute CheckInForm data,
                               @PathVariable(name = "bookingId") int bookingId) {
    Occupancy occupancy = new Occupancy();
    occupancy.setCheckIn(bookingRepo.findBookingById(bookingId).getCheckIn());
    occupancy.setCheckOut(bookingRepo.findBookingById(bookingId).getCheckOut());
    occupancy.setGuest(bookingRepo.findBookingById(bookingId).getGuest());

    for (Room room : occupancyRepo.convertStringSelectedRooms(data.getSelectedRooms())) {
      occupancy.setRoom(room);
      occupancyRepo.addOccupancy(occupancy);
    }
    bookingRepo.deleteBooking(bookingId);

    return "redirect:/findBooking";
  }

  @GetMapping("/occupy")
  public String chooseDates() {
    return "/reception/newGuestDates.html";
  }

  @PostMapping("/occupy")
  public String processDates(@RequestParam(name = "checkin") String checkin,
                             @RequestParam(name = "checkout") String checkout,
                             Model model) {
    Occupancy occupancy = new Occupancy();
    try {
      occupancy.setCheckIn(LocalDate.parse(checkin, AppGlobals.DATE_FORMAT));
      occupancy.setCheckOut(LocalDate.parse(checkout, AppGlobals.DATE_FORMAT));
    } catch (DateTimeParseException e) {
      model.addAttribute("error", "format");
      return "/reception/newGuestDates.html";
    }

    if (!occupancy.getCheckIn().isBefore(occupancy.getCheckOut())) {
      model.addAttribute("error", "dates");
      return "/reception/newGuestDates.html";
    }

    CheckInForm data = new CheckInForm();

    data.setAvailableRoomsForEachCategory(occupancyRepo.getAvailableRoomsForAllCategories());
    data.setSelectedRooms(new ArrayList<>());

    model.addAttribute("data", data);
    model.addAttribute("roomRepo", roomRepo);
    model.addAttribute("roomCatRepo", roomCategoryRepo);
    model.addAttribute("totalAvailable", occupancyRepo.countAvailableRoomsForPeriod(occupancy.getCheckIn(),
        occupancy.getCheckOut()));
    model.addAttribute("occupancy", occupancy);

    return "/reception/newGuestCheckIn.html";
  }

  //this route has a problem
  @PostMapping("/occupy/confirm")
  public String guestDetails(@ModelAttribute("occupancy") Occupancy occupancy,
                             @ModelAttribute("data")CheckInForm data,
                             Model model) {
    model.addAttribute("data", data);
    model.addAttribute("occupancy", occupancy);
    model.addAttribute("roomcatrepo", roomCategoryRepo);
    model.addAttribute("guest", new Guest());
    model.addAttribute("data", data);
    return "/reception/newGuestInfo.html";
  }
//TODO
  @PostMapping("/newGuestReview")
  public String finalReview(@ModelAttribute("occupancy") Occupancy occupancy,
                            @ModelAttribute("guest") Guest guest,
                            @ModelAttribute("dob") String dob,
                            @ModelAttribute("data") CheckInForm data,
                            Model model) {
    model.addAttribute("total", calculateTotalCost(occupancy, data.getSelectedRooms()));
    model.addAttribute("roomcatrepo", roomCategoryRepo);
    try {
      guest.setDateOfBirth(LocalDate.parse(dob, AppGlobals.DATE_FORMAT));
      occupancy.setGuest(guest);
      model.addAttribute("occupancy", occupancy);
      model.addAttribute("data", data);
      return "/reception/newGuestFinalReview.html";
    } catch (DateTimeParseException e) {
      model.addAttribute("occupancy", occupancy);
      model.addAttribute("guest", guest);
      model.addAttribute("error", "format");
      return "/reception/newGuestInfo.html";
    }
  }
//TODO
  private int calculateTotalCost(Occupancy occupancy, List<String> selectedRooms) {
    int total = 0;
    return total;
  }
}
