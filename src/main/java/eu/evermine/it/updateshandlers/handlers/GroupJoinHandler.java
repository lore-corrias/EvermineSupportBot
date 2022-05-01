package eu.evermine.it.updateshandlers.handlers;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import eu.evermine.it.helpers.ActionsAPIHelper;
import eu.evermine.it.updateshandlers.handlers.models.handlers.GenericUpdateHandler;
import eu.evermine.it.wrappers.ConfigsWrapper;


public class GroupJoinHandler extends GenericUpdateHandler {

    private final ConfigsWrapper configs;

    public GroupJoinHandler(ConfigsWrapper configs) {
        this.configs = configs;
    }

    @Override
    public boolean handleUpdate(Update update) {
        if(update.message().chat().id().equals(configs.getAdminGroupID()))
            return true;

        ActionsAPIHelper.leaveChat(update.message().chat().id());
       return true;
    }
}
