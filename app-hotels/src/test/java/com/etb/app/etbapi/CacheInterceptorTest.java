package com.etb.app.etbapi;

import android.support.annotation.NonNull;

import com.easytobook.api.EtbApi;
import com.etb.app.utils.NetworkUtilities;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import com.squareup.okhttp.mockwebserver.RecordedRequest;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;

import okio.BufferedSource;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author alex
 * @date 2015-07-08
 */
public class CacheInterceptorTest {
    @Rule
    public TemporaryFolder cacheRule = new TemporaryFolder();

    private OkHttpClient client;
    private MockWebServer server;

    @Before
    public void setUp() throws Exception {

        server = new MockWebServer();

        server.start();
    }

    @After
    public void tearDown() throws Exception {
        server.shutdown();
    }

    @Test
    public void testResponseInterceptor() throws IOException {

        MockResponse mockResponseNoCache = getMockResponse("A sample body", "no-cache");
        MockResponse mockResponseWithAge = getMockResponse("A sample body", "max-age=100");

        server.enqueue(mockResponseNoCache);
        server.enqueue(mockResponseNoCache);
        server.enqueue(mockResponseWithAge);

        client = new OkHttpClient();
        client.networkInterceptors().add(new CacheResponseInterceptor());

        Request searchRequest = new Request.Builder()
                .url(server.getUrl(EtbApi.PATH_SEARCH))
                .build();

        Response response1 = client.newCall(searchRequest).execute();

        assertEquals(200, response1.code());
        assertEquals("max-age=" + CacheResponseInterceptor.CACHE_AGE_DEFAULT, response1.header("Cache-Control"));

//  TODO: post orders
//        Request orderRequest = new Request.Builder()
//                .url(server.getUrl(EtbApi.PATH_ORDERS))
//                .build();
//
//        Response response2 = client.newCall(orderRequest).execute();
//        assertEquals(200, response2.code());
//        assertEquals("no-cache", response2.header("Cache-Control"));
//
//        Response response3 = client.newCall(searchRequest).execute();
//        assertEquals(200, response3.code());
//        assertEquals("max-age=100", response3.header("Cache-Control"));


    }

    @Test
    public void testSearchRequestCache() throws IOException, InterruptedException {

        MockResponse mockResponseOriginalBody = getMockResponse("Original body", "no-cache");
        MockResponse mockResponseNewBody = getMockResponse("New body", "no-cache");
        MockResponse mockResponseLast = getMockResponse("The last body", "no-cache");

        server.enqueue(mockResponseOriginalBody);
        server.enqueue(mockResponseNewBody);
        server.enqueue(mockResponseLast);

        NetworkUtilities networkUtilities = mock(NetworkUtilities.class);
        CacheResponseInterceptor responseInterceptor = new CacheResponseInterceptor();
        responseInterceptor.setCacheAge(2); // 1 second
        CacheRequestInterceptor requestInterceptor = new CacheRequestInterceptor(networkUtilities);
        requestInterceptor.setSearchMaxAge(1);

        Cache cache = new Cache(cacheRule.getRoot(), Integer.MAX_VALUE);
        client = createHttpClient(responseInterceptor, requestInterceptor, cache);

        when(networkUtilities.isConnected()).thenReturn(true);

        Request searchRequest = new Request.Builder()
                .url(server.getUrl(EtbApi.PATH_SEARCH))
                .build();

        // 1. Initial request stored for 2 seconds
        Response response1 = client.newCall(searchRequest).execute();
        assertEquals("Original body", readBody(response1.body()));
        assertEquals(1, cache.getWriteSuccessCount());
        assertEquals(0, cache.getWriteAbortCount());

        // 2. Cached response
        Response response2 = client.newCall(searchRequest).execute();
        assertEquals("Original body", readBody(response2.body()));
        assertEquals(1, cache.getHitCount());

        Thread.sleep(2000);
        // 3. Not cached response stored for 5 sec
        responseInterceptor.setCacheAge(5);
        Response response3 = client.newCall(searchRequest).execute();
        assertEquals("New body", readBody(response3.body()));
        assertEquals(1, cache.getHitCount());
        assertEquals(2, cache.getWriteSuccessCount());

        when(networkUtilities.isConnected()).thenReturn(false);

        Thread.sleep(2000);
        // 4. No network cached response
        Response response4 = client.newCall(searchRequest).execute();
        assertEquals("New body", readBody(response4.body()));
        assertEquals(2, cache.getHitCount());

        when(networkUtilities.isConnected()).thenReturn(true);
        // 5. The last not cached response
        Response response5 = client.newCall(searchRequest).execute();
        assertEquals("The last body", readBody(response5.body()));
        assertEquals(2, cache.getHitCount());

        RecordedRequest modifiedRequest1 = server.takeRequest();
        assertEquals("max-age=1", modifiedRequest1.getHeader("Cache-Control"));

        RecordedRequest modifiedRequest2 = server.takeRequest();
        assertEquals("max-age=1", modifiedRequest2.getHeader("Cache-Control"));

    }

    private OkHttpClient createHttpClient(CacheResponseInterceptor responseInterceptor, CacheRequestInterceptor requestInterceptor, Cache cache) {
        OkHttpClient client = new OkHttpClient();
        client.networkInterceptors().add(responseInterceptor);
        client.interceptors().add(requestInterceptor);
        client.setCache(cache);
        return client;
    }

    @Test
    public void testAccomodationRequestCache() throws IOException, InterruptedException {
        MockResponse mockResponseAcc1 = getMockResponse("Accommodation #1 body", "no-cache");
        MockResponse mockResponseAcc2 = getMockResponse("Accommodation #2 body", "no-cache");

        server.enqueue(mockResponseAcc1);
        server.enqueue(mockResponseAcc2);

        NetworkUtilities networkUtilities = mock(NetworkUtilities.class);
        CacheResponseInterceptor responseInterceptor = new CacheResponseInterceptor();
        responseInterceptor.setCacheAge(2); // 1 second
        CacheRequestInterceptor requestInterceptor = new CacheRequestInterceptor(networkUtilities);
        requestInterceptor.setSearchMaxAge(1);

        Cache cache = new Cache(cacheRule.getRoot(), Integer.MAX_VALUE);
        client = createHttpClient(responseInterceptor, requestInterceptor, cache);

        when(networkUtilities.isConnected()).thenReturn(true);

        Request accommodationRequest = new Request.Builder()
                .url(server.getUrl(EtbApi.PATH_ACCOMMODATIONS + "/1"))
                .build();

        // 1. Initial request stored for 2 seconds
        responseInterceptor.setCacheAge(5);
        Response responseAcc = client.newCall(accommodationRequest).execute();
        assertEquals("Accommodation #1 body", readBody(responseAcc.body()));
        assertEquals(1, cache.getWriteSuccessCount());
        assertEquals(0, cache.getWriteAbortCount());

        // 2. Network connected - fetch from server
        Response responseConnected = client.newCall(accommodationRequest).execute();
        assertEquals("Accommodation #2 body", readBody(responseConnected.body()));
        assertEquals(2, cache.getWriteSuccessCount());
        assertEquals(0, cache.getHitCount());

        when(networkUtilities.isConnected()).thenReturn(false);
        Response responseDisconnected = client.newCall(accommodationRequest).execute();
        assertEquals("Accommodation #2 body", readBody(responseDisconnected.body()));
        assertEquals(1, cache.getHitCount());
    }

    @NonNull
    private MockResponse getMockResponse(String body, String cacheControl) {
        return new MockResponse()
                .setResponseCode(200)
                .setBody(body)
                .addHeader("Cache-Control", cacheControl);
    }

    private String readBody(ResponseBody body) throws IOException {
        BufferedSource in = body.source();
        String bodyString = in.readUtf8();
        in.close();
        return bodyString;
    }
}
