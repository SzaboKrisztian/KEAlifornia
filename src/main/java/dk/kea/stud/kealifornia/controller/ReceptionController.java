package dk.kea.stud.kealifornia.controller;

import dk.kea.stud.kealifornia.AppGlobals;
import dk.kea.stud.kealifornia.Helper;
import dk.kea.stud.kealifornia.model.CheckInForm;
import dk.kea.stud.kealifornia.model.Guest;
import dk.kea.stud.kealifornia.model.Occupancy;
import dk.kea.stud.kealifornia.model.Room;
import dk.kea.stud.kealifornia.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
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
  @Autowired
  private GuestRepository guestRepo;
  @Autowired
  private Helper helper;

  @GetMapping("/admin/findBooking")
  public String findBooking() {
    return "reception/findBooking.html";
  }

  @PostMapping("/admin/findBooking")
  public String checkIn(@RequestParam String bookingRefNo, Model model) {
    CheckInForm data = new CheckInForm();

    data.setAvailableRoomsForEachCategory(occupancyRepo.getAvailableRoomsForAllCategories());
    data.setSelectedRooms(new ArrayList<>());

    model.addAttribute("data", data);
    model.addAttribute("roomRepo", roomRepo);
    model.addAttribute("roomCatRepo", roomCategoryRepo);
    model.addAttribute("booking", bookingRepo.findBookingByRefNo(bookingRefNo));

    return "/reception/bookingCheckIn.html";
  }


  @PostMapping("/admin/checkIn/{bookingId}")
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
    guestRepo.addGuest(occupancy.getGuest());
    bookingRepo.deleteBooking(bookingId);

    return "redirect:/admin/findBooking";
  }

  @GetMapping("/admin/noBooking")
  public String chooseDates() {
    return "/reception/noBookingDates.html";
  }

  @PostMapping("/admin/noBooking/selectRooms")
  public String processDates(@RequestParam(name = "checkin") String checkin,
                             @RequestParam(name = "checkout") String checkout,
                             Model model) {
    Occupancy occupancy = new Occupancy();
    try {
      occupancy.setCheckIn(LocalDate.parse(checkin, AppGlobals.DATE_FORMAT));
      occupancy.setCheckOut(LocalDate.parse(checkout, AppGlobals.DATE_FORMAT));
    } catch (DateTimeParseException e) {
      model.addAttribute("error", "format");
      return "/reception/noBookingDates.html";
    }

    if (!occupancy.getCheckIn().isBefore(occupancy.getCheckOut())) {
      model.addAttribute("error", "dates");
      return "/reception/noBookingDates.html";
    }

    CheckInForm data = new CheckInForm();

    data.setAvailableRoomsForEachCategory(occupancyRepo.getAvailableRoomsForAllCategories());
    data.setSelectedRooms(new ArrayList<>());

    model.addAttribute("data", data);
    model.addAttribute("roomRepo", roomRepo);
    model.addAttribute("roomCatRepo", roomCategoryRepo);
    model.addAttribute("totalAvailable", helper.countAvailableRoomsForPeriodForHotel(occupancy.getCheckIn(),occupancy.getCheckOut()));
    model.addAttribute("occupancy", occupancy);

    return "/reception/noBookingRooms.html";
  }

  @PostMapping("/admin/noBooking/guestDetails")
  public String guestDetails(@ModelAttribute("occupancy") Occupancy occupancy,
                             @ModelAttribute("data") CheckInForm data,
                             Model model) {
    model.addAttribute("data", data);
    model.addAttribute("occupancy", occupancy);
    model.addAttribute("roomcatrepo", roomCategoryRepo);
    model.addAttribute("guest", new Guest());
    model.addAttribute("data", data);
    return "/reception/noBookingGuest.html";
  }

  @PostMapping("/admin/noBooking/checkIn")
  public String summary(@ModelAttribute("occupancy") Occupancy occupancy,
                        @ModelAttribute("guest") Guest guest,
                        @ModelAttribute("dob") String dob,
                        @ModelAttribute("data") CheckInForm data,
                        Model model) {
    model.addAttribute("total", calculateTotalCost(occupancy, data.getSelectedRooms()));
    model.addAttribute("roomRepo", roomRepo);
    try {
      guest.setDateOfBirth(LocalDate.parse(dob, AppGlobals.DATE_FORMAT));
      occupancy.setGuest(guest);
      model.addAttribute("occupancy", occupancy);
      model.addAttribute("data", data);
      return "/reception/noBookingSummary.html";
    } catch (DateTimeParseException e) {
      model.addAttribute("occupancy", occupancy);
      model.addAttribute("data", data);
      model.addAttribute("guest", guest);
      model.addAttribute("error", "format");
      return "/reception/noBookingGuest.html";
    }
  }

  @PostMapping("/admin/noBooking/checkIn/confirmed")
  public String updateDatabase(@ModelAttribute("occupancy") Occupancy occupancy,
                               @ModelAttribute("data") CheckInForm data,
                               Model model) {
    guestRepo.addGuest(occupancy.getGuest());
    for (Room room : occupancyRepo.convertStringSelectedRooms(data.getSelectedRooms())) {
      occupancy.setRoom(room);
      occupancyRepo.addOccupancy(occupancy);
    }

    model.addAttribute("roomRepo", roomRepo);
    model.addAttribute("occupancy", occupancy);
    model.addAttribute("data", data);
    model.addAttribute("total", calculateTotalCost(occupancy, data.getSelectedRooms()));
    return "/reception/noBookingConfirmed.html";
  }

  @GetMapping("/admin/check-out/rooms")
  public String showAllOccupancy(Model model) throws Exception {
    List<Occupancy> occupancyList = occupancyRepo.getAllOccupancies();
    model.addAttribute("occupancies", occupancyList);
    return "/reception/check-out-rooms";
  }

  //TODO delete guests
  @PostMapping("/admin/check-out/rooms/save")
  public String checkOutRooms(@RequestParam("occupancyChecked") List<String> selectedRooms){
    if(selectedRooms != null){
      for(String occupancy : selectedRooms){
        int occupancyId = Integer.parseInt(occupancy);
        occupancyRepo.deleteOccupancy(occupancyId);
      }
    }
    return "redirect:/admin/check-out/rooms";
  }

  @GetMapping("/admin/check-out/guest")
  public String showAllGuests(Model model) throws Exception {
    List<Occupancy> occupancies = occupancyRepo.getAllOccupancies();
    model.addAttribute("occupancies", occupancies);
    return "/reception/check-out-guest";
  }

  @PostMapping("/admin/check-out/guest/save")
  public String checkOutGuest(@RequestParam("selectedGuest") int guestId){
    for (Occupancy occupancy:occupancyRepo.getAllOccupancies()) {
      if (occupancy.getGuest().getId() == guestId) {
        occupancyRepo.deleteOccupancy(occupancy.getId());
      }
    }
    return "redirect:/admin/check-out/guest";
  }

  private int calculateTotalCost(Occupancy occupancy, List<String> selectedRooms) {
    int total = 0;
    long noOfDays = ChronoUnit.DAYS.between(occupancy.getCheckIn(), occupancy.getCheckOut());
    for (String roomId : selectedRooms) {

      total += roomRepo.findRoomById(Integer.parseInt(roomId)).getRoomCategory().getPricePerNight() * noOfDays;
    }
    return total;
  }
}
