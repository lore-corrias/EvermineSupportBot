package eu.evermine.it.updateshandlers.handlers;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import eu.evermine.it.configs.yamls.LanguageYaml;
import eu.evermine.it.helpers.ActionsAPIHelper;
import eu.evermine.it.updateshandlers.handlers.models.handlers.GenericUpdateHandler;
import eu.evermine.it.wrappers.ConfigsWrapper;
import eu.evermine.it.wrappers.LanguageWrapper;
import eu.evermine.it.wrappers.StaffChatWrapper;
import org.slf4j.Logger;

import java.util.List;

public class MessagesHandler extends GenericUpdateHandler {

    private final Logger logger;
    private final LanguageWrapper language;
    private final ConfigsWrapper configs;
    private final StaffChatWrapper staffChat;


    public MessagesHandler(Logger logger, LanguageWrapper language, ConfigsWrapper configs, StaffChatWrapper staffChat) {
        this.logger = logger;
        this.language = language;
        this.configs = configs;
        this.staffChat = staffChat;
    }

    @Override
    public boolean handleUpdate(Update update) {
        if(!update.message().chat().type().equals(Chat.Type.Private) && !update.message().chat().id().equals(configs.getAdminGroupID()))
            return true;
        if(configs.getAdmins().isEmpty())
            return true;

        if(configs.isAdmin(update.message().from().id()) && !staffChat.isUserInChat(update.message().from().id())) {
            if(update.message().replyToMessage() != null && update.message().replyToMessage().from().username().equals(configs.getBotUsername())) {
                if(update.message().replyToMessage().replyMarkup() != null) {
                    InlineKeyboardMarkup keyboardMarkup = update.message().replyToMessage().replyMarkup();
                    String button = keyboardMarkup.inlineKeyboard()[0][0].callbackData();
                    if(button.startsWith("reply-message-chat")) {
                        String chatId = button.split(" ")[1];
                        String messageId = button.split(" ")[2];
                        try {
                            ActionsAPIHelper.sendMessage(language.getLanguageString(LanguageYaml.LANGUAGE_INDEXES.CHAT_STAFF_RESPONSE), Long.parseLong(chatId), Integer.parseInt(messageId));
                            ActionsAPIHelper.forwardMessage(update.message().chat().id(), Long.parseLong(chatId), Integer.parseInt(messageId));
                        } catch (NumberFormatException e) {
                            logger.error(language.getLanguageString(LanguageYaml.LANGUAGE_INDEXES.ERROR_SEND_CHAT_STAFF_RESPONSE), e);
                        }
                    }
                }
            }
        } else if(!configs.isAdmin(update.message().from().id()) && staffChat.isUserInChat(update.message().chat().id())) {
            InlineKeyboardMarkup keyboardMarkup = language.getKeyboard(LanguageYaml.KEYBOARDS_INDEXES.STAFF_CHAT_RESPONSE_KEYBOARD, null, List.of(update.message().chat().id().toString(), update.message().messageId().toString()));
            if(keyboardMarkup == null) {
                logger.error(language.getLanguageString(LanguageYaml.LANGUAGE_INDEXES.NOT_MATCHING_BUTTONS));
            }

            if(configs.isAdminGroupSet()) {
                ActionsAPIHelper.sendMessage(language.getLanguageString(LanguageYaml.LANGUAGE_INDEXES.MESSAGE_CHAT_STAFF_INCOMING, List.of(update.message().from().firstName(), update.message().from().id().toString())), configs.getAdminGroupID(), null, keyboardMarkup);
                ActionsAPIHelper.forwardMessage(configs.getAdminGroupID(), update.message().chat().id(), update.message().messageId());
            } else {
                for(Long adminId : configs.getAdmins()) {
                    ActionsAPIHelper.sendMessage(language.getLanguageString(LanguageYaml.LANGUAGE_INDEXES.MESSAGE_CHAT_STAFF_INCOMING, List.of(update.message().from().firstName(), update.message().from().id().toString())), adminId, null, keyboardMarkup);
                    ActionsAPIHelper.forwardMessage(adminId, update.message().chat().id(), update.message().messageId());
                }
            }
        }
        return true;
    }
}
