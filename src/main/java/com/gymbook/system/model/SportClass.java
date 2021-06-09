package com.gymbook.system.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SportClass {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @OneToOne
    private User trainer;
    private Integer dayOfTheWeek;

    public SportClass(String name, Integer dayOfTheWeek){
        this.name=name;
        this.dayOfTheWeek=dayOfTheWeek;
    }


}