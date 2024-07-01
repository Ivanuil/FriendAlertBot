package edu.ivanuil.friendalertbot.repository;

import edu.ivanuil.friendalertbot.entity.ChatEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatRepository extends JpaRepository<ChatEntity, Long> {

    Optional<ChatEntity> findByPlatformUsername(String username);

}
