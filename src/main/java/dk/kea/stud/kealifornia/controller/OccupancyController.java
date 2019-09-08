package dk.kea.stud.kealifornia.controller;

import dk.kea.stud.kealifornia.Helper;
import dk.kea.stud.kealifornia.repository.BookingRepository;
import dk.kea.stud.kealifornia.repository.OccupancyRepository;
import dk.kea.stud.kealifornia.repository.RoomCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.DateFormatSymbols;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
public class OccupancyController {

  @Autowired
  BookingRepository bookingRepo;
  @Autowired
  OccupancyRepository occupancyRepo;
  @Autowired
  RoomCategoryRepository roomCategoryRepo;
  @Autowired
  Helper helper;

  @GetMapping("/test")
  public String showDefault(Model model) {
    model.addAttribute("padding", LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonthValue(), 1).getDayOfWeek().getValue() % 7);
    model.addAttribute("calendar", getAvailabilityCalendar(LocalDate.now().getYear(), LocalDate.now().getMonthValue(), 0));
    model.addAttribute("months", generateMonths());
    model.addAttribute("monthNames", new DateFormatSymbols().getMonths());
    model.addAttribute("roomCategories", roomCategoryRepo.getAllRoomCategories());
    return "reception/pickdate.html";
  }

  @PostMapping("/test")
  public String showParticular(Model model,
                               @RequestParam("month") String monthData,
                               @RequestParam("category") int category) {
    String[] splitMonthData = monthData.split(", ");
    int year = Integer.parseInt(splitMonthData[0]);
    int month = Integer.parseInt(splitMonthData[1]);
    model.addAttribute("padding", LocalDate.of(year, month, 1).getDayOfWeek().getValue() % 7);
    model.addAttribute("calendar", getAvailabilityCalendar(year, month, category));
    model.addAttribute("months", generateMonths());
    model.addAttribute("monthNames", new DateFormatSymbols().getMonths());
    model.addAttribute("roomCategories", roomCategoryRepo.getAllRoomCategories());
    return "reception/pickdate.html";
  }

  private List<List<Integer>> generateMonths() {
    List<List<Integer>> result = new ArrayList<>();
    for (int i = 0; i < 24; i++) {
      List<Integer> entry = new ArrayList<>();
      int monthNumber = (LocalDate.now().getMonthValue() + i) % 12 == 0 ? 12 : ((LocalDate.now().getMonthValue() + i) % 12);
      entry.add(LocalDate.now().getYear() + (LocalDate.now().getMonthValue() + i - 1) / 12);
      entry.add(monthNumber);
      result.add(entry);
    }
    return result;
  }

  private List<LocalDate> generateDays(int year, int month) {
    List<LocalDate> result = new ArrayList<>();
    int monthLength = LocalDate.of(year, month, 1).lengthOfMonth();
    for (int i = 1; i <= monthLength; i++) {
      result.add(LocalDate.of(year, month, i));
    }
    return result;
  }

  private Map<LocalDate, Integer> getAvailabilityCalendar(int year, int month, int category) {
    Map<LocalDate, Integer> result = new LinkedHashMap<>();
    List<LocalDate> dates = generateDays(year, month);
    for (LocalDate day: dates) {
      Map<Integer, Integer> availability = helper.countAvailableRoomsForPeriod(day, day.plusDays(1));
      int noRoomsAvailable = 0;
      if (category == 0) {
        for (int noRoomsInCat: availability.values()) {
          noRoomsAvailable += noRoomsInCat;
        }
      } else {
        noRoomsAvailable = availability.get(category);
      }
      result.put(day, noRoomsAvailable);
    }

    return result;
  }
}
