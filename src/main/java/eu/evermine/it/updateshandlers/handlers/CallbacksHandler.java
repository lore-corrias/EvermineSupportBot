package eu.evermine.it.updateshandlers.handlers;

import eu.evermine.it.configs.yamls.LanguageYaml;
import eu.evermine.it.updateshandlers.AbstractUpdateHandler;
import eu.evermine.it.updateshandlers.handlers.callbacks.*;
import eu.evermine.it.wrappers.LanguageWrapper;
import eu.evermine.it.wrappers.StaffChatWrapper;
import org.slf4j.Logger;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.LinkedHashMap;
import java.util.List;

public class CallbacksHandler extends AbstractUpdateHandler {

    private final LinkedHashMap<String, AbstractCallback> callbackHandlers = new LinkedHashMap<>();

    private final Logger logger;
    private final LanguageWrapper language;
    private final StaffChatWrapper staffChat;


    public CallbacksHandler(Logger logger, LanguageWrapper language, StaffChatWrapper staffChat) {
        this.logger = logger;
        this.language = language;
        this.staffChat = staffChat;

        this.registerCallbackHandler("status", new StatusCallback(this, this.logger, this.language));
        this.registerCallbackHandler("chat-start", new ChatStartCallback(this, this.logger, this.language, this.staffChat));
        this.registerCallbackHandler("start", new StartCallback(this, this.logger, this.language, this.staffChat));
        this.registerCallbackHandler("serverip", new ServerIpCallback(this, this.logger, this.language));
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

    @Override
    public void handleUpdate(Update update) {
        String callback = update.getCallbackQuery().getData();
        if(this.hasCallbackHandler(callback)) {
            this.getCallbackHandler(callback).handleCallback(update);
        }
    }

}
