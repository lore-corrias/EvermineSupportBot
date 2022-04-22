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

public class EndChat implements IBotCommand {

    private final Logger logger;
    private final LanguageWrapper language;
    private final ConfigsWrapper configs;
    private final StaffChatWrapper staffChat;



    public EndChat(Logger logger, LanguageWrapper language, ConfigsWrapper configs, StaffChatWrapper staffChatWrapper) {
        this.logger = logger;
        this.language = language;
        this.configs = configs;
        this.staffChat = staffChatWrapper;
    }

    @Override
    public String getCommandIdentifier() {
        return "endchat";
    }

    @Override
    public String getDescription() {
        return "Termina la chat di un utente.";
    }

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] arguments) {
        try {
            if(!configs.isAdmin(message.getFrom().getId()))
                return; // TODO: magari inserire un messaggio "permessi insufficienti"
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(message.getChatId().toString());
            sendMessage.setReplyToMessageId(message.getMessageId());
            sendMessage.setParseMode("HTML");
            if(arguments.length != 1) {
                sendMessage.setText(language.getLanguageString(LanguageYaml.LANGUAGE_INDEXES.END_CHAT_SYNTAX));
            } else {
                try {
                    Long userID = Long.parseLong(arguments[0]);
                    if(!staffChat.isUserInChat(userID)) {
                        sendMessage.setText(language.getLanguageString(LanguageYaml.LANGUAGE_INDEXES.END_CHAT_USER_NOT_IN_CHAT));
                    } else {
                        staffChat.removeInChatUser(userID);
                        sendMessage.setText(language.getLanguageString(LanguageYaml.LANGUAGE_INDEXES.END_CHAT_USER_REMOVED));
                    }
                    SendMessage sendMessage1 = new SendMessage();
                    sendMessage1.setChatId(userID.toString());
                    sendMessage1.setText(language.getLanguageString(LanguageYaml.LANGUAGE_INDEXES.END_CHAT_USER_REMOVED_BY_ADMIN));
                    absSender.execute(sendMessage1);
                } catch (NumberFormatException e) {
                    sendMessage.setText(language.getLanguageString(LanguageYaml.LANGUAGE_INDEXES.END_CHAT_USER_NOT_IN_CHAT));
                } catch (IOException e) {
                    sendMessage.setText(language.getLanguageString(LanguageYaml.LANGUAGE_INDEXES.ERROR_REMOVING_USER_FROM_STAFF_CHAT_FILE));
                    logger.error(language.getLanguageString(LanguageYaml.LANGUAGE_INDEXES.ERROR_REMOVING_USER_FROM_STAFF_CHAT_FILE), e);
                }
                absSender.execute(sendMessage);

            }
        } catch (TelegramApiException e) {
            logger.error(language.getLanguageString(LanguageYaml.LANGUAGE_INDEXES.ERROR_SENDING_CHAT_COMMAND_MESSAGE), e);
        }
    }
}
