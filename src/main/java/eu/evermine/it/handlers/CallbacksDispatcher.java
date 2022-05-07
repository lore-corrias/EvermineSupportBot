package eu.evermine.it.handlers;

import com.pengrad.telegrambot.model.Update;
import eu.evermine.it.configs.yamls.LanguageYaml;
import eu.evermine.it.handlers.callbacks.ChatStartCallback;
import eu.evermine.it.handlers.callbacks.ServerIpCallback;
import eu.evermine.it.handlers.callbacks.StartCallback;
import eu.evermine.it.handlers.callbacks.StatusCallback;
import eu.evermine.it.handlers.models.AbstractCallback;
import io.github.justlel.models.HandlerInterface;
import io.github.justlel.models.SpecificUpdateHandler;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.List;

public class CallbacksDispatcher extends SpecificUpdateHandler<String> {

    private final LinkedHashMap<String, AbstractCallback> callbackHandlers = new LinkedHashMap<>();


    public CallbacksDispatcher() {
        this.registerCallbackHandler("status", new StatusCallback());
        this.registerCallbackHandler("chat-start", new ChatStartCallback());
        this.registerCallbackHandler("start", new StartCallback());
        this.registerCallbackHandler("serverip", new ServerIpCallback());
    }

    private void registerCallbackHandler(String callback, AbstractCallback callbackHandler) throws IllegalArgumentException {
        if (this.hasCallbackHandler(callback))
            throw new IllegalArgumentException(LanguageYaml.getLanguageString("already-set-callback"));
        if (callbackHandler.getCallback().equals(callback)) {
            this.callbackHandlers.put(callback, callbackHandler);
        } else {
            throw new IllegalArgumentException(LanguageYaml.getLanguageString("invalid-handler-callback", List.of(callback)));
        }
    }

    private boolean hasCallbackHandler(String callback) {
        return this.callbackHandlers.containsKey(callback);
    }

    private AbstractCallback getCallbackHandler(String callback) {
        return this.callbackHandlers.get(callback);
    }

    @Override
    public @Nullable HandlerInterface returnUpdateHandler(Update update) {
        if (update.callbackQuery() == null)
            return null;
        String callback = update.callbackQuery().data();
        if (!this.hasCallbackHandler(callback))
            return null;
        return this.getCallbackHandler(callback);
    }
}
