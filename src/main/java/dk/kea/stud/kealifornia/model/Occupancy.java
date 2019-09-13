package dk.kea.stud.kealifornia.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Occupancy {
  private int id;
  private Room room;
  private Guest guest;
  private LocalDate checkIn;
  private LocalDate checkOut;
  private double exchangeRate;
  private int currencyId;

  public double getExchangeRate() {
    return exchangeRate;
  }

  public void setExchangeRate(double exchangeRate) {
    this.exchangeRate = exchangeRate;
  }

  public int getCurrencyId() {
    return currencyId;
  }

  public void setCurrencyId(int currencyId) {
    this.currencyId = currencyId;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public Room getRoom() {
    return room;
  }

  public void setRoom(Room room) {
    this.room = room;
  }

  public Guest getGuest() {
    return guest;
  }

  public void setGuest(Guest guest) {
    this.guest = guest;
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

  public int getNoNights() {
    if (checkIn != null && checkOut != null) {
      return (int) ChronoUnit.DAYS.between(checkIn, checkOut);
    }
    else return 0;
  }
}
