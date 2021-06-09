package com.gymbook.system.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionDTO {
    private Long id;
    private String name;
    private Integer nrOfEntries;

    public static SubscriptionDTO fromEntity(Subscription subscription){
        return SubscriptionDTO.builder()
                .id(subscription.getId())
                .name(subscription.getName())
                .nrOfEntries(subscription.getNrOfEntries())
                .build();
    }
}
