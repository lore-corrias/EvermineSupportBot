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

public class EndChatCommand extends AbstractCommand {

    private final Logger logger;
    private final LanguageYaml language;
    private final ConfigsYaml configs;
    private final StaffChatYaml staffChat;


    public EndChatCommand(Logger logger, LanguageYaml language, ConfigsYaml configs, StaffChatYaml staffChatWrapper) {
        this.logger = logger;
        this.language = language;
        this.configs = configs;
        this.staffChat = staffChatWrapper;
    }

    @Override
    public String getCommandUsage() {
        return "/endchat <USERID>";
    }

    @Override
    public boolean handleUpdate(Update update) {
        if (!configs.isAdmin(getCommandUserId(update)))
            return true;
        if (getCommandArguments(update).length != 1) {
            ActionsAPIHelper.sendMessage(language.getLanguageString("end-chat-syntax", List.of(getCommandUsage())), getCommandChatId(update), getCommandMessageId(update));
        } else {
            try {
                Long userID = Long.parseLong(getCommandArguments(update)[0]);
                if (!staffChat.isInChatUser(userID)) {
                    ActionsAPIHelper.sendMessage(language.getLanguageString("end-chat-user-not-in-chat"), getCommandChatId(update), getCommandMessageId(update));
                } else {
                    staffChat.removeInChatUser(userID);
                    ActionsAPIHelper.sendMessage(language.getLanguageString("end-chat-user-removed"), getCommandChatId(update), getCommandMessageId(update));
                }
                ActionsAPIHelper.sendMessage(language.getLanguageString("end-chat-user-removed-by-admin"), userID, null);
            } catch (NumberFormatException e) {
                ActionsAPIHelper.sendMessage(language.getLanguageString("end-chat-user-not-in-chat"), getCommandChatId(update), getCommandMessageId(update));
            } catch (IOException e) {
                ActionsAPIHelper.sendMessage(language.getLanguageString("error-removing-user-from-staff-chat-file"), getCommandChatId(update), getCommandMessageId(update));
                logger.error(language.getLanguageString("error-removing-user-from-staff-chat-file"), e);
            }
        }
        return true;
    }
}
