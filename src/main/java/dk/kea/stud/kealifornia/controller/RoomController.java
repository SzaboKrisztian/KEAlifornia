package dk.kea.stud.kealifornia.controller;

import dk.kea.stud.kealifornia.model.Preferences;
import dk.kea.stud.kealifornia.model.Room;
import dk.kea.stud.kealifornia.repository.RoomCategoryRepository;
import dk.kea.stud.kealifornia.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
public class RoomController {
    @Autowired
    private RoomRepository roomRepo;
    @Autowired
    private RoomCategoryRepository roomCategoryRepo;

    @GetMapping("/admin/room")
    public String room(){
        return "/room/room-form.html";
    }


public void useSession(HttpServletRequest req) {
    HttpSession httpSession = req.getSession(false); //False because we do not want it to create a new session if it does not exist.

    Preferences preferences;
    if (httpSession != null) {
        preferences = (Preferences) httpSession.getAttribute("preferences");
    }
    else {
        System.out.println("nu exista");
    }

}

    @PostMapping("/admin/edit/")
    public String editRoom(@ModelAttribute Room room,
                           @RequestParam String roomNumber,
                           @RequestParam String roomCategoryId, Model model){
        String roomId = String.valueOf(room.getId());
        Room currentRoom = roomRepo.findRoomById(Integer.parseInt(roomId));
        //TODO hardcoded 1
        Room roomAlreadyExists = roomRepo.findRoomByRoomNumberForHotel(roomNumber,1);
        if (roomAlreadyExists!= null && !currentRoom.getRoomNumber().equals(roomAlreadyExists.getRoomNumber())){
            String error = "room-already-exists";
            model.addAttribute("error", error);
            model.addAttribute("room", currentRoom);
            //TODO hardcoded 1
            model.addAttribute("categories", roomCategoryRepo.getAllRoomCategoriesForHotel(1));
            return "/room/edit-room.html";
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

    @GetMapping("/admin/rooms/add")
    public String addRoom(Model model) {
        //TODO hardcoded 1
        model.addAttribute("categories", roomCategoryRepo.getAllRoomCategoriesForHotel(1));
        model.addAttribute("room", new Room());
        return "/room/room-add.html";
    }

    @PostMapping("/admin/rooms/save")
    public String saveRoom(@RequestParam("roomNumber") String roomNumber,
                           @RequestParam("roomCategoryId") String roomCategoryId,
                           Model model) {
        if(!roomRepo.checkRoom(roomNumber)){
            String error = "room-already-exists";
            model.addAttribute("error", error);
            //TODO hardcoded 1
            model.addAttribute("categories", roomCategoryRepo.getAllRoomCategoriesForHotel(1));
            model.addAttribute("room", new Room());
            return "room-add";
        }
        roomRepo.addRoom(roomCategoryId,roomNumber);
        return "redirect:/admin/room";
    }

}
