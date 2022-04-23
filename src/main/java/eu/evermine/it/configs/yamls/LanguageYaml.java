package eu.evermine.it.configs.yamls;



import eu.evermine.it.configs.YamlManager;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import javax.annotation.Nullable;
import java.io.InputStream;
import java.util.*;

/**
 * Rappresentazione del file Yaml "language.yml". Contiene i messaggi utilizzati dal bot.
 * Essendo la rappresentazione di un file Yaml, la classe estende la classe {@link AbstractYaml},
 * implementandone i metodi astratti {@link #getConstructor()}, {@link #checkConfigValidity()} e {@link #getDumpableData()}.
 * Il file di config contiene due indici: "language" e "keyboard". La struttura del file è definita nei commenti di "resources/language.yml".
 * Il primo contiene i messaggi di testo utilizzati dal bot, mentre il secondo i valori delle varie linee delle tastiere inline.
 *
 * @author just
 * @version 1.0
 * @see AbstractYaml
 */
public class LanguageYaml extends AbstractYaml {

    /**
     * Mappa che contiene le {@link InlineKeyboardMarkup} generate dal metodo {@link #getKeyboardFromIndex}, a partire
     * dal valore di {@link #keyboardValues}.
     */
    private final Map<String, InlineKeyboardMarkup> inlineKeyboards = new HashMap<>();
    /**
     * Mappa che rappresenta tutti i dati relativi ai messaggi utilizzati dal bot nel file Yaml.
     * La chiave è l'indice del messaggio, mentre il valore è il messaggio stesso.
     */
    private Map<String, String> languageValues;
    /**
     * Mappa che rappresenta tutti i dati relativi alle linee delle tastiere inline utilizzate dal bot nel file Yaml.
     * La chiave è l'indice della tastiera, mentre il valore è una lista che contiene tutte le righe della tastiera,
     * ognuna delle quali contiene una lista di bottoni. Un bottone è una mappa, che contiene il testo del bottone (indice "text")
     * e il comando associato (indice "callback_data") o eventualmente l'URL (indice "url").
     */
    private Map<String, List<List<Map<String, String>>>> keyboardValues;

    /**
     * Costruttore della classe. Fornisce alla classe super il nome del file di config,
     * in questo caso: "language.yml".
     *
     * @see AbstractYaml#getFilename()
     */
    public LanguageYaml() {
        super("language.yml");
    }

    /**
     * Restituisce la mappa contenente i messaggi. Vedi {@link #languageValues}.
     *
     * @return La mappa contenente i messaggi.
     */
    public Map<String, String> getLanguage() {
        return this.languageValues;
    }

    /**
     * Setter utilizzato dalla classe {@link org.yaml.snakeyaml.Yaml} per caricare i valori
     * del file di configurazione Yaml sulle proprietà della classe.
     * Imposta il valore di {@link #languageValues}.
     *
     * @param language La mappa che contiene i messaggi.
     */
    public void setLanguage(Map<String, String> language) {
        this.languageValues = language;
    }

    /**
     * Restituisce la mappa contenente le varie tastiere inline. Vedi {@link #keyboardValues}.
     *
     * @return La mappa contenente le varie tastiere inline.
     */
    public Map<String, List<List<Map<String, String>>>> getKeyboards() {
        return this.keyboardValues;
    }

    /**
     * Setter utilizzato dalla classe {@link org.yaml.snakeyaml.Yaml} per caricare i valori
     * del file di configurazione Yaml sulle proprietà della classe.
     * Imposta il valore di {@link #keyboardValues}.
     *
     * @param keyboard La mappa che rappresenta le varie tastiere inline..
     */
    public void setKeyboards(Map<String, List<List<Map<String, String>>>> keyboard) {
        this.keyboardValues = keyboard;
    }

    /**
     * Restituisce una tastiera inline a partire dall'indice.
     * Vedi {@link LanguageYaml#getKeyboardFromIndex(KEYBOARDS_INDEXES, List, List)}.
     *
     * @param index L'indice della tastiera da restituire.
     * @return La tastiera inline.
     */
    public @Nullable InlineKeyboardMarkup getKeyboardFromIndex(KEYBOARDS_INDEXES index) {
        return getKeyboardFromIndex(index, null, null);
    }

    /**
     * Restituisce una tastiera inline a partire dall'indice, sostituendo eventuali parametri
     * nel testo dei bottoni della tastiera.
     * Vedi {@link LanguageYaml#getKeyboardFromIndex(KEYBOARDS_INDEXES, List, List)}.
     *
     * @param index         L'indice della tastiera da restituire.
     * @param textArguments La lista di parametri da sostituire nel testo dei bottoni della tastiera.
     * @return La tastiera inline.
     */
    public @Nullable InlineKeyboardMarkup getKeyboardFromIndex(KEYBOARDS_INDEXES index, List<String> textArguments) {
        return getKeyboardFromIndex(index, textArguments, null);
    }

    /**
     * Restituisce una tastiera inline a partire dall'indice, sostituendo eventuali parametri
     * nel testo dei bottoni della tastiera e nel comando/URL associato.
     * Il metodo utilizza la mappa {@link #keyboardValues} per ottenere la tastiera, e sostituisce eventuali
     * parametri all'interno del testo o dei bottoni della tastiera con i valori di {@code textArguments} e {@code urlArguments}.
     * Se la tastiera non contiene parametri, i due parametri del metodo sono null, e vengono ignorati.
     * Se la tastiera non contiene parametri, ed è già stata generata, il suo valore è allora conservato in {@link #inlineKeyboards},
     * e viene restituito da qui.
     *
     * @param index           L'indice della tastiera da restituire.
     * @param textArguments   La lista di parametri da sostituire nel testo dei bottoni della tastiera, null se non ci sono parametri.
     * @param buttonArguments La lista di parametri da sostituire nel comando/URL dei bottoni della tastiera, null se non ci sono parametri.
     * @return La tastiera inline.
     * @throws IllegalArgumentException Se le liste {@code textArguments} e {@code buttonArguments} non sono delle stesse dimensioni.
     */
    public @Nullable InlineKeyboardMarkup getKeyboardFromIndex(KEYBOARDS_INDEXES index, @Nullable List<String> textArguments, @Nullable List<String> buttonArguments) throws IllegalArgumentException {
        String indexString = index.toString();
        if (!this.keyboardValues.containsKey(indexString))
            throw new IllegalArgumentException("Il file di lingua non contiene la chiave per la tastiera " + indexString);
        if (textArguments == null && buttonArguments == null) {
            if (this.inlineKeyboards.containsKey(indexString))
                return this.inlineKeyboards.get(indexString);
        }
        List<List<InlineKeyboardButton>> rows = new LinkedList<>();
        for (int i = 0; i < this.keyboardValues.get(indexString).size(); i++) {
            List<Map<String, String>> row = this.keyboardValues.get(indexString).get(i);
            for (Map<String, String> button : row) {
                if (!button.containsKey("text") || (!button.containsKey("callback_data") && !button.containsKey("url")))
                    throw new IllegalArgumentException("Il file di lingua deve contenere la chiave \"text\" e uno tra \"callback_data\" e \"url\" per ogni riga della tastiera");
                InlineKeyboardButton.InlineKeyboardButtonBuilder inlineKeyboardButton = InlineKeyboardButton.builder();
                inlineKeyboardButton.text(this.replaceArgs(button.get("text"), textArguments));
                if (button.containsKey("callback_data")) {
                    inlineKeyboardButton.callbackData(this.replaceArgs(button.get("callback_data"), buttonArguments));
                } else if (button.containsKey("url")) {
                    inlineKeyboardButton.url(this.replaceArgs(button.get("url"), buttonArguments));
                }
                if (rows.size() <= i)
                    rows.add(new LinkedList<>());
                rows.get(i).add(inlineKeyboardButton.build());
            }
        }
        InlineKeyboardMarkup.InlineKeyboardMarkupBuilder keyboardMarkupBuilder = InlineKeyboardMarkup.builder();
        for (List<InlineKeyboardButton> row : rows) {
            keyboardMarkupBuilder.keyboardRow(row);
        }
        InlineKeyboardMarkup keyboardMarkup = keyboardMarkupBuilder.build();
        if (keyboardMarkup != null)
            this.inlineKeyboards.put(indexString, keyboardMarkup);
        return keyboardMarkup;
    }

    /**
     * Restituisce un messaggio a partire da un index di {@link LANGUAGE_INDEXES}.
     * Vedi {@link #getLanguageFromIndex(LANGUAGE_INDEXES, List)}
     *
     * @param index L'indice del messaggio da restituire.
     * @return Il messaggio.
     */
    public String getLanguageFromIndex(LANGUAGE_INDEXES index) {
        return this.getLanguageFromIndex(index, null);
    }

    /**
     * Restituisce un messaggio a partire da un index di {@link LANGUAGE_INDEXES}, e da una lista di parametri
     * da sostituire nel messaggio, nel caso in cui fosse necessario. Se non sono forniti parametri,
     * allora la lista {@code args} è null, e viene ignorata.
     *
     * @param index L'indice del messaggio da restituire.
     * @param args  La lista di parametri da sostituire nel messaggio, null se non ci sono parametri.
     * @return Il messaggio.
     */
    public String getLanguageFromIndex(LANGUAGE_INDEXES index, @Nullable List<String> args) {
        String s = this.languageValues.get(index.toString());
        if (args != null) {
            s = this.replaceArgs(s, args);
        }
        return s;
    }

    /**
     * Fornita una stringa, sostituisce i parametri forniti in {@code args}.
     * Un parametro è definito all'interno del messaggio tramite l'indice "%s".
     *
     * @param s    La stringa da modificare.
     * @param args La lista di parametri da sostituire.
     * @return La stringa modificata.
     */
    private String replaceArgs(String s, @Nullable List<String> args) {
        if (args == null)
            return s;
        for (String arg : args) {
            s = s.replaceFirst("%s", arg);
        }
        return s;
    }

    /**
     * Restituisce un {@link Constructor} per la classe {@link AbstractYaml}.
     * Il Constructor fornito equivale a quello fornito dal metodo {@link AbstractYaml#getCapitalizedConstructor}}.
     *
     * @return Il Constructor per la classe {@link AbstractYaml}.
     * @see AbstractYaml#getConstructor()
     */
    @Override
    public Constructor getConstructor() {
        return getCapitalizedConstructor(this.getClass());
    }

    /**
     * Verifica la validità del file di config.
     * Il metodo si assicura che i valori definiti nel file Yaml siano del tipo corretto,
     * in caso contrario, il metodo lancia una IllegalArgumentException.
     * La verifica viene effettuata confrontando il file in "configs/language.yml" con il file
     * "resources/language.yml", che è il file contenente i valori dei messaggi di default.
     * In particolare, il metodo verifica che il numero di indici in entrambi i file sia uguale,
     * e in caso contrario lancia una IllegalArgumentException.
     *
     * @throws IllegalArgumentException Se uno dei valori definiti nel file Yaml non è del tipo corretto.
     */
    @Override
    public void checkConfigValidity() throws IllegalArgumentException {
        InputStream defaultConfig = YamlManager.getResource("language.yml");
        Yaml yaml = new Yaml(this.getConstructor());
        LanguageYaml defaultConfigMap = yaml.load(defaultConfig);

        if (defaultConfigMap.getLanguage().keySet().size() > this.getLanguage().keySet().size()) {
            Set<String> diff = defaultConfigMap.getLanguage().keySet();
            diff.removeAll(this.getLanguage().keySet());
            throw new IllegalArgumentException("Il file di lingua non è completo, mancano delle chiavi: " + diff);
        }
        for (String key : this.getLanguage().keySet()) {
            if (!this.getLanguage().containsKey(key))
                throw new IllegalArgumentException("Il file di lingua non è completo, manca la chiave: " + key);
        }
    }

    /**
     * Restituisce un oggetto che rappresenta i dati da scrivere nel file in caso di riscrittura.
     * Dato che il file dei messaggi non può essere modificato dal programma, questa lista restituisce
     * semplicemente "null".
     *
     * @return null
     */
    @Override
    public Object getDumpableData() {
        return null;
    }

    /**
     * Enum che contiene tutti gli indici dei messaggi presenti nel file Yaml.
     * Un valore dell'enumeratore è definito in modo che risulti uguale all'indice del file Yaml,
     * ma in maiuscolo e con ogni "-" sostituito da "_".
     */
    public enum LANGUAGE_INDEXES {
        NOT_MATCHING_BUTTONS, ERROR_ADDING_USER_MISSING_CONFIG_FILE, ERROR_REMOVING_USER_MISSING_CONFIG_FILE, ALREADY_IN_CHAT,
        WELCOME_CHAT_MESSAGE, ERROR_STARTING_CHAT, ERROR_REMOVING_USER_FROM_STAFF_CHAT_FILE, START_MESSAGE, ERROR_CREATING_KEYBOARD_START_MESSAGE,
        ERROR_EDITING_START_MESSAGE, ERROR_SENDING_START_MESSAGE, ERROR_STATUS_REQUEST, STATUS_SERVER_MESSAGE, ERROR_SEND_STATUS_MESSAGE,
        ALREADY_SET_CALLBACK, INVALID_HANDLER_CALLBACK, ERROR_LEAVING_CHAT_MESSAGE, LEAVING_CHAT_MESSAGE, CHAT_STAFF_RESPONSE, ERROR_SEND_CHAT_STAFF_RESPONSE,
        MESSAGE_CHAT_STAFF_INCOMING, ERROR_SEND_STAFF_CHAT_RESPONSE, ERROR_BOT_INITIALIZATION, ERROR_HANDLER_INITIALIZATION, ERROR_CREATING_CONFIG_DIR,
        ERROR_CREATING_STAFF_CHAT_CONFIG, END_CHAT_SYNTAX, BAN_CHAT_SYNTAX, PARDON_CHAT_SYNTAX, ERROR_SENDING_CHAT_COMMAND_MESSAGE, END_CHAT_USER_NOT_IN_CHAT,
        END_CHAT_USER_REMOVED, END_CHAT_USER_REMOVED_BY_ADMIN, RELOADED_CONFIGS, ERROR_RELOAD_CONFIGS, ERROR_ADD_BANNED_USERS, BAN_CHAT_ALREADY_BANNED,
        USER_BANNED_MESSAGE, BAN_CHAT_SUCCESS, PARDON_CHAT_MESSAGE, ERROR_REMOVE_BANNED_USER, PARDON_CHAT_NOT_BANNED, PARDON_CHAT_SUCCESS,
        SERVER_IP_CALLBACK_TEXT, ERROR_CALLBACK_SERVER_IP;

        /**
         * Restituisce, a partire dall'indice, il valore che esso ha nel file Yaml di configurazione.
         *
         * @return Index del messaggio nel formato del file Yaml.
         */
        @Override
        public String toString() {
            return super.toString().replace("_", "-").toLowerCase();
        }
    }

    /**
     * Enum che contiene tutti gli indici delle linee delle tastiere inline presenti nel file Yaml.
     * Un valore dell'enumeratore è definito in modo che risulti uguale all'indice del file Yaml,
     * ma in maiuscolo e con ogni "-" sostituito da "_".
     */
    public enum KEYBOARDS_INDEXES {
        BACK_KEYBOARD, START_KEYBOARD, STAFF_CHAT_RESPONSE_KEYBOARD;

        /**
         * Restituisce, a partire dall'indice, il valore che esso ha nel file Yaml di configurazione.
         *
         * @return Index del messaggio nel formato del file Yaml.
         */
        @Override
        public String toString() {
            return super.toString().replace("_", "-").toLowerCase();
        }
    }
}
