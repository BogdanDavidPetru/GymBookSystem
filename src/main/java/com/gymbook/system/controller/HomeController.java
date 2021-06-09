package com.gymbook.system.controller;

import com.gymbook.system.model.Type;
import com.gymbook.system.model.User;
import com.gymbook.system.model.UserDTO;
import com.gymbook.system.service.ReservationService;
import com.gymbook.system.service.SportClassService;
import com.gymbook.system.service.SubscriptionService;
import com.gymbook.system.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Calendar;

import static java.util.stream.Collectors.toList;

@Controller
@RequiredArgsConstructor
public class HomeController {
    @Autowired
    private UserService userService;
    @Autowired
    private SportClassService sportClassService;
    @Autowired
    private SubscriptionService subscriptionService;
    @Autowired
    private ReservationService reservationService;
    @GetMapping({"/home", "/"})
    public String renderHomePage(Model model){
        return "home";
    }

//    @GetMapping("/singup/")
//    public String signUp(Model model){
//        if(!model.containsAttribute("entry")) {
//            User user = new User();
//            model.addAttribute("entry", user);
//        }
//        return "singUp";
//    }

    @GetMapping("/login")
    public String getEntryForm(Model model){
        if(!model.containsAttribute("entry")) {
            User user = new User();
            model.addAttribute("entry", user);
        }
        return "login";
    }
    @PostMapping("/login/submit")
    public String login(@ModelAttribute User form, Model model, RedirectAttributes redirectAttributes){
        UserDTO user = userService.findByUsername(form.getUsername());
        if(user ==null || !user.getPassword().equals(form.getPassword())){
            model.addAttribute("entry", new User());
            model.addAttribute("errorMessage", "Invalid credentials");
            return "login";
        }
        if(user.getRole()== Type.NORMAL){
            StringBuilder sb =  new StringBuilder("redirect:/user/");
            sb.append(user.getId());
            return sb.toString();
        }
        if(user.getRole()== Type.TRAINER){
            StringBuilder sb =  new StringBuilder("redirect:/trainer/");
            sb.append(user.getId());
            return sb.toString();
        }
        return "redirect:/admin/";
    }
    @GetMapping(value = "/admin/")
    public String renderAdmin(Model model) {
        model.addAttribute("sportClasses",sportClassService.findAll());
        model.addAttribute("subscriptions",subscriptionService.findAll());
        model.addAttribute("trainers", userService.findAllUserTrainers());
        model.addAttribute("reservations",reservationService.findAll());
//        model.addAttribute("users", userService.findAll().stream().filter(entry->entry.getRole()==Type.WRITER).collect(toList()));
        return "admin";
    }
    @GetMapping("/trainer/delete/{id}")
    public String delEntry(Model model, @PathVariable String id, RedirectAttributes redirectAttributes){

        if(!userService.findUserById(Long.parseLong(id)).getHeldClasses().isEmpty()){
            model.addAttribute("errorMessageAuthor", "Error! The trainer you want to delete holds some classes. Please either change those classes' trainer or delete them before deleting this trainer!");
           return renderAdmin(model);
        }
        userService.delete(Long.parseLong(id));
        StringBuilder sb  = new StringBuilder("redirect:/admin/");
        return sb.toString();
    }



}
