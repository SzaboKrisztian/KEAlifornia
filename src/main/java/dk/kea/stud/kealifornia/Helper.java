package dk.kea.stud.kealifornia;

import dk.kea.stud.kealifornia.model.Booking;
import dk.kea.stud.kealifornia.model.Occupancy;
import dk.kea.stud.kealifornia.model.Preferences;
import dk.kea.stud.kealifornia.model.Room;
import dk.kea.stud.kealifornia.repository.BookingRepository;
import dk.kea.stud.kealifornia.repository.OccupancyRepository;
import dk.kea.stud.kealifornia.repository.PreferencesRepository;
import dk.kea.stud.kealifornia.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Component
@Scope(value = "singleton")
public class Helper {
  @Autowired
  private BookingRepository bookingRepo;
  @Autowired
  private OccupancyRepository occupancyRepo;
  @Autowired
  private RoomRepository roomRepo;

  @Autowired
  private PreferencesRepository preferencesRepo;

  public Map<Integer, Integer> countAvailableRoomsForPeriodForHotel(LocalDate checkIn,
                                                                    LocalDate checkOut,
                                                                    int hotelId) {
    Map<Integer, Integer> result = getRoomCountByCategoryForHotel(hotelId);

    for (Booking booking: bookingRepo.getAllBookingsForHotel(hotelId)) {
      if (areSchedulesConflicting(checkIn, checkOut, booking.getCheckIn(), booking.getCheckOut())) {
        for (Map.Entry<Integer, Integer> rooms: booking.getBookedRooms().entrySet()) {
          int roomCat = rooms.getKey();
          result.put(roomCat, Math.max(result.get(roomCat) - rooms.getValue(), 0));
        }
      }
    }

    for (Occupancy occupancy: occupancyRepo.getAllOccupancies()) {
      if (areSchedulesConflicting(checkIn, checkOut, occupancy.getCheckIn(), occupancy.getCheckOut())) {
        int roomCat = occupancy.getRoom().getRoomCategory().getId();
        result.put(roomCat, Math.max(result.get(roomCat) - 1, 0));
      }
    }

    return result;
  }

  private boolean areSchedulesConflicting(LocalDate newCheckin, LocalDate newCheckout,
                                          LocalDate existingCheckin, LocalDate existingCheckout) {
    return (newCheckin.isEqual(existingCheckin) || (newCheckin.isAfter(existingCheckin) && newCheckin.isBefore(existingCheckout))) ||
        (newCheckout.isAfter(existingCheckin) && (newCheckout.isBefore(existingCheckout) || newCheckout.isEqual(existingCheckout))) ||
        ((newCheckin.isEqual(existingCheckin) || newCheckin.isAfter(existingCheckin)) && (newCheckout.isBefore(existingCheckout) || newCheckout.isEqual(existingCheckout))) ||
        (newCheckin.isBefore(existingCheckin) && newCheckout.isAfter(existingCheckout));
  }

  public Map<Integer, Integer> getRoomCountByCategoryForHotel(int hotelId) {
    Map<Integer, Integer> result = new HashMap<>();
    for (Room room: roomRepo.findAllRoomsFromHotel(hotelId)) {
      int roomCat = room.getRoomCategory().getId();
      if (!result.containsKey(roomCat)) {
        result.put(roomCat, 1);
      } else {
        result.put(roomCat, result.get(roomCat) + 1);
      }
    }
    return result;
  }

  public Preferences getPreferences(HttpServletRequest request)
  {
    HttpSession httpSession = request.getSession(false); //False because we do not want it to create a new session if it does not exist.

    Preferences preferences = null;
    if(httpSession != null)
      preferences = (Preferences) httpSession.getAttribute("preferences");
    else
      preferences = preferencesRepo.getDefaultPreference();
    return preferences;
  }
}
