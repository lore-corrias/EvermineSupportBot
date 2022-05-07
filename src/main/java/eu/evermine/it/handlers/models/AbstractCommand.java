package eu.evermine.it.handlers.models;

import com.pengrad.telegrambot.model.Update;
import io.github.justlel.models.HandlerInterface;

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

    public abstract String getCommandUsage();
}
