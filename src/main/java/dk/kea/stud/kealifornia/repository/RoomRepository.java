package dk.kea.stud.kealifornia.repository;

import dk.kea.stud.kealifornia.model.Room;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class RoomRepository {

  @Autowired
  private JdbcTemplate jdbc;

  @Autowired
  private RoomCategoryRepository roomCategoryRepo;

  public List<Room> findAllRoomsFromHotel(int hotelId) {
    List<Room> result = new ArrayList<>();

    String query = "SELECT rooms.* FROM " +
        "rooms INNER JOIN room_categories " +
        "ON rooms.room_cat_id = room_categories.id " +
        "WHERE room_categories.hotel_id = ?";
    SqlRowSet rs = jdbc.queryForRowSet(query, hotelId);

    while (rs.next()) {
      result.add(extractNextRoomFromRowSet(rs));
    }

    return result;
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

  public Room findRoomByRoomNumberForHotel(String roomNumber, int hotelId){
    Room result = null;

    String query = "SELECT rooms.id, rooms.room_cat_id, rooms.room_number FROM rooms " +
            "INNER JOIN room_categories " +
            "ON rooms.room_cat_id = room_categories.id " +
            "WHERE rooms.room_number = ? AND room_categories.hotel_id = ?;";
    SqlRowSet rs = jdbc.queryForRowSet(query, roomNumber,1);

    if(rs.first()){
      result = extractNextRoomFromRowSet(rs);
    }

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
    String query = ("SELECT COUNT(*) FROM occupancies WHERE room_id = ?;");
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

  private Room extractNextRoomFromRowSet(SqlRowSet rs) {
    Room result = new Room();

    result.setId(rs.getInt("id"));
    result.setRoomCategory(roomCategoryRepo.findRoomCategoryById(rs.getInt("room_cat_id")));
    result.setRoomNumber(rs.getString("room_number"));

    return result;
  }
}
