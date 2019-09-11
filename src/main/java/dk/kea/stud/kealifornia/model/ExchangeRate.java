package dk.kea.stud.kealifornia.model;

public class ExchangeRate {
    private int id;
    private String currencyName;
    private double exchangeRate;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCurrencyName() {
        return currencyName;
    }

    public void setCurrencyName(String currencyName) {
        this.currencyName = currencyName;
    }

    public double getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(double exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    public ExchangeRate(int id, String currencyName, double exchangeRate) {
        this.id = id;
        this.currencyName = currencyName;
        this.exchangeRate = exchangeRate;
    }

    public ExchangeRate()
    {

    }

    @Override
    public String toString() {
        return "ExchangeRate{" +
                "id=" + id +
                ", currencyName='" + currencyName + '\'' +
                ", exchangeRate=" + exchangeRate +
                '}';
    }
}
