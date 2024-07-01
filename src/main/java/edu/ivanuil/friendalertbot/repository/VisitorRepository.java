package edu.ivanuil.friendalertbot.repository;

import edu.ivanuil.friendalertbot.entity.ClusterEntity;
import edu.ivanuil.friendalertbot.entity.VisitorEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;

public interface VisitorRepository extends JpaRepository<VisitorEntity, Integer> {

    Set<VisitorEntity> findAllByCluster(ClusterEntity cluster);

    Optional<VisitorEntity> findByLogin(String login);

}
