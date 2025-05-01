package com.example.lineta_posts_interaction.client;

import com.example.lineta_posts_interaction.dto.response.UserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserClient {

    private final RestTemplate restTemplate;

    @Value("http://localhost:8080")
    private String userServiceUrl;

    public Map<String, UserDTO> getUsersByUsernames(Set<String> usernames, String token) {
        String url = userServiceUrl + "/api/users/batch-by-username";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Thêm token vào header Authorization
        headers.setBearerAuth(token); // tương đương: headers.set("Authorization", "Bearer " + token);

        HttpEntity<Set<String>> request = new HttpEntity<>(usernames, headers);

        ResponseEntity<UserDTO[]> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                request,
                UserDTO[].class
        );

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return Arrays.stream(response.getBody())
                    .collect(Collectors.toMap(UserDTO::getUsername, user -> user));
        }

        return new HashMap<>();
    }

}
