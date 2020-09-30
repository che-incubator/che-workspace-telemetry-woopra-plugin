package com.redhat.che.workspace.services.telemetry.woopra.exception;

public class WoopraCredentialException extends RuntimeException {

    public WoopraCredentialException() {
        super();
    }

    public WoopraCredentialException(String message) {
        super(message);
    }
}
