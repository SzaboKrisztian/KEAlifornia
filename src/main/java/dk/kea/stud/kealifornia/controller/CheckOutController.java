package dk.kea.stud.kealifornia.controller;

import dk.kea.stud.kealifornia.model.Occupancy;
import dk.kea.stud.kealifornia.model.RoomCategory;
import dk.kea.stud.kealifornia.repository.OccupancyRepository;
import dk.kea.stud.kealifornia.repository.RoomCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class CheckOutController {

    @Autowired
    private OccupancyRepository occupancyRepo;

    @GetMapping("/admin/check-out")
    public String showAllOccupancy(Model model) throws Exception {
        List<Occupancy> occupancyList = occupancyRepo.getAllOccupancies();
        model.addAttribute("occupancies", occupancyList);
        return "/reception/check-out";
    }

    @PostMapping("/admin/check-out/save")
    public String checkOut(@RequestParam("occupancyChecked") List<String> selectedRooms){

        if(selectedRooms != null){
            for(String occupancy : selectedRooms){
                int occupancyId = Integer.parseInt(occupancy);
                occupancyRepo.deleteOccupancy(occupancyId);
                System.out.println(occupancyId);
            }
        }
        return "redirect:/admin/check-out/";
    }
}
