package com.gymbook.system.controller;

import com.gymbook.system.model.*;
import com.gymbook.system.service.ReservationService;
import com.gymbook.system.service.SportClassService;
import com.gymbook.system.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/classes")
@RequiredArgsConstructor
public class SportController {
    @Autowired
    private SportClassService sportClassService;
    @Autowired
    private UserService userService;
    @Autowired
    private ReservationService reservationService;
    @GetMapping(value = "/all")
    public String findAllSportsClasses(Model model){
        model.addAttribute("classes",sportClassService.findAll());
        return "sportClasses";
    }

    @GetMapping(value = "/add/")
    public String initCreateClass(Model model) {
        model.addAttribute("entry", new SportClassDTO());
        model.addAttribute("days", DayOfWeek.values());
        model.addAttribute("trainers", userService.findAllTrainers());
        return "addSportClass";
    }
    @PostMapping("/add/submit/")
    public String addEntry(@ModelAttribute SportClassDTO form, Model model, RedirectAttributes redirectAttributes){
        if(form.getName().equals("")  ){
            model.addAttribute("errorMessageAuthor", "Error! Please fill all the necessary fields!");
            return initCreateClass(model);
        }
        if(sportClassService.findByName(form.getName())!=null){
            model.addAttribute("errorMessageAuthor", "Error! A sport class with the same name already exists! If you want you can update that one from the admin page.");
            return initCreateClass(model);
        }
        if(sportClassService.findByDayOfWeek( DayOfWeek.valueOf(form.getDayOfTheWeek()).getValue())!=null){
            model.addAttribute("errorMessageAuthor", "Error! A sport class in the same day already exists! If you want you can update that one from the admin page.");
            return initCreateClass(model);
        }
        SportClass sportClass = new SportClass(form.getName(), DayOfWeek.valueOf(form.getDayOfTheWeek()).getValue());
        User trainer = userService.findUserByUsername(form.getTrainerUsername());
        if(trainer!=null){
            sportClass.setTrainer(trainer);
        }
        else{
            return initCreateClass(model);
        }
        if(sportClassService.save(sportClass) != null){
            redirectAttributes.addFlashAttribute("success", "Training successfully added!");
        }else{
            redirectAttributes.addFlashAttribute("success", "Error!");
            return initCreateClass(model);
        }
        StringBuilder sb = new StringBuilder("redirect:/admin/");
        return sb.toString();
    }
    @GetMapping("/delete/{id}")
    public String delEntry(Model model, @PathVariable String id, RedirectAttributes redirectAttributes){
        sportClassService.delete(Long.parseLong(id));

        StringBuilder sb  = new StringBuilder("redirect:/admin/");
        return sb.toString();
    }
    @GetMapping("/update/{id}")
    public String searchEntry(Model model, @PathVariable String id) {
        SportClassDTO item = sportClassService.findById(Long.parseLong(id));;
        model.addAttribute("entry", item);
        model.addAttribute("days", DayOfWeek.values());
        model.addAttribute("trainers", userService.findAllTrainers());
        return "updateSportClass";
    }
    private void changeReservations(SportClassDTO form) throws ParseException {
        SportClassDTO initial = sportClassService.findById(form.getId());
        if(!initial.getDayOfTheWeek().contentEquals(form.getDayOfTheWeek())){
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            List<ReservationDTO> bookings = reservationService.findAll().stream().filter(entry->entry.isForClass()).filter(entry->entry.getFrom().after(new Date())).collect(Collectors.toList());
            List<ReservationDTO> reservationsToChange = bookings.stream().filter(entry-> LocalDate.parse(dateFormat.format(entry.getFrom()), DateTimeFormatter.ofPattern( "yyyy-MM-dd" )).getDayOfWeek().toString().contentEquals(initial.getDayOfTheWeek())).collect(Collectors.toList());
            LocalDate ld = LocalDate.now();
            ld = ld.with(TemporalAdjusters.next(DayOfWeek.valueOf(form.getDayOfTheWeek())));
            Date reservedDate = new SimpleDateFormat("yyyy-MM-dd").parse(ld.toString());
            reservedDate.setHours(16);
            for(ReservationDTO res : reservationsToChange){
                User user = userService.findUserByUsername(res.getPersonUsername());
                reservationService.save(new Reservation(res.getId(),user, reservedDate,true));
            }
        }
    }
    @PostMapping("/update/submit/")
    public String updateEntry(@ModelAttribute SportClassDTO form, Model model, RedirectAttributes redirectAttributes) throws ParseException {
        if(form.getName().equals("")  ){
            model.addAttribute("errorMessageAuthor", "Error! Please fill all the necessary fields!");
            return searchEntry(model, form.getId().toString());
        }
        SportClassDTO s = sportClassService.findByName(form.getName());
        if(s!=null && s.getId()!=form.getId()){
            model.addAttribute("errorMessageAuthor", "Error! A sport class with the same name already exists! If you want you can update that one from the admin page.");
            return searchEntry(model, form.getId().toString());
        }
        SportClassDTO s2 = sportClassService.findByDayOfWeek(DayOfWeek.valueOf(form.getDayOfTheWeek()).getValue());
        if(s2!=null && s2.getId()!=form.getId()){
            model.addAttribute("errorMessageAuthor", "Error! A sport class in that day already exists! If you want you can update that one from the admin page.");
            return searchEntry(model, form.getId().toString());
        }
        SportClass sportClass = new SportClass(form.getName(), DayOfWeek.valueOf(form.getDayOfTheWeek()).getValue());
        User trainer = userService.findUserByUsername(form.getTrainerUsername());
        if(trainer!=null){
            sportClass.setTrainer(trainer);
        }
        else{
            return searchEntry(model, form.getId().toString());
        }
        sportClass.setId(form.getId());
        changeReservations(form);
        if(sportClassService.save(sportClass) != null){
            redirectAttributes.addFlashAttribute("success", "Training successfully added!");
        }else{
            redirectAttributes.addFlashAttribute("success", "Error!");
            return searchEntry(model, form.getId().toString());
        }
        StringBuilder sb = new StringBuilder("redirect:/admin/");
        return sb.toString();
    }
}
