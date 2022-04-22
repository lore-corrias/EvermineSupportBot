package eu.evermine.it.configs;

import eu.evermine.it.configs.yamls.AbstractYaml;
import org.yaml.snakeyaml.Yaml;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

/**
 * Manager dei file Yaml.
 * La classe utilizza il package {@link eu.evermine.it.configs.yamls} per caricare i file Yaml.
 * La classe non può essere istanziata. Per ottenere un'istanza, utilizzare il metodo {@link #getInstance}.
 * Il metodo responsabile per il caricamento di un file Yaml in un oggetto è {@link #loadYaml}.
 * Il metodo responsabile per il dump di un oggetto in un file Yaml è {@link #dumpYaml}.
 * I file di config predefiniti vengono ottenuti dalla cartella "resources" attraverso il metodo {@link #getResource}
 *
 * @param <T> Tipo dell'oggetto su cui caricare il file Yaml.
 * @author just
 * @version 1.0
 * @see AbstractYaml
 */
public class YamlManager<T extends AbstractYaml> {

    /**
     * Istanza di YamlManager. Se definita, viene utilizzata, altrimenti viene creata una nuova istanza
     * dal metodo {@link #getInstance}.
     */
    private static YamlManager<?> yamlManager;


    /**
     * Costruttore privato della classe.
     * Non essendo istanziabile, nel caso si voglia ottenere un'istanza, utilizzare il metodo {@link #getInstance}.
     * Se la classe viene istanziata una seconda volta, viene lanciata un'eccezione.
     *
     * @throws IllegalAccessException Se la classe viene istanziata una seconda volta.
     */
    private YamlManager() throws IllegalArgumentException {
        if (yamlManager != null)
            throw new IllegalArgumentException("YamlLoader non può essere istanziato.");
        yamlManager = this;
    }

    /**
     * Restituisce l'istanza di YamlManager. Se non è definita, viene creata una nuova istanza.
     *
     * @return L'istanza di YamlManager.
     */
    public static YamlManager<?> getInstance() {
        if (yamlManager == null)
            return new YamlManager<>();
        return yamlManager;
    }

    /**
     * Restituisce i contenuti del file "filename" nella cartella "resources".
     * Se il file non è presente nella cartella "resources", restituisce null.
     *
     * @param filename Nome del file.
     * @return Un'istanza di {@link InputStream} che rappresenta il contenuto del file, null se il file non è trovato.
     */
    public static @Nullable InputStream getResource(String filename) {
        try {
            return Objects.requireNonNull(AbstractYaml.class.getClassLoader().getResourceAsStream(filename));
        } catch (NullPointerException e) {
            return null;
        }
    }

    /**
     * Carica un file Yaml in un oggetto.
     * L'oggetto fornito deve estendere la classe astratta {@link AbstractYaml}, in modo da
     * identificare un oggetto che rappresenta un file Yaml.
     * Il nome del file viene fornito dal metodo {@link AbstractYaml#getFilename()}, e se il file
     * "configs/filename" non esiste, il metodo copia nella stessa path il file presente in "resources/filename".
     * Se il file non è presente nella cartella resources, viene lanciata un'eccezione.
     * Copiato il file, il metodo carica il file Yaml nell'oggetto di tipo "T".
     * L'oggetto viene verificato dal metodo {@link #validateConfig}, e in caso di errore, viene lanciata un'eccezione.
     * Il metodo restituisce poi l'oggetto popolato con i dati del file Yaml.
     *
     * @param abstractYaml Oggetto che rappresenta un file Yaml.
     * @return L'oggetto popolato con i dati del file Yaml.
     * @throws IOException Se il file Yaml non è presente nella cartella resources.
     * @throws IllegalArgumentException Se il file Yaml non è valido.
     */
    public T loadYaml(AbstractYaml abstractYaml) throws IOException, IllegalArgumentException {
        this.validateConfig(abstractYaml.getFilename());
        Yaml yaml = new Yaml(abstractYaml.getConstructor());

        T loadedYaml = yaml.load(Files.newBufferedReader(Path.of("config/" + abstractYaml.getFilename())));
        loadedYaml.checkConfigValidity();
        return loadedYaml;
    }

    /**
     * Carica i valori di un oggetto all'interno di un file Yaml.
     * L'oggetto fornito deve estendere la classe astratta {@link AbstractYaml}, in modo da
     * identificare un oggetto che rappresenta un file Yaml.
     * Il nome del file viene fornito dal metodo {@link AbstractYaml#getFilename()}.
     * I dati da caricare all'interno del file Yaml vengono forniti dal metodo {@link AbstractYaml#getDumpableData}.
     * Se il metodo getDumpableData restituisce null, e quindi la classe non ha dati da salvare,
     * viene lanciata un'eccezione.
     * La stringa di valori del file Yaml viene scritta nel file "config/filename",
     * dopo essere stata ricavata dal metodo {@link Yaml#dump}.
     *
     * @param abstractYaml Oggetto che rappresenta un file Yaml.
     * @throws IOException In caso di errore nella scrittura su file.
     * @throws IllegalArgumentException Se l'oggetto non presenta dati da scrivere.
     */
    public void dumpYaml(AbstractYaml abstractYaml) throws IOException, IllegalArgumentException {
        Yaml yaml = new Yaml();
        if (abstractYaml.getDumpableData() == null)
            throw new IllegalArgumentException("Dumpable data non definito");
        Files.writeString(Path.of("config/" + abstractYaml.getFilename()), yaml.dump(abstractYaml.getDumpableData()));
    }

    /**
     * Verifica che il file Yaml sia valido.
     * Se il file non esiste nella cartella "config/filename", viene copiato il file di default "resources/filename".
     * Se non viene trovato il file "resources/filename", viene lanciata un'eccezione.
     * Se non è possibile leggere il file o creare la cartella config, viene lanciata un'eccezione.
     *
     * @param filename Nome del file Yaml.
     * @throws IllegalArgumentException Se il file Yaml non è trovato nella cartella resources.
     * @throws IOException In caso di errori nella scrittura su file/creazione delle cartelle.
     */
    private void validateConfig(String filename) throws IllegalArgumentException, IOException {
        File configFile = new File(Path.of("config/" + filename).toString());
        if (!configFile.exists()) {
            InputStream resource = getResource(filename);
            if (resource == null)
                throw new IllegalArgumentException("Config file " + filename + " non trovato nella cartella resources..");
            if (!Files.isDirectory(Path.of("config")) && !new File("config").mkdir())
                throw new IOException("Impossibile creare la cartella config");
            Files.copy(resource, configFile.toPath());
        }
    }
}
