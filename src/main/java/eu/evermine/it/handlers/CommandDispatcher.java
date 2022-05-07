package eu.evermine.it.handlers;

import com.pengrad.telegrambot.model.Update;
import eu.evermine.it.handlers.commands.ReloadCommand;
import eu.evermine.it.handlers.commands.StartCommand;
import eu.evermine.it.handlers.commands.staffchat.BanChatCommand;
import eu.evermine.it.handlers.commands.staffchat.EndChatCommand;
import eu.evermine.it.handlers.commands.staffchat.PardonChatCommand;
import io.github.justlel.models.HandlerInterface;
import io.github.justlel.models.SpecificUpdateHandler;
import org.jetbrains.annotations.Nullable;


public class CommandDispatcher extends SpecificUpdateHandler<String> {

    public CommandDispatcher() {
        super.registerSpecificHandler("start", new StartCommand());
        super.registerSpecificHandler("reload", new ReloadCommand());
        super.registerSpecificHandler("banchat", new BanChatCommand());
        super.registerSpecificHandler("pardonchat", new PardonChatCommand());
        super.registerSpecificHandler("endchat", new EndChatCommand());
    }

    @Nullable
    @Override
    public HandlerInterface returnUpdateHandler(Update update) {
        if (!update.message().text().startsWith("/")) return null;
        String command = update.message().text().replace("/", "");
        return super.getSpecificHandler(command);
    }
}
