package com.gymbook.system.repository;

import com.gymbook.system.model.SportClass;
import com.gymbook.system.model.SportClassDTO;
import com.gymbook.system.model.Subscription;
import com.gymbook.system.service.SportClassService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SportClassRepository extends JpaRepository<SportClass, Long> {
    SportClass findByName(String name);
    SportClass findSportClassByDayOfTheWeek(Integer dayOfWeek);
}
