package eu.evermine.it.updateshandlers.handlers.models.handlers;

import com.pengrad.telegrambot.model.Update;
import eu.evermine.it.wrappers.LanguageWrapper;
import org.slf4j.Logger;

public abstract class AbstractUpdateHandler {

    private Logger logger;
    private LanguageWrapper language; // TODO: Astrarre le configurazioni


    protected void setLogger(Logger logger) {
        this.logger = logger;
    }

    protected void setLanguageWrapper(LanguageWrapper language) {
        this.language = language;
    }

    public Logger getLogger() {
        return logger;
    }

    public LanguageWrapper getLanguageWrapper() {
        return language;
    }

    public boolean handleUpdate(Update update) {
        HandlerInterface handler = returnUpdateHandler(update);
        if(handler != null) {
            handler.handleUpdate(update);
            return true;
        }
        return false;
    }

    public abstract HandlerInterface returnUpdateHandler(Update update);
}
