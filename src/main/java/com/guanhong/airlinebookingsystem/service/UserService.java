package com.guanhong.airlinebookingsystem.service;

import com.guanhong.airlinebookingsystem.entity.User;
import com.guanhong.airlinebookingsystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    public User getUserById(int id) throws Exception {
        Optional<User> user = userRepository.findById((long) id);

        if (user.isPresent()){
            return user.get();
        }
        else {
            throw new Exception("No user record exist for given id");
        }

    }

}
