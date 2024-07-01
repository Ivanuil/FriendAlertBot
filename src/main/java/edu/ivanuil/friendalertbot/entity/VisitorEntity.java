package edu.ivanuil.friendalertbot.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@Entity
@Table(name = "visitors")
public class VisitorEntity {

    @Id
    @Column(name = "username", nullable = false, length = Integer.MAX_VALUE)
    private String login;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "cluster")
    private ClusterEntity cluster;

    @Column(name = "\"row\"", length = 1)
    private String row;

    @Column(name = "number")
    private Integer number;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VisitorEntity visitor)) return false;
        return Objects.equals(login, visitor.login);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(login);
    }

    @Override
    public String toString() {
        return login;
    }
}