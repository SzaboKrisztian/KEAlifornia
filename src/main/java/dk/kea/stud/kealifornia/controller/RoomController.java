package dk.kea.stud.kealifornia.controller;

import dk.kea.stud.kealifornia.model.Room;
import dk.kea.stud.kealifornia.repository.RoomCategoryRepository;
import dk.kea.stud.kealifornia.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class RoomController {
    @Autowired
    private RoomRepository roomRepo;
    @Autowired
    private RoomCategoryRepository roomCategoryRepo;

    @GetMapping("manager/rooms")
    public String findAllRooms(Model model) {
        model.addAttribute("rooms", roomRepo.findAllRooms());
        return (""); //nu stiu cum o sa se cheme HTML-urile
    }

    @GetMapping("manager/rooms/{id}")
    public String findRoom(@PathVariable(name = "id") int id, Model model) {
        model.addAttribute("room", roomRepo.findRoomById(id));
        return ("");
    }

    @PostMapping("manager/rooms/delete")
    public String deleteRoom(@PathVariable(name = "id") int id, Model model) {
        Room room = roomRepo.findRoomById(id);
        if (roomRepo.canDelete(room)) {
            roomRepo.deleteRoom(id);
            return "";
        } else
            return "";
    }

    @PostMapping("/admin/edit/")
    public String editRoom(@ModelAttribute Room room,
                           @RequestParam String roomNumber,
                           @RequestParam String roomCategoryId, Model model){
        String roomId = String.valueOf(room.getId());
        Room currentRoom = roomRepo.findRoomById(Integer.parseInt(roomId));
        Room roomAlreadyExists = roomRepo.findRoomByNumber(roomNumber);
        if (roomAlreadyExists!= null && !currentRoom.getRoomNumber().equals(roomAlreadyExists.getRoomNumber())){
            String error = "room-already-exists";
            model.addAttribute("error", error);
            model.addAttribute("room", currentRoom);
            model.addAttribute("categories", roomCategoryRepo.getAllRoomCategories());
            return "edit-room";
        }
        roomRepo.updateRoom(roomCategoryId,roomNumber,roomId);
        return "redirect:/admin/room";
    }

    @PostMapping("/admin/rooms/delete")
    public String deleteRoom(@RequestParam("roomId") String roomId) {
        Room room = roomRepo.findRoomById(Integer.parseInt(roomId));
            roomRepo.deleteRoom(Integer.parseInt(roomId));
            return "redirect:/admin/room";
    }

    @GetMapping("/manager/rooms/edit/{id}")
    public String editRoom(@PathVariable("id") int id, Model model) {
        Room selectedRoom = roomRepo.findRoomById(id);
        boolean hasOccupancy = !roomRepo.canDelete(selectedRoom);
        model.addAttribute("categories", roomCategoryRepo.getAllRoomCategories());
        model.addAttribute("room", new Room());
        return "room-add";
    }

    @PostMapping("/admin/rooms/save")
    public String saveRoom(@RequestParam("roomNumber") String roomNumber,
                           @RequestParam("roomCategoryId") String roomCategoryId,
                           Model model) {
        if(!roomRepo.checkRoom(roomNumber)){
            String error = "room-already-exists";
            model.addAttribute("error", error);
            model.addAttribute("categories", roomCategoryRepo.getAllRoomCategories());
            model.addAttribute("room", new Room());
            return "room-add";
        }
        roomRepo.addRoom(roomCategoryId,roomNumber);
        return "redirect:/admin/room";
    }

}
