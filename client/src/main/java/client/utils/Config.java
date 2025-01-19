package client.utils;

public class Config {
    private String language;

    public Config() {
        // Jackson needs a no-arg constructor
    }

    public Config(String language) {
        this.language = language;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}