package dk.kea.stud.kealifornia.model;

import java.util.List;

public class Hotel {
  private static Hotel instance;
  private List<Booking> bookings;
  private List<Occupancy> occupancies;

  private Hotel() {

  }

  public static Hotel getInstance() {
    if (instance == null) {
      instance = new Hotel();
    }

    return instance;
  }

  public List<Booking> getBookings() {
    return bookings;
  }

  public void setBookings(List<Booking> bookings) {
    this.bookings = bookings;
  }

  public List<Occupancy> getOccupancies() {
    return occupancies;
  }

  public void setOccupancies(List<Occupancy> occupancies) {
    this.occupancies = occupancies;
  }
}
