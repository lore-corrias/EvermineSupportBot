package eu.evermine.it.handlers.callbacks;

import com.pengrad.telegrambot.model.Update;
import eu.evermine.it.configs.yamls.LanguageYaml;
import eu.evermine.it.handlers.models.AbstractCallback;
import io.github.justlel.api.ActionsAPIHelper;

public class ServerIpCallback extends AbstractCallback {

    public ServerIpCallback() {
    }

    @Override
    public void handleUpdate(Update update) {
        ActionsAPIHelper.answerCallbackQuery(update.callbackQuery().id(), LanguageYaml.getLanguageString("server-ip-callback-text"));
    }

    @Override
    public String getCallback() {
        return "serverip";
    }
}
