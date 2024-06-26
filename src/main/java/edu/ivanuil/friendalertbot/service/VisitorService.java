package edu.ivanuil.friendalertbot.service;

import edu.ivanuil.friendalertbot.entity.CampusEntity;
import edu.ivanuil.friendalertbot.entity.ClusterEntity;
import edu.ivanuil.friendalertbot.entity.VisitorEntity;
import edu.ivanuil.friendalertbot.entity.VisitorsLogEntity;
import edu.ivanuil.friendalertbot.mapper.CampusMapper;
import edu.ivanuil.friendalertbot.mapper.ClusterMapper;
import edu.ivanuil.friendalertbot.mapper.VisitorMapper;
import edu.ivanuil.friendalertbot.repository.CampusRepository;
import edu.ivanuil.friendalertbot.repository.ClusterRepository;
import edu.ivanuil.friendalertbot.repository.VisitorRepository;
import edu.ivanuil.friendalertbot.repository.VisitorsLogRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class VisitorService {

    private final School21PlatformBinding platformBinding;
    private final List<UUID> campusIgnoreList;
    private final List<Integer> clusterIgnoreList;

    private final CampusRepository campusRepository;
    private final ClusterRepository clusterRepository;
    private final VisitorRepository visitorRepository;
    private final VisitorsLogRepository visitorsLogRepository;

    private final CampusMapper campusMapper;
    private final ClusterMapper clusterMapper;
    private final VisitorMapper visitorMapper;

    private final static Map<CampusEntity, List<ClusterEntity>> clusterMap = new HashMap<>();
    private static Set<VisitorEntity> visitors = new HashSet<>();

    private List<CampusEntity> refreshCampusList() {
        var campusList = campusMapper.toCampusEntityList(
                platformBinding.getCampuses());
        int totalCampuses = campusList.size();
        campusList = campusList.stream()
                .filter(campus -> !campusIgnoreList.contains(campus.getId()))
                .toList();

        campusRepository.saveAll(campusList);
        log.info("Refreshed campuses list: {} campuses total, {} ignored",
                totalCampuses, totalCampuses - campusList.size());
        return campusList;
    }

    private List<ClusterEntity> refreshClusterListForCampus(CampusEntity campusEntity) {
        var clusterList = clusterMapper.toClusterEntityList(
                platformBinding.getClusters(campusEntity.getId()));
        int totalClusters = clusterList.size();
        clusterList = clusterList.stream()
                .filter(cluster -> !clusterIgnoreList.contains(cluster.getId()))
                .toList();

        for (var cluster : clusterList)
            cluster.setCampus(campusEntity);
        clusterRepository.saveAll(clusterList);
        log.info("Refreshed cluster list for campus '{}': {} clusters total, {} ignored",
                campusEntity.getName(), totalClusters, totalClusters - clusterList.size());
        return clusterList;
    }

    private List<VisitorEntity> getVisitorsForCluster(ClusterEntity cluster) {
        var visitorList = visitorMapper.toVisitorList(
                platformBinding.getClusterVisitors(cluster.getId()));
        for (var visitor : visitorList)
            visitor.setCluster(cluster);

        logVisitorsCount(cluster.getCampus(), cluster, visitorList.size());
        return visitorList;
    }

    private void logVisitorsCount(CampusEntity campus, ClusterEntity cluster, int visitors) {
        visitorsLogRepository.save(new VisitorsLogEntity(null, null, campus.getName(), cluster.getName(), visitors));
    }

    public void refreshCampusesAndClusters() {
        var campusList = refreshCampusList();
        for (CampusEntity campus : campusList) {
            var clusterList = refreshClusterListForCampus(campus);
            clusterMap.put(campus, clusterList);
        }
    }

    public List<VisitorEntity>[] getIncomingAndLeavingVisitors() {
        Set<VisitorEntity> newVisitors = new HashSet<>();
        for (var entry : clusterMap.entrySet()) {
            for (var cluster : entry.getValue()) {
                newVisitors.addAll(getVisitorsForCluster(cluster));
            }
        }

        List<VisitorEntity> incomingVisitors = new LinkedList<>();
        for (VisitorEntity visitor : newVisitors) {
            if (!visitors.contains(visitor))
                incomingVisitors.add(visitor);
        }

        List<VisitorEntity> leavingVisitors = new LinkedList<>();
        for (VisitorEntity visitor : visitors) {
            if (!newVisitors.contains(visitor))
                leavingVisitors.add(visitor);
        }

        visitorRepository.saveAll(incomingVisitors);
        visitorRepository.deleteAll(leavingVisitors);
        visitors = newVisitors;
        log.info("Retrieved visitors for cluster : {} total, {} incoming, {} leaving",
                visitors.size(), incomingVisitors.size(), leavingVisitors.size());
        return new List[] {incomingVisitors, leavingVisitors};
    }

    @PostConstruct
    public void init() {
        refreshCampusesAndClusters();

        for (var entry : clusterMap.entrySet())
            for (var cluster: entry.getValue())
                visitors.addAll(visitorRepository.findAllByCluster(cluster));
    }

}
