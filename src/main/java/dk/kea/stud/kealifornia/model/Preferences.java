package dk.kea.stud.kealifornia.model;

public class Preferences {
    private Hotel hotel;
    private int currencyId;

    public Preferences(Hotel hotel, int currencyId) {
        this.hotel = hotel;
        this.currencyId = currencyId;
    }

    public Preferences() {
    }

    public Hotel getHotel() {
        return hotel;
    }

    public void setHotel(Hotel hotel) {
        this.hotel = hotel;
    }

    public int getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(int currencyId) {
        this.currencyId = currencyId;
    }

    @Override
    public String toString() {
        return "Preferences{" +
                "hotel=" + hotel +
                ", currencyId=" + currencyId +
                '}';
    }
}
