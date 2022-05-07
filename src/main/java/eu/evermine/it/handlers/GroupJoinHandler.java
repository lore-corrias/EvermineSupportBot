package eu.evermine.it.handlers;

import com.pengrad.telegrambot.model.Update;
import eu.evermine.it.configs.yamls.ConfigsYaml;
import io.github.justlel.api.ActionsAPIHelper;
import io.github.justlel.models.GenericUpdateHandler;


public class GroupJoinHandler extends GenericUpdateHandler {

    public GroupJoinHandler() {
    }

    @Override
    public void handleUpdate(Update update) {
        if (update.message().chat().id().equals(ConfigsYaml.getAdminGroupID()))
            return;

        ActionsAPIHelper.leaveChat(update.message().chat().id());
    }
}
