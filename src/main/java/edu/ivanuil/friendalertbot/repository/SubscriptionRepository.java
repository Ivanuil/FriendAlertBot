package edu.ivanuil.friendalertbot.repository;

import edu.ivanuil.friendalertbot.entity.SubscriptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface SubscriptionRepository extends JpaRepository<SubscriptionEntity, Integer> {

    Set<SubscriptionEntity> findAllBySubscriptionUsername(String subscriptionUsername);

    List<SubscriptionEntity> findAllBySubscriberChat_TelegramUsername(String username);

}
