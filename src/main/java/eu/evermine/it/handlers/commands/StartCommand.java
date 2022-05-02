package eu.evermine.it.handlers.commands;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import eu.evermine.it.configs.yamls.LanguageYaml;
import eu.evermine.it.configs.yamls.StaffChatYaml;
import eu.evermine.it.handlers.models.AbstractCommand;
import eu.evermine.it.helpers.ActionsAPIHelper;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.List;

public class StartCommand extends AbstractCommand {

    /**
     * Logger del bot.
     */
    private final Logger logger;
    /**
     * Istanza della classe {@link eu.evermine.it.configs.yamls.StaffChatYaml}.
     */
    private final StaffChatYaml staffChat;
    /**
     * Istanza della classe {@link eu.evermine.it.configs.yamls.LanguageYaml}.
     */
    private final LanguageYaml language;

    /**
     * Costruttore della classe.
     *
     * @param logger    Logger del bot.
     * @param staffChat Istanza di {@link StaffChatYaml}.
     */
    public StartCommand(Logger logger, LanguageYaml language, StaffChatYaml staffChat) {

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
        text.append(language.getLanguageString("start-message", List.of(user)));

        // Creo la tastiera inline.
        InlineKeyboardMarkup inlineKeyboardMarkup = language.getKeyboard("start-keyboard");
        if (inlineKeyboardMarkup == null) {
            logger.error(language.getLanguageString("not-matching-buttons"));
        }

        // Invio il messaggio.
        ActionsAPIHelper.sendMessage(text.toString(), getCommandChatId(update), getCommandMessageId(update), inlineKeyboardMarkup);
        return true;
    }

    @Override
    public String getCommandUsage() {
        return "/start";
    }
}
