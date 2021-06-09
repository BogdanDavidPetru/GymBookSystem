package com.gymbook.system.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String name;

    private String pnc;

    private String username;

    private String password;
    private String subscriptionTypeName;
    private Date expirationDate;
    private List<Long> heldClassesIds;
    private Type role;
    private List<Long> reservationsIDs;

    public static UserDTO fromEntity(User user){
        return UserDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .password(user.getPassword())
                .pnc(user.getPnc())
                .username(user.getUsername())
                .subscriptionTypeName(user.getSubscriptionType()!=null ? user.getSubscriptionType().getName() : "")
                .expirationDate(user.getExpirationDate())
                .heldClassesIds(user.getHeldClasses().stream().map(entry->entry.getId()).collect(Collectors.toList()))
                .role(user.getRole())
                .reservationsIDs(user.getReservations().stream().map(entry->entry.getId()).collect(Collectors.toList()))
                .build();
    }
    public void addClass(SportClass sportClass) {
        this.heldClassesIds.add(sportClass.getId());
    }

    public void removeClass(SportClass sportClass) {
        this.heldClassesIds.remove(sportClass.getId());
    }

    public void addReservation(Reservation reservation) {
        this.reservationsIDs.add(reservation.getId());
    }
}