package dk.kea.stud.kealifornia.controller;

import dk.kea.stud.kealifornia.AppGlobals;
import dk.kea.stud.kealifornia.Helper;
import dk.kea.stud.kealifornia.model.*;
import dk.kea.stud.kealifornia.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@SessionAttributes("booking")
@ControllerAdvice
public class BookingController {
  @Autowired
  BookingRepository bookingRepo;
  @Autowired
  GuestRepository guestRepo;
  @Autowired
  RoomCategoryRepository roomCategoryRepo;
  @Autowired
  RoomRepository roomRepo;
  @Autowired
  OccupancyRepository occupancyRepo;
  @Autowired
  Helper helper;

  @Autowired
  private HotelRepository hotelRepo;

  @Autowired
  private ExchangeRateRepository exchangeRateRepo;

//  //Making hotels model global
//  @ModelAttribute("hotels")
//  public List<Hotel> getHotels(){
//    List<Hotel> hotelList= hotelRepo.getAllHotels();
//    return hotelList;
//  }
//
//  //Making currency model global
//  @ModelAttribute("currencies")
//  public List<ExchangeRate> getExchangeRates(){
//    List<ExchangeRate> exchangeRateList= exchangeRateRepo.getAllExchangeRates();
//    return exchangeRateList;
//  }


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

      model.addAttribute("available", helper.countAvailableRoomsForPeriod(booking.getCheckIn(),
          booking.getCheckOut()));
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
    Map<Integer, Integer> available = helper.countAvailableRoomsForPeriod(booking.getCheckIn(),
        booking.getCheckOut());
    if (action.equals("add")) {
      int numberOfRooms;
      try {
        numberOfRooms = Integer.parseInt(noRooms);
      } catch (NumberFormatException e) {
        model.addAttribute("available", available);
        model.addAttribute("booking", booking);
        model.addAttribute("roomcatrepo", roomCategoryRepo);
        model.addAttribute("error", "invalid");
        return "/booking/rooms.html";
      }
      int roomTypeId = Integer.parseInt(typeId);
      int noAlreadyBooked = booking.getBookedRooms().get(roomTypeId) == null ? 0 :
          booking.getBookedRooms().get(roomTypeId);
      if (available.get(roomTypeId) >= numberOfRooms + noAlreadyBooked) {
        if (booking.getBookedRooms().containsKey(roomTypeId)) {
          int oldAmount = booking.getBookedRooms().get(roomTypeId);
          booking.getBookedRooms().replace(roomTypeId, oldAmount + numberOfRooms);
        } else {
          booking.getBookedRooms().put(roomTypeId, numberOfRooms);
        }
        model.addAttribute("available", available);
        model.addAttribute("booking", booking);
        model.addAttribute("roomcatrepo", roomCategoryRepo);
        return "/booking/rooms.html";
      } else {
        model.addAttribute("available", available);
        model.addAttribute("booking", booking);
        model.addAttribute("roomcatrepo", roomCategoryRepo);
        model.addAttribute("error", "notenough");
        return "/booking/rooms.html";
      }
    } else if (action.equals("reset")) {
      booking.setBookedRooms(new HashMap<>());
      model.addAttribute("available", available);
      model.addAttribute("booking", booking);
      model.addAttribute("roomcatrepo", roomCategoryRepo);
      return "/booking/rooms.html";
    } else {
      if (booking.getBookedRooms().isEmpty()) {
        model.addAttribute("available", available);
        model.addAttribute("booking", booking);
        model.addAttribute("roomcatrepo", roomCategoryRepo);
        model.addAttribute("error", "norooms");
        return "/booking/rooms.html";
      } else {
        model.addAttribute("total", calculateTotalCost(booking));
        model.addAttribute("booking", booking);
        model.addAttribute("roomcatrepo", roomCategoryRepo);
        return "/booking/review.html";
      }
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
