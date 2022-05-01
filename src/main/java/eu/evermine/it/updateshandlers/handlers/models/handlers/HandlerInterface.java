package eu.evermine.it.updateshandlers.handlers.models.handlers;

import com.pengrad.telegrambot.model.Update;

public interface HandlerInterface {

    boolean handleUpdate(Update update);
}
