package edu.ivanuil.friendalertbot.service;

import edu.ivanuil.friendalertbot.exception.TooManyRequestsException;
import edu.ivanuil.friendalertbot.dto.platform.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Service
public class School21PlatformBinding {

    private final RestTemplate restTemplate = new RestTemplate();

    private static final String GET_CAMPUSES_URL = "https://edu-api.21-school.ru/services/21-school/api/v1/campuses";
    private static final String GET_CLUSTERS_URL = "https://edu-api.21-school.ru/services/21-school/api/v1/campuses/%s/clusters";
    private static final String GET_CLUSTER_VISITORS_URL = "https://edu-api.21-school.ru/services/21-school/api/v1/clusters/%s/map?limit=1000&offset=0&occupied=true";
    private static final String GET_USER_INFO_URL = "https://edu-api.21-school.ru/services/21-school/api/v1/participants/%s";

    @Value("${school21.platform.token}")
    private String TOKEN;

    private HttpEntity<Void> getRequestEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", TOKEN);
        return new HttpEntity<>(headers);
    }

    @Retryable(retryFor = TooManyRequestsException.class, maxAttempts = 5, backoff = @Backoff(delay = 1000))
    public CampusDto[] getCampuses() {
        try {
            ResponseEntity<CampusesDto> response = restTemplate.exchange(
                    GET_CAMPUSES_URL, HttpMethod.GET, getRequestEntity(), CampusesDto.class);
            return response.getBody().getCampuses();
        } catch (RuntimeException e) {
            throw new TooManyRequestsException(e);
        }
    }

    @Retryable(retryFor = TooManyRequestsException.class, maxAttempts = 5, backoff = @Backoff(delay = 1000))
    public ClusterDto[] getClusters(UUID campusId) {
        try {
            ResponseEntity<ClustersDto> response = restTemplate.exchange(
                    String.format(GET_CLUSTERS_URL, campusId), HttpMethod.GET, getRequestEntity(), ClustersDto.class);
            return response.getBody().getClusters();
        } catch (RuntimeException e) {
            throw new TooManyRequestsException(e);
        }
    }

    @Retryable(retryFor = TooManyRequestsException.class, maxAttempts = 5, backoff = @Backoff(delay = 1500))
    public WorkplaceDto[] getClusterVisitors(Integer clusterId) {
        try {
            ResponseEntity<ClusterMapDto> response = restTemplate.exchange(
                    String.format(GET_CLUSTER_VISITORS_URL, clusterId), HttpMethod.GET,
                    getRequestEntity(), ClusterMapDto.class);
            return response.getBody().getClusterMap();
        } catch (RuntimeException e) {
            throw new TooManyRequestsException(e);
        }
    }

    @Retryable(retryFor = TooManyRequestsException.class, maxAttempts = 5, backoff = @Backoff(delay = 1000))
    public boolean checkIfUserExists(String username) {
        try {
            ResponseEntity<?> response = restTemplate.exchange(
                    String.format(GET_USER_INFO_URL, username), HttpMethod.GET,
                    getRequestEntity(), ParticipantDto.class);
            return true;
        } catch (HttpClientErrorException e) {
            if (e.getMessage().contains("404"))
                return false;
            else
                throw new TooManyRequestsException(e);
        }
    }

}
