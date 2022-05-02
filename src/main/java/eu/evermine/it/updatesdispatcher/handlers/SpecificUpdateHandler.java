package eu.evermine.it.updatesdispatcher.handlers;

import com.pengrad.telegrambot.model.Update;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public abstract class SpecificUpdateHandler<T> extends AbstractUpdateHandler {

    private final Map<T, HandlerInterface> specificHandlers = new HashMap<>();


    public void setSpecificHandler(T identifier, HandlerInterface specificHandler) {
        specificHandlers.putIfAbsent(identifier, specificHandler);
    }

    public void setSpecificHandler(Map<T, HandlerInterface> specificHandlers) {
        specificHandlers.forEach(this::setSpecificHandler);
    }

    public HandlerInterface getSpecificHandler(T identifier) {
        return specificHandlers.get(identifier);
    }

    public void removeSpecificHandler(T identifier) {
        specificHandlers.remove(identifier);
    }

    public abstract @Nullable HandlerInterface dispatchUpdate(Update update);

    @Override
    public HandlerInterface returnUpdateHandler(Update update) {
        return dispatchUpdate(update);
    }
}
