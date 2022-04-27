package eu.evermine.it.updateshandlers.handlers.callbacks;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import eu.evermine.it.configs.yamls.LanguageYaml;
import eu.evermine.it.updateshandlers.handlers.CallbacksHandler;
import eu.evermine.it.wrappers.LanguageWrapper;
import eu.evermine.it.wrappers.StaffChatWrapper;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.List;

public class StartCallback extends AbstractCallback{

    private final Logger logger;
    private final LanguageWrapper language;
    private final StaffChatWrapper staffChat;


    public StartCallback(CallbacksHandler callbacksHandler, Logger logger, LanguageWrapper languageWrapper, StaffChatWrapper staffChatWrapper) {
        super(callbacksHandler);
        this.logger = logger;
        this.language = languageWrapper;
        this.staffChat = staffChatWrapper;
    }

    @Override
    public boolean handleCallback(Update update) {
        try {
            staffChat.removeInChatUser(getCallbackUserID(update));
        } catch (IOException e) {
            logger.error(language.getLanguageString(LanguageYaml.LANGUAGE_INDEXES.ERROR_REMOVING_USER_MISSING_CONFIG_FILE), e);
        }

        StringBuilder text = new StringBuilder();
        String user = getCallbackUserName(update) == null ? getCallbackFirstName(update) : "@" + getCallbackUserName(update);
        text.append(language.getLanguageString(LanguageYaml.LANGUAGE_INDEXES.START_MESSAGE, List.of(user)));

        InlineKeyboardMarkup inlineKeyboardMarkup = language.getKeyboard(LanguageYaml.KEYBOARDS_INDEXES.START_KEYBOARD);
        if(inlineKeyboardMarkup == null) {
            logger.error(language.getLanguageString(LanguageYaml.LANGUAGE_INDEXES.NOT_MATCHING_BUTTONS));
        }

        editMessage(text.toString(), getCallbackChatID(update), getCallbackMessageID(update), inlineKeyboardMarkup);
        return true;
    }

    @Override
    public String getCallback() {
        return "start";
    }
}
