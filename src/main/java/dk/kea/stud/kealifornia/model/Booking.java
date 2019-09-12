package dk.kea.stud.kealifornia.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

public class Booking {
  private int id;
  private String refNo;
  private Guest guest;
  private Map<Integer, Integer> bookedRooms;
  private LocalDate checkIn;
  private LocalDate checkOut;
  private double exchangeRate;
  private int currencyId;

  public Booking() {
    this.bookedRooms = new HashMap<>();
  }

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

  public Map<Integer, Integer> getBookedRooms() {
    return bookedRooms;
  }

  public void setBookedRooms(Map<Integer, Integer> bookedRooms) {
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

  public int getNoNights() {
    if (checkIn != null && checkOut != null) {
      return (int)ChronoUnit.DAYS.between(checkIn, checkOut);
    }
    else return 0;
  }

}
