package com.gymbook.system.service;

import com.gymbook.system.model.ReservationDTO;
import com.gymbook.system.model.Subscription;
import com.gymbook.system.model.SubscriptionDTO;
import com.gymbook.system.model.User;
import com.gymbook.system.repository.SubscriptionRepository;
import com.gymbook.system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
    @Autowired
    private final SubscriptionRepository subscriptionRepository;
    @Autowired
    private final UserRepository userRepository;

    public List<SubscriptionDTO> findAll(){
        return subscriptionRepository.findAll().stream().map(SubscriptionDTO::fromEntity).collect(Collectors.toList());
    }
    public SubscriptionDTO findById(Long id){
        return SubscriptionDTO.fromEntity(subscriptionRepository.findById(id).get());
    }
    public SubscriptionDTO findByName(String name){
        Subscription s = subscriptionRepository.findByName(name);
        if(s==null){
            return null;
        }
        return SubscriptionDTO.fromEntity(s);
    }
    public Subscription findSubscriptionByName(String name){
        Subscription s = subscriptionRepository.findByName(name);
        if(s==null){
            return null;
        }
        return s;
    }
    public Subscription save(Subscription user) {
        Subscription saved = subscriptionRepository.save(user);
        if(saved!=null){
            return saved;
        }
        return null;
    }
    public boolean delete(Long id){
        if(subscriptionRepository.findById(id).isPresent()){
            Subscription subscription = subscriptionRepository.findById(id).get();
            userRepository.findAllBySubscriptionType(subscription).stream().forEach(entry->{
                entry.setSubscriptionType(null);
                userRepository.save(entry);
});
            subscriptionRepository.delete(subscription);
            return true;
        }
        return false;
    }
}
