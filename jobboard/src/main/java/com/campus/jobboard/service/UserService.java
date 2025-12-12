package com.campus.jobboard.service;

import com.campus.jobboard.model.User;
import com.campus.jobboard.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // controller will pass full User object
    public User registerUser(User user) {
        return userRepository.save(user);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElse(null);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public void activateUser(Long id) {
        userRepository.findById(id).ifPresent(user -> {
            user.setStatus(User.Status.ACTIVE);
        });
    }

    public void deactivateUser(Long id) {
        userRepository.findById(id).ifPresent(user -> {
            user.setStatus(User.Status.INACTIVE);
        });
    }

    public void toggleStatus(Long id) {
        userRepository.findById(id).ifPresent(user -> {
            if (user.getStatus() == User.Status.ACTIVE) {
                user.setStatus(User.Status.INACTIVE);
            } else {
                user.setStatus(User.Status.ACTIVE);
            }
        });
    }
}
