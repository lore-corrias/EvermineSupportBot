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

public class EndChatCommand extends AbstractCommand {

    private final Logger logger;
    private final LanguageWrapper language;
    private final ConfigsWrapper configs;
    private final StaffChatWrapper staffChat;



    public EndChatCommand(CommandHandler commandHandler, Logger logger, LanguageWrapper language, ConfigsWrapper configs, StaffChatWrapper staffChatWrapper) {
        super(commandHandler);

        this.logger = logger;
        this.language = language;
        this.configs = configs;
        this.staffChat = staffChatWrapper;
    }

    @Override
    public String getCommandName() {
        return "endchat";
    }

    @Override
    public String getCommandDescription() {
        return "Termina la chat di un utente.";
    }

    @Override
    public String getCommandUsage() {
        return "/endchat <USERID>";
    }

    @Override
    public boolean handleUpdate(Update update) {
        if(!configs.isAdmin(getCommandUserId(update)))
            return true;
        if(getCommandArguments(update).length != 1) {
            sendMessage(language.getLanguageString(LanguageYaml.LANGUAGE_INDEXES.END_CHAT_SYNTAX, List.of(getCommandUsage())), getCommandChatId(update), getCommandMessageId(update));
        } else {
            try {
                Long userID = Long.parseLong(getCommandArguments(update)[0]);
                if (!staffChat.isUserInChat(userID)) {
                    sendMessage(language.getLanguageString(LanguageYaml.LANGUAGE_INDEXES.END_CHAT_USER_NOT_IN_CHAT), getCommandChatId(update), getCommandMessageId(update));
                } else {
                    staffChat.removeInChatUser(userID);
                    sendMessage(language.getLanguageString(LanguageYaml.LANGUAGE_INDEXES.END_CHAT_USER_REMOVED), getCommandChatId(update), getCommandMessageId(update));
                }
                sendMessage(language.getLanguageString(LanguageYaml.LANGUAGE_INDEXES.END_CHAT_USER_REMOVED_BY_ADMIN), userID, null);
            } catch (NumberFormatException e) {
                sendMessage(language.getLanguageString(LanguageYaml.LANGUAGE_INDEXES.END_CHAT_USER_NOT_IN_CHAT), getCommandChatId(update), getCommandMessageId(update));
            } catch (IOException e) {
                sendMessage(language.getLanguageString(LanguageYaml.LANGUAGE_INDEXES.ERROR_REMOVING_USER_FROM_STAFF_CHAT_FILE), getCommandChatId(update), getCommandMessageId(update));
                logger.error(language.getLanguageString(LanguageYaml.LANGUAGE_INDEXES.ERROR_REMOVING_USER_FROM_STAFF_CHAT_FILE), e);
            }
        }
        return true;
    }
}
