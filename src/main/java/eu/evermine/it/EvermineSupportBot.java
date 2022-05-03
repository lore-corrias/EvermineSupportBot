package eu.evermine.it;

import com.pengrad.telegrambot.TelegramBot;
import eu.evermine.it.configs.YamlManager;
import eu.evermine.it.configs.yamls.ConfigsYaml;
import eu.evermine.it.configs.yamls.LanguageYaml;
import eu.evermine.it.configs.yamls.StaffChatYaml;
import eu.evermine.it.handlers.CallbacksDispatcher;
import eu.evermine.it.handlers.CommandDispatcher;
import eu.evermine.it.handlers.GroupJoinHandler;
import eu.evermine.it.handlers.MessagesHandler;
import eu.evermine.it.updatesdispatcher.UpdatesDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public class EvermineSupportBot {

    /**
     * Logger.
     */
    public static final Logger logger = LoggerFactory.getLogger(EvermineSupportBot.class);


    /**
     * Costruttore iniziale del bot, inizializza le variabili con le informazioni vitali.
     */
    private EvermineSupportBot() {
        try {
            loadConfigurations();
            EvermineSupportBot.logger.debug("Configurazioni inizializzate.");

            if (ConfigsYaml.getBotToken().isEmpty() || ConfigsYaml.getBotUsername().isEmpty()) {
                throw new IllegalArgumentException("Token o username del bot non impostati.");
            }

            EvermineSupportBot.logger.debug("API configurate.");

            UpdatesDispatcher updatesDispatcher = new UpdatesDispatcher();
            updatesDispatcher.registerUpdateHandler(UpdatesDispatcher.MessageUpdateTypes.COMMAND, new CommandDispatcher());
            updatesDispatcher.registerUpdateHandler(UpdatesDispatcher.GenericUpdateTypes.CALLBACK_QUERY, new CallbacksDispatcher());
            updatesDispatcher.registerUpdateHandler(List.of(UpdatesDispatcher.MessageUpdateTypes.GROUP_CHAT_CREATED, UpdatesDispatcher.MessageUpdateTypes.SUPERGROUP_CHAT_CREATED), new GroupJoinHandler());
            updatesDispatcher.registerUpdateHandler(UpdatesDispatcher.MessageUpdateTypes.getMediaUpdates(), new MessagesHandler());

            updatesDispatcher.runUpdateListener(new TelegramBot(ConfigsYaml.getBotToken()));
            EvermineSupportBot.logger.debug("Bot avviato.");
        } catch (IOException | IllegalArgumentException | IllegalAccessException e) {
            EvermineSupportBot.logger.error("", e);
        }
    }

    /**
     * Main method.
     *
     * @param args Argomenti di avvio del bot. Se avviato correttamente, il primo corrisponde al token del bot, il secondo al suo username e dal terzo in poi la lista degli ID degli admin.
     */
    public static void main(String[] args) {
        new EvermineSupportBot();
    }

    public static void loadConfigurations() throws IOException {
        YamlManager.getInstance().loadYaml(LanguageYaml.class);
        if (!LanguageYaml.getInlineKeyboards().isEmpty())
            LanguageYaml.getInlineKeyboards().clear(); // pulendo la lista delle tastiere memorizzate
        YamlManager.getInstance().loadYaml(ConfigsYaml.class);
        YamlManager.getInstance().loadYaml(StaffChatYaml.class);
    }
}
