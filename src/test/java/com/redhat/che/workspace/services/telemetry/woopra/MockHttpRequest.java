package com.redhat.che.workspace.services.telemetry.woopra;

import org.eclipse.che.api.core.BadRequestException;
import org.eclipse.che.api.core.ConflictException;
import org.eclipse.che.api.core.ForbiddenException;
import org.eclipse.che.api.core.NotFoundException;
import org.eclipse.che.api.core.ServerException;
import org.eclipse.che.api.core.UnauthorizedException;
import org.eclipse.che.api.core.rest.HttpJsonRequest;
import org.eclipse.che.api.core.rest.HttpJsonResponse;

import javax.validation.constraints.NotNull;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

public class MockHttpRequest implements HttpJsonRequest {

    private String mockResponse;

    public MockHttpRequest() {
        mockResponse = this.getMockResponse();
    }

    @Override
    public HttpJsonRequest setMethod(@NotNull String method) {
        return null;
    }

    private String getMockResponse() {
        String workspaceResponse = "";
        try {
            InputStream is = this.getClass().getClassLoader().getResourceAsStream("mock-response.json");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String l;
            while ((l = reader.readLine()) != null) {
                workspaceResponse = workspaceResponse + l;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return workspaceResponse;
    }


    @Override
    public HttpJsonRequest setBody(@NotNull Object body) {
        return null;
    }

    @Override
    public HttpJsonRequest setBody(@NotNull Map<String, String> map) {
        return null;
    }

    @Override
    public HttpJsonRequest setBody(@NotNull List<?> list) {
        return null;
    }

    @Override
    public HttpJsonRequest addQueryParam(@NotNull String name, @NotNull Object value) {
        return null;
    }

    @Override
    public HttpJsonRequest addHeader(@NotNull String name, @NotNull String value) {
        return null;
    }

    @Override
    public HttpJsonRequest setAuthorizationHeader(@NotNull String value) {
        return null;
    }

    @Override
    public HttpJsonRequest setTimeout(int timeoutMs) {
        return null;
    }

    @Override
    public String getUrl() {
        return null;
    }

    @Override
    public HttpJsonResponse request() throws IOException, ServerException, UnauthorizedException, ForbiddenException, NotFoundException, ConflictException, BadRequestException {
        return new MockHttpJsonResponse(mockResponse, 200);
    }
}
