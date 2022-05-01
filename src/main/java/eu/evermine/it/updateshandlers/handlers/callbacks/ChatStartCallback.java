package eu.evermine.it.updateshandlers.handlers.callbacks;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import eu.evermine.it.configs.yamls.LanguageYaml;
import eu.evermine.it.helpers.ActionsAPIHelper;
import eu.evermine.it.updateshandlers.handlers.models.AbstractCallback;
import eu.evermine.it.wrappers.LanguageWrapper;
import eu.evermine.it.wrappers.StaffChatWrapper;
import org.slf4j.Logger;

import java.io.IOException;

public class ChatStartCallback extends AbstractCallback {

    private final Logger logger;
    private final LanguageWrapper language;
    private final StaffChatWrapper staffChat;


    public ChatStartCallback(Logger logger, LanguageWrapper languageWrapper, StaffChatWrapper staffChatWrapper) {
        this.logger = logger;
        this.language = languageWrapper;
        this.staffChat = staffChatWrapper;
    }

    @Override
    public boolean handleUpdate(Update update) {
        StringBuilder text = new StringBuilder();
        InlineKeyboardMarkup keyboardMarkup = null;
        if(staffChat.isUserInChat(getCallbackUserID(update))) {
            text.append(language.getLanguageString(LanguageYaml.LANGUAGE_INDEXES.ALREADY_IN_CHAT));
        } else {
            keyboardMarkup = language.getKeyboard(LanguageYaml.KEYBOARDS_INDEXES.BACK_KEYBOARD);
            if(staffChat.isBannedUser(getCallbackUserID(update))) {
                text.append(language.getLanguageString(LanguageYaml.LANGUAGE_INDEXES.USER_BANNED_MESSAGE));
            } else {
                text.append(language.getLanguageString(LanguageYaml.LANGUAGE_INDEXES.WELCOME_CHAT_MESSAGE));
            }
        }
        try {
            ActionsAPIHelper.editMessage(text.toString(), getCallbackChatID(update), getCallbackMessageID(update), keyboardMarkup);
            if (staffChat.isBannedUser(getCallbackUserID(update)))
                return true;
            if (!staffChat.isUserInChat(getCallbackUserID(update)))
                staffChat.addInChatUser(getCallbackUserID(update));
        } catch (IOException e) {
            logger.error(language.getLanguageString(LanguageYaml.LANGUAGE_INDEXES.ERROR_ADDING_USER_MISSING_CONFIG_FILE), e);
        }
        return true;
    }

    @Override
    public String getCallback() {
        return "chat-start";
    }
}
