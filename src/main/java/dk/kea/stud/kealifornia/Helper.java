package dk.kea.stud.kealifornia;

import dk.kea.stud.kealifornia.model.Booking;
import dk.kea.stud.kealifornia.model.Occupancy;
import dk.kea.stud.kealifornia.model.Room;
import dk.kea.stud.kealifornia.repository.BookingRepository;
import dk.kea.stud.kealifornia.repository.OccupancyRepository;
import dk.kea.stud.kealifornia.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class Helper {
  private static Helper instance;
  @Autowired
  private BookingRepository bookingRepo;
  @Autowired
  private OccupancyRepository occupancyRepo;
  @Autowired
  private RoomRepository roomRepo;

  private Helper() {}

  public static Helper getInstance() {
    if (instance == null) {
      instance = new Helper();
    }
    return instance;
  }

  public Map<Integer, Integer> countAvailableRoomsForPeriod(LocalDate checkIn,
                                                             LocalDate checkOut) {
    Map<Integer, Integer> result = getRoomCountByCategory();

    for (Booking booking: bookingRepo.getAllBookings()) {
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

  public Map<Integer, Integer> getRoomCountByCategory() {
    Map<Integer, Integer> result = new HashMap<>();
    for (Room room: roomRepo.findAllRooms()) {
      int roomCat = room.getRoomCategory().getId();
      if (!result.containsKey(roomCat)) {
        result.put(roomCat, 1);
      } else {
        result.put(roomCat, result.get(roomCat) + 1);
      }
    }
    return result;
  }
}
