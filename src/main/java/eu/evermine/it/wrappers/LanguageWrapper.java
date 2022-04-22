package eu.evermine.it.wrappers;

import eu.evermine.it.configs.YamlManager;
import eu.evermine.it.configs.yamls.LanguageYaml;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.List;

final public class LanguageWrapper {

    private static LanguageWrapper languageWrapper;
    private static LanguageYaml languageYaml;


    private LanguageWrapper() {
        if(languageWrapper != null)
            throw new IllegalStateException("LanguageWrapper è già stato inizializzato.");
        languageWrapper = this;
    }

    public static LanguageWrapper getInstance() throws IOException {
        if(languageWrapper == null)
            languageWrapper = new LanguageWrapper();
        languageYaml = (LanguageYaml) YamlManager.getInstance().loadYaml(new LanguageYaml());
        return languageWrapper;
    }

    public String getLanguageString(LanguageYaml.LANGUAGE_INDEXES languageIndex, List<String> args) {
        return languageYaml.getLanguageFromIndex(languageIndex, args);
    }

    public String getLanguageString(LanguageYaml.LANGUAGE_INDEXES languageIndex) {
        return languageYaml.getLanguageFromIndex(languageIndex);
    }

    public InlineKeyboardMarkup getKeyboard(LanguageYaml.KEYBOARDS_INDEXES keyboardIndex) {
        return languageYaml.getKeyboardFromIndex(keyboardIndex);
    }

    public InlineKeyboardMarkup getKeyboard(LanguageYaml.KEYBOARDS_INDEXES keyboardIndex, List<String> textArguments) {
        return languageYaml.getKeyboardFromIndex(keyboardIndex, textArguments);
    }

    public InlineKeyboardMarkup getKeyboard(LanguageYaml.KEYBOARDS_INDEXES keyboardIndex, @Nullable List<String> textArguments, @Nullable List<String> callbackArguments) {
        return languageYaml.getKeyboardFromIndex(keyboardIndex, textArguments, callbackArguments);
    }

    public void reloadLanguage() throws IOException {
        languageYaml = (LanguageYaml) YamlManager.getInstance().loadYaml(new LanguageYaml());
    }
}
