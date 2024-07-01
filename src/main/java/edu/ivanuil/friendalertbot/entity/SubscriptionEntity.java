package edu.ivanuil.friendalertbot.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "friend_subscription")
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "friend_subscription_id_gen")
    @SequenceGenerator(name = "friend_subscription_id_gen", sequenceName = "friend_subscription_sequence", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "subscriber_telegram", referencedColumnName = "telegram_username")
    private ChatEntity subscriberChat;

    @Column(name = "subscription_username", length = Integer.MAX_VALUE)
    private String subscriptionUsername;

}