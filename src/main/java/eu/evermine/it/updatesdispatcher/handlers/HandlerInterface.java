package eu.evermine.it.updatesdispatcher.handlers;

import com.pengrad.telegrambot.model.Update;

public interface HandlerInterface {

    boolean handleUpdate(Update update);
}
