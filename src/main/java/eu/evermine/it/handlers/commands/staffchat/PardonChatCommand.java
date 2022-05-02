package eu.evermine.it.handlers.commands.staffchat;

import com.pengrad.telegrambot.model.Update;
import eu.evermine.it.configs.yamls.ConfigsYaml;
import eu.evermine.it.configs.yamls.LanguageYaml;
import eu.evermine.it.configs.yamls.StaffChatYaml;
import eu.evermine.it.handlers.models.AbstractCommand;
import eu.evermine.it.helpers.ActionsAPIHelper;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.List;

public class PardonChatCommand extends AbstractCommand {

    private final Logger logger;
    private final LanguageYaml language;
    private final ConfigsYaml configs;
    private final StaffChatYaml staffChat;


    public PardonChatCommand(Logger logger, LanguageYaml language, ConfigsYaml configs, StaffChatYaml staffChatWrapper) {
        this.logger = logger;
        this.language = language;
        this.configs = configs;
        this.staffChat = staffChatWrapper;
    }

    @Override
    public String getCommandUsage() {
        return "/pardonchat <USERID>";
    }

    @Override
    public boolean handleUpdate(Update update) {
        if (!configs.isAdmin(getCommandUserId(update)))
            return true;

        if (getCommandArguments(update).length != 1) {
            ActionsAPIHelper.sendMessage(language.getLanguageString("pardon-chat-syntax", List.of()), getCommandChatId(update), getCommandMessageId(update));
        } else {
            try {
                Long userId = Long.parseLong(getCommandArguments(update)[0]);
                try {
                    if (staffChat.isBannedUser(userId)) {
                        ActionsAPIHelper.sendMessage(language.getLanguageString("pardon-chat-message"), userId);

                        if (staffChat.isInChatUser(userId))
                            staffChat.removeInChatUser(userId);
                        staffChat.removeBannedUser(userId);
                        ActionsAPIHelper.sendMessage(language.getLanguageString("pardon-chat-success"), getCommandChatId(update), getCommandMessageId(update));
                    } else {
                        ActionsAPIHelper.sendMessage(language.getLanguageString("pardon-chat-not-banned"), getCommandChatId(update), getCommandMessageId(update));
                    }
                } catch (IOException e) {
                    logger.error(language.getLanguageString("error-remove-banned-user"), e);
                }
            } catch (NumberFormatException e) {
                ActionsAPIHelper.sendMessage(language.getLanguageString("pardon-chat-syntax", List.of(getCommandUsage())), getCommandChatId(update), getCommandMessageId(update));
            }
        }
        return true;
    }
}
