package ch.skyfy.enderbackpack;

public enum ConstantsMessage {
    CONFIG_FOLDER_COULD_NOT_BE_CREATED("The folder that should contain the configuration files could not be created !");
    private final String message;

    ConstantsMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
