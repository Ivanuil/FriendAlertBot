package edu.ivanuil.friendalertbot.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "cluster")
public class ClusterEntity {

    @Id
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "campus")
    private CampusEntity campus;

    @Column(name = "name", length = Integer.MAX_VALUE)
    private String name;

}