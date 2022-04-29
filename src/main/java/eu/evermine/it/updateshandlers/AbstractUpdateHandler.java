package eu.evermine.it.updateshandlers;

import com.pengrad.telegrambot.Callback;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.*;
import com.pengrad.telegrambot.response.BaseResponse;
import eu.evermine.it.configs.yamls.LanguageYaml;
import eu.evermine.it.wrappers.LanguageWrapper;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.List;

public abstract class AbstractUpdateHandler implements UpdatesListener {

    private final Callback callbackClass;


    public AbstractUpdateHandler(Logger logger, LanguageWrapper languageWrapper) {
        callbackClass = new CallbackClass(logger, languageWrapper);
    }

    public int process(List<Update> updates) {
        for (Update update : updates) {
            handleUpdate(update);
        }
        return CONFIRMED_UPDATES_ALL;
    }

    public abstract boolean handleUpdate(Update update);

    public void sendMessage(String text, long chatId, @Nullable Integer replyToMessage, @Nullable InlineKeyboardMarkup keyboardMarkup) {
        SendMessage sendMessage = new SendMessage(chatId, text)
                .disableWebPagePreview(true)
                .parseMode(ParseMode.HTML);
        if(keyboardMarkup != null)
                sendMessage.replyMarkup(keyboardMarkup);
        if(replyToMessage != null)
            sendMessage.replyToMessageId(replyToMessage);
        System.out.println(getTelegramBotInstance());
        getTelegramBotInstance().execute(sendMessage, callbackClass);
    }

    public void sendMessage(String text, long chatId, @Nullable Integer replyToMessage) {
        sendMessage(text, chatId, replyToMessage, null);
    }

    public void sendMessage(String text, long chatId) {
        sendMessage(text, chatId, null, null);
    }

    public void editMessage(String text, long chatId, int messageId, @Nullable InlineKeyboardMarkup keyboardMarkup) {
        EditMessageText editMessageText = new EditMessageText(chatId, messageId, text)
                .disableWebPagePreview(true)
                .parseMode(ParseMode.HTML);
        if(keyboardMarkup != null)
            editMessageText.replyMarkup(keyboardMarkup);
        getTelegramBotInstance().execute(editMessageText, callbackClass);
    }

    public void editMessage(String text, long chatId, int messageId) {
        editMessage(text, chatId, messageId, null);
    }

    public void answerCallbackQuery(String callbackQueryId, String text) {
        AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery(callbackQueryId)
                .text(text)
                .showAlert(true);
        getTelegramBotInstance().execute(answerCallbackQuery, callbackClass);
    }

    public void leaveChat(Long chatId) {
        getTelegramBotInstance().execute(new LeaveChat(chatId), callbackClass);
    }

    public void forwardMessage(Long toChatId, Long fromChatId, Integer messageId) {
        getTelegramBotInstance().execute(new ForwardMessage(toChatId, fromChatId, messageId), callbackClass);
    }

    private record CallbackClass(Logger logger, LanguageWrapper languageWrapper) implements Callback {

        @Override
        public void onResponse(BaseRequest request, BaseResponse response) {
            logger.trace("onResponse: {}", response);
        }

        @Override
        public void onFailure(BaseRequest request, IOException e) {
            logger.error(languageWrapper.getLanguageString(LanguageYaml.LANGUAGE_INDEXES.REQUEST_FAILURE, List.of(request.toString())), e);
        }
    }

    public abstract void setTelegramBotInstance(TelegramBot telegramBot);

    public abstract TelegramBot getTelegramBotInstance();
}
