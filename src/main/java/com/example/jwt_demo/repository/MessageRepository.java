package com.example.jwt_demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.jwt_demo.model.MessageEntity;

@Repository
public interface MessageRepository extends JpaRepository<MessageEntity, Long> {
    @Query("SELECT m FROM MessageEntity m WHERE " +
           "(m.sender = :sender1 AND m.recipient = :recipient1) OR " +
           "(m.sender = :sender2 AND m.recipient = :recipient2) " +
           "ORDER BY m.timestamp")
    List<MessageEntity> findBySenderAndRecipientOrRecipientAndSenderOrderByTimestamp(
        @Param("sender1") String sender1,
        @Param("recipient1") String recipient1,
        @Param("sender2") String sender2,
        @Param("recipient2") String recipient2
    );
} 