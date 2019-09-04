package dk.kea.stud.kealifornia.controller;

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
    List<String> selectedRooms = new ArrayList<>();

    for (Integer category:occupancyRepo.getAvailableRoomsForAllCategories().keySet()) {
      for (Integer room:occupancyRepo.getAvailableRoomsForAllCategories().get(category)) {
        selectedRooms.add("");
      }
    }
    model.addAttribute("booking", bookingRepo.findBookingByRefNo(bookingNo));
    model.addAttribute("rooms", occupancyRepo.getAvailableRoomsForAllCategories());
    model.addAttribute("selectedRooms", selectedRooms);
    model.addAttribute("roomRepo", roomRepo);
    model.addAttribute("roomCatRepo", roomCategoryRepo);
    return "reception/check-in";
  }

  @ResponseBody
  @PostMapping("/checkIn")
  public String updateDatabase(@RequestParam ("bookingId") String id,
                               @RequestParam List<String> selectedRooms) {
    return selectedRooms.toString();
  }

  @GetMapping("/newGuest")
  public String findRoomsForNewGuest(Model model) {
    model.addAttribute("rooms", occupancyRepo.getAvailableRoomsForAllCategories());
    return "";
  }
}
