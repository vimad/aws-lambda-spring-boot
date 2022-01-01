package com.bookrepo;


import com.amazonaws.serverless.proxy.internal.LambdaContainerHandler;
import com.amazonaws.serverless.proxy.internal.testutils.AwsProxyRequestBuilder;
import com.amazonaws.serverless.proxy.internal.testutils.MockLambdaContext;
import com.amazonaws.serverless.proxy.model.AwsProxyResponse;
import com.amazonaws.services.lambda.runtime.Context;
import com.bookrepo.domain.Book;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Map;


public class StreamLambdaHandlerTest {

    private static StreamLambdaHandler handler;
    private static Context lambdaContext;
    private static ObjectMapper objectMapper;

    @BeforeAll
    public static void setUp() {
        updateEnv(Constants.APP_CONFIG_APPLICATION,"book-shop");
        updateEnv(Constants.APP_CONFIG_ENVIRONMENT,"dev");
        updateEnv(Constants.APP_CONFIG_PROFILE,"app-configuration");
        updateEnv(Constants.APP_CONFIG_READ_STRATEGY,"SDK");
        handler = new StreamLambdaHandler();
        lambdaContext = new MockLambdaContext();
        objectMapper = LambdaContainerHandler.getObjectMapper();
    }

    @Test
    @Disabled
    public void getAllBooks() {
        InputStream requestStream = new AwsProxyRequestBuilder("/books", HttpMethod.GET)
                                            .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
                                            .buildStream();
        ByteArrayOutputStream responseStream = new ByteArrayOutputStream();

        handle(requestStream, responseStream);

        AwsProxyResponse response = readResponse(responseStream);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(Response.Status.OK.getStatusCode(), response.getStatusCode());

        Assertions.assertFalse(response.isBase64Encoded());

        Assertions.assertTrue(response.getMultiValueHeaders().containsKey(HttpHeaders.CONTENT_TYPE));
        Assertions.assertTrue(response.getMultiValueHeaders().getFirst(HttpHeaders.CONTENT_TYPE).startsWith(MediaType.APPLICATION_JSON));
    }

    @Test
    @Disabled
    public void SaveBook() throws JsonProcessingException {
        Book book = new Book();
        book.setAuthor("author");
        book.setIsbn(String.valueOf(System.currentTimeMillis()));
        book.setName(String.valueOf(System.currentTimeMillis()) + "book");
        InputStream requestStream = new AwsProxyRequestBuilder("/books", HttpMethod.POST)
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .body(objectMapper.writeValueAsString(book))
                .buildStream();
        ByteArrayOutputStream responseStream = new ByteArrayOutputStream();

        handle(requestStream, responseStream);

        AwsProxyResponse response = readResponse(responseStream);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(Response.Status.OK.getStatusCode(), response.getStatusCode());

        Assertions.assertFalse(response.isBase64Encoded());
    }

    private void handle(InputStream is, ByteArrayOutputStream os) {
        try {
            handler.handleRequest(is, os, lambdaContext);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private AwsProxyResponse readResponse(ByteArrayOutputStream responseStream) {
        try {
            return objectMapper.readValue(responseStream.toByteArray(), AwsProxyResponse.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void updateEnv(String name, String val) {
        try {
            Map<String, String> env = System.getenv();
            Field field = env.getClass().getDeclaredField("m");
            field.setAccessible(true);
            ((Map<String, String>) field.get(env)).put(name, val);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
