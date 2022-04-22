package eu.evermine.it.updateshandlers.handlers.commands;

import eu.evermine.it.configs.yamls.LanguageYaml;
import eu.evermine.it.wrappers.LanguageWrapper;
import eu.evermine.it.wrappers.StaffChatWrapper;
import org.slf4j.Logger;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.util.List;

public class StartCommand implements IBotCommand {

    /**
     * Logger del bot.
     */
    private final Logger logger;
    /**
     * Wrapper della classe {@link eu.evermine.it.configs.yamls.StaffChatYaml}.
     */
    private final StaffChatWrapper staffChat;
    /**
     * Wrapper della classe {@link eu.evermine.it.configs.yamls.LanguageYaml}.
     */
    private final LanguageWrapper language;

    /**
     * Costruttore della classe.
     * 
     * @param logger Logger del bot.
     * @param staffChat Istanza di {@link StaffChatWrapper}.
     */
    public StartCommand(Logger logger, LanguageWrapper language, StaffChatWrapper staffChat) {
        this.logger = logger;
        this.language = language;
        this.staffChat = staffChat;
    }

    /**
     * Metodo per processare il comando in arrivo.
     *
     * @param absSender Istanza di AbsSender, che contiene il metodo per inviare la risposta al comando.
     * @param message Isntanza di Message, che contiene le informazioni del messaggio ricevuto.
     * @param arguments Array contenente i parametri del comando.
     */
    @Override
    public void processMessage(AbsSender absSender, Message message, String[] arguments) {
        // Mi assicuro che l'utente non sia in chat con lo staff
        try {
            staffChat.removeInChatUser(message.getFrom().getId());
        } catch (IOException e) {
            logger.error("", e);
        }

        // Messaggio di benvenuto.
        StringBuilder sb = new StringBuilder();
        String user = message.getFrom().getUserName().isEmpty() ? message.getFrom().getFirstName() : "@" + message.getFrom().getUserName();
        sb.append(language.getLanguageString(LanguageYaml.LANGUAGE_INDEXES.START_MESSAGE, List.of(user)));

        // Imposto le informazioni relative al messaggio da inviare.
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(sb.toString());
        sendMessage.enableHtml(true);
        sendMessage.setChatId(message.getFrom().getId().toString());

        // Creo la tastiera inline.
        InlineKeyboardMarkup inlineKeyboardMarkup = language.getKeyboard(LanguageYaml.KEYBOARDS_INDEXES.START_KEYBOARD);
        if (inlineKeyboardMarkup != null) {
            sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        } else {
            logger.error(language.getLanguageString(LanguageYaml.LANGUAGE_INDEXES.NOT_MATCHING_BUTTONS));
        }

        // Invio il messaggio.
        try {
            absSender.execute(sendMessage);
        } catch (TelegramApiException e) {
            logger.error(language.getLanguageString(LanguageYaml.LANGUAGE_INDEXES.ERROR_SENDING_START_MESSAGE), e);
        }
    }

    /**
     * Getter del comando.
     *
     * @return Il comando.
     */
    @Override
    public String getCommandIdentifier() {
        return "start";
    }

    /**
     * Descrizione del comando.
     *
     * @return La descrizione del comando.
     */
    @Override
    public String getDescription() {
        return "Avvia il bot.";
    }
}
