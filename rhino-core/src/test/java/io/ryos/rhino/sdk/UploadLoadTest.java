package io.ryos.rhino.sdk;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;

@Ignore
public class UploadLoadTest {

  private static final String SIM_NAME = "Reactive Upload Test";
  private static final String PROPERTIES_FILE = "classpath:///rhino.properties";

  @Rule
  public WireMockRule wireMockRule = new WireMockRule(8089);

  @Test
  public void testReactiveBasicHttp() {

    stubFor(WireMock.post(urlEqualTo("/token"))
            .willReturn(aResponse()
                    .withStatus(200)
                    .withBody("{\"access_token\": \"abc123\", \"refresh_token\": \"abc123\"}")));

    stubFor(WireMock.put(urlEqualTo("/api/files"))
            .willReturn(aResponse()
                    .withStatus(201)));

    Simulation.create(PROPERTIES_FILE, SIM_NAME).start();
  }
}
