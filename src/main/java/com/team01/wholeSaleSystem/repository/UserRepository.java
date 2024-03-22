package com.team01.wholeSaleSystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.team01.wholeSaleSystem.entity.User;
import com.team01.wholeSaleSystem.liveChat.user.Status;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUserName(String userName);
    User findByRememberMeToken(String rememberMeToken);
    User findBySessionToken(String sessionToken);

    List<User> findAllByStatus(Status status);
}
