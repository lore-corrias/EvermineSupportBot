package eu.evermine.it.handlers.commands;

import com.pengrad.telegrambot.model.Update;
import eu.evermine.it.EvermineSupportBot;
import eu.evermine.it.configs.yamls.ConfigsYaml;
import eu.evermine.it.configs.yamls.LanguageYaml;
import eu.evermine.it.handlers.models.AbstractCommand;
import io.github.justlel.api.ActionsAPIHelper;

import java.io.IOException;

public class ReloadCommand extends AbstractCommand {


    public ReloadCommand() {
    }

    @Override
    public String getCommandUsage() {
        return "/relaod";
    }

    @Override
    public void handleUpdate(Update update) {
        if (!ConfigsYaml.isAdmin(getCommandUserId(update)))
            return;

        try {
            EvermineSupportBot.loadConfigurations();
            ActionsAPIHelper.sendMessage(LanguageYaml.getLanguageString("reloaded-configs"), getCommandChatId(update), getCommandMessageId(update));
        } catch (IOException e) {
            EvermineSupportBot.logger.error(LanguageYaml.getLanguageString("error-reload-configs"), e);
        }
    }
}
