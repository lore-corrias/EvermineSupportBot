package eu.evermine.it.updateshandlers.handlers.commands.staffchat;

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

public class PardonChat implements IBotCommand {

    private final Logger logger;
    private final LanguageWrapper language;
    private final ConfigsWrapper configs;
    private final StaffChatWrapper staffChat;


    public PardonChat(Logger logger, LanguageWrapper language, ConfigsWrapper configs, StaffChatWrapper staffChatWrapper) {
        this.logger = logger;
        this.language = language;
        this.configs = configs;
        this.staffChat = staffChatWrapper;
    }

    @Override
    public String getCommandIdentifier() {
        return "pardonchat";
    }

    @Override
    public String getDescription() {
        return "Unbanna un utente dalla chat.";
    }

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] arguments) {
        if(!configs.isAdmin(message.getFrom().getId()))
            return;
        SendMessage sendMessage = new SendMessage();
        sendMessage.setDisableWebPagePreview(true);
        sendMessage.setChatId(String.valueOf(message.getChatId()));
        sendMessage.setParseMode("HTML");

        if(arguments.length != 1) {
            sendMessage.setText(language.getLanguageString(LanguageYaml.LANGUAGE_INDEXES.PARDON_CHAT_SYNTAX));
        } else {
            try {
                Long userId = Long.parseLong(arguments[0]);
                try {
                    if(staffChat.isBannedUser(userId)) {
                        SendMessage userMessage = new SendMessage();
                        userMessage.setDisableWebPagePreview(true);
                        userMessage.setChatId(String.valueOf(userId));
                        userMessage.setText(language.getLanguageString(LanguageYaml.LANGUAGE_INDEXES.PARDON_CHAT_MESSAGE));
                        userMessage.setParseMode("HTML");
                        absSender.execute(userMessage);

                        if(staffChat.isUserInChat(userId))
                            staffChat.removeInChatUser(userId);
                        staffChat.removeBannedUser(userId);
                        sendMessage.setText(language.getLanguageString(LanguageYaml.LANGUAGE_INDEXES.PARDON_CHAT_SUCCESS));
                    } else {
                        sendMessage.setText(language.getLanguageString(LanguageYaml.LANGUAGE_INDEXES.PARDON_CHAT_NOT_BANNED));
                    }
                } catch (IOException e) {
                    logger.error(language.getLanguageString(LanguageYaml.LANGUAGE_INDEXES.ERROR_REMOVE_BANNED_USER), e);
                } catch (TelegramApiException e) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {
                sendMessage.setText(language.getLanguageString(LanguageYaml.LANGUAGE_INDEXES.PARDON_CHAT_SYNTAX));
            }
        }

        try {
            absSender.execute(sendMessage);
        } catch (TelegramApiException e) {
            logger.error(language.getLanguageString(LanguageYaml.LANGUAGE_INDEXES.ERROR_SENDING_CHAT_COMMAND_MESSAGE), e);
        }
    }
}
