package eu.evermine.it.handlers.commands;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import eu.evermine.it.EvermineSupportBot;
import eu.evermine.it.configs.yamls.LanguageYaml;
import eu.evermine.it.configs.yamls.StaffChatYaml;
import eu.evermine.it.handlers.models.AbstractCommand;
import eu.evermine.it.helpers.ActionsAPIHelper;

import java.io.IOException;
import java.util.List;

public class StartCommand extends AbstractCommand {

    /**
     * Costruttore della classe.
     */
    public StartCommand() {
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
            StaffChatYaml.removeInChatUser(getCommandUserId(update));
        } catch (IOException e) {
            EvermineSupportBot.logger.error("", e);
        }

        // Messaggio di benvenuto.
        StringBuilder text = new StringBuilder();
        String user = getCommandUserName(update) == null ? getCommandUserFirstName(update) : "@" + getCommandUserName(update);
        text.append(LanguageYaml.getLanguageString("start-message", List.of(user)));

        // Creo la tastiera inline.
        InlineKeyboardMarkup inlineKeyboardMarkup = LanguageYaml.getKeyboard("start-keyboard");
        if (inlineKeyboardMarkup == null) {
            EvermineSupportBot.logger.error(LanguageYaml.getLanguageString("not-matching-buttons"));
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
