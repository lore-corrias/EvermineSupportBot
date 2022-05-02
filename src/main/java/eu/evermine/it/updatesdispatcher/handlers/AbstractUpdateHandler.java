package eu.evermine.it.updatesdispatcher.handlers;

import com.pengrad.telegrambot.model.Update;

public abstract class AbstractUpdateHandler {

    public boolean handleUpdate(Update update) {
        HandlerInterface handler = returnUpdateHandler(update);
        if (handler != null) {
            handler.handleUpdate(update);
            return true;
        }
        return false;
    }

    public abstract HandlerInterface returnUpdateHandler(Update update);
}
