package eu.evermine.it.handlers;

import com.pengrad.telegrambot.model.Update;
import eu.evermine.it.EvermineSupportBot;
import eu.evermine.it.configs.yamls.ConfigsYaml;
import eu.evermine.it.configs.yamls.LanguageYaml;
import eu.evermine.it.configs.yamls.StaffChatYaml;
import eu.evermine.it.handlers.commands.ReloadCommand;
import eu.evermine.it.handlers.commands.StartCommand;
import eu.evermine.it.handlers.commands.staffchat.BanChatCommand;
import eu.evermine.it.handlers.commands.staffchat.EndChatCommand;
import eu.evermine.it.handlers.commands.staffchat.PardonChatCommand;
import eu.evermine.it.updatesdispatcher.handlers.HandlerInterface;
import eu.evermine.it.updatesdispatcher.handlers.SpecificUpdateHandler;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;


public class CommandDispatcher extends SpecificUpdateHandler<String> {

    public CommandDispatcher(Logger logger, LanguageYaml languageWrapper, ConfigsYaml configsWrapper, StaffChatYaml staffChatWrapper, EvermineSupportBot bot) {
        super.setSpecificHandler("start", new StartCommand(logger, languageWrapper, staffChatWrapper));
        super.setSpecificHandler("reload", new ReloadCommand(logger, languageWrapper, configsWrapper, bot));
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
