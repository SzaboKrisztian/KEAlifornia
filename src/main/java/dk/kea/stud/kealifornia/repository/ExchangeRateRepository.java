package dk.kea.stud.kealifornia.repository;

import dk.kea.stud.kealifornia.model.ExchangeRate;
import dk.kea.stud.kealifornia.model.Hotel;
import dk.kea.stud.kealifornia.model.RoomCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ExchangeRateRepository {

    @Autowired
    private JdbcTemplate jdbc;

    public List<ExchangeRate> getAllExchangeRates() {
        List<ExchangeRate> result = new ArrayList<>();

        String query = "SELECT * FROM exchange_rates;";
        SqlRowSet rs = jdbc.queryForRowSet(query);

        while (rs.next()) {
            result.add(extractNextExchangeRateFromRowSet(rs));
        }

        return result;
    }
    public ExchangeRate getExchangeRateById(int id) {
        ExchangeRate result = null;

        String query = "SELECT * FROM exchange_rates where id = ?;";
        SqlRowSet rs = jdbc.queryForRowSet(query,id);


        if (rs.first()) {
            result = extractNextExchangeRateFromRowSet(rs);
        }

        return result;
    }

    private ExchangeRate extractNextExchangeRateFromRowSet(SqlRowSet rs) {
        ExchangeRate result = new ExchangeRate();

        result.setId(rs.getInt("id"));
        result.setCurrencyName(rs.getString("currency_name"));
        result.setExchangeRate(rs.getDouble("exchange_rate"));

        return result;
    }

    public void addExchangeRate(ExchangeRate exchangeRate) {
        jdbc.update("INSERT INTO exchange_rates(currency_name, exchange_rate) VALUES " +
                        "(?, ?);", exchangeRate.getCurrencyName(), exchangeRate.getExchangeRate());
    }

    public void deleteExchangeRate(int id) {
        jdbc.update("DELETE FROM exchange_rate WHERE id = ?;", id);
    }

    public void editExchangeRate(ExchangeRate exchangeRate) {
        PreparedStatementCreator psc = new PreparedStatementCreator() {

            @Override
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement("UPDATE exchange_rate set currency_name = ? , exchange_rate = ? WHERE id = ?");
                ps.setString(1, exchangeRate.getCurrencyName());
                ps.setDouble(2, exchangeRate.getExchangeRate());
                ps.setInt(3, exchangeRate.getId());
                return ps;
            }
        };

        jdbc.update(psc);
    }
}
