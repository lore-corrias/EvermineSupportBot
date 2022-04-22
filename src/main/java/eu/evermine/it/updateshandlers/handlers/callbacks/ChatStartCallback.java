package eu.evermine.it.updateshandlers.handlers.callbacks;

import eu.evermine.it.configs.yamls.LanguageYaml;
import eu.evermine.it.updateshandlers.handlers.CallbacksHandler;
import eu.evermine.it.wrappers.LanguageWrapper;
import eu.evermine.it.wrappers.StaffChatWrapper;
import org.slf4j.Logger;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;

public class ChatStartCallback extends AbstractCallback {

    private final Logger logger;
    private final LanguageWrapper language;
    private final StaffChatWrapper staffChat;


    public ChatStartCallback(CallbacksHandler callbacksHandler, Logger logger, LanguageWrapper languageWrapper, StaffChatWrapper staffChatWrapper) {
        super(callbacksHandler);
        this.logger = logger;
        this.language = languageWrapper;
        this.staffChat = staffChatWrapper;
    }

    @Override
    public void handleCallback(Update update) {
        EditMessageText editMessage = new EditMessageText();
        editMessage.setChatId(getCallbackChatID(update));
        editMessage.setMessageId(getCallbackMessageID(update));

        StringBuilder sb = new StringBuilder();
        if(staffChat.isUserInChat(getCallbackUserID(update))) {
            sb.append(language.getLanguageString(LanguageYaml.LANGUAGE_INDEXES.ALREADY_IN_CHAT));
        } else {
            InlineKeyboardMarkup keyboardMarkup = language.getKeyboard(LanguageYaml.KEYBOARDS_INDEXES.BACK_KEYBOARD);
            if(staffChat.isBannedUser(getCallbackUserID(update))) {
                sb.append(language.getLanguageString(LanguageYaml.LANGUAGE_INDEXES.USER_BANNED_MESSAGE));
            } else {
                sb.append(language.getLanguageString(LanguageYaml.LANGUAGE_INDEXES.WELCOME_CHAT_MESSAGE));
            }
            if(keyboardMarkup == null) {
                logger.error(language.getLanguageString(LanguageYaml.LANGUAGE_INDEXES.NOT_MATCHING_BUTTONS));
            } else {
                editMessage.setReplyMarkup(keyboardMarkup);
            }
        }
        editMessage.setText(sb.toString());

        try {
            getCallbacksHandler().execute(editMessage);
            if(staffChat.isBannedUser(getCallbackUserID(update)))
                return;
            if(!staffChat.isUserInChat(getCallbackUserID(update)))
                staffChat.addInChatUser(getCallbackUserID(update));
        } catch (TelegramApiException e) {
            logger.error(language.getLanguageString(LanguageYaml.LANGUAGE_INDEXES.ERROR_STARTING_CHAT), e);
        } catch (IOException e) {
            logger.error(language.getLanguageString(LanguageYaml.LANGUAGE_INDEXES.ERROR_ADDING_USER_MISSING_CONFIG_FILE), e);
        }
    }

    @Override
    public String getCallback() {
        return "chat-start";
    }

    @Override
    public String getCallbackDescription() {
        return "Inizia una chat con lo staff.";
    }
}
