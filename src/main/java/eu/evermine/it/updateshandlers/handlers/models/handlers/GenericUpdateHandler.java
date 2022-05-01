package eu.evermine.it.updateshandlers.handlers.models.handlers;


import com.pengrad.telegrambot.model.Update;

public abstract class GenericUpdateHandler extends AbstractUpdateHandler implements HandlerInterface {

    @Override
    public HandlerInterface returnUpdateHandler(Update update) {
        return this;
    }
}
