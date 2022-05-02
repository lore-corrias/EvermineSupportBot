package eu.evermine.it.handlers.commands.staffchat;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.response.BaseResponse;
import eu.evermine.it.configs.yamls.ConfigsYaml;
import eu.evermine.it.configs.yamls.LanguageYaml;
import eu.evermine.it.configs.yamls.StaffChatYaml;
import eu.evermine.it.handlers.models.AbstractCommand;
import eu.evermine.it.helpers.ActionsAPIHelper;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.List;

public class BanChatCommand extends AbstractCommand {

    private final Logger logger;
    private final LanguageYaml language;
    private final ConfigsYaml configs;
    private final StaffChatYaml staffChat;


    public BanChatCommand(Logger logger, LanguageYaml language, ConfigsYaml configs, StaffChatYaml staffChatWrapper) {
        this.logger = logger;
        this.language = language;
        this.configs = configs;
        this.staffChat = staffChatWrapper;
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
            ActionsAPIHelper.sendMessage(language.getLanguageString("ban-chat-syntax", List.of(getCommandUsage())), getCommandChatId(update), getCommandMessageId(update));
            return true;
        }
        try {
            Long userId = Long.parseLong(getCommandArguments(update)[0]);
            if (staffChat.isBannedUser(userId)) {
                ActionsAPIHelper.sendMessage(language.getLanguageString("ban-chat-already-banned"), getCommandChatId(update), getCommandMessageId(update));
            } else {
                if (staffChat.isInChatUser(userId))
                    staffChat.removeInChatUser(userId);
                staffChat.addBannedUser(userId);
                BaseResponse response = ActionsAPIHelper.sendMessage(language.getLanguageString("user-banned-message"), userId);
                if (!response.isOk())
                    throw new NumberFormatException();
                ActionsAPIHelper.sendMessage(language.getLanguageString("banned-chat-success"), getCommandChatId(update), getCommandMessageId(update));
            }
        } catch (NumberFormatException e) {
            ActionsAPIHelper.sendMessage(language.getLanguageString("ban-chat-syntax"), getCommandChatId(update), getCommandMessageId(update));
        } catch (IOException e) {
            ActionsAPIHelper.sendMessage(language.getLanguageString("error-add-banned-users"), getCommandChatId(update), getCommandMessageId(update));
            logger.error(language.getLanguageString("error-add-banned-users"), e);
        }
        return true;
    }
}
