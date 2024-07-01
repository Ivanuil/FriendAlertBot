package edu.ivanuil.friendalertbot.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "campus")
public class CampusEntity {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "name", length = Integer.MAX_VALUE)
    private String name;

    @Override
    public String toString() {
        return "Campus{ id=" + id + ", name=" + name + "}";
    }

}