package dk.kea.stud.kealifornia.repository;

import dk.kea.stud.kealifornia.model.Room;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.util.ArrayList;
import java.util.List;

public class RoomRepository {

  @Autowired
  private JdbcTemplate jdbc;
  @Autowired
  private RoomCategoryRepository roomCategoryRepo;

  public List<Room> findAllRooms() {
    List<Room> roomsList = new ArrayList<>();

    String query = "SELECT * FROM rooms";
    SqlRowSet rs = jdbc.queryForRowSet(query);

    while(rs.next()){
      roomsList.add(extractNextRoomFromRowSet(rs));
    }
    return roomsList;
  }

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

  public boolean canDelete(Room room) {
    String query = ("SELECT COUNT(*) FROM rooms WHERE id = ?;");
    SqlRowSet rs = jdbc.queryForRowSet(query, room.getId());
    rs.first();
    int noRooms = rs.getInt(1);

    return noRooms == 0;
  }

  public void updateRoom(Room room) {
    jdbc.update("UPDATE rooms SET " +
                    "room_cat_id = ?," +
                    "room_number = ? " +
                    "WHERE id= ?",
            room.getRoomCategory(),
            room.getRoomNumber(),
            room.getId());
  }
}
