package dk.kea.stud.kealifornia.repository;

import dk.kea.stud.kealifornia.model.Booking;
import dk.kea.stud.kealifornia.model.Guest;
import dk.kea.stud.kealifornia.model.RoomCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

@Repository
public class BookingRepository {

  @Autowired
  private JdbcTemplate jdbc;
  @Autowired
  private GuestRepository guestRepo;

  public Booking findBookingById(int id) {
    Booking result = null;

    String query = "SELECT * FROM bookings WHERE id = ?";
    SqlRowSet rs = jdbc.queryForRowSet(query, id);

    if (rs.first()) {
      result = extractNextBookingFromRowSet(rs);
    }

    return result;
  }

  public Booking findBookingByRefNo(String refNo) {
    Booking result = null;

    String query = "SELECT * FROM bookings WHERE ref_no = ?";
    SqlRowSet rs = jdbc.queryForRowSet(query, refNo);

    if (rs.first()) {
      result = extractNextBookingFromRowSet(rs);
    }

    return result;
  }

  public List<Booking> getAllBookingsForHotel(int hotelId) {
    List<Booking> result = new ArrayList<>();

    String query = "SELECT DISTINCT bookings.* FROM " +
        "bookings INNER JOIN booked_rooms " +
        "ON bookings.id = booked_rooms.booking_id " +
        "INNER JOIN room_categories " +
        "ON booked_rooms.category_id = room_categories.id " +
        "WHERE room_categories.hotel_id = ?";
    SqlRowSet rs = jdbc.queryForRowSet(query, hotelId);

    while (rs.next()) {
      result.add(extractNextBookingFromRowSet(rs));
    }

    return result;
  }

  private Booking extractNextBookingFromRowSet(SqlRowSet rs) {
    Booking result = new Booking();

    result.setId(rs.getInt("id"));
    result.setGuest(guestRepo.findGuestById(rs.getInt("guest_id")));
    result.setCheckIn(rs.getDate("check_in").toLocalDate());
    result.setCheckOut(rs.getDate("check_out").toLocalDate());
    result.setRefNo(rs.getString("ref_no"));
    result.setExchangeRate(rs.getDouble("exchange_rate"));
    result.setCurrencyId(rs.getInt("currency_id"));

    Map<Integer, Integer> bookedRooms = new HashMap<>();
    String query = "SELECT * FROM booked_rooms WHERE booking_id = ?;";
    SqlRowSet rooms_rs = jdbc.queryForRowSet(query, result.getId());

    while (rooms_rs.next()) {
      bookedRooms.put(rooms_rs.getInt("category_id"),
          rooms_rs.getInt("no_of_rooms"));
    }

    result.setBookedRooms(bookedRooms);

    return result;
  }

  public Booking addBooking(Booking booking) {
    Guest updateGuest = guestRepo.addGuest(booking.getGuest());
    booking.setGuest(updateGuest);
    booking.setRefNo(generateUniqueReferenceNo());
    PreparedStatementCreator psc = new PreparedStatementCreator() {
      @Override
      public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("INSERT INTO bookings " +
            "(guest_id, check_in, check_out, ref_no, exchange_rate, currency_id) " +
            "VALUES (?, MAKEDATE(?, ?), MAKEDATE(?, ?), ?, ?, ?)", new String[]{"id"});
        ps.setInt(1, booking.getGuest().getId());
        ps.setInt(2, booking.getCheckIn().getYear());
        ps.setInt(3, booking.getCheckIn().getDayOfYear());
        ps.setInt(4, booking.getCheckOut().getYear());
        ps.setInt(5, booking.getCheckOut().getDayOfYear());
        ps.setString(6, booking.getRefNo());
        ps.setDouble(7, booking.getExchangeRate());
        ps.setInt(8, booking.getCurrencyId());
        return ps;
      }
    };

    KeyHolder key = new GeneratedKeyHolder();
    jdbc.update(psc, key);

    booking.setId(key.getKey().intValue());
    addBookedRooms(booking);

    return booking;
  }

  private void addBookedRooms(Booking booking) {
    String query = "INSERT INTO booked_rooms (booking_id, category_id, no_of_rooms) " +
        "VALUES (?, ?, ?);";
    for (Map.Entry<Integer, Integer> entry : booking.getBookedRooms().entrySet()) {
      jdbc.update(query, booking.getId(), entry.getKey(), entry.getValue());
    }
  }

  public void deleteBooking(int id) {
    jdbc.update("DELETE FROM booked_rooms WHERE booking_id = ?;", id);
    jdbc.update("DELETE FROM bookings WHERE id = ?;", id);
  }

  private String generateUniqueReferenceNo() {
    Random random = new SecureRandom();
    String characters = "abcdefghijklmnopqrstuvwxyz0123456789";
    int length = 8;
    char[] charArr = characters.toCharArray();
    StringBuilder result = null;

    boolean isUnique = false;
    while (!isUnique) {
      result = new StringBuilder();
      for (int i = 0; i < length; i++) {
        result.append(charArr[random.nextInt(charArr.length)]);
      }

      isUnique = isReferenceNoUnique(result.toString());
    }

    return result.toString();
  }

  private boolean isReferenceNoUnique(String refNo) {
    String query = "SELECT * FROM bookings WHERE ref_no = ?;";
    SqlRowSet rs = jdbc.queryForRowSet(query, refNo);

    return !rs.first();
  }
//
////  private int getTotalBookedRoomsForCategory(int category) {
////    int totalBookedRooms;
////    String query = "SELECT"
////  }
//
//  public Map<Integer, Integer> getAllBookedRooms() {
//    HashMap<Integer, Integer> result = new HashMap<>();
//
//    for (Integer category : roomCategoryRepo.getAllRoomIntCategories()) {
//      result.put(category, getBookedRoomsForCategory(category));
//    }
//
//    return result;
//  }
}