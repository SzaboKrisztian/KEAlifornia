package dk.kea.stud.kealifornia.repository;

import dk.kea.stud.kealifornia.model.Guest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class GuestRepository {

  @Autowired
  private JdbcTemplate jdbc;

  public Guest findGuestById(int id) {
    Guest result = null;

    String query = "SELECT * FROM guests WHERE id = ?";
    SqlRowSet rs = jdbc.queryForRowSet(query, id);

    if (rs.first()) {
      result = extractNextGuestFromRowSet(rs);
    }

    return result;
  }

  private Guest extractNextGuestFromRowSet(SqlRowSet rs) {
    Guest result = new Guest();

    result.setId(rs.getInt("id"));
    result.setFirstName(rs.getString("first_name"));
    result.setLastName(rs.getString("last_name"));
    result.setEmail(rs.getString("email"));
    result.setPhoneNo(rs.getString("phone_no"));
    result.setDocumentIdNo(rs.getString("doc_id_no"));
    Date sqlDOB = rs.getDate("date_of_birth");
    result.setDateOfBirth(sqlDOB == null ? null : sqlDOB.toLocalDate());

    return result;
  }

  public Guest addGuest(Guest guest) {
    PreparedStatementCreator psc = new PreparedStatementCreator() {
      @Override
      public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("INSERT INTO guests " +
            "(first_name, last_name, email, phone_no, doc_id_no, date_of_birth) VALUES " +
            "(?, ?, ?, ?, ?, ?);", new String[]{"id"});
        ps.setString(1, guest.getFirstName());
        ps.setString(2, guest.getLastName());
        ps.setString(3, guest.getEmail());
        ps.setString(4, guest.getPhoneNo());
        ps.setString(5, guest.getDocumentIdNo());
        ps.setDate(6, Date.valueOf(guest.getDateOfBirth()));
        return ps;
      }
    };

    KeyHolder id = new GeneratedKeyHolder();
    jdbc.update(psc, id);

    guest.setId(id.getKey().intValue());

    return guest;
  }

  public void deleteGuest(int id) {
    jdbc.update("DELETE FROM guests WHERE id = ?;", id);
  }
}
