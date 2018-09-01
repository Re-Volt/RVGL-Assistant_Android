package io.github.tavisco.rvglassistant.objects.enums;

public enum UpdateStatus {
    UPDATE_AVAIABLE("Update avaible!"),
    UPDATED("Running the last version"),
    NOT_INSTALLED("Package not installed"),
    ERROR("Error"),
    UNKNOWN("Unknown");

    private final String message;


    UpdateStatus(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
