package com.gymbook.system.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private User client;
    @Column(columnDefinition="DATETIME")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH")
    private Date fromDate;

    private boolean isForClass;
//    @Column(columnDefinition="DATETIME")
//    private Date toDate;
}
