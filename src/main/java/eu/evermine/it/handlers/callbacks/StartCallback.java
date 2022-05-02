package eu.evermine.it.handlers.callbacks;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import eu.evermine.it.configs.yamls.LanguageYaml;
import eu.evermine.it.configs.yamls.StaffChatYaml;
import eu.evermine.it.handlers.models.AbstractCallback;
import eu.evermine.it.helpers.ActionsAPIHelper;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.List;

public class StartCallback extends AbstractCallback {

    private final Logger logger;
    private final LanguageYaml language;
    private final StaffChatYaml staffChat;


    public StartCallback(Logger logger, LanguageYaml languageWrapper, StaffChatYaml staffChatWrapper) {
        this.logger = logger;
        this.language = languageWrapper;
        this.staffChat = staffChatWrapper;
    }

    @Override
    public boolean handleUpdate(Update update) {
        try {
            staffChat.removeInChatUser(getCallbackUserID(update));
        } catch (IOException e) {
            logger.error(language.getLanguageString("error-removing-user-missing-config-file"));
        }

        StringBuilder text = new StringBuilder();
        String user = getCallbackUserName(update) == null ? getCallbackFirstName(update) : "@" + getCallbackUserName(update);
        text.append(language.getLanguageString(language.getLanguageString("start-message"), List.of(user)));

        InlineKeyboardMarkup inlineKeyboardMarkup = language.getKeyboard(language.getLanguageString("start-keyboard"));
        if (inlineKeyboardMarkup == null) {
            logger.error(language.getLanguageString(language.getLanguageString("not-matching-buttons")));
        }

        ActionsAPIHelper.editMessage(text.toString(), getCallbackChatID(update), getCallbackMessageID(update), inlineKeyboardMarkup);
        return true;
    }

    @Override
    public String getCallback() {
        return "start";
    }
}
