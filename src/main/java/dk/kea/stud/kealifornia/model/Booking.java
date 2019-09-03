package dk.kea.stud.kealifornia.model;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class Booking {
  private int id;
  private String refNo;
  private Guest guest;
  private Map<RoomCategory, Integer> bookedRooms;
  private LocalDate checkIn;
  private LocalDate checkOut;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getRefNo() {
    return refNo;
  }

  public void setRefNo(String refNo) {
    this.refNo = refNo;
  }

  public Guest getGuest() {
    return guest;
  }

  public void setGuest(Guest guest) {
    this.guest = guest;
  }

  public Map<RoomCategory, Integer> getBookedRooms() {
    return bookedRooms;
  }

  public void setBookedRooms(Map<RoomCategory, Integer> bookedRooms) {
    this.bookedRooms = bookedRooms;
  }

  public LocalDate getCheckIn() {
    return checkIn;
  }

  public void setCheckIn(LocalDate checkIn) {
    this.checkIn = checkIn;
  }

  public LocalDate getCheckOut() {
    return checkOut;
  }

  public void setCheckOut(LocalDate checkOut) {
    this.checkOut = checkOut;
  }
}
