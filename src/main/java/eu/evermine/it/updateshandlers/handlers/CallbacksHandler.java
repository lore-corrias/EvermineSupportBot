package eu.evermine.it.updateshandlers.handlers;

import com.pengrad.telegrambot.model.Update;
import eu.evermine.it.configs.yamls.LanguageYaml;
import eu.evermine.it.updateshandlers.AbstractUpdateHandler;
import eu.evermine.it.updateshandlers.handlers.callbacks.*;
import eu.evermine.it.wrappers.LanguageWrapper;
import eu.evermine.it.wrappers.StaffChatWrapper;
import org.slf4j.Logger;

import java.util.LinkedHashMap;
import java.util.List;

public class CallbacksHandler extends AbstractUpdateHandler {

    private final LinkedHashMap<String, AbstractCallback> callbackHandlers = new LinkedHashMap<>();

    private final LanguageWrapper language;


    public CallbacksHandler(Logger logger, LanguageWrapper language, StaffChatWrapper staffChat) {
        this.language = language;

        this.registerCallbackHandler("status", new StatusCallback(this, logger, language));
        this.registerCallbackHandler("chat-start", new ChatStartCallback(this, logger, language, staffChat));
        this.registerCallbackHandler("start", new StartCallback(this, logger, language, staffChat));
        this.registerCallbackHandler("serverip", new ServerIpCallback(this, language));
    }

    private void registerCallbackHandler(String callback, AbstractCallback callbackHandler) throws IllegalArgumentException {
        if(this.hasCallbackHandler(callback))
            throw new IllegalArgumentException(language.getLanguageString(LanguageYaml.LANGUAGE_INDEXES.ALREADY_SET_CALLBACK));
        if(callbackHandler.getCallback().equals(callback)) {
            this.callbackHandlers.put(callback, callbackHandler);
        } else {
            throw new IllegalArgumentException(language.getLanguageString(LanguageYaml.LANGUAGE_INDEXES.INVALID_HANDLER_CALLBACK, List.of(callback)));
        }
    }

    private boolean hasCallbackHandler(String callback) {
        return this.callbackHandlers.containsKey(callback);
    }

    private AbstractCallback getCallbackHandler(String callback) {
        return this.callbackHandlers.get(callback);
    }

    public boolean handleUpdate(Update update) {
        if(update.callbackQuery() == null)
            return false;
        String callback = update.callbackQuery().data();
        if(!this.hasCallbackHandler(callback))
            return false;
        return this.getCallbackHandler(callback).handleCallback(update);
    }
}
