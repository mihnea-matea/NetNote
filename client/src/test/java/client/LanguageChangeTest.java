package client;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class LanguageChangeTest {

    private LanguageChange languageChange;

    @BeforeEach
    public void setUp() {
        languageChange = LanguageChange.getInstance();
        languageChange.changeLanguage("en");
    }

    @Test
    public void testDefaultLanguage() {
        assertEquals("en", languageChange.getCurrentLanguage());
    }

    @Test
    public void testChangeLanguageToDutch() {
        languageChange.changeLanguage("nl");

        assertEquals("nl", languageChange.getCurrentLanguage());
    }

    @Test
    public void testChangeLanguageToRomanian() {
        languageChange.changeLanguage("ro");

        assertEquals("ro", languageChange.getCurrentLanguage());
    }

    @Test
    public void testGetTextEnglish() {
        languageChange.changeLanguage("en");
        String result = languageChange.getText("searchBar");
        assertNotNull(result);
        assertEquals("Enter the note you are looking for:", result);
    }

    @Test
    public void testGetTextDutch() {
        languageChange.changeLanguage("nl");
        String result = languageChange.getText("searchBar");
        assertNotNull(result);
        assertEquals("Voer de notitie in die u wilt zoeken", result);
    }

    @Test
    public void testGetTextRomanian() {
        languageChange.changeLanguage("ro");
        String result = languageChange.getText("searchBar");
        assertNotNull(result);
        assertEquals("Scrieti titlul notitei pe care o cautati:", result);
    }

    @Test
    public void testNoChangeIfSameLanguage() {
        languageChange.changeLanguage("en");
        languageChange.changeLanguage("en");

        assertEquals("en", languageChange.getCurrentLanguage());
    }
}