package eu.evermine.it.configs.yamls;

import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.introspector.PropertyUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Rappresentazione del file Yaml "staff-chat.yml". Contiene le configurazioni per la StaffChat.
 * Essendo la rappresentazione di un file Yaml, la classe estende la classe {@link AbstractYaml},
 * implementandone i metodi astratti {@link #getConstructor()}, {@link #checkConfigValidity()} e {@link #getDumpableData()}.
 * Il file di config contiene solo due valori: "in-chat-users", che rappresenta una lista di utenti
 * attualmente impegnati nella StaffChat, e "banned-users", che rappresenta una lista di utenti banditi
 * dalla StaffChat.
 *
 * @author just
 * @version 1.0
 * @see AbstractYaml
 */
public class StaffChatYaml extends AbstractYaml {

    /**
     * Lista degli ID degli utenti attualmente impegnati nella StaffChat.
     */
    private List<Long> inChatUsers;
    /**
     * Lista degli ID degli utenti banditi dalla StaffChat.
     */
    private List<Long> bannedUsers;


    /**
     * Costruttore della classe. Fornisce alla classe super il nome del file di config,
     * in questo caso: "staff-chat.yml".
     *
     * @see AbstractYaml#getFilename()
     */
    public StaffChatYaml() {
        super("staff-chat.yml");
    }

    /**
     * Setter utilizzato dalla classe {@link org.yaml.snakeyaml.Yaml} per caricare i valori
     * del file di configurazione Yaml sulle proprietà della classe.
     * Imposta il valore di {@link #bannedUsers}.
     *
     * @param bannedUsers La lista che contiene gli ID degli utenti banditi.
     */
    public void setBanned_users(List<Long> bannedUsers) {
        this.bannedUsers = bannedUsers;
    }

    /**
     * Restituisce la Lista degli ID degli utenti banditi dalla StaffChat. Vedi {@link #bannedUsers}.
     *
     * @return La lista degli ID degli utenti banditi dalla StaffChat.
     */
    public List<Long> getBannedUsers() {
        return bannedUsers;
    }

    /**
     * Setter utilizzato dalla classe {@link org.yaml.snakeyaml.Yaml} per caricare i valori
     * del file di configurazione Yaml sulle proprietà della classe.
     * Imposta il valore di {@link #inChatUsers}.
     *
     * @param inChatUsers La lista che contiene gli ID degli utenti impegnati nella StaffChat.
     */
    public void setIn_chat_users(List<Long> inChatUsers) {
        this.inChatUsers = inChatUsers;
    }

    /**
     * Restituisce la Lista degli ID degli utenti impegnati nella StaffChat. Vedi {@link #inChatUsers}.
     *
     * @return La lista degli ID degli utenti impegnati nella StaffChat.
     */
    public List<Long> getInChatUsers() {
        return inChatUsers;
    }

    /**
     * Aggiunge un utente alla lista degli utenti banditi.
     * La lista degli utenti non viene aggiornata su file,
     * ma il metodo si limita a modificare il valore di {@link #bannedUsers}.
     *
     * @param userId L'ID dell'utente da aggiungere alla lista.
     */
    public void addBannedUser(Long userId) {
        if (bannedUsers.contains(userId))
            return;
        bannedUsers.add(userId);
    }

    /**
     * Verifica che l'utente fornito sia bandito dalla StaffChat.
     *
     * @param userId L'ID dell'utente da verificare.
     * @return True se l'utente è bandito, false altrimenti.
     */
    public boolean isBannedUser(Long userId) {
        return bannedUsers.contains(userId);
    }

    /**
     * Rimuove un utente dalla lista degli utenti banditi.
     * La lista degli utenti non viene aggiornata su file,
     * ma il metodo si limita a modificare il valore di {@link #bannedUsers}.
     *
     * @param userId L'ID dell'utente da rimuovere dalla lista.
     */
    public void removeBannedUser(Long userId) {
        if (!bannedUsers.contains(userId))
            return;
        bannedUsers.remove(userId);
    }

    /**
     * Aggiunge un utente alla lista degli utenti impegnati nella StaffChat.
     * La lista degli utenti non viene aggiornata su file,
     * ma il metodo si limita a modificare il valore di {@link #inChatUsers}.
     *
     * @param userId L'ID dell'utente da aggiungere alla lista.
     */
    public void addInChatUser(Long userId) {
        if (inChatUsers.contains(userId))
            return;
        inChatUsers.add(userId);
    }

    /**
     * Verifica che l'utente fornito sia impegnato nella StaffChat.
     *
     * @param userId L'ID dell'utente da verificare.
     * @return True se l'utente è impegnato nella StaffChat, false altrimenti.
     */
    public boolean isInChatUser(Long userId) {
        return inChatUsers.contains(userId);
    }

    /**
     * Rimuove un utente dalla lista degli utenti impegnati nella StaffChat.
     * La lista degli utenti non viene aggiornata su file,
     * ma il metodo si limita a modificare il valore di {@link #inChatUsers}.
     *
     * @param userId L'ID dell'utente da rimuovere dalla lista.
     */
    public void removeInChatUser(Long userId) {
        if (!inChatUsers.contains(userId))
            return;
        inChatUsers.remove(userId);
    }

    /**
     * Restituisce un {@link Constructor} per la classe {@link AbstractYaml}.
     * Il Constructor fornito si basa su quello fornito dal metodo {@link AbstractYaml#getCapitalizedConstructor}},
     * con la differenza che ogni "-" viene sostituito in ogni valore degli index con "_".
     *
     * @return Il Constructor per la classe {@link AbstractYaml}.
     * @see AbstractYaml#getConstructor()
     */
    @Override
    public Constructor getConstructor() {
        final PropertyUtils propertyUtils = new PropertyUtils() {
            @Override
            public Property getProperty(Class<?> type, String name) {
                return StaffChatYaml.super.getCapitalizedConstructor(this.getClass()).getPropertyUtils().getProperty(type, name.replace("-", "_"));
            }
        };
        Constructor constructor = new Constructor(this.getClass());
        constructor.setPropertyUtils(propertyUtils);
        return constructor;
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
        // TODO: Implementare il metodo checkConfigValidity() per la classe StaffChatYaml
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
