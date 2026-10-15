package com.cabshare.repository;

import com.cabshare.model.Notification;
import com.cabshare.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findTop10ByUserOrderByIdDesc(User user);
}
