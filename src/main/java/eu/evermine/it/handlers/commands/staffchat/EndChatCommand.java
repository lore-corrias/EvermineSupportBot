package eu.evermine.it.handlers.commands.staffchat;

import com.pengrad.telegrambot.model.Update;
import eu.evermine.it.EvermineSupportBot;
import eu.evermine.it.configs.yamls.ConfigsYaml;
import eu.evermine.it.configs.yamls.LanguageYaml;
import eu.evermine.it.configs.yamls.StaffChatYaml;
import eu.evermine.it.handlers.models.AbstractCommand;
import eu.evermine.it.helpers.ActionsAPIHelper;

import java.io.IOException;
import java.util.List;

public class EndChatCommand extends AbstractCommand {

    public EndChatCommand() {
    }

    @Override
    public String getCommandUsage() {
        return "/endchat <USERID>";
    }

    @Override
    public boolean handleUpdate(Update update) {
        if (!ConfigsYaml.isAdmin(getCommandUserId(update)))
            return true;
        if (getCommandArguments(update).length != 1) {
            ActionsAPIHelper.sendMessage(LanguageYaml.getLanguageString("end-chat-syntax", List.of(getCommandUsage())), getCommandChatId(update), getCommandMessageId(update));
        } else {
            try {
                Long userID = Long.parseLong(getCommandArguments(update)[0]);
                if (!StaffChatYaml.isInChatUser(userID)) {
                    ActionsAPIHelper.sendMessage(LanguageYaml.getLanguageString("end-chat-user-not-in-chat"), getCommandChatId(update), getCommandMessageId(update));
                } else {
                    StaffChatYaml.removeInChatUser(userID);
                    ActionsAPIHelper.sendMessage(LanguageYaml.getLanguageString("end-chat-user-removed"), getCommandChatId(update), getCommandMessageId(update));
                }
                ActionsAPIHelper.sendMessage(LanguageYaml.getLanguageString("end-chat-user-removed-by-admin"), userID, null);
            } catch (NumberFormatException e) {
                ActionsAPIHelper.sendMessage(LanguageYaml.getLanguageString("end-chat-user-not-in-chat"), getCommandChatId(update), getCommandMessageId(update));
            } catch (IOException e) {
                ActionsAPIHelper.sendMessage(LanguageYaml.getLanguageString("error-removing-user-from-staff-chat-file"), getCommandChatId(update), getCommandMessageId(update));
                EvermineSupportBot.logger.error(LanguageYaml.getLanguageString("error-removing-user-from-staff-chat-file"), e);
            }
        }
        return true;
    }
}
