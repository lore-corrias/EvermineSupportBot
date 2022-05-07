package eu.evermine.it.configs.yamls;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.justlel.configs.YamlManager;
import io.github.justlel.configs.yamls.YamlInterface;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Rappresentazione del file Yaml "staff-chat.yml". Contiene le configurazioni per la StaffChat.
 * Essendo la rappresentazione di un file Yaml, la classe implementa l'interfaccia {@link YamlInterface},
 * implementandone i metodi astratti {@link #checkConfigValidity()} e {@link #getDumpableData()}.
 * Il file di config contiene solo due valori: "in-chat-users", che rappresenta una lista di utenti
 * attualmente impegnati nella StaffChat, e "banned-users", che rappresenta una lista di utenti banditi
 * dalla StaffChat.
 *
 * @author just
 * @version 2.1
 */
public class StaffChatYaml implements YamlInterface {

    /**
     * Lista degli ID degli utenti attualmente impegnati nella StaffChat.
     */
    private static List<Long> inChatUsers;
    /**
     * Lista degli ID degli utenti banditi dalla StaffChat.
     */
    private static List<Long> bannedUsers;


    /**
     * Costruttore della classe.
     */
    public StaffChatYaml() {
    }

    /**
     * Restituisce la Lista degli ID degli utenti banditi dalla StaffChat. Vedi {@link #bannedUsers}.
     *
     * @return La lista degli ID degli utenti banditi dalla StaffChat.
     */
    @JsonProperty("banned-users")
    public static List<Long> getBannedUsers() {
        return bannedUsers;
    }

    /**
     * Setter utilizzato dalla classe {@link com.fasterxml.jackson.databind.ObjectMapper} per caricare i valori
     * del file di configurazione Yaml sulle proprietà della classe.
     * Imposta il valore di {@link #bannedUsers}.
     *
     * @param bannedUsers La lista che contiene gli ID degli utenti banditi.
     */
    @JsonProperty("banned-users")
    public void setBannedUsers(List<Long> bannedUsers) {
        StaffChatYaml.bannedUsers = bannedUsers;
    }

    /**
     * Restituisce la Lista degli ID degli utenti impegnati nella StaffChat. Vedi {@link #inChatUsers}.
     *
     * @return La lista degli ID degli utenti impegnati nella StaffChat.
     */
    @JsonProperty("in-chat-users")
    public static List<Long> getInChatUsers() {
        return inChatUsers;
    }

    /**
     * Setter utilizzato dalla classe {@link com.fasterxml.jackson.databind.ObjectMapper} per caricare i valori
     * del file di configurazione Yaml sulle proprietà della classe.
     * Imposta il valore di {@link #inChatUsers}.
     *
     * @param inChatUsers La lista che contiene gli ID degli utenti impegnati nella StaffChat.
     */
    @JsonProperty("in-chat-users")
    public void setInChatUsers(List<Long> inChatUsers) {
        StaffChatYaml.inChatUsers = inChatUsers;
    }

    /**
     * Aggiunge un utente alla lista degli utenti banditi.
     * La lista degli utenti non viene aggiornata su file,
     * ma il metodo si limita a modificare il valore di {@link #bannedUsers}.
     *
     * @param userId L'ID dell'utente da aggiungere alla lista.
     */
    public static void addBannedUser(Long userId) throws IOException {
        if (bannedUsers.contains(userId))
            return;
        bannedUsers.add(userId);
        YamlManager.getInstance().dumpYaml(StaffChatYaml.class);
    }

    /**
     * Verifica che l'utente fornito sia bandito dalla StaffChat.
     *
     * @param userId L'ID dell'utente da verificare.
     * @return True se l'utente è bandito, false altrimenti.
     */
    public static boolean isBannedUser(Long userId) {
        return bannedUsers.contains(userId);
    }

    /**
     * Rimuove un utente dalla lista degli utenti banditi.
     * La lista degli utenti non viene aggiornata su file,
     * ma il metodo si limita a modificare il valore di {@link #bannedUsers}.
     *
     * @param userId L'ID dell'utente da rimuovere dalla lista.
     */
    public static void removeBannedUser(Long userId) throws IOException {
        if (!bannedUsers.contains(userId))
            return;
        bannedUsers.remove(userId);
        YamlManager.getInstance().dumpYaml(StaffChatYaml.class);
    }

    /**
     * Aggiunge un utente alla lista degli utenti impegnati nella StaffChat.
     * La lista degli utenti non viene aggiornata su file,
     * ma il metodo si limita a modificare il valore di {@link #inChatUsers}.
     *
     * @param userId L'ID dell'utente da aggiungere alla lista.
     */
    public static void addInChatUser(Long userId) throws IOException {
        if (inChatUsers.contains(userId))
            return;
        inChatUsers.add(userId);
        YamlManager.getInstance().dumpYaml(StaffChatYaml.class);
    }

    /**
     * Verifica che l'utente fornito sia impegnato nella StaffChat.
     *
     * @param userId L'ID dell'utente da verificare.
     * @return True se l'utente è impegnato nella StaffChat, false altrimenti.
     */
    public static boolean isInChatUser(Long userId) {
        return inChatUsers.contains(userId);
    }

    /**
     * Rimuove un utente dalla lista degli utenti impegnati nella StaffChat.
     * La lista degli utenti non viene aggiornata su file,
     * ma il metodo si limita a modificare il valore di {@link #inChatUsers}.
     *
     * @param userId L'ID dell'utente da rimuovere dalla lista.
     */
    public static void removeInChatUser(Long userId) throws IOException {
        if (!inChatUsers.contains(userId))
            return;
        inChatUsers.remove(userId);
        YamlManager.getInstance().dumpYaml(StaffChatYaml.class);
    }

    /**
     * Fornisce alla classe super il nome del file di config,
     * in questo caso: "staff-chat.yml".
     *
     * @return Il nome del file di config.
     */
    @Override
    public String getFilename() {
        return "staff-chat.yml";
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
    }

    /**
     * Restituisce un oggetto che rappresenta i dati da scrivere nel file in caso di riscrittura.
     * L'oggetto restituito è una HashMap, che presenta come indici gli stessi di quelli del file Yaml,
     * e come valori quelli contenuti nelle proprietà della classe.
     * La struttura risulta quindi:
     * {@code in-chat-users: <lista degli ID degli utenti impegnati nella StaffChat>
     * banned-users: <lista degli utenti banditi dalla StaffChat>}
     *
     * @return Un oggetto che rappresenta i dati da scrivere nel file in caso di riscrittura.
     */
    @Override
    public Object getDumpableData() {
        Map<String, List<Long>> map = new HashMap<>();
        map.put("in-chat-users", inChatUsers);
        map.put("banned-users", bannedUsers);
        return map;
    }
}
