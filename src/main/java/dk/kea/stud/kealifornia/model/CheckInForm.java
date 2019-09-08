package dk.kea.stud.kealifornia.model;

import java.util.List;
import java.util.Map;

public class CheckInForm {
  private List<String> selectedRooms;
  private Map<Integer, List<Integer>> availableRoomsForEachCategory;

  public List<String> getSelectedRooms() {
    return selectedRooms;
  }

  public void setSelectedRooms(List<String> selectedRooms) {
    this.selectedRooms = selectedRooms;
  }

  public Map<Integer, List<Integer>> getAvailableRoomsForEachCategory() {
    return availableRoomsForEachCategory;
  }

  public void setAvailableRoomsForEachCategory(Map<Integer, List<Integer>> availableRoomsForEachCategory) {
    this.availableRoomsForEachCategory = availableRoomsForEachCategory;
  }
}
