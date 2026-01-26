package com.code.duel.code.duel.Judge;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;

@Component
public class Judge0Wrapper {

    private static final Logger logger = LoggerFactory.getLogger(Judge0Wrapper.class);

    private static final String JUDGE0_API_KEY = "64431d57cbmsh5e695d9da960983p1ca418jsn067194f9b2fa";
    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();

    public String getJUDGE0API_KEY() {
        return JUDGE0_API_KEY;
    }

    public String submit(String sourceCode, int languageId, String input, String expected_output) throws IOException, InterruptedException {
        String sourceCodeEncoded = Base64.getEncoder().encodeToString(sourceCode.getBytes());
        String inputEncoded = Base64.getEncoder().encodeToString(input.getBytes());
        String expectedOutputEncoded = Base64.getEncoder().encodeToString(expected_output.getBytes());
        String jsonPayload = "{\n" +
                "  \"source_code\": \"" + sourceCodeEncoded + "\",\n" +
                "  \"language_id\": " + languageId + ",\n" +
                "  \"stdin\": \"" + inputEncoded + "\",\n" +
                "  \"expected_output\": \"" + expectedOutputEncoded + "\"\n" +
                "}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://127.0.0.1:2358/submissions?base64_encoded=true&wait=true&fields=*"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();

        logger.info("Sending request to Judge0 API: payload length={}, languageId={}", jsonPayload.length(), languageId);
        HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        logger.debug("Judge0 response status code: {}, body length: {}", response.statusCode(), response.body().length());

        validateResponse(response, "Judge0 submissions");

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(response.body());
        String statusDescription = rootNode.path("status").path("description").asText();

        if (statusDescription == null || statusDescription.isBlank()) {
            logger.warn("Judge0 response missing status description, raw body: {}", response.body());
        } else {
            logger.info("Received Judge0 status: {}", statusDescription);
        }

        return statusDescription;
    }

    public String getLanguages() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://127.0.0.1:2358/languages"))
                .GET()
                .build();

        HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        logger.info("Fetched language list, status code {}", response.statusCode());
        validateResponse(response, "Judge0 languages");

        return response.body();
    }

    private void validateResponse(HttpResponse<String> response, String context) throws IOException {
        int statusCode = response.statusCode();
        if (statusCode < 200 || statusCode >= 300) {
            String message = String.format("%s request failed with status %d and body: %s", context, statusCode, response.body());
            logger.error(message);
            throw new IOException(message);
        }
    }
}