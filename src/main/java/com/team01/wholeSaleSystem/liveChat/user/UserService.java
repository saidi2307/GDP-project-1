package com.team01.wholeSaleSystem.liveChat.user;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.team01.wholeSaleSystem.entity.User;
import com.team01.wholeSaleSystem.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;

    public User saveUser(AddUser user) {
        User user1 = repository.findByUserName(user.userName);
        user1.setStatus(Status.ONLINE);
        return repository.save(user1);
    }

    public User disconnect(AddUser user) {
        User storedUser = repository.findByUserName(user.userName);
        if (storedUser != null) {
            storedUser.setStatus(Status.OFFLINE);
            repository.save(storedUser);
        }
        return storedUser;
    }

    public List<User> findConnectedUsers() {
        return repository.findAllByStatus(Status.ONLINE);
    }
}
