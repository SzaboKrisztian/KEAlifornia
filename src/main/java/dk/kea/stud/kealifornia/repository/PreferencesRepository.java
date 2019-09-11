package dk.kea.stud.kealifornia.repository;

import dk.kea.stud.kealifornia.model.Hotel;
import dk.kea.stud.kealifornia.model.Preferences;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

@Repository
public class PreferencesRepository {

    @Autowired
    private JdbcTemplate jdbc;

    public Preferences getDefaultPreference() {
        Preferences result = new Preferences();

        String query = "select hotels.id as hotel_id, hotels.name as hotel_name, exchange_rates.id as exchange_rate_id, currency_name, exchange_rate from hotels\n" +
                "inner join exchange_rates;";
        SqlRowSet rs = jdbc.queryForRowSet(query);


        if (rs.first()) {

            Hotel hotel = new Hotel();
            hotel.setId(rs.getInt("hotel_id"));
            hotel.setName(rs.getString("hotel_name"));
            result.setHotel(hotel);
            result.setCurrencyId(rs.getInt("exchange_rate_id"));

        }
        return result;
    }
}
