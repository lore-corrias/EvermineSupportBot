package eu.evermine.it.configs.yamls;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import eu.evermine.it.EvermineSupportBot;
import eu.evermine.it.helpers.InlineKeyboardButtonBuilder;
import io.github.justlel.configs.YamlManager;
import io.github.justlel.configs.yamls.YamlInterface;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Rappresentazione del file Yaml "language.yml". Contiene i messaggi utilizzati dal bot.
 * Essendo la rappresentazione di un file Yaml, la classe implementa l'interfaccia {@link YamlInterface},
 * implementandone i metodi astratti {@link #checkConfigValidity()} e {@link #getDumpableData()}.
 * Il file di config contiene due indici: "language" e "keyboard". La struttura del file è definita nei commenti di "resources/language.yml".
 * Il primo contiene i messaggi di testo utilizzati dal bot, mentre il secondo i valori delle varie linee delle tastiere inline.
 *
 * @author just
 * @version 2.1
 */
public class LanguageYaml implements YamlInterface {

    /**
     * Mappa che contiene le {@link InlineKeyboardMarkup} generate dal metodo {@link #getKeyboard}, a partire
     * dal valore di {@link #keyboards}.
     */
    private static final Map<String, InlineKeyboardMarkup> inlineKeyboards = new HashMap<>();
    /**
     * Mappa che rappresenta tutti i dati relativi ai messaggi utilizzati dal bot nel file Yaml.
     * La chiave è l'indice del messaggio, mentre il valore è il messaggio stesso.
     */
    private static Map<String, String> language;
    /**
     * Mappa che rappresenta tutti i dati relativi alle linee delle tastiere inline utilizzate dal bot nel file Yaml.
     * La chiave è l'indice della tastiera, mentre il valore è una lista che contiene tutte le righe della tastiera,
     * ognuna delle quali contiene una lista di bottoni. Un bottone è una mappa, che contiene il testo del bottone (indice "text")
     * e il comando associato (indice "callback_data") o eventualmente l'URL (indice "url").
     */
    private static Map<String, List<List<Map<String, String>>>> keyboards;

    /**
     * Costruttore della classe.
     */
    public LanguageYaml() {
    }

    /**
     * Getter per le tastiere inline che sono state memorizzate.
     *
     * @return La mappa contenente le tastiere inline.
     */
    public static Map<String, InlineKeyboardMarkup> getInlineKeyboards() {
        return inlineKeyboards;
    }

    /**
     * Restituisce la mappa contenente i messaggi. Vedi {@link #language}.
     *
     * @return La mappa contenente i messaggi.
     */
    public static Map<String, String> getLanguage() {
        return language;
    }

    /**
     * Setter utilizzato dalla classe {@link com.fasterxml.jackson.databind.ObjectMapper} per caricare i valori
     * del file di configurazione Yaml sulle proprietà della classe.
     * Imposta il valore di {@link #language}.
     *
     * @param language La mappa che contiene i messaggi.
     */
    public void setLanguage(Map<String, String> language) {
        LanguageYaml.language = language;
    }

    /**
     * Restituisce la mappa contenente le varie tastiere inline. Vedi {@link #keyboards}.
     *
     * @return La mappa contenente le varie tastiere inline.
     */
    public static Map<String, List<List<Map<String, String>>>> getKeyboards() {
        return keyboards;
    }

    /**
     * Setter utilizzato dalla classe {@link com.fasterxml.jackson.databind.ObjectMapper} per caricare i valori
     * del file di configurazione Yaml sulle proprietà della classe.
     * Imposta il valore di {@link #keyboards}.
     *
     * @param keyboard La mappa che rappresenta le varie tastiere inline..
     */
    public void setKeyboards(Map<String, List<List<Map<String, String>>>> keyboard) {
        LanguageYaml.keyboards = keyboard;
    }


    /**
     * Restituisce una tastiera inline a partire dall'indice, sostituendo eventuali parametri
     * nel testo dei bottoni della tastiera e nel comando/URL associato.
     * Il metodo utilizza la mappa {@link #keyboards} per ottenere la tastiera, e sostituisce eventuali
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
    public static @Nullable InlineKeyboardMarkup getKeyboard(String index, @Nullable List<String> textArguments, @Nullable List<String> buttonArguments) {
        if (!LanguageYaml.keyboards.containsKey(index))
            throw new IllegalArgumentException("Il file di lingua non contiene la chiave per la tastiera " + index);
        if (textArguments == null && buttonArguments == null) {
            if (LanguageYaml.inlineKeyboards.containsKey(index))
                return LanguageYaml.inlineKeyboards.get(index);
        }
        InlineKeyboardButton[][] rows = new InlineKeyboardButton[LanguageYaml.keyboards.get(index).size()][];
        for (int i = 0; i < LanguageYaml.keyboards.get(index).size(); i++) {
            List<Map<String, String>> row = LanguageYaml.keyboards.get(index).get(i);
            for (int j = 0; j < row.size(); j++) {
                Map<String, String> button = row.get(j);
                if (!button.containsKey("text") || (!button.containsKey("callback_data") && !button.containsKey("url")))
                    throw new IllegalArgumentException("Il file di lingua deve contenere la chiave \"text\" e uno tra \"callback_data\" e \"url\" per ogni riga della tastiera");
                InlineKeyboardButtonBuilder inlineKeyboardButton = InlineKeyboardButtonBuilder.getBuilder();
                inlineKeyboardButton.setText(LanguageYaml.replaceArgs(button.get("text"), textArguments));
                if (button.containsKey("callback_data")) {
                    inlineKeyboardButton.setCallbackData(LanguageYaml.replaceArgs(button.get("callback_data"), buttonArguments));
                } else if (button.containsKey("url")) {
                    inlineKeyboardButton.setUrl(LanguageYaml.replaceArgs(button.get("url"), buttonArguments));
                }
                if (rows[i] == null)
                    rows[i] = new InlineKeyboardButton[row.size()];
                rows[i][j] = inlineKeyboardButton.buildButton();
            }
        }
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup(rows);
        if (textArguments != null && buttonArguments != null)
            LanguageYaml.inlineKeyboards.put(index, keyboardMarkup);
        return keyboardMarkup;
    }

    public static InlineKeyboardMarkup getKeyboard(String index) {
        return LanguageYaml.getKeyboard(index, null, null);
    }

    public static InlineKeyboardMarkup getKeyboard(String index, List<String> textArguments) {
        return LanguageYaml.getKeyboard(index, textArguments, null);
    }


    /**
     * Restituisce un messaggio a partire da un index, e da una lista di parametri
     * da sostituire nel messaggio, nel caso in cui fosse necessario. Se non sono forniti parametri,
     * allora la lista {@code args} è null, e viene ignorata.
     *
     * @param index L'indice del messaggio da restituire.
     * @param args  La lista di parametri da sostituire nel messaggio, null se non ci sono parametri.
     * @return Il messaggio.
     */
    public static String getLanguageString(String index, @Nullable List<String> args) {
        String s = LanguageYaml.getLanguage().get(index);
        if (s == null)
            EvermineSupportBot.logger.error("Il messaggio " + index + " non è presente nel file di lingua.");
        if (args != null) {
            s = LanguageYaml.replaceArgs(s, args);
        }
        return s;
    }

    public static String getLanguageString(String index) {
        return getLanguageString(index, null);
    }

    /**
     * Fornita una stringa, sostituisce i parametri forniti in {@code args}.
     * Un parametro è definito all'interno del messaggio tramite l'indice "%s".
     *
     * @param s    La stringa da modificare.
     * @param args La lista di parametri da sostituire.
     * @return La stringa modificata.
     */
    private static String replaceArgs(String s, @Nullable List<String> args) {
        if (args == null)
            return s;
        for (String arg : args) {
            s = s.replaceFirst("%s", arg);
        }
        return s;
    }

    /**
     * Fornisce alla classe super il nome del file di config, in questo caso: "language.yml".
     *
     * @return Il nome del file di config.
     */
    @Override
    public String getFilename() {
        return "language.yml";
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
    public void checkConfigValidity() throws IllegalArgumentException, IOException {
        try (InputStream defaultConfig = YamlManager.getResource("language.yml")) {
            final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            HashMap<?, ?> defaultConfigMap = mapper.readValue(defaultConfig, HashMap.class);

            Set<String> keysLang = ((HashMap<String, ?>) defaultConfigMap.get("language")).keySet();
            Set<String> keysKeyboard = ((HashMap<String, ?>) defaultConfigMap.get("keyboards")).keySet();
            if (keysLang.size() < LanguageYaml.getLanguage().keySet().size()) {
                Set<String> diff = LanguageYaml.getLanguage().keySet();
                diff.removeAll(keysLang);
                throw new IllegalArgumentException("Il file di lingua non è completo, mancano delle chiavi per i messaggi: " + diff);
            }
            if (keysKeyboard.size() < LanguageYaml.getKeyboards().keySet().size()) {
                Set<String> diff = LanguageYaml.getKeyboards().keySet();
                diff.removeAll(keysKeyboard);
                throw new IllegalArgumentException("Il file di lingua non è completo, mancano delle chiavi per le tastiere: " + diff);
            }
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
        Map<String, Map<?, ?>> dump = new HashMap<>();
        dump.put("language", LanguageYaml.language);
        dump.put("keyboards", LanguageYaml.keyboards);
        return dump;
    }
}
