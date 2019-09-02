package dk.kea.stud.kealifornia.model;

/**
 * Represents the user for the authentication
 */
public class User {
  private int user_id;
  private String username;
  private String password;
  private String role;
  private Boolean enabled = true;

  /**
   * @param user_id The user's id
   * @param username The user's username
   * @param password The user's password
   * @param role The user's role (Employee or Manager)
   * @param enabled The user's access status (false = user can't authenticate & true = user can authenticate)
   */
  public User(int user_id, String username, String password, String role, Boolean enabled) {
    this.user_id = user_id;
    this.username = username;
    this.password = password;
    this.role = role;
    this.enabled = enabled;
  }

  /**
   * The default constructor for the user
   */
  public User(){

  }

  /**
   * @return a string containing the user's role
   */
  public String getRole() {
    return role;
  }

  /**
   * @param role represents the user's role
   */
  public void setRole(String role) {
    this.role = role;
  }

  /**
   * @return an int representing the user's id
   */
  public int getUser_id() {
    return user_id;
  }

  /**
   * @param user_id represents the user's id
   */
  public void setUser_id(int user_id) {
    this.user_id = user_id;
  }

  /**
   * @return a string containing the user's username
   */
  public String getUsername() {
    return username;
  }

  /**
   * @param username represents the user's username
   */
  public void setUsername(String username) {
    this.username = username;
  }

  /**
   * @return a string containing the user's password
   */
  public String getPassword() {
    return password;
  }

  /**
   * @param password represents the user's password
   */
  public void setPassword(String password) {
    this.password = password;
  }

  /**
   * @return a boolean representing the access status of the user (false = user can't authenticate & true = user can authenticate)
   */
  public Boolean getEnabled() {
    return enabled;
  }

  /**
   * @param enabled represents the user access status
   */
  public void setEnabled(Boolean enabled) {
    this.enabled = enabled;
  }

  /**
   * @return a string with the user object's data
   */
  @Override
  public String toString() {
    return "User{" +
        "user_id=" + user_id +
        ", username='" + username + '\'' +
        ", password='" + password + '\'' +
        ", role='" + role + '\'' +
        ", enabled=" + enabled +
        '}';
  }
}