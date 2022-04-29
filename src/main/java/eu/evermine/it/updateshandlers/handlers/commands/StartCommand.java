package eu.evermine.it.updateshandlers.handlers.commands;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import eu.evermine.it.configs.yamls.LanguageYaml;
import eu.evermine.it.updateshandlers.handlers.CommandHandler;
import eu.evermine.it.updateshandlers.handlers.models.AbstractCommand;
import eu.evermine.it.wrappers.LanguageWrapper;
import eu.evermine.it.wrappers.StaffChatWrapper;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.List;

public class StartCommand extends AbstractCommand {

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
    public StartCommand(CommandHandler commandHandler, Logger logger, LanguageWrapper language, StaffChatWrapper staffChat) {
        super(commandHandler);

        this.logger = logger;
        this.language = language;
        this.staffChat = staffChat;
    }

    /**
     * Metodo per processare il comando in arrivo.
     *
     * @param update L'update da processare.
     */
    @Override
    public boolean handleUpdate(Update update) {
        // Mi assicuro che l'utente non sia in chat con lo staff
        try {
            staffChat.removeInChatUser(getCommandUserId(update));
        } catch (IOException e) {
            logger.error("", e);
        }

        // Messaggio di benvenuto.
        StringBuilder text = new StringBuilder();
        String user = getCommandUserName(update) == null ? getCommandUserFirstName(update) : "@" + getCommandUserName(update);
        text.append(language.getLanguageString(LanguageYaml.LANGUAGE_INDEXES.START_MESSAGE, List.of(user)));

        // Creo la tastiera inline.
        InlineKeyboardMarkup inlineKeyboardMarkup = language.getKeyboard(LanguageYaml.KEYBOARDS_INDEXES.START_KEYBOARD);
        if (inlineKeyboardMarkup == null) {
            logger.error(language.getLanguageString(LanguageYaml.LANGUAGE_INDEXES.NOT_MATCHING_BUTTONS));
        }

        // Invio il messaggio.
        sendMessage(text.toString(), getCommandChatId(update), getCommandMessageId(update), inlineKeyboardMarkup);
        return true;
    }

    /**
     * Getter del comando.
     *
     * @return Il comando.
     */
    @Override
    public String getCommandName() {
        return "start";
    }

    /**
     * Descrizione del comando.
     *
     * @return La descrizione del comando.
     */
    @Override
    public String getCommandDescription() {
        return "Avvia il bot.";
    }

    @Override
    public String getCommandUsage() {
        return "/start";
    }
}
