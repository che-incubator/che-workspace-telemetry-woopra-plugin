package com.redhat.che.workspace.services.telemetry.woopra;

import org.eclipse.che.incubator.workspace.telemetry.base.AbstractAnalyticsManager;
import org.eclipse.che.incubator.workspace.telemetry.base.BaseConfiguration;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import java.util.Optional;

@Dependent
public class MainConfiguration extends BaseConfiguration {
    @ConfigProperty(name = "segment.write.key")
    Optional<String> segmentWriteKey;

    @ConfigProperty(name = "woopra.domain")
    Optional<String> woopraDomain;

    @ConfigProperty(name = "segment.write.key.endpoint")
    Optional<String> segmentWriteKeyEndpoint;

    @ConfigProperty(name = "woopra.domain.endpoint")
    Optional<String> woopraDomainEndpoint;


    @Produces
    public AbstractAnalyticsManager analyticsManager() {
        return new AnalyticsManager(segmentWriteKey.orElse(null),
                woopraDomain.orElse(null),
                segmentWriteKeyEndpoint.orElse(null), woopraDomainEndpoint.orElse(null),
                apiEndpoint,
                workspaceId,
                machineToken,
                requestFactory(),
                new AnalyticsProvider(),
                new HttpUrlConnectionProvider());
    }
}
