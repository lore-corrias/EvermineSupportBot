package eu.evermine.it.updateshandlers.handlers.commands;

import com.pengrad.telegrambot.model.Update;
import eu.evermine.it.configs.yamls.LanguageYaml;
import eu.evermine.it.helpers.ActionsAPIHelper;
import eu.evermine.it.updateshandlers.handlers.CommandDispatcher;
import eu.evermine.it.updateshandlers.handlers.models.AbstractCommand;
import eu.evermine.it.wrappers.ConfigsWrapper;
import eu.evermine.it.wrappers.LanguageWrapper;
import org.slf4j.Logger;

import java.io.IOException;

public class ReloadCommand extends AbstractCommand {

    private final Logger logger;
    private final LanguageWrapper language;
    private final ConfigsWrapper configs;


    public ReloadCommand(Logger logger, LanguageWrapper language, ConfigsWrapper configs) {
        this.logger = logger;
        this.language = language;
        this.configs = configs;
    }

    @Override
    public String getCommandName() {
        return "reload";
    }

    @Override
    public String getCommandDescription() {
        return "Reload i file di configurazione.";
    }

    @Override
    public String getCommandUsage() {
        return "/relaod";
    }

    @Override
    public boolean handleUpdate(Update update) {
        if(!configs.isAdmin(getCommandUserId(update)))
            return true;

        try {
            language.reloadLanguage();
            configs.reloadConfigs();
            ActionsAPIHelper.sendMessage(language.getLanguageString(LanguageYaml.LANGUAGE_INDEXES.RELOADED_CONFIGS), getCommandChatId(update), getCommandMessageId(update));
        } catch (IOException e) {
            logger.error(language.getLanguageString(LanguageYaml.LANGUAGE_INDEXES.ERROR_RELOAD_CONFIGS), e);
        }
        return true;
    }
}
