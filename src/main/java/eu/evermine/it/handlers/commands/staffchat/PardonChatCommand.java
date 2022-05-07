package eu.evermine.it.handlers.commands.staffchat;

import com.pengrad.telegrambot.model.Update;
import eu.evermine.it.EvermineSupportBot;
import eu.evermine.it.configs.yamls.ConfigsYaml;
import eu.evermine.it.configs.yamls.LanguageYaml;
import eu.evermine.it.configs.yamls.StaffChatYaml;
import eu.evermine.it.handlers.models.AbstractCommand;
import io.github.justlel.api.ActionsAPIHelper;

import java.io.IOException;
import java.util.List;

public class PardonChatCommand extends AbstractCommand {


    public PardonChatCommand() {
    }

    @Override
    public String getCommandUsage() {
        return "/pardonchat <USERID>";
    }

    @Override
    public void handleUpdate(Update update) {
        if (!ConfigsYaml.isAdmin(getCommandUserId(update)))
            return;

        if (getCommandArguments(update).length != 1) {
            ActionsAPIHelper.sendMessage(LanguageYaml.getLanguageString("pardon-chat-syntax", List.of()), getCommandChatId(update), getCommandMessageId(update));
        } else {
            try {
                Long userId = Long.parseLong(getCommandArguments(update)[0]);
                try {
                    if (StaffChatYaml.isBannedUser(userId)) {
                        ActionsAPIHelper.sendMessage(LanguageYaml.getLanguageString("pardon-chat-message"), userId);

                        if (StaffChatYaml.isInChatUser(userId))
                            StaffChatYaml.removeInChatUser(userId);
                        StaffChatYaml.removeBannedUser(userId);
                        ActionsAPIHelper.sendMessage(LanguageYaml.getLanguageString("pardon-chat-success"), getCommandChatId(update), getCommandMessageId(update));
                    } else {
                        ActionsAPIHelper.sendMessage(LanguageYaml.getLanguageString("pardon-chat-not-banned"), getCommandChatId(update), getCommandMessageId(update));
                    }
                } catch (IOException e) {
                    EvermineSupportBot.logger.error(LanguageYaml.getLanguageString("error-remove-banned-user"), e);
                }
            } catch (NumberFormatException e) {
                ActionsAPIHelper.sendMessage(LanguageYaml.getLanguageString("pardon-chat-syntax", List.of(getCommandUsage())), getCommandChatId(update), getCommandMessageId(update));
            }
        }
    }
}
