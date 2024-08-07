package edu.ivanuil.friendalertbot.configuration;

import com.clickhouse.client.ClickHouseCredentials;
import com.clickhouse.client.ClickHouseNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClickHouseConfiguration {

    @Bean
    public ClickHouseNode clickHouseNode(@Value("${clickhouse.url}") final String url) {
        return ClickHouseNode.of(url + "?compress=0");
    }

    @Bean
    public ClickHouseCredentials clickHouseCredentials(@Value("${clickhouse.username}") final String username,
                                                       @Value("${clickhouse.password}") final String password) {
        return ClickHouseCredentials
                .fromUserAndPassword(username, password);
    }

}

