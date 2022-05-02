package eu.evermine.it.configs.yamls;


import java.io.IOException;

/**
 * Astrazione di un file di configurazione Yaml. Ogni classe che rappresenta uno specifico file config
 * deve obbligatoriamente essere estensione di questa. Inoltre, ogni classe figlia deve necessariamente
 * effettuare l'override dei metodi {@link #checkConfigValidity()}, per controllare la validità dei config forniti,
 * e {@link #getDumpableData()}, che fornisce i dati che saranno scritti nel file di configurazione quando richiesto.
 * Se il file di configurazione non deve essere riscritto, {@link #getDumpableData()} può essere vuoto.
 * Il nome del file di configurazione viene fornito dal metodo {@link #getFilename()}, e indica un file presente
 * all'interno della directory "/config".
 *
 * @author just
 * @version 2.0
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
     * Metodo astratto, verifica l'integrità del file di config.
     * Questo metodo viene utilizzato per verificare che il file di configurazione sia valido.
     * L'override viene effettuato in modo tale che, nel caso in cui un file di config risultasse non valido,
     * il codice lancerebbe una IllegalArgumentException.
     * Nel caso la validazione dei dati per uno specifico file di config non sia implementata,
     * il metodo può essere sovrascritto in modo che non verifichi nulla.
     *
     * @throws IllegalArgumentException Nel caso in cui il file di configurazione non sia valido.
     * @throws IOException              In caso di errore nella chiusura del file di configurazione.
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

    @Override
    public String toString() {
        return getDumpableData().toString();
    }
}
