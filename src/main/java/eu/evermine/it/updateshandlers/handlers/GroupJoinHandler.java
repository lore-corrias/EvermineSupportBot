package eu.evermine.it.updateshandlers.handlers;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import eu.evermine.it.updateshandlers.AbstractUpdateHandler;
import eu.evermine.it.wrappers.ConfigsWrapper;
import eu.evermine.it.wrappers.LanguageWrapper;
import org.slf4j.Logger;


public class GroupJoinHandler extends AbstractUpdateHandler {

    private final ConfigsWrapper configs;
    private TelegramBot bot;

    public GroupJoinHandler(Logger logger, LanguageWrapper language, ConfigsWrapper configs) {
        super(logger, language);

        this.configs = configs;
    }

    @Override
    public boolean handleUpdate(Update update) {
        if(update.message().chat().id().equals(configs.getAdminGroupID()))
            return true;

       leaveChat(update.message().chat().id());
       return true;
    }

    @Override
    public TelegramBot getTelegramBotInstance() {
        return bot;
    }

    @Override
    public void setTelegramBotInstance(TelegramBot bot) {
        this.bot = bot;
    }
}
