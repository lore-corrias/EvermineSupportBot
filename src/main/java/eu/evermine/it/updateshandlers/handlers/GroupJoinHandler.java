package eu.evermine.it.updateshandlers.handlers;

import eu.evermine.it.configs.yamls.LanguageYaml;
import eu.evermine.it.updateshandlers.AbstractUpdateHandler;
import eu.evermine.it.wrappers.ConfigsWrapper;
import eu.evermine.it.wrappers.LanguageWrapper;
import org.slf4j.Logger;
import org.telegram.telegrambots.meta.api.methods.groupadministration.LeaveChat;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


public class GroupJoinHandler extends AbstractUpdateHandler {

    private final Logger logger;
    private final ConfigsWrapper configs;
    private final LanguageWrapper language;


    public GroupJoinHandler(Logger logger, LanguageWrapper languageWrapper, ConfigsWrapper configs) {
        this.logger = logger;
        this.configs = configs;
        this.language = languageWrapper;
    }

    @Override
    public void handleUpdate(Update update) {
        if(update.getMessage().getChat().getId().equals(configs.getAdminGroupID()))
            return;

        LeaveChat leaveChat = new LeaveChat();
        leaveChat.setChatId(String.valueOf(update.getMessage().getChat().getId()));
        try {
            super.execute(leaveChat);
        } catch (TelegramApiException e) {
            logger.error(language.getLanguageString(LanguageYaml.LANGUAGE_INDEXES.ERROR_LEAVING_CHAT_MESSAGE), e);
        }
    }
}
