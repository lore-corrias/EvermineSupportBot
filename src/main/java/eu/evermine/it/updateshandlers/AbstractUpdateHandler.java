package eu.evermine.it.updateshandlers;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.impl.UpdatesHandler;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.AnswerCallbackQuery;
import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.request.SendMessage;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class AbstractUpdateHandler extends UpdatesHandler {

    private TelegramBot telegramBotInstance;


    public AbstractUpdateHandler() {
        super(0);
    }

    public int process(List<Update> updates) {
        int parsedUpdates = 0;
        for (Update update : updates) {
            if(handleUpdate(update))
                parsedUpdates++;
        }
        return parsedUpdates;
    }

    public abstract boolean handleUpdate(Update update);

    public void setTelegramBotInstance(TelegramBot telegramBotInstance) {
        if(this.telegramBotInstance != null)
            throw new IllegalStateException();
        this.telegramBotInstance = telegramBotInstance;
    }

    public void sendMessage(String text, long chatId, int replyToMessage, @Nullable InlineKeyboardMarkup keyboardMarkup) {
        SendMessage sendMessage = new SendMessage(chatId, text)
                .replyToMessageId(replyToMessage)
                .disableWebPagePreview(true)
                .parseMode(ParseMode.HTML);
        if(keyboardMarkup != null)
                sendMessage.replyMarkup(keyboardMarkup);
        telegramBotInstance.execute(sendMessage);
    }

    public void sendMessage(String text, long chatId, int replyToMessage) {
        sendMessage(text, chatId, replyToMessage, null);
    }

    public void editMessage(String text, long chatId, int messageId, @Nullable InlineKeyboardMarkup keyboardMarkup) {
        EditMessageText editMessageText = new EditMessageText(chatId, messageId, text)
                .disableWebPagePreview(true)
                .parseMode(ParseMode.HTML);
        if(keyboardMarkup != null)
            editMessageText.replyMarkup(keyboardMarkup);
        telegramBotInstance.execute(editMessageText);
    }

    public void editMessage(String text, long chatId, int messageId) {
        editMessage(text, chatId, messageId, null);
    }

    public void answerCallbackQuery(String callbackQueryId, String text) {
        AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery(callbackQueryId)
                .text(text)
                .showAlert(true);
        telegramBotInstance.execute(answerCallbackQuery);
    }
}
