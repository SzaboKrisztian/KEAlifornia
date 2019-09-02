package dk.kea.stud.kealifornia.model;

import java.time.LocalDate;

public class Guest {
  private int id;
  private String firstName;
  private String lastName;
  private String email;
  private String phoneNo;
  private String documentIdNo;
  private LocalDate dateOfBirth;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPhoneNo() {
    return phoneNo;
  }

  public void setPhoneNo(String phoneNo) {
    this.phoneNo = phoneNo;
  }

  public String getDocumentIdNo() {
    return documentIdNo;
  }

  public void setDocumentIdNo(String documentIdNo) {
    this.documentIdNo = documentIdNo;
  }

  public LocalDate getDateOfBirth() {
    return dateOfBirth;
  }

  public void setDateOfBirth(LocalDate dateOfBirth) {
    this.dateOfBirth = dateOfBirth;
  }
}
