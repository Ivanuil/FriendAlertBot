package edu.ivanuil.friendalertbot.mapper;

import edu.ivanuil.friendalertbot.dto.VisitorDto;
import edu.ivanuil.friendalertbot.dto.platform.WorkplaceDto;
import edu.ivanuil.friendalertbot.entity.CampusEntity;
import edu.ivanuil.friendalertbot.entity.ClusterEntity;
import edu.ivanuil.friendalertbot.entity.VisitorEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface VisitorMapper {

    VisitorEntity toVisitorEntity(WorkplaceDto workplaceDto);

    List<VisitorEntity> toVisitorList(WorkplaceDto[] workplaceDto);

    @Mapping(source = "cluster", target = "cluster", qualifiedByName = "getClusterName")
    @Mapping(source = "cluster.campus", target = "campus", qualifiedByName = "getCampusName")
    VisitorDto toVisitorDto(VisitorEntity visitorEntity);

    @Named("getClusterName")
    default String getClusterName(ClusterEntity cluster) {
        return cluster.getName();
    }

    @Named("getCampusName")
    default String getCampusName(CampusEntity campus) {
        return campus.getName();
    }

}
