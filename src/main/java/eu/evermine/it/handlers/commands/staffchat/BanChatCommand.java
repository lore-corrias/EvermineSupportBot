package eu.evermine.it.handlers.commands.staffchat;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.response.BaseResponse;
import eu.evermine.it.EvermineSupportBot;
import eu.evermine.it.configs.yamls.ConfigsYaml;
import eu.evermine.it.configs.yamls.LanguageYaml;
import eu.evermine.it.configs.yamls.StaffChatYaml;
import eu.evermine.it.handlers.models.AbstractCommand;
import io.github.justlel.api.ActionsAPIHelper;

import java.io.IOException;
import java.util.List;

public class BanChatCommand extends AbstractCommand {


    public BanChatCommand() {
    }

    @Override
    public String getCommandUsage() {
        return "/banchat <USERID>";
    }

    @Override
    public void handleUpdate(Update update) {
        if (!ConfigsYaml.isAdmin(getCommandUserId(update)))
            return;
        if (getCommandArguments(update).length != 1) {
            ActionsAPIHelper.sendMessage(LanguageYaml.getLanguageString("ban-chat-syntax", List.of(getCommandUsage())), getCommandChatId(update), getCommandMessageId(update));
            return;
        }
        try {
            Long userId = Long.parseLong(getCommandArguments(update)[0]);
            if (StaffChatYaml.isBannedUser(userId)) {
                ActionsAPIHelper.sendMessage(LanguageYaml.getLanguageString("ban-chat-already-banned"), getCommandChatId(update), getCommandMessageId(update));
            } else {
                if (StaffChatYaml.isInChatUser(userId))
                    StaffChatYaml.removeInChatUser(userId);
                StaffChatYaml.addBannedUser(userId);
                BaseResponse response = ActionsAPIHelper.sendMessage(LanguageYaml.getLanguageString("user-banned-message"), userId);
                if (!response.isOk())
                    throw new NumberFormatException();
                ActionsAPIHelper.sendMessage(LanguageYaml.getLanguageString("banned-chat-success"), getCommandChatId(update), getCommandMessageId(update));
            }
        } catch (NumberFormatException e) {
            ActionsAPIHelper.sendMessage(LanguageYaml.getLanguageString("ban-chat-syntax"), getCommandChatId(update), getCommandMessageId(update));
        } catch (IOException e) {
            ActionsAPIHelper.sendMessage(LanguageYaml.getLanguageString("error-add-banned-users"), getCommandChatId(update), getCommandMessageId(update));
            EvermineSupportBot.logger.error(LanguageYaml.getLanguageString("error-add-banned-users"), e);
        }
    }
}
