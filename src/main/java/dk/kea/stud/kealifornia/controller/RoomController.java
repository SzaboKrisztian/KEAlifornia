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

    @GetMapping("/admin/room")
    public String room(){
        return "room-form";
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

    @GetMapping("/manager/rooms/add")
    public String addRoom(Model model) {
        model.addAttribute("categories", roomCategoryRepo.getAllRoomCategories());
        model.addAttribute("room", new Room());
        return "movies/movies-add";
    }

    @PostMapping("/manager/rooms/save")
    public String saveRoom(@ModelAttribute Room room) {
        roomRepo.addRoom(room);
        return "redirect:/";
    }

    @GetMapping("/manager/rooms/edit/{id}")
    public String editRoom(@PathVariable("id") int id, Model model) {
        Room selectedRoom = roomRepo.findRoomById(id);
        boolean hasOccupancy = !roomRepo.canDelete(selectedRoom);
        model.addAttribute("categories", roomCategoryRepo.getAllRoomCategories());
        model.addAttribute("currentRoom", selectedRoom);
        model.addAttribute("hasOccupancy", hasOccupancy);
        return "";
    }

    @PostMapping("/manager/rooms/edit")
    public String updateMovie(@ModelAttribute Room room) {
        roomRepo.updateRoom(room);
        return "";
    }
}
