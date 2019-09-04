package dk.kea.stud.kealifornia.controller;

import dk.kea.stud.kealifornia.model.CheckInForm;
import dk.kea.stud.kealifornia.model.Occupancy;
import dk.kea.stud.kealifornia.repository.BookingRepository;
import dk.kea.stud.kealifornia.repository.OccupancyRepository;
import dk.kea.stud.kealifornia.repository.RoomCategoryRepository;
import dk.kea.stud.kealifornia.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
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
    return "reception/find-booking";
  }

  @PostMapping("/findBooking")
  public String checkIn(@RequestParam String bookingNo, Model model) {
    CheckInForm data = new CheckInForm();

    data.setBookingId(bookingRepo.findBookingByRefNo(bookingNo).getId());
    data.setAvailableRoomsForEachCategory(occupancyRepo.getAvailableRoomsForAllCategories());
    data.setSelectedRooms(new ArrayList<>());

    model.addAttribute("data", data);
    model.addAttribute("roomRepo", roomRepo);
    model.addAttribute("roomCatRepo", roomCategoryRepo);

    return "reception/check-in";
  }


  @PostMapping("/checkIn/{bookingId}")
  public String updateDatabase(@ModelAttribute CheckInForm data,
                               @PathVariable (name = "bookingId") int bookingId) {
    for (Occupancy occupancy :
        occupancyRepo.convertStringSelectedRooms(data.getSelectedRooms(), bookingId)) {
      occupancyRepo.addOccupancy(occupancy);
    }
    bookingRepo.deleteBooking(bookingId);

    return "redirect:/findBooking";
  }

  @GetMapping("/newGuest")
  public String findRoomsForNewGuest(Model model) {
    model.addAttribute("rooms", occupancyRepo.getAvailableRoomsForAllCategories());
    return "";
  }
}
