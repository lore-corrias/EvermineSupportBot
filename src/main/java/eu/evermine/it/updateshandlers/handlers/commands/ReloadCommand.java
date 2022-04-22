package eu.evermine.it.updateshandlers.handlers.commands;

import eu.evermine.it.configs.yamls.LanguageYaml;
import eu.evermine.it.wrappers.ConfigsWrapper;
import eu.evermine.it.wrappers.LanguageWrapper;
import org.slf4j.Logger;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;

public class ReloadCommand implements IBotCommand {

    private final Logger logger;
    private final LanguageWrapper language;
    private final ConfigsWrapper configs;


    public ReloadCommand(Logger logger, LanguageWrapper language, ConfigsWrapper configs) {
        this.logger = logger;
        this.language = language;
        this.configs = configs;
    }

    @Override
    public String getCommandIdentifier() {
        return "reload";
    }

    @Override
    public String getDescription() {
        return "Reload i file di configurazione.";
    }

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] arguments) {
        if(!configs.isAdmin(message.getFrom().getId()))
            return;
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(message.getChatId()));
        sendMessage.setText(language.getLanguageString(LanguageYaml.LANGUAGE_INDEXES.RELOADED_CONFIGS));
        sendMessage.setReplyToMessageId(message.getMessageId());
        sendMessage.setParseMode("HTML");
        try {
            language.reloadLanguage();
            configs.reloadConfigs();
            absSender.execute(sendMessage);
        } catch (TelegramApiException | IOException e) {
            logger.error(language.getLanguageString(LanguageYaml.LANGUAGE_INDEXES.ERROR_RELOAD_CONFIGS), e);
        }
    }
}
