package io.ryos.examples.benchmark;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import io.ryos.rhino.sdk.Simulation;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class Rhino {

    private static final String PROPS = "classpath:///rhino.properties";
    private static final String SIM_NAME = "Server-Status Simulation";
    private static final int PORT = 8089;

    public static void main(String ... args) {

        var wireMockServer = new WireMockServer(PORT);
        wireMockServer.start();

        configureFor("localhost", 8089);
        stubFor(WireMock.get(urlEqualTo("/api/status")).willReturn(aResponse()
                        .withStatus(200)));
        stubFor(WireMock.post(urlEqualTo("/token"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody("{\"access_token\": \"abc123\", \"refresh_token\": \"abc123\"}")));


        Simulation.create(PROPS, SIM_NAME).start();

        wireMockServer.stop();
    }
}
