package com.gymbook.system.repository;

import com.gymbook.system.model.Subscription;
import com.gymbook.system.service.SubscriptionService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    Subscription findByName(String name);
}
