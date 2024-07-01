package edu.ivanuil.friendalertbot.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "chat")
@NoArgsConstructor
@AllArgsConstructor
public class ChatEntity {

    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "telegram_username", length = Integer.MAX_VALUE)
    private String telegramUsername;

    @Column(name = "platform_username", length = Integer.MAX_VALUE)
    private String platformUsername;

    @Column(name = "state")
    @Enumerated(EnumType.STRING)
    private ChatState state;

    @OneToMany(mappedBy = "subscriberChat")
    private Set<SubscriptionEntity> friendSubscriptions = new LinkedHashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChatEntity that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

}