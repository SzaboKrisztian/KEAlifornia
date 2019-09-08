package dk.kea.stud.kealifornia.controller;

import dk.kea.stud.kealifornia.model.Guest;
import dk.kea.stud.kealifornia.model.Occupancy;
import dk.kea.stud.kealifornia.model.RoomCategory;
import dk.kea.stud.kealifornia.repository.GuestRepository;
import dk.kea.stud.kealifornia.repository.OccupancyRepository;
import dk.kea.stud.kealifornia.repository.RoomCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class CheckOutController {

    @Autowired
    private OccupancyRepository occupancyRepo;

    @Autowired
    private GuestRepository guestRepo;

    @GetMapping("/admin/check-out/rooms")
    public String showAllOccupancy(Model model) throws Exception {
        List<Occupancy> occupancyList = occupancyRepo.getAllOccupancies();
        model.addAttribute("occupancies", occupancyList);
        return "/reception/check-out-rooms";
    }

    //TODO delete guests
    @PostMapping("/admin/check-out/rooms/save")
    public String checkOutRooms(@RequestParam("occupancyChecked") List<String> selectedRooms){
        if(selectedRooms != null){
            for(String occupancy : selectedRooms){
                int occupancyId = Integer.parseInt(occupancy);
                occupancyRepo.deleteOccupancy(occupancyId);
            }
        }
        return "redirect:/admin/check-out/rooms";
    }

    @GetMapping("/admin/check-out/guest")
    public String showAllGuests(Model model) throws Exception {
        List<Guest> guestList = guestRepo.getAllGuest();
        model.addAttribute("guests", guestList);
        return "/reception/check-out-guest";
    }

    @PostMapping("/admin/check-out/guest/save")
    public String checkOutGuest(@RequestParam("selectedGuest") int guest){
        List<Occupancy> occupancyList = occupancyRepo.getAllOccupancies();
        for(int i=0; i<occupancyList.size(); i++)
        {
           if(occupancyList.get(i).getGuest().getId() == guest)
               occupancyRepo.deleteOccupancy(i);
        }
        guestRepo.deleteGuest(guest);
        return "redirect:/admin/check-out/guest";
    }
}
