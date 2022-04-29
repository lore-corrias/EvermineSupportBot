package eu.evermine.it.updateshandlers.handlers.models;

import com.pengrad.telegrambot.model.Update;

public interface HandlerInterface {

    public boolean handleUpdate(Update update);
}
