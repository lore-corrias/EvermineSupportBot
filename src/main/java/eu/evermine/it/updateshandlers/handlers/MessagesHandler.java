package eu.evermine.it.updateshandlers.handlers;

import eu.evermine.it.configs.yamls.LanguageYaml;
import eu.evermine.it.updateshandlers.AbstractUpdateHandler;
import eu.evermine.it.wrappers.ConfigsWrapper;
import eu.evermine.it.wrappers.LanguageWrapper;
import eu.evermine.it.wrappers.StaffChatWrapper;
import org.slf4j.Logger;
import org.telegram.telegrambots.meta.api.methods.ForwardMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

public class MessagesHandler extends AbstractUpdateHandler {

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
    public void handleUpdate(Update update) {
        if(!update.getMessage().getChat().isUserChat() && !update.getMessage().getChat().getId().equals(configs.getAdminGroupID()))
            return;
        if(configs.getAdmins().isEmpty())
            return;

        if(configs.isAdmin(update.getMessage().getFrom().getId()) && !staffChat.isUserInChat(update.getMessage().getFrom().getId())) {
            if(update.getMessage().isReply() && update.getMessage().getReplyToMessage().getFrom().getUserName().equals(configs.getBotUsername())) {
                if(update.getMessage().getReplyToMessage().hasReplyMarkup()) {
                    InlineKeyboardMarkup keyboardMarkup = update.getMessage().getReplyToMessage().getReplyMarkup();
                    String button = keyboardMarkup.getKeyboard().get(0).get(0).getCallbackData();
                    if(button.startsWith("reply-message-chat")) {
                        String chatId = button.split(" ")[1];
                        String messageId = button.split(" ")[2];

                        SendMessage sendMessage = new SendMessage();
                        sendMessage.setDisableWebPagePreview(true);
                        sendMessage.setChatId(chatId);
                        try {
                            sendMessage.setReplyToMessageId(Integer.parseInt(messageId));
                            sendMessage.setText(language.getLanguageString(LanguageYaml.LANGUAGE_INDEXES.CHAT_STAFF_RESPONSE));
                            sendMessage.setParseMode("HTML");

                            ForwardMessage forwardMessage = new ForwardMessage();
                            forwardMessage.setFromChatId(String.valueOf(update.getMessage().getChatId()));
                            forwardMessage.setMessageId(update.getMessage().getMessageId());
                            forwardMessage.setChatId(chatId);

                            try {
                                this.execute(sendMessage);
                                this.execute(forwardMessage);
                            } catch (TelegramApiException e) {
                                logger.error(language.getLanguageString(LanguageYaml.LANGUAGE_INDEXES.ERROR_SEND_CHAT_STAFF_RESPONSE), e);
                            }
                        } catch (NumberFormatException e) {
                            logger.error(language.getLanguageString(LanguageYaml.LANGUAGE_INDEXES.ERROR_SEND_CHAT_STAFF_RESPONSE), e);
                        }
                    }
                }
            }
        } else if(!configs.isAdmin(update.getMessage().getFrom().getId()) && staffChat.isUserInChat(update.getMessage().getChat().getId())) {
            SendMessage message = new SendMessage();
            message.setDisableWebPagePreview(true);
            ForwardMessage forwardMessage = new ForwardMessage();
            message.setText(language.getLanguageString(LanguageYaml.LANGUAGE_INDEXES.MESSAGE_CHAT_STAFF_INCOMING, List.of(update.getMessage().getFrom().getFirstName(), update.getMessage().getFrom().getId().toString())));
            message.setParseMode("HTML");
            InlineKeyboardMarkup keyboardMarkup = language.getKeyboard(LanguageYaml.KEYBOARDS_INDEXES.STAFF_CHAT_RESPONSE_KEYBOARD, null, List.of(update.getMessage().getChat().getId().toString(), update.getMessage().getMessageId().toString()));
            if(keyboardMarkup == null) {
                logger.error(language.getLanguageString(LanguageYaml.LANGUAGE_INDEXES.NOT_MATCHING_BUTTONS));
            } else {
                message.setReplyMarkup(keyboardMarkup);
            }

            forwardMessage.setFromChatId(String.valueOf(update.getMessage().getChatId()));
            forwardMessage.setMessageId(update.getMessage().getMessageId());

            if(configs.isAdminGroupSet()) {
                message.setChatId(String.valueOf(configs.getAdminGroupID()));
                forwardMessage.setChatId(String.valueOf(configs.getAdminGroupID()));
                try {
                    this.execute(message);
                    this.execute(forwardMessage);
                } catch (TelegramApiException e) {
                    logger.error(language.getLanguageString(LanguageYaml.LANGUAGE_INDEXES.ERROR_SEND_STAFF_CHAT_RESPONSE), e);
                }
            } else {
                for(Long adminId : configs.getAdmins()) {
                    message.setChatId(String.valueOf(adminId));
                    forwardMessage.setChatId(String.valueOf(adminId));
                    try {
                        this.execute(message);
                        this.execute(forwardMessage);
                    } catch (TelegramApiException e) {
                        logger.error(language.getLanguageString(LanguageYaml.LANGUAGE_INDEXES.ERROR_SEND_STAFF_CHAT_RESPONSE), e);
                    }
                }
            }
        }
    }
}
