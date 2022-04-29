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

public class BanChatCommand extends AbstractCommand {

    private final Logger logger;
    private final LanguageWrapper language;
    private final ConfigsWrapper configs;
    private final StaffChatWrapper staffChat;


    public BanChatCommand(CommandHandler commandHandler, Logger logger, LanguageWrapper language, ConfigsWrapper configs, StaffChatWrapper staffChatWrapper) {
        super(commandHandler);

        this.logger = logger;
        this.language = language;
        this.configs = configs;
        this.staffChat = staffChatWrapper;
    }

    @Override
    public String getCommandName() {
        return "banchat";
    }

    @Override
    public String getCommandDescription() {
        return "Banna un utente dalla chat.";
    }

    @Override
    public String getCommandUsage() {
        return "/banchat <USERID>";
    }

    @Override
    public boolean handleUpdate(Update update) {
        if (!configs.isAdmin(getCommandUserId(update)))
            return true;
        if (getCommandArguments(update).length != 1) {
            sendMessage(language.getLanguageString(LanguageYaml.LANGUAGE_INDEXES.BAN_CHAT_SYNTAX, List.of(getCommandUsage())), getCommandChatId(update), getCommandMessageId(update));
            return true;
        }
        try {
            Long userId = Long.parseLong(getCommandArguments(update)[0]);
            if (staffChat.isBannedUser(userId)) {
                sendMessage(language.getLanguageString(LanguageYaml.LANGUAGE_INDEXES.BAN_CHAT_ALREADY_BANNED), getCommandChatId(update), getCommandMessageId(update));
            } else {
                if (staffChat.isUserInChat(userId))
                    staffChat.removeInChatUser(userId);
                staffChat.addBannedUser(userId);
                sendMessage(language.getLanguageString(LanguageYaml.LANGUAGE_INDEXES.USER_BANNED_MESSAGE), userId);
                sendMessage(language.getLanguageString(LanguageYaml.LANGUAGE_INDEXES.BAN_CHAT_SUCCESS), getCommandChatId(update), getCommandMessageId(update));
            }
        } catch (NumberFormatException e) {
            sendMessage(language.getLanguageString(LanguageYaml.LANGUAGE_INDEXES.BAN_CHAT_SYNTAX), getCommandChatId(update), getCommandMessageId(update));
        } catch (IOException e) {
            sendMessage(language.getLanguageString(LanguageYaml.LANGUAGE_INDEXES.ERROR_ADD_BANNED_USERS), getCommandChatId(update), getCommandMessageId(update));
            logger.error(language.getLanguageString(LanguageYaml.LANGUAGE_INDEXES.ERROR_ADD_BANNED_USERS), e);
        }
        // TODO: Inserire un catch per capire il caso in cui l'ID fornito non sia valido
        return true;
    }
}
