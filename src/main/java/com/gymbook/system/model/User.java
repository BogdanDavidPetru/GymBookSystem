package com.gymbook.system.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    private String pnc;

    private String username;

    private String password;
    @OneToOne
    private Subscription subscriptionType;
    @Column(columnDefinition="DATETIME")
    private Date expirationDate;
    @OneToMany
    @JoinColumn(name="trainer_id")
    private List<SportClass> heldClasses;
    @Enumerated(EnumType.STRING)
    private Type role;
    @OneToMany
    @JoinColumn(name="client_id")
    private List<Reservation> reservations;
    public User(String name, String pnc, String username, String password){
        this.name=name;
        this.pnc=pnc;
        this.username=username;
        this.password=password;
        this.heldClasses=new ArrayList<>();
        this.reservations = new ArrayList<>();
    }

    public void addClass(SportClass sportClass){
        this.heldClasses.add(sportClass);
    }
    public void removeClass(SportClass sportClass){
        this.heldClasses.remove(sportClass);
    }
    public void addReservation(Reservation reservation){
        this.reservations.add(reservation);
    }

    public void removeReservation(Reservation reservation){
        this.reservations.remove(reservation);
    }


}
