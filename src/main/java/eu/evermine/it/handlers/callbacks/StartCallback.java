package eu.evermine.it.handlers.callbacks;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import eu.evermine.it.EvermineSupportBot;
import eu.evermine.it.configs.yamls.LanguageYaml;
import eu.evermine.it.configs.yamls.StaffChatYaml;
import eu.evermine.it.handlers.models.AbstractCallback;
import io.github.justlel.api.ActionsAPIHelper;

import java.io.IOException;
import java.util.List;

public class StartCallback extends AbstractCallback {


    public StartCallback() {
    }

    @Override
    public void handleUpdate(Update update) {
        try {
            StaffChatYaml.removeInChatUser(getCallbackUserID(update));
        } catch (IOException e) {
            EvermineSupportBot.logger.error(LanguageYaml.getLanguageString("error-removing-user-missing-config-file"));
        }

        StringBuilder text = new StringBuilder();
        String user = getCallbackUserName(update) == null ? getCallbackFirstName(update) : "@" + getCallbackUserName(update);
        text.append(LanguageYaml.getLanguageString("start-message", List.of(user)));

        InlineKeyboardMarkup inlineKeyboardMarkup = LanguageYaml.getKeyboard("start-keyboard");
        if (inlineKeyboardMarkup == null) {
            EvermineSupportBot.logger.error(LanguageYaml.getLanguageString("not-matching-buttons"));
        }

        ActionsAPIHelper.editMessage(text.toString(), getCallbackChatID(update), getCallbackMessageID(update), inlineKeyboardMarkup);
    }

    @Override
    public String getCallback() {
        return "start";
    }
}
