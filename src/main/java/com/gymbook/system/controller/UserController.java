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

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
        @Autowired
        private UserService userService;
        @Autowired
        private ReservationService reservationService;
        @Autowired
        private SportClassService sportClassService;
        @Autowired
        private SubscriptionService subscriptionService;
        @GetMapping(value = "/{id}")
        public String renderHomePage(Model model, @PathVariable String id, RedirectAttributes redirectAttributes) {
            List<String> fullyBooked = reservationService.findAllFullyBookedClassDates();
            List<DayOfWeek> bookedClasses = fullyBooked.stream().map(entry-> LocalDate.parse(entry, DateTimeFormatter.ofPattern( "yyyy-MM-dd" )).getDayOfWeek()).collect(Collectors.toList());
            List<SportClassDTO> availableClasses = sportClassService.findAll().stream().filter(entry->!bookedClasses.contains(DayOfWeek.valueOf(entry.getDayOfTheWeek()))).collect(Collectors.toList());
            model.addAttribute("reservations", reservationService.findAllWithUser(Long.parseLong(id)));
            model.addAttribute("idUser", id);
            model.addAttribute("classesFullyBooked", availableClasses.isEmpty());
            model.addAttribute("entry", userService.findUserById(Long.parseLong(id)));
            model.addAttribute("currentDate", new Date());
            return "viewUser";
        }

//        @GetMapping(value="//articles/{id}")
//        public String showArticle(Model model, @PathVariable String id, RedirectAttributes redirectAttributes) {
//            ArticleDTO article = articleService.findById(Long.valueOf(id));
//            List<ArticleDTO> articleDTOS = article.getRelatedArticlesTitles().stream().map(entry->articleService.findById(entry)).collect(toList());
//            model.addAttribute("article", article);
//            model.addAttribute("idAuthor",articleService.findArticleById(Long.parseLong(id)).getAuthor().getId());
//            model.addAttribute("relatedArticles",articleDTOS);
//            return "viewArticleUser";
//        }
        @GetMapping(value = "/add/")
        public String initCreateWriter(Model model) {
            model.addAttribute("entry", new User());
            return "signUp";
        }
        @PostMapping("/add/submit/")
        public String addEntry(@ModelAttribute User form, Model model, RedirectAttributes redirectAttributes){
            if(form.getPassword().equals("") || form.getUsername().equals("") || form.getName().equals("") || form.getPnc().equals("")){
                model.addAttribute("errorMessageAuthor", "Error! Please fill all the necessary fields(Name,Username, Password, PNC)!");
                return initCreateWriter(model);
            }
            if(userService.findByUsername(form.getUsername())!=null){
                model.addAttribute("errorMessageAuthor", "Error! A user with the same username already exists! If you want you can update that one from the admin page.");
                return initCreateWriter(model);
            }
            User user = new User(form.getName(),form.getPnc(),form.getUsername(),form.getPassword());
            user.setRole(Type.NORMAL);
            User result = userService.save(user);
            if(result!= null){
                redirectAttributes.addFlashAttribute("success", "Training successfully added!");
            }else{
                redirectAttributes.addFlashAttribute("success", "Error!");
                return initCreateWriter(model);
            }
            StringBuilder sb = new StringBuilder("redirect:/user/");
            sb.append(result.getId());
            return sb.toString();
        }
    @GetMapping("/update/{id}")
    public String searchEntry(Model model, @PathVariable String id) {
        UserDTO item = userService.findById(Long.parseLong(id));
        System.out.println(item.getExpirationDate());
        model.addAttribute("entry", item);
        model.addAttribute("classes", sportClassService.findAll());
        return "updateUser";
    }
    @PostMapping("/update/submit/")
    public String updateEntry(@ModelAttribute UserDTO form, Model model, RedirectAttributes redirectAttributes){
        if(form.getName().equals("") || form.getPassword().equals("") || form.getUsername().equals("") || form.getPnc().equals("")){
            model.addAttribute("errorMessageAuthor", "Error! Please fill all the necessary fields!");
            return searchEntry(model,form.getId().toString());
        }
        UserDTO s = userService.findByUsername(form.getUsername());
        if(s.getId()!=form.getId()){
            model.addAttribute("errorMessageAuthor", "Error! A user with the same username already exists!");
            return searchEntry(model,form.getId().toString());
        }
        User user = new User(form.getName(),form.getPnc(),form.getUsername(), form.getPassword());
        user.setId(form.getId());
        user.setRole(Type.NORMAL);
        User old = userService.findUserById(form.getId());
//        List<Reservation> reservations = reservationService.findAllReservationsWithUser(form.getId());
        user.setExpirationDate(old.getExpirationDate());
        user.setSubscriptionType(old.getSubscriptionType());
        user.setHeldClasses(old.getHeldClasses());
        user.setReservations(old.getReservations());
        User newUser = userService.save(user);
        if( newUser != null){
            redirectAttributes.addFlashAttribute("success", "Training successfully added!");
        }else{
            redirectAttributes.addFlashAttribute("success", "Error!");
            return searchEntry(model,form.getId().toString());
        }
        StringBuilder sb = new StringBuilder("redirect:/user/");
        sb.append(form.getId());
        return sb.toString();
    }

}
