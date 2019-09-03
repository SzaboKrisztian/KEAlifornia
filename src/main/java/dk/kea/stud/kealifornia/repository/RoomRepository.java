package dk.kea.stud.kealifornia.repository;

import dk.kea.stud.kealifornia.model.Room;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

public class RoomRepository {

  @Autowired
  private JdbcTemplate jdbc;
  @Autowired
  private RoomCategoryRepository roomCategoryRepo;

  public Room findRoomtById(int id) {
    Room result = null;

    String query = "SELECT * FROM rooms WHERE id = ?";
    SqlRowSet rs = jdbc.queryForRowSet(query, id);

    if (rs.first()) {
      result = extractNextRoomFromRowSet(rs);
    }

    return result;
  }

  private Room extractNextRoomFromRowSet(SqlRowSet rs) {
    Room result = new Room();

    result.setId(rs.getInt("id"));
    result.setRoomCategory(roomCategoryRepo.findRoomCategoryById(rs.getInt("room_cat_id")));
    result.setRoomNumber(rs.getString("room_number"));

    return result;
  }

  public void addRoom(Room room) {
    jdbc.update("INSERT INTO rooms(room_cat_id, room_number) VALUES (?, ?);",
        room.getRoomCategory(), room.getRoomNumber());
  }

  public void deleteRoom(int id) {
    jdbc.update("DELETE FROM rooms WHERE id = ?;", id);
  }
}
