package eu.evermine.it.handlers.callbacks;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import eu.evermine.it.EvermineSupportBot;
import eu.evermine.it.configs.yamls.LanguageYaml;
import eu.evermine.it.configs.yamls.StaffChatYaml;
import eu.evermine.it.handlers.models.AbstractCallback;
import eu.evermine.it.helpers.ActionsAPIHelper;

import java.io.IOException;


public class ChatStartCallback extends AbstractCallback {

    public ChatStartCallback() {
    }

    @Override
    public boolean handleUpdate(Update update) {
        StringBuilder text = new StringBuilder();
        InlineKeyboardMarkup keyboardMarkup = null;
        if (StaffChatYaml.isInChatUser(getCallbackUserID(update))) {
            text.append(LanguageYaml.getLanguageString("already-in-chat"));
        } else {
            keyboardMarkup = LanguageYaml.getKeyboard("back-keyboard");
            if (StaffChatYaml.isBannedUser(getCallbackUserID(update))) {
                text.append(LanguageYaml.getLanguageString("user-banned-message"));
            } else {
                text.append(LanguageYaml.getLanguageString("welcome-chat-message"));
            }
        }
        ActionsAPIHelper.editMessage(text.toString(), getCallbackChatID(update), getCallbackMessageID(update), keyboardMarkup);
        if (StaffChatYaml.isBannedUser(getCallbackUserID(update)))
            return true;
        try {
            if (!StaffChatYaml.isInChatUser(getCallbackUserID(update)))
                StaffChatYaml.addInChatUser(getCallbackUserID(update));
        } catch (IOException e) {
            EvermineSupportBot.logger.error(LanguageYaml.getLanguageString("error-adding-user-missing-config-file"));
        }
        return true;
    }

    @Override
    public String getCallback() {
        return "chat-start";
    }
}
