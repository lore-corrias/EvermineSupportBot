package eu.evermine.it.handlers.commands;

import com.pengrad.telegrambot.model.Update;
import eu.evermine.it.EvermineSupportBot;
import eu.evermine.it.configs.yamls.ConfigsYaml;
import eu.evermine.it.configs.yamls.LanguageYaml;
import eu.evermine.it.handlers.models.AbstractCommand;
import eu.evermine.it.helpers.ActionsAPIHelper;
import org.slf4j.Logger;

import java.io.IOException;

public class ReloadCommand extends AbstractCommand {

    private final Logger logger;
    private final LanguageYaml language;
    private final ConfigsYaml configs;
    private final EvermineSupportBot bot;


    public ReloadCommand(Logger logger, LanguageYaml language, ConfigsYaml configs, EvermineSupportBot evermineSupportBot) {
        this.logger = logger;
        this.language = language;
        this.configs = configs;
        this.bot = evermineSupportBot;
    }

    @Override
    public String getCommandUsage() {
        return "/relaod";
    }

    @Override
    public boolean handleUpdate(Update update) {
        if (!configs.isAdmin(getCommandUserId(update)))
            return true;

        try {
            bot.reloadLanguage();
            bot.reloadConfigs();
            ActionsAPIHelper.sendMessage(language.getLanguageString("reloaded-configs"), getCommandChatId(update), getCommandMessageId(update));
        } catch (IOException e) {
            logger.error(language.getLanguageString("error-reload-configs"), e);
        }
        return true;
    }
}
