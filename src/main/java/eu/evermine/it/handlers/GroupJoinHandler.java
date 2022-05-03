package eu.evermine.it.handlers;

import com.pengrad.telegrambot.model.Update;
import eu.evermine.it.configs.yamls.ConfigsYaml;
import eu.evermine.it.helpers.ActionsAPIHelper;
import eu.evermine.it.updatesdispatcher.handlers.GenericUpdateHandler;


public class GroupJoinHandler extends GenericUpdateHandler {

    public GroupJoinHandler() {
    }

    @Override
    public boolean handleUpdate(Update update) {
        if (update.message().chat().id().equals(ConfigsYaml.getAdminGroupID()))
            return true;

        ActionsAPIHelper.leaveChat(update.message().chat().id());
        return true;
    }
}
