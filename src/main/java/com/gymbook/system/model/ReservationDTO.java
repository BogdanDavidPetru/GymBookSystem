package com.gymbook.system.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import java.util.Date;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationDTO {
        private Long id;
        private String personUsername;
        private Date from;
        private boolean isForClass;
     //   private Date to;

        public static ReservationDTO fromEntity(Reservation reservation){
            return ReservationDTO.builder()
                    .id(reservation.getId())
                    .personUsername(reservation.getClient().getUsername())
                    .from(reservation.getFromDate())
                    .isForClass(reservation.isForClass())
                //    .to(reservation.getToDate())
                    .build();
        }
}
