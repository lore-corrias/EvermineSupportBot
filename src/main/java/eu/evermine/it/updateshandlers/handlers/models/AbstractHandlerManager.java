package eu.evermine.it.updateshandlers.handlers.models;

import com.pengrad.telegrambot.TelegramBot;
import eu.evermine.it.updateshandlers.AbstractUpdateHandler;
import eu.evermine.it.wrappers.ConfigsWrapper;
import eu.evermine.it.wrappers.LanguageWrapper;
import eu.evermine.it.wrappers.StaffChatWrapper;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.HashMap;

public abstract class AbstractHandlerManager extends AbstractUpdateHandler {

    private final Logger logger;
    private final LanguageWrapper language;
    private final ConfigsWrapper configs;
    private final StaffChatWrapper staffChat;

    private TelegramBot telegramBot;

    private final HashMap<String, HandlerInterface> handlers = new HashMap<>();

    public AbstractHandlerManager(Logger logger, LanguageWrapper language, ConfigsWrapper configs, StaffChatWrapper staffChat) {
        super(logger, language);

        this.logger = logger;
        this.language = language;
        this.configs = configs;
        this.staffChat = staffChat;
    }

    public void registerHandler(String name, HandlerInterface handler) throws IllegalArgumentException {
        if(handlers.containsKey(name))
            throw new IllegalArgumentException();
        handlers.put(name, handler);
    }

    public @Nullable HandlerInterface getHandler(String name) {
        return handlers.getOrDefault(name, null);
    }

    public boolean hasHandler(String name) {
        return handlers.containsKey(name);
    }

    public Logger getLogger() {
        return logger;
    }

    public LanguageWrapper getLanguage() {
        return language;
    }

    public ConfigsWrapper getConfigs() {
        return configs;
    }

    public StaffChatWrapper getStaffChat() {
        return staffChat;
    }

    public void setTelegramBotInstance(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    @Override
    public TelegramBot getTelegramBotInstance() {
        return telegramBot;
    }
}
