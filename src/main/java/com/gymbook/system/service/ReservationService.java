package com.gymbook.system.service;

import com.gymbook.system.model.*;
import com.gymbook.system.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class ReservationService {
    @Autowired
    private final ReservationRepository reservationRepository;



    public List<String> findAllFullyBookedDates(){
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH");
    //    String strDate = dateFormat.format(date);
        Set<Date> setOfDates= reservationRepository.findAll().stream().filter(entry-> !entry.isForClass()).map(entry->entry.getFromDate())
                                        .collect(Collectors.toSet());
        List<Date> result = new ArrayList<>();
        for(Date date : setOfDates){
            if(reservationRepository.findAll().stream().filter(entry->(entry.getFromDate().compareTo(date)==0)).count()>1){
                result.add(date);
            }
        }
        return result.stream().map(dateFormat::format).collect(Collectors.toList());
    }
    public List<ReservationDTO> findAllWithUser(Long id){
        if(reservationRepository.findAll()!=null){
            return reservationRepository.findAll().stream().filter(entry->entry.getClient().getId()==id).map(ReservationDTO::fromEntity).collect(Collectors.toList());
        }
        return null;
    }
    public List<Reservation> findAllReservationsWithUser(Long id){
        if(reservationRepository.findAll()!=null){
            return reservationRepository.findAll().stream().filter(entry->entry.getClient().getId()==id).collect(Collectors.toList());
        }
        return null;
    }
    public List<String> findAllFullyBookedClassDates(){

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Set<Date> setOfDates= reservationRepository.findAll().stream().filter(entry-> entry.isForClass()).map(entry->entry.getFromDate()).filter(entry->entry.after(new Date()))
                .collect(Collectors.toSet());
        List<Date> result = new ArrayList<>();
        for(Date date : setOfDates){
            if(reservationRepository.findAll().stream().filter(entry->(entry.getFromDate().compareTo(date)==0)).count()>1){
                result.add(date);
            }
        }
        return result.stream().map(dateFormat::format).collect(Collectors.toList());
    }
    public Reservation save(Reservation user) {
        Reservation saved = reservationRepository.save(user);
        if(saved!=null){
            return saved;
        }
        return null;
    }
    public List<ReservationDTO> findAll() {
        return reservationRepository.findAll().stream()
                .map(ReservationDTO::fromEntity)
                .collect(toList());
    }
}
