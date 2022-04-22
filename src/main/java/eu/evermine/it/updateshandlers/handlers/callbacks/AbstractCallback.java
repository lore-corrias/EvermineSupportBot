package eu.evermine.it.updateshandlers.handlers.callbacks;

import eu.evermine.it.updateshandlers.handlers.CallbacksHandler;
import org.telegram.telegrambots.meta.api.objects.Update;

public abstract class AbstractCallback {

    private final CallbacksHandler callbacksHandler;


    public AbstractCallback(CallbacksHandler callbacksHandler) {
        this.callbacksHandler = callbacksHandler;
    }

    public abstract void handleCallback(Update update);

    public Long getCallbackUserID(Update update) {
        return update.getCallbackQuery().getFrom().getId();
    }

    public String getCallbackUserName(Update update) {
        return update.getCallbackQuery().getFrom().getUserName();
    }

    public String getCallbackFirstName(Update update) {
        return update.getCallbackQuery().getFrom().getFirstName();
    }

    public Integer getCallbackMessageID(Update update) {
        return update.getCallbackQuery().getMessage().getMessageId();
    }

    public String getCallbackChatID(Update update) {
        return update.getCallbackQuery().getMessage().getChatId().toString();
    }

    public String getCallbackMessageText(Update update) {
        return update.getCallbackQuery().getMessage().getText();
    }

    protected CallbacksHandler getCallbacksHandler() {
        return callbacksHandler;
    }

    public abstract String getCallback();

    public abstract String getCallbackDescription();
}
