package dk.kea.stud.kealifornia.repository;

import dk.kea.stud.kealifornia.model.Booking;
import dk.kea.stud.kealifornia.model.Occupancy;
import dk.kea.stud.kealifornia.model.Room;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
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

  public Room findRoomById(int id) {
    Room result = null;

    String query = "SELECT * FROM rooms WHERE id = ?";
    SqlRowSet rs = jdbc.queryForRowSet(query, id);

    if (rs.first()) {
      result = extractNextRoomFromRowSet(rs);
    }

    return result;
  }

  public Room findRoomByNumber(String roomNumber){
    Room result = null;

    String query = "SELECT * FROM rooms where room_number = ?";
    SqlRowSet rs = jdbc.queryForRowSet(query, roomNumber);

    if(rs.first()){
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

  public void addRoom(String roomCategoryId, String roomNumber) {
    jdbc.update("INSERT INTO rooms(room_cat_id, room_number) VALUES (?, ?);",
        roomCategoryId, roomNumber);
  }

  public void deleteRoom(int id) {
    jdbc.update("DELETE FROM rooms WHERE id = ?;", id);
  }

  public boolean canDelete(Room room) {
    String query = ("SELECT COUNT(*) FROM occupancy WHERE room_id = ?;");
    SqlRowSet rs = jdbc.queryForRowSet(query, room.getId());
    rs.first();
    int noRooms = rs.getInt(1);

    return noRooms == 0;
  }

  public boolean checkRoom(String roomNumber) {
    String query = ("SELECT COUNT(*) FROM rooms WHERE room_number = ?;");
    SqlRowSet rs = jdbc.queryForRowSet(query, roomNumber);
    rs.first();
    int noRooms = rs.getInt(1);
    return noRooms == 0;
  }

  public void updateRoom(String roomCategoryId, String roomNumber, String roomId) {
    jdbc.update("UPDATE rooms SET " +
                    "room_cat_id = ?," +
                    "room_number = ? " +
                    "WHERE id = ?",
            roomCategoryId,
            roomNumber,
            roomId);
  }
}
