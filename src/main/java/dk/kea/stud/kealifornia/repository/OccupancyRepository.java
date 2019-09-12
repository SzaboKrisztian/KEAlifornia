package dk.kea.stud.kealifornia.repository;

import dk.kea.stud.kealifornia.model.Occupancy;
import dk.kea.stud.kealifornia.model.Room;
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

  public List<Occupancy> getAllOccupanciesForHotel(int hotelId) {
    List<Occupancy> result = new ArrayList<>();

    String query = "SELECT occupancies.* FROM " +
        "occupancies INNER JOIN rooms " +
        "ON occupancies.room_id = rooms.id " +
        "INNER JOIN room_categories " +
        "ON rooms.room_cat_id = room_categories.id " +
        "WHERE room_categories.hotel_id = ?";
    SqlRowSet rs = jdbc.queryForRowSet(query, hotelId);

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
    result.setExchangeRate(rs.getDouble("exchange_rate"));
    result.setCurrencyId(rs.getInt("currency_id"));

    return result;
  }

  public Occupancy addOccupancy(Occupancy occupancy) {

    PreparedStatementCreator psc = new PreparedStatementCreator() {
      @Override
      public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("INSERT INTO occupancies " +
            "(room_id, guest_id, check_in, check_out, exchange_rate, currency_id) VALUES (?, ?, ?, ?, ?, ?)", new String[]{"id"});
        ps.setInt(1, occupancy.getRoom().getId());
        ps.setInt(2, occupancy.getGuest().getId());
        ps.setDate(3, Date.valueOf(occupancy.getCheckIn()));
        ps.setDate(4, Date.valueOf(occupancy.getCheckOut()));
        ps.setDouble(5, occupancy.getExchangeRate());
        ps.setInt(6, occupancy.getCurrencyId());
        return ps;
      }
    };

    KeyHolder key = new GeneratedKeyHolder();
    jdbc.update(psc, key);

    occupancy.setId(key.getKey().intValue());

    return occupancy;
  }

  public void deleteOccupancy(int id) {
    jdbc.update("DELETE FROM occupancies WHERE id = ?;", id);
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

  public HashMap<Integer, List<Integer>> getAvailableRoomsForAllCategoriesForHotel(int hotelId) {
    HashMap<Integer, List<Integer>> result = new HashMap<>();

    for (Integer category : roomCategoryRepo.getAllRoomIntCategoriesForHotel(hotelId)) {
      result.put(category, getAvailableRoomsForCategory(category));
    }

    return result;
  }

  public List<Room> convertStringSelectedRooms(List<String> selectedRooms) {
    List<Room> occupiedRooms = new ArrayList<>();

    if (selectedRooms != null) {
      for (String id : selectedRooms) {
        occupiedRooms.add(roomRepo.findRoomById(Integer.parseInt(id)));
      }
      return occupiedRooms;
    }
    return null;
  }

}
