package eu.evermine.it.updateshandlers.handlers.callbacks;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import eu.evermine.it.updateshandlers.handlers.CallbacksHandler;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractCallback {

    private final CallbacksHandler callbacksHandler;


    public AbstractCallback(CallbacksHandler callbacksHandler) {
        this.callbacksHandler = callbacksHandler;
    }

    public abstract boolean handleCallback(Update update);

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

    public void sendMessage(String text, long chatId, int replyToMessage, @Nullable InlineKeyboardMarkup keyboardMarkup) {
        callbacksHandler.sendMessage(text, chatId, replyToMessage, keyboardMarkup);
    }

    public void sendMessage(String text, long chatId, int replyToMessage) {
        sendMessage(text, chatId, replyToMessage, null);
    }

    public void editMessage(String text, long chatId, int messageId, @Nullable InlineKeyboardMarkup keyboardMarkup) {
        callbacksHandler.editMessage(text, chatId, messageId, keyboardMarkup);
    }

    public void editMessage(String text, long chatId, int messageId) {
        editMessage(text, chatId, messageId, null);
    }

    public void answerCallbackQuery(String callbackQueryId, String text) {
        callbacksHandler.answerCallbackQuery(callbackQueryId, text);
    }
}
