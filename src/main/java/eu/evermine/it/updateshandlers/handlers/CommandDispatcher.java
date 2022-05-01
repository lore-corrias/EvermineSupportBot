package eu.evermine.it.updateshandlers.handlers;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import eu.evermine.it.updateshandlers.handlers.commands.ReloadCommand;
import eu.evermine.it.updateshandlers.handlers.commands.StartCommand;
import eu.evermine.it.updateshandlers.handlers.commands.staffchat.BanChatCommand;
import eu.evermine.it.updateshandlers.handlers.commands.staffchat.EndChatCommand;
import eu.evermine.it.updateshandlers.handlers.commands.staffchat.PardonChatCommand;
import eu.evermine.it.updateshandlers.handlers.models.handlers.HandlerInterface;
import eu.evermine.it.updateshandlers.handlers.models.handlers.SpecificUpdateHandler;
import eu.evermine.it.wrappers.ConfigsWrapper;
import eu.evermine.it.wrappers.LanguageWrapper;
import eu.evermine.it.wrappers.StaffChatWrapper;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;



public class CommandDispatcher extends SpecificUpdateHandler<String> {

    public CommandDispatcher(Logger logger, LanguageWrapper languageWrapper, ConfigsWrapper configsWrapper, StaffChatWrapper staffChatWrapper) {
        setLogger(logger);
        setLanguageWrapper(languageWrapper);


        super.setSpecificHandler("start", new StartCommand(logger, languageWrapper, staffChatWrapper));
        super.setSpecificHandler("reload", new ReloadCommand(logger, languageWrapper, configsWrapper));
        super.setSpecificHandler("banchat", new BanChatCommand(logger, languageWrapper, configsWrapper, staffChatWrapper));
        super.setSpecificHandler("pardonchat", new PardonChatCommand(logger, languageWrapper, configsWrapper, staffChatWrapper));
        super.setSpecificHandler("endchat", new EndChatCommand(logger, languageWrapper, configsWrapper, staffChatWrapper));
    }

    @Override
    public @Nullable HandlerInterface dispatchUpdate(Update update) {
        String command = update.message().text();
        if (command.startsWith("/")) {
            command = command.substring(1);
        } else {
            return null;
        }
        return super.getSpecificHandler(command);
    }
}
