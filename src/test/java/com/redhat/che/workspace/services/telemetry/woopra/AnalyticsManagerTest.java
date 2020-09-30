package com.redhat.che.workspace.services.telemetry.woopra;

import com.redhat.che.workspace.services.telemetry.woopra.exception.WoopraCredentialException;
import io.quarkus.test.Mock;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class AnalyticsManagerTest {

    @Mock
    AnalyticsProvider analyticsProvider;

    @Mock
    HttpUrlConnectionProvider httpUrlConnectionProvider;

    @Test
    public void willNotStartWithoutWriteKeyEndpoint() {
        assertThrows(WoopraCredentialException.class, () -> {
            AnalyticsManager am = new AnalyticsManager(null,
                    null,
                    null,
                    null,
                    "https://fake-che-api",
                    "fake-workspace",
                    "fake-token",
                    new MockHttpJsonRequestFactory().requestFactory(),
                    analyticsProvider,
                    httpUrlConnectionProvider);
        });
    }

    @Test
    public void willNotStartWithoutWoopraDomain() {
        assertThrows(WoopraCredentialException.class, () -> {
            AnalyticsManager am = new AnalyticsManager("defined write key",
                    null,
                    null,
                    null,
                    "https://fake-che-api",
                    "fake-workspace",
                    "fake-token",
                    new MockHttpJsonRequestFactory().requestFactory(),
                    analyticsProvider,
                    httpUrlConnectionProvider);
        });
    }
}
