package eu.evermine.it.updateshandlers.handlers.callbacks;

import com.pengrad.telegrambot.model.Update;
import eu.evermine.it.configs.yamls.LanguageYaml;
import eu.evermine.it.updateshandlers.handlers.CallbacksHandler;
import eu.evermine.it.updateshandlers.handlers.models.AbstractCallback;
import eu.evermine.it.wrappers.LanguageWrapper;

public class ServerIpCallback extends AbstractCallback {
    private final LanguageWrapper language;


    public ServerIpCallback(CallbacksHandler handler, LanguageWrapper language) {
        super(handler);
        this.language = language;
    }

    @Override
    public boolean handleUpdate(Update update) {
        super.answerCallbackQuery(update.callbackQuery().id(), language.getLanguageString(LanguageYaml.LANGUAGE_INDEXES.SERVER_IP_CALLBACK_TEXT));
        return true;
    }

    @Override
    public String getCallback() {
        return "serverip";
    }
}
