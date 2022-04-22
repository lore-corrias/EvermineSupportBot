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
    public void handleCallback(Update update) {
        try {
            staffChat.removeInChatUser(update.getCallbackQuery().getMessage().getChatId());
        } catch (IOException e) {
            logger.error(language.getLanguageString(LanguageYaml.LANGUAGE_INDEXES.ERROR_REMOVING_USER_MISSING_CONFIG_FILE), e);
        }

        StringBuilder sb = new StringBuilder();
        String user = getCallbackUserName(update).isEmpty() ? getCallbackFirstName(update) : "@" + getCallbackUserName(update);
        sb.append(language.getLanguageString(LanguageYaml.LANGUAGE_INDEXES.START_MESSAGE, List.of(user)));

        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(getCallbackChatID(update));
        editMessageText.setMessageId(getCallbackMessageID(update));
        editMessageText.setText(sb.toString());
        editMessageText.setParseMode("HTML");

        InlineKeyboardMarkup inlineKeyboardMarkup = language.getKeyboard(LanguageYaml.KEYBOARDS_INDEXES.START_KEYBOARD);
        if(inlineKeyboardMarkup == null) {
            logger.error(language.getLanguageString(LanguageYaml.LANGUAGE_INDEXES.NOT_MATCHING_BUTTONS));
        } else {
            editMessageText.setReplyMarkup(inlineKeyboardMarkup);
        }

        try {
            super.getCallbacksHandler().execute(editMessageText);
        } catch (TelegramApiException e) {
            logger.error(language.getLanguageString(LanguageYaml.LANGUAGE_INDEXES.ERROR_EDITING_START_MESSAGE), e);
        }
    }

    @Override
    public String getCallback() {
        return "start";
    }

    @Override
    public String getCallbackDescription() {
        return "Callback equivalente al comando /start.";
    }
}
