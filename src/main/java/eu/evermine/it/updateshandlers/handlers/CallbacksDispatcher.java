package eu.evermine.it.updateshandlers.handlers;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import eu.evermine.it.configs.yamls.LanguageYaml;
import eu.evermine.it.updateshandlers.handlers.callbacks.*;
import eu.evermine.it.updateshandlers.handlers.models.AbstractCallback;
import eu.evermine.it.updateshandlers.handlers.models.handlers.HandlerInterface;
import eu.evermine.it.updateshandlers.handlers.models.handlers.SpecificUpdateHandler;
import eu.evermine.it.wrappers.LanguageWrapper;
import eu.evermine.it.wrappers.StaffChatWrapper;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.LinkedHashMap;
import java.util.List;

public class CallbacksDispatcher extends SpecificUpdateHandler<String> {

    private final LinkedHashMap<String, AbstractCallback> callbackHandlers = new LinkedHashMap<>();


    public CallbacksDispatcher(Logger logger, LanguageWrapper language, StaffChatWrapper staffChat) {
        setLogger(logger);
        setLanguageWrapper(language);

        this.registerCallbackHandler("status", new StatusCallback(logger, language));
        this.registerCallbackHandler("chat-start", new ChatStartCallback(logger, language, staffChat));
        this.registerCallbackHandler("start", new StartCallback(logger, language, staffChat));
        this.registerCallbackHandler("serverip", new ServerIpCallback(language));
    }

    private void registerCallbackHandler(String callback, AbstractCallback callbackHandler) throws IllegalArgumentException {
        if(this.hasCallbackHandler(callback))
            throw new IllegalArgumentException(getLanguageWrapper().getLanguageString(LanguageYaml.LANGUAGE_INDEXES.ALREADY_SET_CALLBACK));
        if(callbackHandler.getCallback().equals(callback)) {
            this.callbackHandlers.put(callback, callbackHandler);
        } else {
            throw new IllegalArgumentException(getLanguageWrapper().getLanguageString(LanguageYaml.LANGUAGE_INDEXES.INVALID_HANDLER_CALLBACK, List.of(callback)));
        }
    }

    private boolean hasCallbackHandler(String callback) {
        return this.callbackHandlers.containsKey(callback);
    }

    private AbstractCallback getCallbackHandler(String callback) {
        return this.callbackHandlers.get(callback);
    }

    public @Nullable HandlerInterface dispatchUpdate(Update update) {
        if(update.callbackQuery() == null)
            return null;
        String callback = update.callbackQuery().data();
        if(!this.hasCallbackHandler(callback))
            return null;
        return this.getCallbackHandler(callback);
    }
}
