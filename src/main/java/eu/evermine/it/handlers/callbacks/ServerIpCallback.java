package eu.evermine.it.handlers.callbacks;

import com.pengrad.telegrambot.model.Update;
import eu.evermine.it.configs.yamls.LanguageYaml;
import eu.evermine.it.handlers.models.AbstractCallback;
import eu.evermine.it.helpers.ActionsAPIHelper;

public class ServerIpCallback extends AbstractCallback {
    private final LanguageYaml language;


    public ServerIpCallback(LanguageYaml language) {
        this.language = language;
    }

    @Override
    public boolean handleUpdate(Update update) {
        ActionsAPIHelper.answerCallbackQuery(update.callbackQuery().id(), language.getLanguageString("server-ip-callback-text"));
        return true;
    }

    @Override
    public String getCallback() {
        return "serverip";
    }
}
