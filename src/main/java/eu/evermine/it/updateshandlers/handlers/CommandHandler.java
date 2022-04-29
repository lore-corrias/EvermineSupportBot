package eu.evermine.it.updateshandlers.handlers;

import com.pengrad.telegrambot.model.Update;
import eu.evermine.it.updateshandlers.handlers.commands.ReloadCommand;
import eu.evermine.it.updateshandlers.handlers.commands.StartCommand;
import eu.evermine.it.updateshandlers.handlers.commands.staffchat.BanChatCommand;
import eu.evermine.it.updateshandlers.handlers.commands.staffchat.EndChatCommand;
import eu.evermine.it.updateshandlers.handlers.commands.staffchat.PardonChatCommand;
import eu.evermine.it.updateshandlers.handlers.models.AbstractHandlerManager;
import eu.evermine.it.wrappers.ConfigsWrapper;
import eu.evermine.it.wrappers.LanguageWrapper;
import eu.evermine.it.wrappers.StaffChatWrapper;
import org.slf4j.Logger;


public class CommandHandler extends AbstractHandlerManager {

    public CommandHandler(Logger logger, LanguageWrapper languageWrapper, ConfigsWrapper configsWrapper, StaffChatWrapper staffChatWrapper) {
        super(logger, languageWrapper, configsWrapper, staffChatWrapper);

        super.registerHandler("start", new StartCommand(this, logger, languageWrapper, staffChatWrapper));
        super.registerHandler("reload", new ReloadCommand(this, logger, languageWrapper, configsWrapper));
        super.registerHandler("banchat", new BanChatCommand(this, logger, languageWrapper, configsWrapper, staffChatWrapper));
        super.registerHandler("pardonchat", new PardonChatCommand(this, logger, languageWrapper, configsWrapper, staffChatWrapper));
        super.registerHandler("endchat", new EndChatCommand(this, logger, languageWrapper, configsWrapper, staffChatWrapper));
    }

    @Override
    public boolean handleUpdate(Update update) {
        String command = update.message().text().split(" ")[0].replace("/", "");
        if(hasHandler(command))
            return getHandler(command).handleUpdate(update);
        return false;
    }
}
