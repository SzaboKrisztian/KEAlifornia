package dk.kea.stud.kealifornia.repository;

import dk.kea.stud.kealifornia.model.Booking;
import dk.kea.stud.kealifornia.model.Guest;
import dk.kea.stud.kealifornia.model.Occupancy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Repository
public class OccupancyRepository {

  @Autowired
  private JdbcTemplate jdbc;
  @Autowired
  private RoomRepository roomRepo;
  @Autowired
  private GuestRepository guestRepo;
  @Autowired
  private RoomCategoryRepository roomCategoryRepo;
  @Autowired
  private BookingRepository bookingRepo;

  public Occupancy findOccupancyById(int id) {
    Occupancy result = null;

    String query = "SELECT * FROM occupancies WHERE id = ?";
    SqlRowSet rs = jdbc.queryForRowSet(query, id);

    if (rs.first()) {
      result = extractNextOccupancyFromRowSet(rs);
    }

    return result;
  }

  public List<Occupancy> getAllOccupancies() {
    List<Occupancy> result = new ArrayList<>();

    String query = "SELECT * FROM occupancies;";
    SqlRowSet rs = jdbc.queryForRowSet(query);

    while (rs.next()) {
      result.add(extractNextOccupancyFromRowSet(rs));
    }

    return result;
  }

  private Occupancy extractNextOccupancyFromRowSet(SqlRowSet rs) {
    Occupancy result = new Occupancy();

    result.setId(rs.getInt("id"));
    result.setRoom(roomRepo.findRoomById(rs.getInt("room_id")));
    result.setGuest(guestRepo.findGuestById(rs.getInt("guest_id")));
    result.setCheckIn(rs.getDate("check_in").toLocalDate());
    result.setCheckOut(rs.getDate("check_out").toLocalDate());

    return result;
  }

  public Occupancy addOccupancy(Occupancy occupancy) {
    Guest guest = guestRepo.addGuest(occupancy.getGuest());

    PreparedStatementCreator psc = new PreparedStatementCreator() {
      @Override
      public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("INSERT INTO occupancies " +
            "(room_id, guest_id, check_in, check_out) VALUES (?, ?, ?, ?)", new String[]{"id"});
        ps.setInt(1, occupancy.getRoom().getId());
        ps.setInt(2, guest.getId());
        ps.setDate(3, Date.valueOf(occupancy.getCheckIn()));
        ps.setDate(4, Date.valueOf(occupancy.getCheckOut()));
        return ps;
      }
    };

    KeyHolder key = new GeneratedKeyHolder();
    jdbc.update(psc, key);

    occupancy.setId(key.getKey().intValue());

    return occupancy;
  }

  public void deleteOccupancy(Occupancy occupancy) {
    jdbc.update("DELETE FROM occupancies WHERE id = ?;");
  }

  public List<Integer> getAvailableRoomsForCategory(int category) {
    List<Integer> result = new ArrayList<>();

    String query = "SELECT id FROM rooms WHERE room_cat_id = ? AND " +
        "id NOT IN (SELECT room_id FROM occupancies)";
    SqlRowSet rs = jdbc.queryForRowSet(query, category);

    while (rs.next()) {
      result.add(Integer.valueOf(rs.getInt("id")));
    }

    return result;
  }

  public HashMap<Integer, List<Integer>> getAvailableRoomsForAllCategories() {
    HashMap<Integer, List<Integer>> result = new HashMap<>();

    for (Integer category : roomCategoryRepo.getAllRoomIntCategories()) {
      result.put(category, getAvailableRoomsForCategory(category));
    }

    return result;
  }

  public List<Occupancy> convertStringSelectedRooms(List<String> selectedRooms,
                                                    int bookingId) {
    Booking booking = bookingRepo.findBookingById(bookingId);
    List<Occupancy> occupiedRooms = new ArrayList<>();

    if (selectedRooms != null) {
      for (String id : selectedRooms) {
        Occupancy occupancy = new Occupancy();
        occupancy.setRoom(roomRepo.findRoomById(Integer.parseInt(id)));
        occupancy.setGuest(booking.getGuest());
        occupancy.setCheckIn(booking.getCheckIn());
        occupancy.setCheckOut(booking.getCheckOut());

        occupiedRooms.add(occupancy);
      }
      return occupiedRooms;
    }
    return null;
  }
}
