package com.gymbook.system.service;

import com.gymbook.system.model.Type;
import com.gymbook.system.model.User;
import com.gymbook.system.model.UserDTO;
import com.gymbook.system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class UserService {
    @Autowired
    private final UserRepository userRepository;


    public List<UserDTO> findAllTrainers(){
        return userRepository.findAll().stream()
                .filter(entry->entry.getRole()== Type.TRAINER)
                .map(com.gymbook.system.model.UserDTO::fromEntity)
                .collect(toList());
    }
    public List<User> findAllUserTrainers(){
        return userRepository.findAll().stream()
                .filter(entry->entry.getRole()== Type.TRAINER)
                .collect(toList());
    }
    public List<UserDTO> findAll() {
        return userRepository.findAll().stream()
                .map(UserDTO::fromEntity)
                .collect(toList());
    }

    public User save(User user) {
        User saved = userRepository.save(user);
        if(saved!=null){
            return saved;
        }
        return null;
    }
    public UserDTO findById(Long id){
        if(userRepository.findById(id).isPresent())
            return UserDTO.fromEntity(userRepository.findById(id).get());
        return null;
    }
    public User findUserById(Long id){
        return userRepository.findById(id).get();
    }
    public boolean delete(Long id){
        if(userRepository.findById(id).isPresent()){
            userRepository.delete(userRepository.findById(id).get());
            return true;
        }
        return false;
    }
    public UserDTO findByUsername(String username){
        if(userRepository.findByUsername(username)==null)
            return null;
        return UserDTO.fromEntity(userRepository.findByUsername(username));
    }
    public User findUserByUsername(String username){
        return userRepository.findByUsername(username);
    }
}
