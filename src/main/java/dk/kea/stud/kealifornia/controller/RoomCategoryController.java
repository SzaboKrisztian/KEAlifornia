package dk.kea.stud.kealifornia.controller;

import dk.kea.stud.kealifornia.model.RoomCategory;
import dk.kea.stud.kealifornia.repository.RoomCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class RoomCategoryController {

    @Autowired
    private RoomCategoryRepository roomCategoryRepo;

    @GetMapping("/admin/room-category")
    public String showAllRoomCategory(Model model) throws Exception {
        List<RoomCategory> roomCategoryList = roomCategoryRepo.getAllRoomCategories();
        model.addAttribute("roomCategory", roomCategoryList);
        return "/admin/room-category";
    }


    @GetMapping("/admin/delete-room-category/{id}")
    public String deleteRoomCategory(@PathVariable("id") int id) throws Exception {
        roomCategoryRepo.deleteRoomCategory(id);
        return "redirect:/admin/room-category";
    }

    @GetMapping("/admin/add-room-category")
    public String addRoomCategory(Model m) {
        m.addAttribute("newRoomCategory", new RoomCategory());
        return "/admin/add-room-category";
    }

    @PostMapping("/admin/add-room-category/save")
    public String saveRoomCategory(@ModelAttribute RoomCategory roomCategory, @ModelAttribute("a") String type) {
        roomCategoryRepo.addRoomCategory(roomCategory);
        return "redirect:/admin/room-category";
    }


    @GetMapping("/admin/edit-room-category/{id}")
    public String editRoomCategory(@PathVariable("id") int id, Model model) {
        RoomCategory roomCategory = roomCategoryRepo.findRoomCategoryById(id);
        model.addAttribute("editedRoomCategory", roomCategory);
        return "/admin/edit-room-category";
    }

    @PostMapping("/admin/edit-room-category/save")
    public String saveEditedRoomCategory(@ModelAttribute RoomCategory RoomCategory, @ModelAttribute("a") String type) {

        roomCategoryRepo.editRoomCategory(RoomCategory);
        return "redirect:/admin/room-catgegory";
    }
}