package dk.kea.stud.kealifornia.repository;

import dk.kea.stud.kealifornia.model.RoomCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class RoomCategoryRepository {

  @Autowired
  private JdbcTemplate jdbc;

  public RoomCategory findRoomCategoryById(int id) {
    RoomCategory result = null;

    String query = "SELECT * FROM room_categories WHERE id = ?";
    SqlRowSet rs = jdbc.queryForRowSet(query, id);

    if (rs.first()) {
      result = extractNextRoomCategoryFromRowSet(rs);
    }

    return result;
  }


  public List<RoomCategory> getAllRoomCategoriesForHotel(int hotelId) {
    List<RoomCategory> result = new ArrayList<>();

    String query = "SELECT * FROM room_categories WHERE hotel_id = ?;";
    SqlRowSet rs = jdbc.queryForRowSet(query, hotelId);

    while (rs.next()) {
      result.add(extractNextRoomCategoryFromRowSet(rs));
    }

    return result;
  }

  private RoomCategory extractNextRoomCategoryFromRowSet(SqlRowSet rs) {
    RoomCategory result = new RoomCategory();

    result.setId(rs.getInt("id"));
    result.setName(rs.getString("name"));
    result.setDescription(rs.getString("description"));
    result.setPricePerNight(rs.getInt("price_per_night"));
    result.setHotelId(rs.getInt("id"));
    return result;
  }

  public List<Integer> getAllRoomIntCategoriesForHotel(int hotelId) {
    List<Integer> result = new ArrayList<>();

    String query = "SELECT id FROM room_categories WHERE hotel_id = ?;";
    SqlRowSet rs = jdbc.queryForRowSet(query, hotelId);

    while (rs.next()) {
      result.add(rs.getInt("id"));
    }

    return result;
  }

  public void addRoomCategory(RoomCategory roomCategory) {
    System.out.println(roomCategory.getHotelId());
    jdbc.update("INSERT INTO room_categories(name, description, price_per_night, hotel_id) VALUES " +
            "(?, ?, ?, ?);", roomCategory.getName(), roomCategory.getDescription(),
        roomCategory.getPricePerNight(), roomCategory.getHotelId());
  }

  public void deleteRoomCategory(int id) {
    jdbc.update("DELETE FROM room_categories WHERE id = ?;", id);
  }

  public void editRoomCategory(RoomCategory roomCategory) {
    PreparedStatementCreator psc = new PreparedStatementCreator() {

      @Override
      public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("UPDATE room_categories set name = ? , description = ? , price_per_night = ? , hotel_id = ?  WHERE id = ?");
        ps.setString(1, roomCategory.getName());
        ps.setString(2, roomCategory.getDescription());
        ps.setInt(3, roomCategory.getPricePerNight());
        ps.setInt(4,roomCategory.getHotelId());
        ps.setInt(5, roomCategory.getId());
        return ps;
      }
    };

    jdbc.update(psc);
  }
}
