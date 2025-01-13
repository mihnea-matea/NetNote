package client;

import java.util.Locale;
import java.util.ResourceBundle;

public class LanguageChange {
    private static LanguageChange instance;
    private ResourceBundle resourceBundle;
    private String currentLanguage;

    private LanguageChange(String languageCode) {
        changeLanguage(languageCode);
    }

    public String getCurrentLanguage() {
        return currentLanguage;
    }

    public void changeLanguage(String languageCode) {
        if (languageCode.equals(currentLanguage)) {
            return;
        }

        currentLanguage = languageCode;
        Locale locale = Locale.of(languageCode);
        resourceBundle = ResourceBundle.getBundle("client.Messages", locale);
    }

    public String getText(String words) {
        return resourceBundle.getString(words);
    }

    public static LanguageChange getInstance() {
        if (instance == null) {
            instance = new LanguageChange("en"); // Default language
        }
        return instance;
    }
}