package com.redhat.che.workspace.services.telemetry.woopra;

import org.eclipse.che.api.core.rest.DefaultHttpJsonRequestFactory;
import org.eclipse.che.api.core.rest.HttpJsonRequest;
import org.eclipse.che.api.core.rest.HttpJsonRequestFactory;
import org.eclipse.che.api.core.rest.shared.dto.Link;

import javax.validation.constraints.NotNull;

public class MockHttpJsonRequestFactory implements HttpJsonRequestFactory {
    public HttpJsonRequestFactory requestFactory() {
        return new DefaultHttpJsonRequestFactory() {

            @Override
            public HttpJsonRequest fromUrl(String url) {
                return new MockHttpRequest();
            }

            @Override
            public HttpJsonRequest fromLink(Link link) {
                return new MockHttpRequest();
            }
        };
    }

    @Override
    public HttpJsonRequest fromUrl(@NotNull String s) {
        return null;
    }

    @Override
    public HttpJsonRequest fromLink(@NotNull Link link) {
        return null;
    }
}
