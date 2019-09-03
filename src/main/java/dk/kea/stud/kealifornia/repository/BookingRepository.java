package dk.kea.stud.kealifornia.repository;

import dk.kea.stud.kealifornia.model.Booking;
import dk.kea.stud.kealifornia.model.RoomCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

public class BookingRepository {

  @Autowired
  private JdbcTemplate jdbc;
  @Autowired
  private GuestRepository guestRepo;
  @Autowired
  private RoomCategoryRepository roomCategoryRepo;

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

  public List<Booking> getAllBookings() {
    List<Booking> result = new ArrayList<>();

    String query = "SELECT * FROM bookings;";
    SqlRowSet rs = jdbc.queryForRowSet(query);

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

    Map<RoomCategory, Integer> bookedRooms = new HashMap<>();
    String query = "SELECT * FROM booked_rooms WHERE booking_id = ?;";
    SqlRowSet rooms_rs = jdbc.queryForRowSet(query, result.getId());

    while (rs.next()) {
      bookedRooms.put(roomCategoryRepo.findRoomCategoryById(rooms_rs.getInt("category_id")),
          rooms_rs.getInt("no_of_rooms"));
    }

    result.setBookedRooms(bookedRooms);

    return result;
  }

  public Booking addBooking(Booking booking) {
    PreparedStatementCreator psc = new PreparedStatementCreator() {
      @Override
      public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("INSERT INTO bookings " +
            "(guest_id, check_in, check_out, ref_no) VALUES (?, ?, ?, ?)", new String[]{"id"});
        ps.setInt(1, booking.getGuest().getId());
        ps.setDate(2, Date.valueOf(booking.getCheckIn()));
        ps.setDate(3, Date.valueOf(booking.getCheckOut()));
        ps.setString(4, generateUniqueReferenceNo());
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
    for (Map.Entry<RoomCategory, Integer> entry : booking.getBookedRooms().entrySet()) {
      jdbc.update(query, booking.getId(), entry.getKey().getId(), entry.getValue());
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

    return rs.first();
  }
}