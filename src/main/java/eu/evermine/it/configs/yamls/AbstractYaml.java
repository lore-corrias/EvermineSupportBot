package eu.evermine.it.configs.yamls;


import eu.evermine.it.configs.YamlManager;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.introspector.PropertyUtils;

import java.io.IOException;

/**
 * Astrazione di un file di configurazione Yaml. Ogni classe che rappresenta uno specifico file config
 * deve obbligatoriamente essere estensione di questa. Inoltre, ogni classe figlia deve necessariamente
 * effettuare l'override dei metodi {@link #getConstructor()}, per permettere correttamente il parsing
 * dei dati, {@link #checkConfigValidity()}, per controllare la validità dei config forniti,
 * e {@link #getDumpableData()}, che fornisce i dati che saranno scritti nel file di configurazione quando richiesto.
 * Se il file di configurazione non deve essere riscritto, {@link #getDumpableData()} può essere vuoto.
 * Il nome del file di configurazione viene fornito dal metodo {@link #getFilename()}, e indica un file presente
 * all'interno della directory "/config".
 *
 * @author just
 * @version 1.0
 * @see ConfigsYaml
 * @see LanguageYaml
 * @see StaffChatYaml
 */
public abstract class AbstractYaml {

    /**
     * Filename del config. La path del file è costruita come {@code "config/{@link #getFilename()}"}.
     */
    private final String filename;


    /**
     * Costruttore della classe astratta. Definisce il filename del file di config attraverso il parametro fornito.
     *
     * @param filename Nome del file di config. Vedi {@link #filename}.
     */
    public AbstractYaml(String filename) {
        this.filename = filename;
    }

    /**
     * Restituisce il nome del file di config precedentemente definito dal costruttore.
     *
     * @return Nome del file di config. Vedi {@link #filename}.
     */
    public String getFilename() {
        return filename;
    }

    /**
     * Metodo astratto, restituisce una istanza di {@link Constructor}.
     * Questo metodo viene utilizzato per il parsing del file di configurazione. L'istanza
     * di Constructor viene utilizzata dalla classe {@link YamlManager},
     * che richiama a sua volta il costruttore della classe {@link org.yaml.snakeyaml.Yaml}, passando
     * il Constructor come parametro. Il Constructor viene utilizzato per indicare alla classe Yaml
     * la modalità con cui effettuare il parsing dei dati, in modo che utilizzi in maniera corretta i metodi
     * della classe che rappresenta il file di config.
     *
     * @return Un'istanza di Constructor.
     */
    public abstract Constructor getConstructor();

    /**
     * Restituisce un {@link Constructor} predefinito, che indica alla classe {@link org.yaml.snakeyaml.Yaml}
     * di utilizzare i metodi setter della classe che rappresenta i file di configurazione in modo che siano della forma:
     * {@code proprietà: valore} (File Yaml).
     * {@code setProprietà(Object valore)}. (Setter)
     * Fondamentalmente, capitalizzando la prima lettera del nome della proprietà.
     *
     * @param clazz Classe che rappresenta il file di configurazione. Necessaria per istanziare un Constructor.
     * @return Il Constructor descritto sopra.
     */
    protected Constructor getCapitalizedConstructor(Class<?> clazz) {
        Constructor constructor = new Constructor(clazz);
        final PropertyUtils propertyUtils = new PropertyUtils() {
            @Override
            public Property getProperty(Class<?> type, String name) {
                return super.getProperty(type, name.substring(0, 1).toLowerCase() + name.substring(1));
            }
        };
        constructor.setPropertyUtils(propertyUtils);
        return constructor;
    }

    /**
     * Metodo astratto, verifica l'integrità del file di config.
     * Questo metodo viene utilizzato per verificare che il file di configurazione sia valido.
     * L'override viene effettuato in modo tale che, nel caso in cui un file di config risultasse non valido,
     * il codice lancerebbe una IllegalArgumentException.
     * Nel caso la validazione dei dati per uno specifico file di config non sia implementata,
     * il metodo può essere sovrascritto in modo che non verifichi nulla.
     *
     * @throws IllegalArgumentException Nel caso in cui il file di configurazione non sia valido.
     * @throws IOException In caso di errore nella chiusura del file di configurazione.
     */
    public abstract void checkConfigValidity() throws IllegalArgumentException, IOException;

    /**
     * Metodo astratto, restituisce una rappresentazione dei dati che possono essere scritti su file.
     * Questo metodo viene utilizzato per scrivere i dati su file. L'override viene effettuato in modo tale
     * che il metodo restituisca un qualsiasi oggetto, che rappresenta i dati che saranno scritti su file
     * nel caso di una scrittura. Nel caso in cui non sia implementato, il metodo può essere sovrascritto
     * in modo che restituisca "null".
     *
     * @return Oggetto che rappresenta i dati che possono essere scritti su file.
     */
    public abstract Object getDumpableData();
}
