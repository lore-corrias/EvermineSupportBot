package eu.evermine.it.configs.yamls;

import io.github.justlel.configs.yamls.YamlInterface;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Rappresentazione del file Yaml "configs.yml". Contiene le configurazioni per il plugin.
 * Essendo la rappresentazione di un file Yaml, la classe implementa l'interfaccia {@link YamlInterface},
 * implementandone i metodi astratti {@link #checkConfigValidity()} e {@link #getDumpableData()}.
 * Il file di config contiene le seguenti informazioni:
 * <ul>
 *     <li>{@link #admingroup}: ID dell'eventuale gruppo di amministratori</li>
 *     <li>{@link #admins}: Lista degli ID degli admin del bot</li>
 *     <li>{@link #token}: Il token identificativo del bot</li>
 *     <li>{@link #username}: L'username del bot.</li>
 * </ul>
 *
 * @author just
 * @version 2.1
 */
public class ConfigsYaml implements YamlInterface {

    /**
     * ID dell'eventuale gruppo di amministratori. Nel caso in cui il gruppo non sia stato
     * definito nel file di config, il suo valore sarà null oppure minore di zero.
     */
    private static Long admingroup;
    /**
     * Lista degli ID degli admin del bot. Gli admin sono utenti che possono eseguire
     * operazioni di amministrazione del bot, tra cui diversi comandi riservati a loro.
     * Nel caso in cui la lista dovesse essere vuota, il valore sarà null.
     */
    private static List<Long> admins;
    /**
     * Il token identificativo del bot, necessario per la comunicazione con l'API di Telegram.
     * Se questo token non è definito nel file di config, può essere fornito durante l'avvio del bot
     * attraverso linea di comando. Vedi {@link eu.evermine.it.EvermineSupportBot#main(String[])}.
     * Se questo token non è definito in nessun caso, il bot non potrà funzionare.
     */
    private static String token;
    /**
     * L'username del bot, definito tramite t.me/BotFather su Telegram. Non contiene la "@".
     */
    private static String username;


    /**
     * Costruttore della classe.
     */
    public ConfigsYaml() {
    }

    /**
     * Verifica che l'ID del gruppo di amministratori sia stato fornito.
     *
     * @return True se l'ID è stato fornito, false altrimenti.
     */
    public static boolean isAdminGroupSet() {
        return getAdminGroupID() != null;
    }

    /**
     * Restituisce il valore di {@link #admingroup}. Se il valore della proprietà
     * è minore di zero o non definito, questo metodo restituisce null.
     *
     * @return L'ID del gruppo di amministratori, se definito, null altrimenti.
     */
    public static @Nullable Long getAdminGroupID() {
        if (ConfigsYaml.admingroup == -1)
            return null;
        return admingroup;
    }

    /**
     * Restituisce true o false a seconda che l'ID dell'utente fornito
     * sia presente o meno nella lista di amministratori del bot.
     *
     * @param id L'ID dell'utente da controllare.
     * @return True se l'ID è presente nella lista degli amministratori, false altrimenti.
     */
    public static boolean isAdmin(Long id) {
        return admins.contains(id);
    }

    /**
     * Restituisce il valore di {@link #admins}.
     *
     * @return La lista degli ID degli amministratori del bot, se definita.
     */
    public static List<Long> getAdmins() {
        return admins;
    }

    /**
     * Setter utilizzato dalla classe {@link com.fasterxml.jackson.databind.ObjectMapper} per caricare i valori
     * del file di configurazione Yaml sulle proprietà della classe.
     * Imposta il valore di {@link #admins}.
     *
     * @param admins La lista degli ID degli amministratori del bot, se definita.
     */
    public void setAdmins(List<Long> admins) {
        ConfigsYaml.admins = admins;
    }

    /**
     * Restituisce il token del bot.
     *
     * @return Il token del bot.
     */
    public static String getBotToken() {
        return ConfigsYaml.token;
    }

    /**
     * Restituisce l'username del bot.
     *
     * @return L'username del bot.
     */
    public static String getBotUsername() {
        return ConfigsYaml.username;
    }

    /**
     * Setter utilizzato dalla classe {@link com.fasterxml.jackson.databind.ObjectMapper} per caricare i valori
     * del file di configurazione Yaml sulle proprietà della classe.
     * Imposta il valore di {@link #admingroup}.
     *
     * @param admingroup L'ID del gruppo di amministratori, se definito.
     */
    public void setAdmingroup(Long admingroup) {
        ConfigsYaml.admingroup = admingroup;
    }

    /**
     * Setter utilizzato dalla classe {@link com.fasterxml.jackson.databind.ObjectMapper} per caricare i valori
     * del file di configurazione Yaml sulle proprietà della classe.
     * Imposta il valore di {@link #token}.
     *
     * @param token Il token del bot, se definito.
     */
    public void setToken(String token) {
        ConfigsYaml.token = token;
    }

    /**
     * Setter utilizzato dalla classe {@link com.fasterxml.jackson.databind.ObjectMapper} per caricare i valori
     * del file di configurazione Yaml sulle proprietà della classe.
     * Imposta il valore di {@link #username}.
     *
     * @param username L'username del bot.
     */
    public void setUsername(String username) {
        ConfigsYaml.username = username;
    }

    /**
     * Restituisce il nome del file di config, in questo caso "config.yaml".
     *
     * @return Il nome del file di config.
     */
    @Override
    public String getFilename() {
        return "configs.yml";
    }

    /**
     * Verifica la validità del file di config.
     * Il metodo si assicura che i valori definiti nel file Yaml siano del tipo corretto,
     * in caso contrario, il metodo lancia una IllegalArgumentException.
     *
     * @throws IllegalArgumentException Se uno dei valori definiti nel file Yaml non è del tipo corretto.
     */
    @Override
    public void checkConfigValidity() throws IllegalArgumentException {
        try {
            List.of(ConfigsYaml.token, ConfigsYaml.username).forEach(Objects::requireNonNull);
        } catch (NullPointerException e) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Restituisce un oggetto che rappresenta i dati da scrivere nel file in caso di riscrittura.
     * L'oggetto restituito è una HashMap, che presenta come indici gli stessi di quelli del file Yaml,
     * e come valori quelli contenuti nelle proprietà della classe.
     * La struttura risulta quindi:
     * {@code admingroup: <ID del gruppo admin>
     * admins: <lista degli ID degli amministratori
     * token: <token del bot>
     * username: <username del bot>}
     *
     * @return Un oggetto che rappresenta i dati da scrivere nel file in caso di riscrittura.
     */
    @Override
    public Object getDumpableData() {
        Map<String, Object> data = new HashMap<>();
        data.put("admingroup", ConfigsYaml.admingroup);
        data.put("admins", ConfigsYaml.admins);
        data.put("token", ConfigsYaml.token);
        data.put("username", ConfigsYaml.username);
        return data;
    }
}