package eu.evermine.it.updateshandlers;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public abstract class AbstractUpdateHandler {

    private TelegramLongPollingBot telegramLongPollingBot;


    public abstract void handleUpdate(Update update);

    public void setTelegramLongPollingBotInstance(TelegramLongPollingBot telegramLongPollingBotInstance) {
        this.telegramLongPollingBot = telegramLongPollingBotInstance;
    }

    public void execute(BotApiMethod<?> botApiMethod) throws TelegramApiException {
        this.telegramLongPollingBot.execute(botApiMethod);
    }

}
