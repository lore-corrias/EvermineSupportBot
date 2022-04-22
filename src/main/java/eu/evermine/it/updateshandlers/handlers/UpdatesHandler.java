package eu.evermine.it.updateshandlers.handlers;

import eu.evermine.it.EvermineSupportBot;
import eu.evermine.it.updateshandlers.AbstractUpdateHandler;
import eu.evermine.it.updateshandlers.handlers.commands.ReloadCommand;
import eu.evermine.it.updateshandlers.handlers.commands.StartCommand;
import eu.evermine.it.updateshandlers.handlers.commands.staffachat.BanChat;
import eu.evermine.it.updateshandlers.handlers.commands.staffachat.EndChat;
import eu.evermine.it.updateshandlers.handlers.commands.staffachat.PardonChat;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.Nullable;
import java.util.*;

public class UpdatesHandler extends TelegramLongPollingCommandBot {

    public enum UpdateTypes {
        PRIVATE_MESSAGE(".hasText"),
        CHANNEL_POST("hasChannelPost"),
        EDITED_CHANNEL_POST("hasEditedChannelPost"),
        INLINE_QUERY("hasInlineQuery"),
        EDITED_MESSAGE("hasEditedMessage"),
        CHOSEN_INLINE_QUERY("hasChosenInlineQuery"),
        POLL("hasPoll"),
        PHOTO(".hasPhoto"),
        VIDEO(".hasVideo"),
        ANIMATION(".hasAnimation"),
        AUDIO(".hasAudio"),
        DICE(".hasDice"),
        DOCUMENT(".hasDocument"),
        INVOICE(".hasInvoice"),
        STICKER(".hasSticker"),
        GROUP_MESSAGE(".isGroupMessage"),
        CHANNEL_MESSAGE(".isChannelMessage"),
        SUPERGROUP_MESSAGE(".isSuperGroupMessage"),
        LOCATION(".hasLocation"),
        GROUP_CHAT_CREATED(".getGroupchatCreated"),
        SUPERGROUP_CHAT_CREATED(".getSupergroupCreated"),
        CHANNEL_CHAT_CREATED(".getChannelChatCreated"),
        CALLBACK_QUERY("hasCallbackQuery"),
        NEW_CHAT_MEMBER(""),
        LEFT_CHAT_MEMBER("");

        public static List<UpdateTypes> getMediaUpdates() {
            return new LinkedList<>(List.of(POLL, PHOTO, VIDEO, ANIMATION, AUDIO, DICE, DOCUMENT, INVOICE, STICKER, LOCATION, PRIVATE_MESSAGE));
        }

        public static LinkedList<UpdateTypes> getTextsUpdates() {
            return new LinkedList<>(List.of(CHANNEL_POST, EDITED_CHANNEL_POST, EDITED_MESSAGE, GROUP_MESSAGE, CHANNEL_MESSAGE, SUPERGROUP_MESSAGE));
        }

        public static LinkedList<UpdateTypes> getNonUserChatUpdates() {
            return new LinkedList<>(List.of(GROUP_CHAT_CREATED, SUPERGROUP_CHAT_CREATED, CHANNEL_CHAT_CREATED));
        }

        public static LinkedList<UpdateTypes> getInlineUpdates() {
            return new LinkedList<>(List.of(INLINE_QUERY, CHOSEN_INLINE_QUERY));
        }

        private final String methodName;


        UpdateTypes(String methodName) {
            this.methodName = methodName;
        }

        public boolean updateMatches(Update update) {
            try {
                if(!this.methodName.contains(".")) {
                    return (boolean) update.getClass().getMethod(this.methodName).invoke(update);
                } else {
                    return (boolean) update.getMessage().getClass().getMethod(this.methodName.replace(".", "")).invoke(update.getMessage());
                }
            } catch(Exception e) {
                return false;
            }
        }
    }

    private final HashMap<UpdateTypes, AbstractUpdateHandler> updatesHandlers = new LinkedHashMap<>();
    private AbstractUpdateHandler defaultHandler;

    /**
     * Istanza di EvermineSupportBot.
     *
     */
    private final EvermineSupportBot evermineSupportBot;

    /**
     * Costruttore per l'handler CommandsHandler, che gestisce i comandi in arrivo.
     *
     * @param evermineSupportBot Istanza della classe EvermineSupportBot.
     */
    public UpdatesHandler(EvermineSupportBot evermineSupportBot) {
        this.evermineSupportBot = evermineSupportBot;

        super.register(new StartCommand(evermineSupportBot.getLogger(), evermineSupportBot.getLanguage(), evermineSupportBot.getStaffChat()));
        super.register(new EndChat(evermineSupportBot.getLogger(), evermineSupportBot.getLanguage(), evermineSupportBot.getConfigs(), evermineSupportBot.getStaffChat()));
        super.register(new BanChat(evermineSupportBot.getLogger(), evermineSupportBot.getLanguage(), evermineSupportBot.getConfigs(), evermineSupportBot.getStaffChat()));
        super.register(new PardonChat(evermineSupportBot.getLogger(), evermineSupportBot.getLanguage(), evermineSupportBot.getConfigs(), evermineSupportBot.getStaffChat()));
        super.register(new ReloadCommand(evermineSupportBot.getLogger(), evermineSupportBot.getLanguage(), evermineSupportBot.getConfigs()));

        this.registerNonCommandUpdateHandler(UpdateTypes.CALLBACK_QUERY, new CallbacksHandler(evermineSupportBot.getLogger(), evermineSupportBot.getLanguage(), evermineSupportBot.getStaffChat()));
        this.registerNonCommandUpdateHandler(List.of(UpdateTypes.GROUP_CHAT_CREATED, UpdateTypes.NEW_CHAT_MEMBER), new GroupJoinHandler(evermineSupportBot.getLogger(), evermineSupportBot.getLanguage(), evermineSupportBot.getConfigs()));
        List<UpdateTypes> registeredMessagesUpdates = new ArrayList<>(UpdateTypes.getMediaUpdates());
        registeredMessagesUpdates.add(UpdateTypes.GROUP_MESSAGE);
        registeredMessagesUpdates.add(UpdateTypes.SUPERGROUP_MESSAGE);
        this.registerNonCommandUpdateHandler(registeredMessagesUpdates, new MessagesHandler(evermineSupportBot.getLogger(), evermineSupportBot.getLanguage(), evermineSupportBot.getConfigs(), evermineSupportBot.getStaffChat()));
    }

    /**
     * Getter per l'username del bot.
     *
     * @return L'username del bot.
     */
    @Override
    public String getBotUsername() {
        return this.evermineSupportBot.getBotUsername();
    }

    @Override
    public void processNonCommandUpdate(Update update) {
        long start = System.nanoTime();
        UpdateTypes updateType = this.getUpdateType(update);
        evermineSupportBot.getLogger().info("Tempo di esecuzione per la definizione di un update di tipo " + updateType + ": " + (System.nanoTime() - start)*1e-6 + "ms");
        if(this.hasUpdateHandler(updateType)) {
            this.updatesHandlers.get(updateType).handleUpdate(update);
        } else if(updateType == null && !this.hasDefaultHandler()) {
            evermineSupportBot.getLogger().info("Update non gestito: " + update);
        } else if(this.hasDefaultHandler()) {
            this.defaultHandler.handleUpdate(update);
        }
    }

    @Nullable
    private UpdateTypes getUpdateType(Update update) {
        if(update.getMessage() != null) {
            if(update.getMessage().hasText()) {
                for(UpdateTypes updateType : UpdateTypes.getTextsUpdates()) {
                    if(updateType.updateMatches(update))
                        return updateType;
                }
            }
            for(UpdateTypes updateType : UpdateTypes.getMediaUpdates()) {
                if(updateType.updateMatches(update))
                    return updateType;
            }
            for(UpdateTypes updateType : UpdateTypes.getNonUserChatUpdates()) {
                if (updateType.updateMatches(update))
                    return updateType;
            }
            if(update.getMessage().getLeftChatMember() != null)
                return UpdateTypes.LEFT_CHAT_MEMBER;
            if(update.getMessage().getNewChatMembers() != null)
                return UpdateTypes.NEW_CHAT_MEMBER;
        } else {
            for(UpdateTypes updateType : UpdateTypes.getInlineUpdates()) {
                if(updateType.updateMatches(update))
                    return updateType;
            }
            if(UpdateTypes.CALLBACK_QUERY.updateMatches(update))
                return UpdateTypes.CALLBACK_QUERY;
        }
        return null;
    }

    /**
     * Getter per il token del bot.
     *
     * @return Il token del bot.
     */
    @Override
    public String getBotToken() {
        return this.evermineSupportBot.getBotToken();
    }

    public void registerNonCommandUpdateHandler(List<UpdateTypes> updateTypes, AbstractUpdateHandler abstractUpdateHandler) {
        updateTypes.forEach(updateType -> this.registerNonCommandUpdateHandler(updateType, abstractUpdateHandler));
    }

    public void registerNonCommandUpdateHandler(UpdateTypes updateType, AbstractUpdateHandler abstractUpdateHandler) {
        abstractUpdateHandler.setTelegramLongPollingBotInstance(this);
        this.updatesHandlers.put(updateType, abstractUpdateHandler);
    }

    public void registerDefaultUpdateHandler(AbstractUpdateHandler abstractUpdateHandler) {
        abstractUpdateHandler.setTelegramLongPollingBotInstance(this);
        this.defaultHandler = abstractUpdateHandler;
    }

    private boolean hasUpdateHandler(UpdateTypes updateType) {
        return this.updatesHandlers.containsKey(updateType);
    }

    private boolean hasDefaultHandler() {
        return this.defaultHandler != null;
    }
}
