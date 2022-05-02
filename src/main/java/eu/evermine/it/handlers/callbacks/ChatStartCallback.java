package eu.evermine.it.handlers.callbacks;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import eu.evermine.it.configs.yamls.LanguageYaml;
import eu.evermine.it.configs.yamls.StaffChatYaml;
import eu.evermine.it.handlers.models.AbstractCallback;
import eu.evermine.it.helpers.ActionsAPIHelper;
import org.slf4j.Logger;

import java.io.IOException;


public class ChatStartCallback extends AbstractCallback {

    private final Logger logger;
    private final LanguageYaml language;
    private final StaffChatYaml staffChat;


    public ChatStartCallback(Logger logger, LanguageYaml languageWrapper, StaffChatYaml staffChatWrapper) {
        this.logger = logger;
        this.language = languageWrapper;
        this.staffChat = staffChatWrapper;
    }

    @Override
    public boolean handleUpdate(Update update) {
        StringBuilder text = new StringBuilder();
        InlineKeyboardMarkup keyboardMarkup = null;
        if (staffChat.isInChatUser(getCallbackUserID(update))) {
            text.append(language.getLanguageString("already-in-chat"));
        } else {
            keyboardMarkup = language.getKeyboard("back-keyboard");
            if (staffChat.isBannedUser(getCallbackUserID(update))) {
                text.append(language.getLanguageString("user-banned-message"));
            } else {
                text.append(language.getLanguageString("welcome-chat-message"));
            }
        }
        ActionsAPIHelper.editMessage(text.toString(), getCallbackChatID(update), getCallbackMessageID(update), keyboardMarkup);
        if (staffChat.isBannedUser(getCallbackUserID(update)))
            return true;
        try {
            if (!staffChat.isInChatUser(getCallbackUserID(update)))
                staffChat.addInChatUser(getCallbackUserID(update));
        } catch (IOException e) {
            logger.error(language.getLanguageString("error-adding-user-missing-config-file"));
        }
        return true;
    }

    @Override
    public String getCallback() {
        return "chat-start";
    }
}
