package edu.ivanuil.friendalertbot.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Configuration
public class PlatformIntegrationConfiguration {

    @Bean
    public List<UUID> campusIgnoreList() {
        return Arrays.asList(
                UUID.fromString("c707e947-a097-459f-8c82-5ca548e46abe"),  // Canary 21
                UUID.fromString("9bc1136e-b5c8-4dd1-94e8-f94e4029c9f8"),  // 21 Test QA
                UUID.fromString("8832e878-577e-4583-847a-d7e1db5d5507"),  // 21 Test
                UUID.fromString("bad03b39-ffd4-4217-9d24-65535fe1f293"),  // 21 Tashkent
                UUID.fromString("04989f19-21a3-41c0-af4e-89db4d8d4c6b"),  // 21 Sakhalin
                UUID.fromString("981e10b5-7553-406a-940e-83d34342c113"),  // 21 Belgorod
                UUID.fromString("e561d833-400b-44f5-a6ac-951b0378a9f6")); // School 21 online
    }

    @Bean
    public List<Integer> clusterIgnoreList() {
        return Arrays.asList(
                36736, // ulugbek
                36799, // Dune
                36735  // register
        );
    }

}
