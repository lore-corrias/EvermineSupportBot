package eu.evermine.it;

import com.pengrad.telegrambot.TelegramBot;
import eu.evermine.it.configs.yamls.ConfigsYaml;
import eu.evermine.it.configs.yamls.LanguageYaml;
import eu.evermine.it.configs.yamls.StaffChatYaml;
import eu.evermine.it.updateshandlers.UpdatesDispatcher;
import eu.evermine.it.updateshandlers.handlers.CallbacksDispatcher;
import eu.evermine.it.updateshandlers.handlers.CommandDispatcher;
import eu.evermine.it.updateshandlers.handlers.GroupJoinHandler;
import eu.evermine.it.updateshandlers.handlers.MessagesHandler;
import eu.evermine.it.wrappers.ConfigsWrapper;
import eu.evermine.it.wrappers.LanguageWrapper;
import eu.evermine.it.wrappers.StaffChatWrapper;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.util.List;

public class EvermineSupportBot {

    /**
     * Il token del bot.
     *
     */
    private String botToken;
    /**
     * L'username del bot.
     *
     */
    private String botUsername;
    /**
     * Istanza della classe {@link StaffChatYaml}, per gestire la staff chat.
     *
     */
    private LanguageWrapper languageWrapper;
    private ConfigsWrapper configsWrapper;
    private StaffChatWrapper staffChatWrapper;
    /**
     * Istanza della classe {@link LanguageYaml}, per gestire i messaggi.
     *
     */
    private LanguageYaml languageYaml;
    /**
     * Istanza della classe {@link ConfigsYaml}, per gestire le configurazioni principali.
     *
     */
    private ConfigsYaml configsYaml;
    private TelegramBot telegramBot;

    /**
     * Logger.
     */
    private final Logger LOGGER = LoggerFactory.getLogger(EvermineSupportBot.class);


    private EvermineSupportBot() {
        this(null, null);
    }

    /**
     * Costruttore iniziale del bot, inizializza le variabili con le informazioni vitali.
     *
     * @param botToken Token del bot, fornito da BotFather.
     * @param botUsername Username del bot, fornito da BotFather.
     */
    private EvermineSupportBot(@Nullable String botToken, @Nullable String botUsername) {
        try {
            this.languageWrapper = LanguageWrapper.getInstance();
            this.configsWrapper = ConfigsWrapper.getInstance();
            this.staffChatWrapper = StaffChatWrapper.getInstance();
            getLogger().debug("Configurazioni inizializzate.");

            if(botToken == null && botUsername == null) {
                botToken = configsWrapper.getBotToken();
                botUsername = configsWrapper.getBotUsername();
                if(botToken == null || botUsername == null)
                    throw new IllegalArgumentException("Token e username del bot non forniti.");
            }

            this.telegramBot = new TelegramBot(getBotToken());
            getLogger().debug("API configurate.");

            UpdatesDispatcher updatesDispatcher = new UpdatesDispatcher(this);
            updatesDispatcher.registerUpdateHandler(UpdatesDispatcher.MessageUpdateTypes.COMMAND, new CommandDispatcher(getLogger(), getLanguage(), getConfigs(), getStaffChat()));
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

    public ConfigsWrapper getConfigs() {
        return configsWrapper;
    }

    public StaffChatWrapper getStaffChat() {
        return staffChatWrapper;
    }

    public LanguageWrapper getLanguage() {
        return languageWrapper;
    }

    public TelegramBot getTelegramBot() {
        return telegramBot;
    }
}
