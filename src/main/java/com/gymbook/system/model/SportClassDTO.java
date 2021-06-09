package com.gymbook.system.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.DayOfWeek;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SportClassDTO {
    private Long id;
    private String name;
    private String trainerUsername;
    private String dayOfTheWeek;

    public SportClassDTO(String name, String dayOfTheWeek){
        this.name=name;
        this.dayOfTheWeek=dayOfTheWeek;
    }

    public static SportClassDTO fromEntity(SportClass sportClass){
        return SportClassDTO.builder()
                .id(sportClass.getId())
                .name(sportClass.getName())
                .trainerUsername(sportClass.getTrainer().getUsername())
                .dayOfTheWeek(DayOfWeek.of(sportClass.getDayOfTheWeek()).toString())
                .build();
    }

    public void setId(Long id) {
        this.id = id;
    }
    public Long getId(){
        return this.id;
    }
}
