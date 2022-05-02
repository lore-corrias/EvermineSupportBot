package eu.evermine.it.updatesdispatcher;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Update;
import eu.evermine.it.EvermineSupportBot;
import eu.evermine.it.helpers.ActionsAPIHelper;
import eu.evermine.it.updatesdispatcher.handlers.AbstractUpdateHandler;
import eu.evermine.it.updatesdispatcher.handlers.GenericUpdateHandler;
import eu.evermine.it.updatesdispatcher.handlers.SpecificUpdateHandler;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class UpdatesDispatcher extends AbstractUpdateDispatcher {

    public interface UpdateTypes {
        String getMethodName();
    }

    @Override
    public boolean handleUpdate(Update update) {
        UpdateTypes updateType = this.getUpdateType(update);
        AbstractUpdateHandler updateHandler = this.getUpdateHandler(updateType);
        if (updateType == null) {
            evermineSupportBot.getLogger().error("Update non riconosciuto: " + update.toString());
        }
        if (updateHandler != null) {
            return updateHandler.handleUpdate(update);
        }
        return false;
    }

    public enum GenericUpdateTypes implements UpdateTypes {
        EDITED_MESSAGE("editedMessage"),
        CALLBACK_QUERY("callbackQuery"),
        CHANNEL_POST("channelPost"),
        CHAT_JOIN_REQUEST("chatJoinRequest"),
        CHAT_MEMBER_UPDATED("chatMember"),
        CHOSEN_INLINE_RESULT("chosenInlineResult"),
        EDITED_CHANNEL_POST("editedChannelPost"),
        INLINE_QUERY("inlineQuery"),
        POLL_ANSWER("pollAnswer"),
        PRE_SHIPPING_QUERY("preShippingQuery"),
        CHECKOUT_QUERY("checkoutQuery");

        private final String methodName;


        GenericUpdateTypes(String methodName) {
            this.methodName = methodName;
        }

        public String getMethodName() {
            return methodName;
        }
    }

    private final HashMap<UpdateTypes, GenericUpdateHandler> genericUpdatesHandlers = new LinkedHashMap<>();
    private final HashMap<UpdateTypes, SpecificUpdateHandler<?>> specificUpdatesHandlers = new LinkedHashMap<>();
    private AbstractUpdateHandler defaultUpdatesHandler;


    /**
     * Istanza di EvermineSupportBot.
     */
    private final EvermineSupportBot evermineSupportBot;

    /**
     * Costruttore per l'handler CommandsHandler, che gestisce i comandi in arrivo.
     *
     * @param evermineSupportBot Istanza della classe EvermineSupportBot.
     */
    public UpdatesDispatcher(EvermineSupportBot evermineSupportBot) throws IllegalAccessException {
        this.evermineSupportBot = evermineSupportBot;

        ActionsAPIHelper.setTelegramBotInstance(evermineSupportBot.getTelegramBot());
    }

    @Nullable
    private UpdateTypes getUpdateType(Update update) {
        try {
            if (update.message() != null) {
                for (MessageUpdateTypes updateType : MessageUpdateTypes.values()) {
                    boolean check;
                    if (updateType == MessageUpdateTypes.PRIVATE_MESSAGE) {
                        check = update.message().chat().type().equals(Chat.Type.Private);
                    } else if (updateType == MessageUpdateTypes.GROUP_MESSAGE) {
                        check = update.message().chat().type().equals(Chat.Type.group);
                    } else if (updateType == MessageUpdateTypes.SUPERGROUP_MESSAGE) {
                        check = update.message().chat().type().equals(Chat.Type.supergroup);
                    } else if (updateType == MessageUpdateTypes.COMMAND) {
                        check = update.message().text().startsWith("/");
                    } else {
                        check = updateMatchesType(update, updateType);
                    }
                    if (check)
                        return updateType;
                }
            } else {
                for (GenericUpdateTypes updateType : GenericUpdateTypes.values()) {
                    if (updateMatchesType(update, updateType))
                        return updateType;
                }
            }
        } catch (Exception e) {
            evermineSupportBot.getLogger().error("", e);
        }
        return null;
    }

    private boolean updateMatchesType(Update update, UpdateTypes updateType) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String propertyName = updateType.getMethodName();
        boolean check;
        if (updateType instanceof MessageUpdateTypes) {
            check = update.message().getClass().getMethod(propertyName).invoke(update.message()) != null;
        } else {
            check = update.getClass().getMethod(propertyName).invoke(update) != null;
        }
        return check;
    }

    public void registerUpdateHandler(UpdateTypes updateType, AbstractUpdateHandler updateHandler) {
        if (updateHandler instanceof GenericUpdateHandler) {
            this.genericUpdatesHandlers.putIfAbsent(updateType, (GenericUpdateHandler) updateHandler);
        } else if (updateHandler instanceof SpecificUpdateHandler<?>) {
            this.specificUpdatesHandlers.putIfAbsent(updateType, (SpecificUpdateHandler<?>) updateHandler);
        }
    }

    public @Nullable AbstractUpdateHandler getUpdateHandler(UpdateTypes updateType) {
        if (!hasUpdateHandler(updateType) && !hasDefaultHandler()) {
            return null;
        } else if (hasDefaultHandler()) {
            return this.defaultUpdatesHandler;
        }
        if (this.genericUpdatesHandlers.containsKey(updateType)) {
            return this.genericUpdatesHandlers.get(updateType);
        }
        return this.specificUpdatesHandlers.get(updateType);
    }

    public void registerUpdateHandler(List<UpdateTypes> types, AbstractUpdateHandler updateHandler) {
        types.forEach(type -> this.registerUpdateHandler(type, updateHandler));
    }

    public void registerDefaultUpdatesHandler(AbstractUpdateHandler updateHandler) {
        if (defaultUpdatesHandler == null)
            defaultUpdatesHandler = updateHandler;
    }

    public enum MessageUpdateTypes implements UpdateTypes {
        POLL,
        PHOTO,
        VIDEO,
        ANIMATION,
        AUDIO,
        DICE,
        INVOICE,
        STICKER,
        CHANNEL_CHAT_CREATED,
        CONTACT,
        DELETE_CHAT_PHOTO,
        FORWARD_FROM,
        GAME,
        GROUP_CHAT_CREATED,
        LEFT_CHAT_MEMBER,
        MESSAGE_AUTO_DELETE_TIMER_CHANGED,
        MIGRATE_FROM_CHAT_ID,
        NEW_CHAT_PHOTO,
        NEW_CHAT_TITLE,
        PASSPORT_DATA,
        PINNED_MESSAGE,
        PROXIMITY_ALERT_TRIGGERED,
        SUCCESSFUL_PAYMENT,
        SUPERGROUP_CHAT_CREATED,
        VENUE,
        VIDEO_NOTE,
        WEB_APP_DATA,
        COMMAND,
        PRIVATE_MESSAGE,
        GROUP_MESSAGE,
        SUPERGROUP_MESSAGE;

        public static List<UpdateTypes> getMediaUpdates() {
            return Arrays.asList(POLL, PHOTO, VIDEO, ANIMATION, AUDIO, DICE,
                    INVOICE, STICKER, VIDEO_NOTE, CONTACT, FORWARD_FROM, GAME,
                    PRIVATE_MESSAGE, GROUP_MESSAGE, SUPERGROUP_MESSAGE
            );
        }

        public String getMethodName() {
            StringBuilder sb = new StringBuilder();
            String[] split = this.name().split("_");
            sb.append(split[0].toLowerCase());
            if (split.length == 1)
                return sb.toString().toLowerCase();
            for (int i = 1; i < split.length; i++) {
                sb.append(split[i].substring(0, 1).toUpperCase()).append(split[i].substring(1).toLowerCase());
            }
            return sb.toString();
        }
    }

    private boolean hasUpdateHandler(UpdateTypes updateType) {
        return this.genericUpdatesHandlers.containsKey(updateType) || specificUpdatesHandlers.containsKey(updateType);
    }

    private boolean hasDefaultHandler() {
        return this.defaultUpdatesHandler != null;
    }

    public void runUpdateListener() {
        evermineSupportBot.getTelegramBot().setUpdatesListener(this);
    }
}
