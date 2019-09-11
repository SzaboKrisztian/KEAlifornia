package dk.kea.stud.kealifornia.repository;

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
public class HotelRepository {

    @Autowired
    private JdbcTemplate jdbc;

    public List<Hotel> getAllHotels() {
        List<Hotel> result = new ArrayList<>();

        String query = "SELECT * FROM hotels;";
        SqlRowSet rs = jdbc.queryForRowSet(query);

        while (rs.next()) {
            result.add(extractNextHotelFromRowSet(rs));
        }

        return result;
    }

    public Hotel getHotelById(int id) {
        Hotel result = null;

        String query = "SELECT * FROM hotels where id = ?;";
        SqlRowSet rs = jdbc.queryForRowSet(query,id);


        if (rs.first()) {
            result = extractNextHotelFromRowSet(rs);
        }

        return result;
    }

    private Hotel extractNextHotelFromRowSet(SqlRowSet rs) {
        Hotel result = new Hotel();

        result.setId(rs.getInt("id"));
        result.setName(rs.getString("name"));

        return result;
    }

    public void addHotel(Hotel hotel) {
        jdbc.update("INSERT INTO hotels where name = ?;", hotel.getName());
    }

    public void deleteHotel(int id) {
        jdbc.update("DELETE FROM hotels WHERE id = ?;", id);
    }

    public void editHotel(Hotel hotel) {
        PreparedStatementCreator psc = new PreparedStatementCreator() {

            @Override
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement("UPDATE hotel set name = ? WHERE id = ?");
                ps.setString(1, hotel.getName());
                ps.setInt(2, hotel.getId());
                return ps;
            }
        };

        jdbc.update(psc);
    }
}
