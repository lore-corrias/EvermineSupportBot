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
     * Il token del bot.
     */
    private String botToken;
    /**
     * L'username del bot.
     */
    private String botUsername;
    /**
     * Istanza della classe {@link LanguageYaml}, per gestire i messaggi.
     */
    private LanguageYaml languageYaml;
    /**
     * Istanza della classe {@link ConfigsYaml}, per gestire le configurazioni principali.
     */
    private ConfigsYaml configsYaml;
    private StaffChatYaml staffChatYaml;
    private TelegramBot telegramBot;

    /**
     * Logger.
     */
    private final Logger LOGGER = LoggerFactory.getLogger(EvermineSupportBot.class);


    /**
     * Costruttore iniziale del bot, inizializza le variabili con le informazioni vitali.
     */
    private EvermineSupportBot() {
        try {
            this.languageYaml = (LanguageYaml) YamlManager.getInstance().loadYaml(new LanguageYaml());
            this.configsYaml = (ConfigsYaml) YamlManager.getInstance().loadYaml(new ConfigsYaml());
            this.staffChatYaml = (StaffChatYaml) YamlManager.getInstance().loadYaml(new StaffChatYaml());
            getLogger().debug("Configurazioni inizializzate.");

            if (getConfigs().getBotToken().isEmpty() || getConfigs().getBotUsername().isEmpty()) {
                throw new IllegalArgumentException("Token o username del bot non impostati.");
            }

            this.telegramBot = new TelegramBot(getBotToken());
            getLogger().debug("API configurate.");

            UpdatesDispatcher updatesDispatcher = new UpdatesDispatcher(this);
            updatesDispatcher.registerUpdateHandler(UpdatesDispatcher.MessageUpdateTypes.COMMAND, new CommandDispatcher(getLogger(), getLanguage(), getConfigs(), getStaffChat(), this));
            updatesDispatcher.registerUpdateHandler(UpdatesDispatcher.GenericUpdateTypes.CALLBACK_QUERY, new CallbacksDispatcher(getLogger(), getLanguage(), getStaffChat()));
            updatesDispatcher.registerUpdateHandler(List.of(UpdatesDispatcher.MessageUpdateTypes.GROUP_CHAT_CREATED, UpdatesDispatcher.MessageUpdateTypes.SUPERGROUP_CHAT_CREATED), new GroupJoinHandler(getConfigs()));
            updatesDispatcher.registerUpdateHandler(UpdatesDispatcher.MessageUpdateTypes.getMediaUpdates(), new MessagesHandler(getLogger(), getLanguage(), getConfigs(), getStaffChat()));

            updatesDispatcher.runUpdateListener();
            getLogger().debug("Bot avviato.");
        } catch (IOException | IllegalArgumentException | IllegalAccessException e) {
            this.getLogger().error("", e);
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

    /**
     * Getter per il logger del bot.
     *
     * @return Logger del bot.
     */
    public Logger getLogger() {
        return LOGGER;
    }

    /**
     * Getter per il token del bot.
     *
     * @return Token del bot.
     */
    public String getBotToken() {
        return getConfigs().getBotToken();
    }

    /**
     * Getter per l'username del bot.
     *
     * @return Username del bot.
     */
    public String getBotUsername() {
        return getConfigs().getBotUsername();
    }

    public ConfigsYaml getConfigs() {
        return configsYaml;
    }

    public StaffChatYaml getStaffChat() {
        return staffChatYaml;
    }

    public LanguageYaml getLanguage() {
        return languageYaml;
    }

    public TelegramBot getTelegramBot() {
        return telegramBot;
    }

    public void reloadLanguage() throws IOException {
        YamlManager.getInstance().dumpYaml(languageYaml);
    }

    public void reloadConfigs() throws IOException {
        YamlManager.getInstance().dumpYaml(configsYaml);
    }
}
