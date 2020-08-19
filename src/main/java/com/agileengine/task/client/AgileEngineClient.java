package com.agileengine.task.client;

import com.agileengine.task.configuration.AgileEngineApiConfig;
import com.agileengine.task.dto.AuthResponse;
import com.agileengine.task.dto.ImageDto;
import com.agileengine.task.dto.Page;
import com.sun.istack.NotNull;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.agileengine.task.Constants.*;

@Component
public class AgileEngineClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(AgileEngineClient.class);

    private AgileEngineApiConfig apiConfig;
    private String loginUrl;
    private String bearerToken;
    private RestTemplate restTemplate;
    private Boolean loggedIn;

    @Autowired
    public AgileEngineClient(AgileEngineApiConfig agileEngineApiConfig, RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        loggedIn = false;
        apiConfig = agileEngineApiConfig;
        loginUrl = apiConfig.getBaseUri() + PATH_LOGIN;
        restTemplate.setInterceptors(configureInterceptor());
    }

    public Page getInitialPage() {
        ResponseEntity<Page> page = restTemplate
                .getForEntity(UriComponentsBuilder
                        .fromHttpUrl(apiConfig.getBaseUri())
                        .path(PATH_IMAGES)
                        .build().toUriString(), Page.class);
        if (page.getStatusCode().equals(HttpStatus.OK)) {
            return page.getBody();
        } else {
            LOGGER.info(String.valueOf(page.getStatusCodeValue()));
            throw new IllegalStateException("Failed to load page");
        }
    }

    public Page getPage(@NotNull Integer pageNumber) {
        UriComponents uri = UriComponentsBuilder.fromHttpUrl(apiConfig.getBaseUri()).path(PATH_IMAGES).queryParam(PARAM_PAGES, pageNumber).build();
        ResponseEntity<Page> resultPage = restTemplate.getForEntity(uri.toUriString(), Page.class);
        if (resultPage.getStatusCode().equals(HttpStatus.OK)) {
            return resultPage.getBody();
        } else {
            throw new IllegalStateException("Failed to load page " + pageNumber);
        }
    }

    public ImageDto getImageDetails(@NotNull String imageId) {
        UriComponents uri = UriComponentsBuilder.fromHttpUrl(apiConfig.getBaseUri()).path(PATH_IMAGES).path("/" + imageId).build();
        ResponseEntity<ImageDto> resultImage = restTemplate.getForEntity(uri.toUriString(), ImageDto.class);
        if (resultImage.getStatusCode().equals(HttpStatus.OK)) {
            return resultImage.getBody();
        } else {
            throw new IllegalStateException("Failed to load image with id " + imageId);
        }
    }

    public boolean login() throws AccessDeniedException {
        LOGGER.info("Login to agile engine api");
        ResponseEntity<AuthResponse> response =
                restTemplate.postForEntity(loginUrl, constructAuthRequest(), AuthResponse.class);
        final AuthResponse authorization = response.getBody();
        if (HttpStatus.OK.equals(response.getStatusCode()) && BooleanUtils.isTrue(authorization.getAuth())) {
            bearerToken = Objects.requireNonNull(authorization).getToken();
            loggedIn = true;
            LOGGER.info("Login to api success");
        } else if (HttpStatus.FORBIDDEN.equals(response.getStatusCode())) {
            throw new AccessDeniedException("Failed to login to api");
        } else {
            throw new IllegalStateException("Failed to login to api");
        }
        return HttpStatus.OK.equals(response.getStatusCode());
    }

    private List<ClientHttpRequestInterceptor> configureInterceptor() {
        List<ClientHttpRequestInterceptor> interceptors
                = restTemplate.getInterceptors();
        if (CollectionUtils.isEmpty(interceptors)) {
            interceptors = new ArrayList<>();
        }
        interceptors.add((httpRequest, bytes, clientHttpRequestExecution) -> {
            if (!loggedIn && !httpRequest.getURI().getPath().contains
                    (PATH_LOGIN)) {
                login();
            }

            httpRequest.getHeaders().add(AUTHORIZATION, bearerToken);

            return clientHttpRequestExecution.execute(httpRequest, bytes);
        });
        return interceptors;
    }

    private HttpEntity<String> constructAuthRequest() {
        JSONObject apiKeyJson = new JSONObject();
        try {
            apiKeyJson.put(API_KEY_FIELD, apiConfig.getKey());
        } catch (JSONException e) {
            LOGGER.error("Failed to construct json: {}", e.getMessage());
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(apiKeyJson.toString(), headers);
    }

}
