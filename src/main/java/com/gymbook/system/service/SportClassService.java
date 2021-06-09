package com.gymbook.system.service;

import com.gymbook.system.model.*;
import com.gymbook.system.model.SportClass;
import com.gymbook.system.model.SportClassDTO;
import com.gymbook.system.repository.SportClassRepository;
import com.gymbook.system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SportClassService {
    @Autowired
    private final SportClassRepository sportClassRepository;
    @Autowired
    private final UserRepository userRepository;

    public List<SportClassDTO> findAll(){
        return sportClassRepository.findAll().stream().map(SportClassDTO::fromEntity).collect(Collectors.toList());
    }
    public SportClassDTO findByName(String name){
        SportClass s = sportClassRepository.findByName(name);
        if(s==null){
            return null;
        }
        return SportClassDTO.fromEntity(s);
    }
    public SportClassDTO findByDayOfWeek(Integer dayOfWeek){
        SportClass s = sportClassRepository.findSportClassByDayOfTheWeek(dayOfWeek);
        if(s==null){
            return null;
        }
        return SportClassDTO.fromEntity(s);
    }
    public SportClassDTO findById(Long id){
        return SportClassDTO.fromEntity(sportClassRepository.findById(id).get());
    }
    public SportClass findClassById(Long id){
        return sportClassRepository.findById(id).get();
    }
    public SportClass save(SportClass user) {
        SportClass saved = sportClassRepository.save(user);
        if(saved!=null){
            return saved;
        }
        return null;
    }
    public boolean delete(Long id){
        if(sportClassRepository.findById(id).isPresent()){
            SportClass sportClass = sportClassRepository.findById(id).get();
            userRepository.findAll().stream().filter(entry->entry.getRole()==Type.TRAINER).forEach(entry->{
                entry.removeClass(sportClass);
                userRepository.save(entry);
            });
            sportClassRepository.delete(sportClass);
            return true;
        }
        return false;
    }
}
