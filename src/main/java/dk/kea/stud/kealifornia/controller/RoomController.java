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

    @GetMapping("/room")
    public String room(){
        return "room-form";
    }
    @PostMapping("/manager/room")
    public String findRoom(@RequestParam(name = "getRoomNumber") String roomNumber, Model model) {
        Room room = roomRepo.findRoomByNumber(roomNumber);
        model.addAttribute("room", room);
        model.addAttribute("categories", roomCategoryRepo.getAllRoomCategories());
        return "edit-room";
    }

    @PostMapping("/manager/edit/")
    public String editRoom(@ModelAttribute Room room,
                           @RequestParam String roomNumber,
                           @RequestParam String roomCategoryId){

        String roomId = String.valueOf(room.getId());
        roomRepo.updateRoom(roomCategoryId,roomNumber,roomId);
        return "redirect:/room";
    }

    @PostMapping("/manager/rooms/delete")
    public String deleteRoom(@RequestParam("roomId") String roomId) {
//        if (roomRepo.canDelete(room)) {
            roomRepo.deleteRoom(Integer.parseInt(roomId));
            return "redirect:/room";
//        } else
//            return "";
    }

    @GetMapping("/manager/rooms/add")
    public String addRoom(Model model) {
        model.addAttribute("categories", roomCategoryRepo.getAllRoomCategories());
        model.addAttribute("room", new Room());
        return "room-add";
    }

    @PostMapping("/manager/rooms/save")
    public String saveRoom(@RequestParam("roomNumber") String roomNumber,
                           @RequestParam("roomCategoryId") String roomCategoryId) {
        roomRepo.addRoom(roomCategoryId,roomNumber);
        return "redirect:/";
    }

}
