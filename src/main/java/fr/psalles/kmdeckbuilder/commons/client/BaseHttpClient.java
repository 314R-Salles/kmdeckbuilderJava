package fr.psalles.kmdeckbuilder.commons.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.psalles.kmdeckbuilder.commons.exceptions.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;

import java.io.IOException;

import static org.apache.logging.log4j.util.Strings.EMPTY;

@Slf4j
@Component
public class BaseHttpClient {
    private final ObjectMapper mapper;
    private final RestClient restClient;

    @Autowired
    public BaseHttpClient() {
        this.mapper = new ObjectMapper();
        this.restClient = RestClient.create();
    }

    public <R> R makeCall(HttpMethod method,
                          String url,
                          Class<R> responseClass,
                          Object request,
                          HttpHeaders extraHeaders) {

        String jsonRequest;
        if (request instanceof String) {
            jsonRequest = (String) request;
        } else {
            jsonRequest = formatRequestToJson(request);
        }
        HttpStatusCode httpStatus;
        String responseBody;
        try {
            ResponseEntity<String> responseEntity = sendHttpRequest(method, url, extraHeaders, jsonRequest);
            httpStatus = responseEntity.getStatusCode();
            responseBody = responseEntity.hasBody() ? responseEntity.getBody() : EMPTY;

            return parseResponseFromJson(responseBody, responseClass);

        } catch (HttpClientErrorException | HttpServerErrorException restEx) {
            httpStatus = restEx.getStatusCode();
            responseBody = restEx.getResponseBodyAsString();
            log.info("Following ERROR response has been received [{}] : {}", httpStatus, responseBody);
            if (httpStatus.equals(HttpStatus.UNAUTHORIZED)) {
                throw new UnauthorizedException(responseBody);
            }
            if (httpStatus.equals(HttpStatus.NOT_FOUND)) {
                throw new ResourceNotFoundException(responseBody);
            }
            if (httpStatus.equals(HttpStatus.FORBIDDEN)) {
                throw new ForbiddenException(responseBody);
            }
            if (httpStatus.equals(HttpStatus.BAD_REQUEST)) {
                throw new BusinessException(responseBody);
            }
            throw restEx;
        } catch (Exception e) {
            log.info("Error while parsing response", e);
            throw e;
        }
    }

    private String formatRequestToJson(Object request) {
        try {
            return mapper.writeValueAsString(request);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    private <R> R parseResponseFromJson(String responseBody, Class<R> responseClass) {
        try {
            if (responseClass == String.class) {
                return (R) responseBody;
            }
            if (!responseBody.equals(EMPTY)) {
                return mapper.readValue(responseBody, responseClass);
            } else {
                return null;
            }
        } catch (IOException e) {
            throw new TechnicalException("Erreur lors du parsing d'un appel http");
        }
    }


    private ResponseEntity<String> sendHttpRequest(HttpMethod method, String url, HttpHeaders extraHeaders,
                                                   String jsonRequest) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        if (extraHeaders != null) {
            headers.putAll(extraHeaders);
        }

        log.info(url);
        if (method.equals(HttpMethod.POST)) {
            return restClient.method(method).uri(url)
                    .body(jsonRequest)
                    .headers(h -> h.addAll(headers))
                    .retrieve().toEntity(String.class);
        } else {
            // GET
            return restClient.get().uri(url)
                    .headers(h -> h.addAll(headers))
                    .retrieve().toEntity(String.class);
        }

    }


}
