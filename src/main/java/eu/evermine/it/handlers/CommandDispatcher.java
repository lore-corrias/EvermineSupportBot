package eu.evermine.it.handlers;

import com.pengrad.telegrambot.model.Update;
import eu.evermine.it.handlers.commands.ReloadCommand;
import eu.evermine.it.handlers.commands.StartCommand;
import eu.evermine.it.handlers.commands.staffchat.BanChatCommand;
import eu.evermine.it.handlers.commands.staffchat.EndChatCommand;
import eu.evermine.it.handlers.commands.staffchat.PardonChatCommand;
import eu.evermine.it.updatesdispatcher.handlers.HandlerInterface;
import eu.evermine.it.updatesdispatcher.handlers.SpecificUpdateHandler;
import org.jetbrains.annotations.Nullable;


public class CommandDispatcher extends SpecificUpdateHandler<String> {

    public CommandDispatcher() {
        super.setSpecificHandler("start", new StartCommand());
        super.setSpecificHandler("reload", new ReloadCommand());
        super.setSpecificHandler("banchat", new BanChatCommand());
        super.setSpecificHandler("pardonchat", new PardonChatCommand());
        super.setSpecificHandler("endchat", new EndChatCommand());
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
