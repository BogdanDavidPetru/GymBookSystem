package com.gymbook.system.controller;

import com.gymbook.system.model.*;
import com.gymbook.system.service.ReservationService;
import com.gymbook.system.service.SportClassService;
import com.gymbook.system.service.SubscriptionService;
import com.gymbook.system.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.persistence.Access;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/trainer")
@RequiredArgsConstructor
public class TrainerController {
    @Autowired
    private final UserService userService;
    @Autowired
    private final SubscriptionService subscriptionService;
    @Autowired
    private final SportClassService sportClassService;
    @Autowired
    private final ReservationService reservationService;
    @GetMapping(value = "/{id}")
    public String initCreateTrainer(Model model, @PathVariable String id) {
        User trainer = userService.findUserById(Long.parseLong(id));
        model.addAttribute("entry", trainer);
        model.addAttribute("currentDate", new Date());
        model.addAttribute("sportClasses",sportClassService.findAll().stream().filter(entry->entry.getTrainerUsername().contentEquals(trainer.getUsername())).collect(Collectors.toList()));
        return "viewTrainer";
    }
    @GetMapping(value = "/add/")
    public String initCreateTrainer(Model model) {
        model.addAttribute("entry", new UserDTO());
        return "addTrainer";
    }
    @PostMapping("/add/submit/")
    public String addEntry(@ModelAttribute UserDTO form, Model model, RedirectAttributes redirectAttributes){
        if(form.getName().equals("") || form.getPassword().equals("") || form.getUsername().equals("") || form.getPnc().equals("")){
            model.addAttribute("errorMessageAuthor", "Error! Please fill all the necessary fields!");
            return initCreateTrainer(model);
        }
        if(userService.findByUsername(form.getUsername())!=null){
            model.addAttribute("errorMessageAuthor", "Error! A user with the same username already exists! If you want you can update that one from the admin page.");
            return initCreateTrainer(model);
        }
        User trainer = new User(form.getName(),form.getPnc(),form.getUsername(), form.getPassword());
        trainer.setRole(Type.TRAINER);
        Subscription sub = subscriptionService.findSubscriptionByName("Trainers Subscription");
        if(sub!=null){
            trainer.setSubscriptionType(sub);
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MONTH, 1);
            trainer.setExpirationDate(cal.getTime());
        }
        if(userService.save(trainer) != null){
            redirectAttributes.addFlashAttribute("success", "Training successfully added!");
        }else{
            redirectAttributes.addFlashAttribute("success", "Error!");
            return initCreateTrainer(model);
        }
        StringBuilder sb = new StringBuilder("redirect:/admin/");
        return sb.toString();
    }
//    @GetMapping("/delete/{id}")
//    public String delEntry(Model model, @PathVariable String id, RedirectAttributes redirectAttributes){
//
//        if(userService.findUserById(Long.parseLong(id)).getHeldClasses()!=null){
//            model.addAttribute("errorMessageAuthor", "Error! The trainer you want to delete holds some classes. Please either change those classes' trainer or delete them before deleting this trainer!");
//            HomeController hc = new HomeController();
//
//            return hc.renderAdmin(model);//"redirect:/admin/";
//        }
//        userService.delete(Long.parseLong(id));
////        Long idAuthor = articleService.getAuthorOfBook(Long.parseLong(id)).getId();
////        articleService.delete();
//       /* try {
//            Thread.sleep(30000);
//        } catch (InterruptedException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }*/
//        StringBuilder sb  = new StringBuilder("redirect:/admin/");
//        return sb.toString();
//    }
    @GetMapping("/update/{id}")
    public String searchEntry(Model model, @PathVariable String id) {
        UserDTO item = userService.findById(Long.parseLong(id));;
        model.addAttribute("entry", item);
        model.addAttribute("classes", sportClassService.findAll());
        return "updateTrainer";
    }
    @PostMapping("/update/submit/")
    public String updateEntry(@ModelAttribute UserDTO form, Model model, RedirectAttributes redirectAttributes){
        if(form.getName().equals("") || form.getPassword().equals("") || form.getUsername().equals("") || form.getPnc().equals("")){
            model.addAttribute("errorMessageAuthor", "Error! Please fill all the necessary fields!");
            return searchEntry(model,form.getId().toString());
        }
        UserDTO s = userService.findByUsername(form.getUsername());
        if(s.getId()!=form.getId()){
            model.addAttribute("errorMessageAuthor", "Error! A user with the same username already exists! If you want you can update that one from the admin page.");
            return searchEntry(model,form.getId().toString());
        }
        User trainer = new User(form.getName(),form.getPnc(),form.getUsername(), form.getPassword());
        trainer.setId(form.getId());
        trainer.setRole(Type.TRAINER);
        User old = userService.findUserById(form.getId());
        trainer.setSubscriptionType(old.getSubscriptionType());
        trainer.setExpirationDate(old.getExpirationDate());
        if(form.getHeldClassesIds()!=null){
            form.getHeldClassesIds().stream().map(entry->sportClassService.findClassById(entry)).forEach(entry->trainer.addClass(entry));
        }
        trainer.setReservations(old.getReservations());
        User newUser = userService.save(trainer);
        if( newUser != null){
//            for(Reservation res: old.getReservations()){
//                res.setClient(newUser);
//                reservationService.save(res);
//            }
            redirectAttributes.addFlashAttribute("success", "Training successfully added!");
        }else{
            redirectAttributes.addFlashAttribute("success", "Error!");
            return searchEntry(model,form.getId().toString());
        }
        StringBuilder sb = new StringBuilder("redirect:/admin/");
        return sb.toString();
    }
    @GetMapping("/class/update/{id}")
    public String searchClassEntry(Model model, @PathVariable String id) {
        SportClassDTO item = sportClassService.findById(Long.parseLong(id));;
        model.addAttribute("entry", item);
        model.addAttribute("days", DayOfWeek.values());
        model.addAttribute("idTrainer", userService.findUserByUsername(item.getTrainerUsername()).getId());
        return "updateSportClassTrainer";
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
    @PostMapping("/class/update/submit/")
    public String updateEntry(@ModelAttribute SportClassDTO form, Model model, RedirectAttributes redirectAttributes) throws ParseException {
        if(form.getName().equals("")  ){
            model.addAttribute("errorMessageAuthor", "Error! Please fill all the necessary fields!");
            return searchClassEntry(model, form.getId().toString());
        }
        SportClassDTO s = sportClassService.findByName(form.getName());
        if(s!=null && s.getId()!=form.getId()){
            model.addAttribute("errorMessageAuthor", "Error! A sport class with the same name already exists!");
            return searchClassEntry(model, form.getId().toString());
        }
        SportClassDTO s2 = sportClassService.findByDayOfWeek(DayOfWeek.valueOf(form.getDayOfTheWeek()).getValue());
        if(s2!=null && s2.getId()!=form.getId()){
            model.addAttribute("errorMessageAuthor", "Error! A sport class in that day already exists!");
            return searchClassEntry(model, form.getId().toString());
        }
        SportClass sportClass = new SportClass(form.getName(), DayOfWeek.valueOf(form.getDayOfTheWeek()).getValue());
        User trainer = userService.findUserByUsername(form.getTrainerUsername());
        if(trainer!=null){
            sportClass.setTrainer(trainer);
        }
        else{
            return searchClassEntry(model, form.getId().toString());
        }
        sportClass.setId(form.getId());
        changeReservations(form);
        if(sportClassService.save(sportClass) != null){
            redirectAttributes.addFlashAttribute("success", "Training successfully added!");
        }else{
            redirectAttributes.addFlashAttribute("success", "Error!");
            return searchClassEntry(model, form.getId().toString());
        }
        StringBuilder sb = new StringBuilder("redirect:/trainer/");
        sb.append(trainer.getId());
        return sb.toString();
    }
    @GetMapping("/ownupdate/{id}")
    public String ownSearchEntry(Model model, @PathVariable String id) {
        UserDTO item = userService.findById(Long.parseLong(id));;
        model.addAttribute("entry", item);
        model.addAttribute("classes", sportClassService.findAll());
        return "updateTrainer2";
    }
    @PostMapping("/ownupdate/submit/")
    public String ownUpdateEntry(@ModelAttribute UserDTO form, Model model, RedirectAttributes redirectAttributes){
        if(form.getName().equals("") || form.getPassword().equals("") || form.getUsername().equals("") || form.getPnc().equals("")){
            model.addAttribute("errorMessageAuthor", "Error! Please fill all the necessary fields!");
            return ownSearchEntry(model,form.getId().toString());
        }
        UserDTO s = userService.findByUsername(form.getUsername());
        if(s.getId()!=form.getId()){
            model.addAttribute("errorMessageAuthor", "Error! A user with the same username already exists!");
            return ownSearchEntry(model,form.getId().toString());
        }
        User trainer = new User(form.getName(),form.getPnc(),form.getUsername(), form.getPassword());
        trainer.setId(form.getId());
        trainer.setRole(Type.TRAINER);
        Subscription sub = subscriptionService.findSubscriptionByName("Trainers Subscription");
        User old = userService.findUserById(form.getId());
        trainer.setSubscriptionType(old.getSubscriptionType());
        trainer.setExpirationDate(old.getExpirationDate());
        if(form.getHeldClassesIds()!=null){
            form.getHeldClassesIds().stream().map(entry->sportClassService.findClassById(entry)).forEach(entry->trainer.addClass(entry));
        }
        trainer.setReservations(old.getReservations());
        User newUser = userService.save(trainer);
        if( newUser != null){
            for(Reservation res: old.getReservations()){
                res.setClient(newUser);
                reservationService.save(res);
            }
            redirectAttributes.addFlashAttribute("success", "Training successfully added!");
        }else{
            redirectAttributes.addFlashAttribute("success", "Error!");
            return ownSearchEntry(model,form.getId().toString());
        }
        StringBuilder sb = new StringBuilder("redirect:/trainer/");
        sb.append(form.getId());
        return sb.toString();
    }
}
