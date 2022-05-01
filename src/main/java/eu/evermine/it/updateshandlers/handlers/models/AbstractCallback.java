package eu.evermine.it.updateshandlers.handlers.models;

import com.pengrad.telegrambot.model.Update;
import eu.evermine.it.updateshandlers.handlers.models.handlers.HandlerInterface;

public abstract class AbstractCallback implements HandlerInterface {



    public abstract boolean handleUpdate(Update update);

    public Long getCallbackUserID(Update update) {
        return update.callbackQuery().from().id();
    }

    public String getCallbackUserName(Update update) {
        return update.callbackQuery().from().username();
    }

    public String getCallbackFirstName(Update update) {
        return update.callbackQuery().from().firstName();
    }

    public Integer getCallbackMessageID(Update update) {
        return update.callbackQuery().message().messageId();
    }

    public Long getCallbackChatID(Update update) {
        return update.callbackQuery().message().chat().id();
    }

    public String getCallbackMessageText(Update update) {
        return update.callbackQuery().message().text();
    }

    public abstract String getCallback();
}
