package eu.evermine.it.updateshandlers.handlers.models;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import eu.evermine.it.helpers.ActionsAPIHelper;
import eu.evermine.it.updateshandlers.handlers.CommandDispatcher;
import eu.evermine.it.updateshandlers.handlers.models.handlers.HandlerInterface;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractCommand implements HandlerInterface {


    public Long getCommandUserId(Update update) {
        return update.message().from().id();
    }

    public String getCommandUserName(Update update) {
        return update.message().from().username();
    }

    public String getCommandUserFirstName(Update update) {
        return update.message().from().firstName();
    }

    public Integer getCommandMessageId(Update update) {
        return update.message().messageId();
    }

    public Long getCommandChatId(Update update) {
        return update.message().chat().id();
    }

    public String[] getCommandArguments(Update update) {
        String[] split = update.message().text().split(" ");
        String[] args = new String[split.length - 1];
        System.arraycopy(split, 1, args, 0, args.length);
        return args;
    }

    public abstract String getCommandName();

    public abstract String getCommandDescription();

    public abstract String getCommandUsage();
}
