package eu.evermine.it.updateshandlers.handlers.commands.staffchat;

import com.pengrad.telegrambot.model.Update;
import eu.evermine.it.configs.yamls.LanguageYaml;
import eu.evermine.it.updateshandlers.handlers.CommandHandler;
import eu.evermine.it.updateshandlers.handlers.models.AbstractCommand;
import eu.evermine.it.wrappers.ConfigsWrapper;
import eu.evermine.it.wrappers.LanguageWrapper;
import eu.evermine.it.wrappers.StaffChatWrapper;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.List;

public class PardonChatCommand extends AbstractCommand {

    private final Logger logger;
    private final LanguageWrapper language;
    private final ConfigsWrapper configs;
    private final StaffChatWrapper staffChat;


    public PardonChatCommand(CommandHandler commandHandler, Logger logger, LanguageWrapper language, ConfigsWrapper configs, StaffChatWrapper staffChatWrapper) {
        super(commandHandler);

        this.logger = logger;
        this.language = language;
        this.configs = configs;
        this.staffChat = staffChatWrapper;
    }

    @Override
    public String getCommandName() {
        return "pardonchat";
    }

    @Override
    public String getCommandDescription() {
        return "Unbanna un utente dalla chat.";
    }

    @Override
    public String getCommandUsage() {
        return "/pardonchat <USERID>";
    }

    @Override
    public boolean handleUpdate(Update update) {
        if(!configs.isAdmin(getCommandUserId(update)))
            return true;

        if(getCommandArguments(update).length != 1) {
            sendMessage(language.getLanguageString(LanguageYaml.LANGUAGE_INDEXES.PARDON_CHAT_SYNTAX, List.of(getCommandUsage())), getCommandChatId(update), getCommandMessageId(update));
        } else {
            try {
                Long userId = Long.parseLong(getCommandArguments(update)[0]);
                try {
                    if(staffChat.isBannedUser(userId)) {
                        sendMessage(language.getLanguageString(LanguageYaml.LANGUAGE_INDEXES.PARDON_CHAT_MESSAGE), userId);

                        if(staffChat.isUserInChat(userId))
                            staffChat.removeInChatUser(userId);
                        staffChat.removeBannedUser(userId);
                        sendMessage(language.getLanguageString(LanguageYaml.LANGUAGE_INDEXES.PARDON_CHAT_SUCCESS), getCommandChatId(update), getCommandMessageId(update));
                    } else {
                        sendMessage(language.getLanguageString(LanguageYaml.LANGUAGE_INDEXES.PARDON_CHAT_NOT_BANNED), getCommandChatId(update), getCommandMessageId(update));
                    }
                } catch (IOException e) {
                    logger.error(language.getLanguageString(LanguageYaml.LANGUAGE_INDEXES.ERROR_REMOVE_BANNED_USER), e);
                }
            } catch (NumberFormatException e) {
                sendMessage(language.getLanguageString(LanguageYaml.LANGUAGE_INDEXES.PARDON_CHAT_SYNTAX, List.of(getCommandUsage())), getCommandChatId(update), getCommandMessageId(update));
            }
        }
        return true;
    }
}
