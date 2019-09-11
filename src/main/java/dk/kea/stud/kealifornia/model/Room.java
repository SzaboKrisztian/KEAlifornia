package dk.kea.stud.kealifornia.model;

public class Room {
    private int id;
    private RoomCategory roomCategory;
    private String roomNumber;




    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public RoomCategory getRoomCategory() {
        return roomCategory;
    }

    public void setRoomCategory(RoomCategory roomCategory) {
        this.roomCategory = roomCategory;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }


}
