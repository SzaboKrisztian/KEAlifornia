package dk.kea.stud.kealifornia.controller;

import dk.kea.stud.kealifornia.model.Preferences;
import dk.kea.stud.kealifornia.model.RoomCategory;
import dk.kea.stud.kealifornia.repository.RoomCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class RoomCategoryController {

    @Autowired
    private RoomCategoryRepository roomCategoryRepo;

    @GetMapping("/admin/room-category")
    public String showAllRoomCategory(Model model,
                                      HttpServletRequest request) throws Exception {
        //TODO hardcoded 1
        List<RoomCategory> roomCategoryList = roomCategoryRepo.getAllRoomCategoriesForHotel(1);
        model.addAttribute("roomCategory", roomCategoryList);
        return "/room-category/room-category.html";
    }


    @GetMapping("/admin/delete-room-category/{id}")
    public String deleteRoomCategory(@PathVariable("id") int id) throws Exception {
        roomCategoryRepo.deleteRoomCategory(id);
        return "redirect:/admin/room-category";
    }

    @GetMapping("/admin/add-room-category")
    public String addRoomCategory(Model m) {
        m.addAttribute("newRoomCategory", new RoomCategory());
        return "/room-category/add-room-category";
    }

    @PostMapping("/admin/add-room-category/save")
    public String saveRoomCategory(@ModelAttribute RoomCategory roomCategory) {
        roomCategoryRepo.addRoomCategory(roomCategory);
        return "redirect:/admin/room-category";
    }


    @GetMapping("/admin/edit-room-category/{id}")
    public String editRoomCategory(@PathVariable("id") int id, Model model) {
        RoomCategory roomCategory = roomCategoryRepo.findRoomCategoryById(id);
        model.addAttribute("editedRoomCategory", roomCategory);
        return "/room-category/edit-room-category";
    }

    @PostMapping("/admin/edit-room-category/save")
    public String saveEditedRoomCategory(@ModelAttribute RoomCategory roomCategory) {

        roomCategoryRepo.editRoomCategory(roomCategory);
        return "redirect:/admin/room-category";
    }
}