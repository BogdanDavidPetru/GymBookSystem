package com.gymbook.system.controller;

import com.gymbook.system.model.Subscription;
import com.gymbook.system.model.SubscriptionDTO;
import com.gymbook.system.model.User;
import com.gymbook.system.model.UserDTO;
import com.gymbook.system.service.SportClassService;
import com.gymbook.system.service.SubscriptionService;
import com.gymbook.system.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Controller
@RequestMapping("/subscription")
@RequiredArgsConstructor
public class SubscriptionController {
    @Autowired
    private SubscriptionService subscriptionService;
    @Autowired
    private UserService userService;

    @GetMapping("/new/{id}")
    public String createSub(Model model, @PathVariable String id){
        UserDTO entry = userService.findById(Long.parseLong(id));
        model.addAttribute("entry", entry);
        model.addAttribute("subscriptions",subscriptionService.findAll());
        return "renewSubscription";
    }
    @PostMapping("/new/submit/")
    public String renewSubscription(@ModelAttribute UserDTO form, Model model, RedirectAttributes redirectAttributes) throws ParseException {
        User user = userService.findUserById(form.getId());
        if (user == null) {
            model.addAttribute("errorMessageAuthor", "Error! The user account is not available!");
            return createSub(model,form.getId().toString());
        }
        Subscription subscription = subscriptionService.findSubscriptionByName(form.getSubscriptionTypeName());
        if (subscription == null) {
            model.addAttribute("errorMessageAuthor", "Error! The selected subscription type is not available!");
            return createSub(model,form.getId().toString());
        }
        user.setSubscriptionType(subscription);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, 1);
      //  Date reservedDate = new SimpleDateFormat("yyyy-MM-dd").parse(cal.toString());
        System.out.println(cal.toString());
        user.setExpirationDate(cal.getTime());
        userService.save(user);
        StringBuilder sb = new StringBuilder("redirect:/user/");
        sb.append(form.getId());
        return sb.toString();
    }

    @GetMapping(value = "/all")
    public String findAllSportsClasses(Model model){
        model.addAttribute("subscriptions",subscriptionService.findAll());
        return "subscriptions";
    }
    @GetMapping(value = "/add/")
    public String initCreateSubscription(Model model) {
        model.addAttribute("entry", new SubscriptionDTO());
        return "addSubscription";
    }
    @PostMapping("/add/submit/")
    public String addEntry(@ModelAttribute SubscriptionDTO form, Model model, RedirectAttributes redirectAttributes){
        if(form.getName().equals("") || form.getNrOfEntries().equals("") ){
            model.addAttribute("errorMessageAuthor", "Error! Please fill all the necessary fields!");
            return initCreateSubscription(model);
        }
        if(subscriptionService.findByName(form.getName())!=null){
            model.addAttribute("errorMessageAuthor", "Error! A subscription with the same name already exists! If you want you can update that one from the admin page.");
            return initCreateSubscription(model);
        }
        Subscription subscription = new Subscription(form.getName(),form.getNrOfEntries());
        if(subscriptionService.save(subscription) != null){
            redirectAttributes.addFlashAttribute("success", "Training successfully added!");
        }else{
            redirectAttributes.addFlashAttribute("success", "Error!");
            return initCreateSubscription(model);
        }
        StringBuilder sb = new StringBuilder("redirect:/admin/");
        return sb.toString();
    }
    @GetMapping("/delete/{id}")
    public String delEntry(Model model, @PathVariable String id, RedirectAttributes redirectAttributes){
        subscriptionService.delete(Long.parseLong(id));
//        Long idAuthor = articleService.getAuthorOfBook(Long.parseLong(id)).getId();
//        articleService.delete();
       /* try {
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }*/
        StringBuilder sb  = new StringBuilder("redirect:/admin/");
        return sb.toString();
    }
    @GetMapping("/update/{id}")
    public String searchEntry(Model model, @PathVariable String id) {
        SubscriptionDTO item = subscriptionService.findById(Long.parseLong(id));;
        model.addAttribute("entry", item);
        return "updateSubscription";
    }
    @PostMapping("/update/submit/")
    public String updateEntry(@ModelAttribute SubscriptionDTO form, Model model, RedirectAttributes redirectAttributes){
        System.out.println(form.getName());
        if(form.getName().equals("") || form.getNrOfEntries().equals("") ){
            model.addAttribute("errorMessageAuthor", "Error! Please fill all the necessary fields!");
            return initCreateSubscription(model);
        }
        SubscriptionDTO s = subscriptionService.findByName(form.getName());
        if(s!=null && s.getId()!=form.getId()){
            model.addAttribute("errorMessageAuthor", "Error! A subscription with the same name already exists! If you want you can update that one from the admin page.");
            return initCreateSubscription(model);
        }
        Subscription subscription = new Subscription(form.getName(),form.getNrOfEntries());
        subscription.setId(form.getId());
        if(subscriptionService.save(subscription) != null){
            redirectAttributes.addFlashAttribute("success", "Training successfully added!");
        }else{
            redirectAttributes.addFlashAttribute("success", "Error!");
            return initCreateSubscription(model);
        }
        StringBuilder sb = new StringBuilder("redirect:/admin/");
        return sb.toString();
    }
}
