package eu.evermine.it.updateshandlers.handlers.commands.staffachat;

import eu.evermine.it.configs.yamls.LanguageYaml;
import eu.evermine.it.wrappers.ConfigsWrapper;
import eu.evermine.it.wrappers.LanguageWrapper;
import eu.evermine.it.wrappers.StaffChatWrapper;
import org.slf4j.Logger;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;

public class BanChat implements IBotCommand {

    private final Logger logger;
    private final LanguageWrapper language;
    private final ConfigsWrapper configs;
    private final StaffChatWrapper staffChat;


    public BanChat(Logger logger, LanguageWrapper language, ConfigsWrapper configs, StaffChatWrapper staffChatWrapper) {
        this.logger = logger;
        this.language = language;
        this.configs = configs;
        this.staffChat = staffChatWrapper;
    }

    @Override
    public String getCommandIdentifier() {
        return "banchat";
    }

    @Override
    public String getDescription() {
        return "Banna un utente dalla chat.";
    }

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] arguments) {
        if (!configs.isAdmin(message.getFrom().getId()))
            return;
        SendMessage sendMessage = new SendMessage();
        sendMessage.setDisableWebPagePreview(true);
        sendMessage.setDisableWebPagePreview(true);
        sendMessage.setChatId(String.valueOf(message.getChatId()));
        sendMessage.setReplyToMessageId(message.getMessageId());
        sendMessage.setParseMode("HTML");
        if (arguments.length != 1) {
            sendMessage.setText(language.getLanguageString(LanguageYaml.LANGUAGE_INDEXES.BAN_CHAT_SYNTAX));
            return;
        }
        try {
            Long userId = Long.parseLong(arguments[0]);
            try {
                if(staffChat.isBannedUser(userId)) {
                    sendMessage.setText(language.getLanguageString(LanguageYaml.LANGUAGE_INDEXES.BAN_CHAT_ALREADY_BANNED));
                } else {
                    SendMessage userMessage = new SendMessage();
                    sendMessage.setDisableWebPagePreview(true);
                    userMessage.setChatId(String.valueOf(userId));
                    userMessage.setText(language.getLanguageString(LanguageYaml.LANGUAGE_INDEXES.USER_BANNED_MESSAGE));
                    userMessage.setParseMode("HTML");
                    absSender.execute(userMessage);

                    if(staffChat.isUserInChat(userId))
                        staffChat.removeInChatUser(userId);
                    staffChat.addBannedUser(userId);
                    sendMessage.setText(language.getLanguageString(LanguageYaml.LANGUAGE_INDEXES.BAN_CHAT_SUCCESS));
                }
            } catch (TelegramApiException e) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            sendMessage.setText(language.getLanguageString(LanguageYaml.LANGUAGE_INDEXES.BAN_CHAT_SYNTAX));
        } catch (IOException e) {
            sendMessage.setText(language.getLanguageString(LanguageYaml.LANGUAGE_INDEXES.ERROR_ADD_BANNED_USERS));
            logger.error(language.getLanguageString(LanguageYaml.LANGUAGE_INDEXES.ERROR_ADD_BANNED_USERS), e);
        }
        try {
            absSender.execute(sendMessage);
        } catch (TelegramApiException e) {
            logger.error(language.getLanguageString(LanguageYaml.LANGUAGE_INDEXES.ERROR_SENDING_CHAT_COMMAND_MESSAGE), e);
        }
    }
}
