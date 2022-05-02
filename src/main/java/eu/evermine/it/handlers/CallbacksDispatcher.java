package eu.evermine.it.handlers;

import com.pengrad.telegrambot.model.Update;
import eu.evermine.it.configs.yamls.LanguageYaml;
import eu.evermine.it.configs.yamls.StaffChatYaml;
import eu.evermine.it.handlers.callbacks.ChatStartCallback;
import eu.evermine.it.handlers.callbacks.ServerIpCallback;
import eu.evermine.it.handlers.callbacks.StartCallback;
import eu.evermine.it.handlers.callbacks.StatusCallback;
import eu.evermine.it.handlers.models.AbstractCallback;
import eu.evermine.it.updatesdispatcher.handlers.HandlerInterface;
import eu.evermine.it.updatesdispatcher.handlers.SpecificUpdateHandler;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.LinkedHashMap;
import java.util.List;

public class CallbacksDispatcher extends SpecificUpdateHandler<String> {

    private final LinkedHashMap<String, AbstractCallback> callbackHandlers = new LinkedHashMap<>();

    private final LanguageYaml languageYaml;


    public CallbacksDispatcher(Logger logger, LanguageYaml language, StaffChatYaml staffChat) {
        this.languageYaml = language;

        this.registerCallbackHandler("status", new StatusCallback(logger, language));
        this.registerCallbackHandler("chat-start", new ChatStartCallback(logger, language, staffChat));
        this.registerCallbackHandler("start", new StartCallback(logger, language, staffChat));
        this.registerCallbackHandler("serverip", new ServerIpCallback(language));
    }

    private void registerCallbackHandler(String callback, AbstractCallback callbackHandler) throws IllegalArgumentException {
        if (this.hasCallbackHandler(callback))
            throw new IllegalArgumentException(languageYaml.getLanguageString("already-set-callback"));
        if (callbackHandler.getCallback().equals(callback)) {
            this.callbackHandlers.put(callback, callbackHandler);
        } else {
            throw new IllegalArgumentException(languageYaml.getLanguageString("invalid-handler-callback", List.of(callback)));
        }
    }

    private boolean hasCallbackHandler(String callback) {
        return this.callbackHandlers.containsKey(callback);
    }

    private AbstractCallback getCallbackHandler(String callback) {
        return this.callbackHandlers.get(callback);
    }

    public @Nullable HandlerInterface dispatchUpdate(Update update) {
        if (update.callbackQuery() == null)
            return null;
        String callback = update.callbackQuery().data();
        if (!this.hasCallbackHandler(callback))
            return null;
        return this.getCallbackHandler(callback);
    }
}
