package eu.evermine.it.helpers;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.*;
import org.jetbrains.annotations.Nullable;

public class ActionsAPIHelper {

    private static TelegramBot telegramBot;

    public static void sendMessage(String text, long chatId, @Nullable Integer replyToMessage, @Nullable InlineKeyboardMarkup keyboardMarkup) {
        SendMessage sendMessage = new SendMessage(chatId, text)
                .disableWebPagePreview(true)
                .parseMode(ParseMode.HTML);
        if(keyboardMarkup != null)
            sendMessage.replyMarkup(keyboardMarkup);
        if(replyToMessage != null)
            sendMessage.replyToMessageId(replyToMessage);
        System.out.println(getTelegramBotInstance());
        getTelegramBotInstance().execute(sendMessage);
    }

    public static void sendMessage(String text, long chatId, @Nullable Integer replyToMessage) {
        sendMessage(text, chatId, replyToMessage, null);
    }

    public static void sendMessage(String text, long chatId) {
        sendMessage(text, chatId, null, null);
    }

    public static void editMessage(String text, long chatId, int messageId, @Nullable InlineKeyboardMarkup keyboardMarkup) {
        EditMessageText editMessageText = new EditMessageText(chatId, messageId, text)
                .disableWebPagePreview(true)
                .parseMode(ParseMode.HTML);
        if(keyboardMarkup != null)
            editMessageText.replyMarkup(keyboardMarkup);
        getTelegramBotInstance().execute(editMessageText);
    }

    public static void editMessage(String text, long chatId, int messageId) {
        editMessage(text, chatId, messageId, null);
    }

    public static void answerCallbackQuery(String callbackQueryId, String text) {
        AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery(callbackQueryId)
                .text(text)
                .showAlert(true);
        getTelegramBotInstance().execute(answerCallbackQuery);
    }

    public static void leaveChat(Long chatId) {
        getTelegramBotInstance().execute(new LeaveChat(chatId));
    }

    public static void forwardMessage(Long toChatId, Long fromChatId, Integer messageId) {
        getTelegramBotInstance().execute(new ForwardMessage(toChatId, fromChatId, messageId));
    }

    public static void setTelegramBotInstance(TelegramBot telegramBotInstance) throws IllegalAccessException {
        if(telegramBot != null)
            throw new IllegalAccessException();
        telegramBot = telegramBotInstance;
    }

    private static TelegramBot getTelegramBotInstance() {
        return telegramBot;
    }
}
