package edu.ivanuil.friendalertbot.dto.platform.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TokenDto {

    @JsonProperty("access_token")
    String accessToken;

    @JsonProperty("expires_in")
    Integer expiresIn;

    @JsonProperty("refresh_expires_in")
    Integer refreshExpiresIn;

    @JsonProperty("refresh_token")
    String refreshToken;

    @JsonProperty("token_type")
    String tokenType;

    @JsonProperty("before")
    Integer nonBeforePolicy;

    @JsonProperty("session_state")
    String sessionState;

    String scope;

}
