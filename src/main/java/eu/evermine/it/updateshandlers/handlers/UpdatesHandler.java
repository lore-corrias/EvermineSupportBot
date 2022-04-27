package eu.evermine.it.updateshandlers.handlers;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Update;
import eu.evermine.it.EvermineSupportBot;
import eu.evermine.it.updateshandlers.AbstractUpdateHandler;
import eu.evermine.it.updateshandlers.handlers.commands.ReloadCommand;
import eu.evermine.it.updateshandlers.handlers.commands.StartCommand;
import eu.evermine.it.updateshandlers.handlers.commands.staffchat.BanChat;
import eu.evermine.it.updateshandlers.handlers.commands.staffchat.EndChat;
import eu.evermine.it.updateshandlers.handlers.commands.staffchat.PardonChat;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class UpdatesHandler extends AbstractUpdateHandler {

    private interface UpdateTypes {
        public String getMethodName();
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
        FORWARD,
        GAME,
        GROUP_CHAT_CREATED,
        LEFT_CHAT_MEMBER,
        MESSAGE_AUTO_DELETE_TIMER_CHANGED,
        MIGRATE,
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
        PRIVATE_MESSAGE,
        GROUP_MESSAGE,
        SUPERGROUP_MESSAGE;

        public String getMethodName() {
            StringBuilder sb = new StringBuilder();
            String[] split = this.name().split("_");
            sb.append(split[0]);
            for (int i = 1; i <= split.length; i++) {
                sb.append(split[i].substring(0, 1).toUpperCase()).append(split[i].substring(1));
            }
            return sb.toString();
        }

        public static List<MessageUpdateTypes> getMediaUpdates() {
            return Arrays.asList(POLL, PHOTO, VIDEO, ANIMATION, AUDIO, DICE,
                    INVOICE, STICKER, VIDEO_NOTE, CONTACT, FORWARD, GAME,
                    PRIVATE_MESSAGE, GROUP_MESSAGE, SUPERGROUP_MESSAGE
            );
        }
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

        this.registerUpdateHandler(GenericUpdateTypes.CALLBACK_QUERY, new CallbacksHandler(evermineSupportBot.getLogger(), evermineSupportBot.getLanguage(), evermineSupportBot.getStaffChat()));
        this.registerUpdateHandler(List.of(MessageUpdateTypes.GROUP_CHAT_CREATED, MessageUpdateTypes.SUPERGROUP_CHAT_CREATED), new GroupJoinHandler(evermineSupportBot.getLogger(), evermineSupportBot.getLanguage(), evermineSupportBot.getConfigs()));
        this.registerUpdateHandler(MessageUpdateTypes.getMediaUpdates(), new MessagesHandler(evermineSupportBot.getLogger(), evermineSupportBot.getLanguage(), evermineSupportBot.getConfigs(), evermineSupportBot.getStaffChat()));
    }

    @Override
    public boolean handleUpdate(Update update) {
        UpdateTypes updateType = this.getUpdateType(update);
        if(this.hasUpdateHandler(updateType)) {
            return this.updatesHandlers.get(updateType).handleUpdate(update);
        } else if(this.hasDefaultHandler()) {
            return this.defaultHandler.handleUpdate(update);
        } else if(updateType != null) {
            evermineSupportBot.getLogger().info("Update non gestito: " + update);
        }
        return false;
    }

    @Nullable
    private UpdateTypes getUpdateType(Update update) {
        try {
            if(update.message() != null) {
                for(MessageUpdateTypes updateType : MessageUpdateTypes.values()) {
                    boolean check = updateMatchesType(update, updateType);
                    if(check) {
                        if(updateType == MessageUpdateTypes.PRIVATE_MESSAGE) {
                            check = update.message().chat().type().equals(Chat.Type.Private);
                        } else if(updateType == MessageUpdateTypes.GROUP_MESSAGE) {
                            check = update.message().chat().type().equals(Chat.Type.group);
                        } else if(updateType == MessageUpdateTypes.SUPERGROUP_MESSAGE) {
                            check = update.message().chat().type().equals(Chat.Type.supergroup);
                        }
                    }
                    if(check)
                        return updateType;
                }
            } else {
                for(GenericUpdateTypes updateType : GenericUpdateTypes.values()) {
                    if(updateMatchesType(update, updateType))
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
        Method method = update.getClass().getMethod(propertyName);
        return method.invoke(update) != null;
    }

    public void registerUpdateHandler(List<? extends UpdateTypes> updateTypes, AbstractUpdateHandler abstractUpdateHandler) {
        updateTypes.forEach(updateType -> this.registerUpdateHandler(updateType, abstractUpdateHandler));
    }

    public void registerUpdateHandler(UpdateTypes updateType, AbstractUpdateHandler abstractUpdateHandler) {
        if(!this.updatesHandlers.containsKey(updateType))
            this.updatesHandlers.put(updateType, abstractUpdateHandler);
    }

    private boolean hasUpdateHandler(UpdateTypes updateType) {
        return this.updatesHandlers.containsKey(updateType);
    }

    private boolean hasDefaultHandler() {
        return this.defaultHandler != null;
    }
}
