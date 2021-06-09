package com.gymbook.system.controller;

import com.gymbook.system.model.*;
import com.gymbook.system.service.ReservationService;
import com.gymbook.system.service.SportClassService;
import com.gymbook.system.service.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.jni.Local;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/reservation")
@RequiredArgsConstructor
public class ReservationController {
    @Autowired
    private ReservationService reservationService;
    @Autowired
    private SportClassService sportClassService;
    @Autowired
    private UserService userService;
    @GetMapping("/{id}")
    public String makeReservation(Model model, @PathVariable String id) throws ParseException {
        List<String> dateList = reservationService.findAllFullyBookedDates();
        dateList.stream().forEach(System.out::println);
//        dateList.add("2021-05-13 21");//new SimpleDateFormat("yyyy-MM-dd HH").parse(
//        dateList.add("2021-05-13 00");
        model.addAttribute("user",userService.findUserById(Long.parseLong(id)));
        model.addAttribute("forbiddenDates",dateList);
        model.addAttribute("reservation", new Reservation());
        return "reservation";
//        return "text";
    }

    @PostMapping("/submit/{id}")
    public String saveReservation(Reservation reservation, Model model,@PathVariable String id) throws ParseException {
        List<String> forbiddenDates = reservationService.findAllFullyBookedDates();
        DateFormat forbiddenDateFormat = new SimpleDateFormat("yyyy-MM-dd HH");
//        for(String date : forbiddenDates){
//            System.out.println(date);
//        }
//        System.out.println("Current: " + forbiddenDateFormat.format(reservation.getFromDate()));
        if(forbiddenDates.contains(forbiddenDateFormat.format(reservation.getFromDate()))){
            model.addAttribute("errorMessageAuthor", "Error! The selected date is not available!");
            return makeReservation(model,id);
        }
        User user = userService.findUserById(Long.parseLong(id));
        if (user == null) {
            model.addAttribute("errorMessageAuthor", "Error! The user account is not available!");
            return makeReservation(model,id);
        }
        List<Date> alreadyReservedDates=user.getReservations().stream().filter(entry->!entry.isForClass()).map(entry->entry.getFromDate()).collect(Collectors.toList());
        if(alreadyReservedDates.contains(reservation.getFromDate())){
            model.addAttribute("errorMessageAuthor", "Error! You already have a reservation for this date!");
            return makeReservation(model,id);
        }
        DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");
        if(alreadyReservedDates.stream().map(dateFormat::format).filter(entry->entry.contentEquals(dateFormat.format(reservation.getFromDate()))).count()==user.getSubscriptionType().getNrOfEntries()){
            model.addAttribute("errorMessageAuthor", "Error! You exceeded the number of reservations per day permitted for your subscription type!");
            return makeReservation(model,id);
        }
        Reservation newRes = new Reservation();
        newRes.setFromDate(reservation.getFromDate());
        newRes.setClient(user);
        newRes.setForClass(false);
        reservationService.save(newRes);
        StringBuilder sb = new StringBuilder("redirect:/");
        if(user.getRole()==Type.TRAINER){
            sb.append("trainer/");
        }
        else{
            sb.append("user/");
        }
        sb.append(id);
        return sb.toString();
    }

    @GetMapping("/classes/{id}")
    public String makeClassReservation(Model model, @PathVariable String id){
        List<String> fullyBooked = reservationService.findAllFullyBookedClassDates();
        List<DayOfWeek> bookedClasses = fullyBooked.stream().map(entry->LocalDate.parse(entry,DateTimeFormatter.ofPattern( "yyyy-MM-dd" )).getDayOfWeek()).collect(Collectors.toList());
        List<SportClassDTO> availableClasses = sportClassService.findAll().stream().filter(entry->!bookedClasses.contains(DayOfWeek.valueOf(entry.getDayOfTheWeek()))).collect(Collectors.toList());
        if(availableClasses.isEmpty()){
            model.addAttribute("errorMessageAuthor", "Error! There are no more available seats for next week classes!");
            model.addAttribute("reservations", reservationService.findAllWithUser(Long.parseLong(id)));
            model.addAttribute("classesFullyBooked", availableClasses.isEmpty());
//            model.addAttribute("user",userService.findUserById(Long.parseLong(id)));
            model.addAttribute("idUser", Long.parseLong(id));
            model.addAttribute("entry", userService.findUserById(Long.parseLong(id)));
            model.addAttribute("currentDate", new Date());
            return "viewUser";
        }
        model.addAttribute("entry",new ReservationDTO());
        model.addAttribute("sportClasses",availableClasses );
        model.addAttribute("idUser", Long.parseLong(id));
        return "sportClassReservation";
    }
    @PostMapping("/classes/submit/{id}")
    public String saveClassReservation(@ModelAttribute ReservationDTO form,@PathVariable String id, Model model, RedirectAttributes redirectAttributes) throws ParseException {
         User user = userService.findUserById(Long.parseLong(id));
        if (user == null) {
            model.addAttribute("errorMessageAuthor", "Error! The user account is not available!");
            return makeClassReservation(model,id);
        }
       // System.out.println(ld);
        //calculate next date of class

        LocalDate ld = LocalDate.now();
        ld = ld.with(TemporalAdjusters.next(DayOfWeek.valueOf(form.getPersonUsername())));
        Date reservedDate = new SimpleDateFormat("yyyy-MM-dd").parse(ld.toString());
        reservedDate.setHours(16);
        System.out.println(reservedDate.toString());
        //verify if same account has already a reservation for that class
        List<Date> alreadyReservedDates=user.getReservations().stream().filter(Reservation::isForClass).map(entry->entry.getFromDate()).collect(Collectors.toList());
        if(alreadyReservedDates.contains(reservedDate)){
            model.addAttribute("errorMessageAuthor", "Error! You already have a reservation for this date!");
            return makeClassReservation(model,id);
        }
        Reservation reservation = new Reservation();
        reservation.setForClass(true);
        reservation.setClient(user);
        reservation.setFromDate(reservedDate);
        reservationService.save(reservation);
        StringBuilder sb = new StringBuilder("redirect:/user/");
        sb.append(id);
        return sb.toString();
    }
}
