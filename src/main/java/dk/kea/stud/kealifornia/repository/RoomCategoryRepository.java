package dk.kea.stud.kealifornia.repository;

import dk.kea.stud.kealifornia.model.RoomCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import java.util.ArrayList;
import java.util.List;

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

  public List<RoomCategory> getAllRoomCategories() {
    List<RoomCategory> result = new ArrayList<>();

    String query = "SELECT * FROM room_categories;";
    SqlRowSet rs = jdbc.queryForRowSet(query);

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

    return result;
  }

  public void addRoomCategory(RoomCategory roomCategory) {
    jdbc.update("INSERT INTO room_categories(name, description, price_per_night) VALUES " +
        "(?, ?, ?);", roomCategory.getName(), roomCategory.getDescription(),
        roomCategory.getPricePerNight());
  }

  public void deleteRoomCategory(int id) {
    jdbc.update("DELETE FROM room_categories WHERE id = ?;", id);
  }
}
