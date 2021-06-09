package com.gymbook.system.repository;

import com.gymbook.system.model.Reservation;
import com.gymbook.system.model.ReservationDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {


}
