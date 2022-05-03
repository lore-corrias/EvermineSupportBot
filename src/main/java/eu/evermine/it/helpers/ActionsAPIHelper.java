package eu.evermine.it.helpers;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.*;
import com.pengrad.telegrambot.response.BaseResponse;
import com.pengrad.telegrambot.response.SendResponse;
import org.jetbrains.annotations.Nullable;

public class ActionsAPIHelper {

    private static TelegramBot telegramBot;

    public static SendResponse sendMessage(String text, long chatId, @Nullable Integer replyToMessage, @Nullable InlineKeyboardMarkup keyboardMarkup) {
        SendMessage sendMessage = new SendMessage(chatId, text)
                .disableWebPagePreview(true)
                .parseMode(ParseMode.HTML);
        if (keyboardMarkup != null)
            sendMessage.replyMarkup(keyboardMarkup);
        if (replyToMessage != null)
            sendMessage.replyToMessageId(replyToMessage);
        return getTelegramBotInstance().execute(sendMessage);
    }

    public static BaseResponse sendMessage(String text, long chatId, @Nullable Integer replyToMessage) {
        return sendMessage(text, chatId, replyToMessage, null);
    }

    public static BaseResponse sendMessage(String text, long chatId) {
        return sendMessage(text, chatId, null, null);
    }

    public static BaseResponse editMessage(String text, long chatId, int messageId, @Nullable InlineKeyboardMarkup keyboardMarkup) {
        EditMessageText editMessageText = new EditMessageText(chatId, messageId, text)
                .disableWebPagePreview(true)
                .parseMode(ParseMode.HTML);
        if (keyboardMarkup != null)
            editMessageText.replyMarkup(keyboardMarkup);
        return getTelegramBotInstance().execute(editMessageText);
    }

    public static BaseResponse editMessage(String text, long chatId, int messageId) {
        return editMessage(text, chatId, messageId, null);
    }

    public static BaseResponse answerCallbackQuery(String callbackQueryId, String text) {
        AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery(callbackQueryId)
                .text(text)
                .showAlert(true);
        return getTelegramBotInstance().execute(answerCallbackQuery);
    }

    public static BaseResponse leaveChat(Long chatId) {
        return getTelegramBotInstance().execute(new LeaveChat(chatId));
    }

    public static BaseResponse forwardMessage(Long toChatId, Long fromChatId, Integer messageId) {
        return getTelegramBotInstance().execute(new ForwardMessage(toChatId, fromChatId, messageId));
    }

    private static TelegramBot getTelegramBotInstance() {
        return telegramBot;
    }

    public static void setTelegramBotInstance(TelegramBot telegramBotInstance) throws IllegalAccessException {
        if (telegramBot != null)
            throw new IllegalAccessException();
        telegramBot = telegramBotInstance;
    }
}
