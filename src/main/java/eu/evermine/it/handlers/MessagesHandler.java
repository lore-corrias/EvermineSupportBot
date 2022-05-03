package eu.evermine.it.handlers;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import eu.evermine.it.EvermineSupportBot;
import eu.evermine.it.configs.yamls.ConfigsYaml;
import eu.evermine.it.configs.yamls.LanguageYaml;
import eu.evermine.it.configs.yamls.StaffChatYaml;
import eu.evermine.it.helpers.ActionsAPIHelper;
import eu.evermine.it.updatesdispatcher.handlers.GenericUpdateHandler;

import java.util.List;

public class MessagesHandler extends GenericUpdateHandler {


    public MessagesHandler() {
    }

    @Override
    public boolean handleUpdate(Update update) {
        if (!update.message().chat().type().equals(Chat.Type.Private) && !update.message().chat().id().equals(ConfigsYaml.getAdminGroupID()))
            return true;
        if (ConfigsYaml.getAdmins().isEmpty())
            return true;

        if (ConfigsYaml.isAdmin(update.message().from().id()) && !StaffChatYaml.isInChatUser(update.message().from().id())) {
            if (update.message().replyToMessage() != null && update.message().replyToMessage().from().username().equals(ConfigsYaml.getBotUsername())) {
                if (update.message().replyToMessage().replyMarkup() != null) {
                    InlineKeyboardMarkup keyboardMarkup = update.message().replyToMessage().replyMarkup();
                    String button = keyboardMarkup.inlineKeyboard()[0][0].callbackData();
                    if (button.startsWith("reply-message-chat")) {
                        String chatId = button.split(" ")[1];
                        String messageId = button.split(" ")[2];
                        try {
                            ActionsAPIHelper.sendMessage(LanguageYaml.getLanguageString("staff-chat-response"), Long.parseLong(chatId), Integer.parseInt(messageId));
                            ActionsAPIHelper.forwardMessage(update.message().chat().id(), Long.parseLong(chatId), Integer.parseInt(messageId));
                        } catch (NumberFormatException e) {
                            EvermineSupportBot.logger.error(LanguageYaml.getLanguageString("error-send-staff-chat-response"), e);
                        }
                    }
                }
            }
        } else if (!ConfigsYaml.isAdmin(update.message().from().id()) && StaffChatYaml.isInChatUser(update.message().chat().id())) {
            InlineKeyboardMarkup keyboardMarkup = LanguageYaml.getKeyboard("staff-chat-response-keyboard", null, List.of(update.message().chat().id().toString(), update.message().messageId().toString()));
            if (keyboardMarkup == null) {
                EvermineSupportBot.logger.error(LanguageYaml.getLanguageString("not-matching-buttons"));
            }

            if (ConfigsYaml.isAdminGroupSet()) {
                ActionsAPIHelper.sendMessage(LanguageYaml.getLanguageString("message-chat-staff-incoming", List.of(update.message().from().firstName(), update.message().from().id().toString())), ConfigsYaml.getAdminGroupID(), null, keyboardMarkup);
                ActionsAPIHelper.forwardMessage(ConfigsYaml.getAdminGroupID(), update.message().chat().id(), update.message().messageId());
            } else {
                for (Long adminId : ConfigsYaml.getAdmins()) {
                    ActionsAPIHelper.sendMessage(LanguageYaml.getLanguageString("message-chat-staff-incoming", List.of(update.message().from().firstName(), update.message().from().id().toString())), adminId, null, keyboardMarkup);
                    ActionsAPIHelper.forwardMessage(adminId, update.message().chat().id(), update.message().messageId());
                }
            }
        }
        return true;
    }
}
