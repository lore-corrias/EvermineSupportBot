package eu.evermine.it.configs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import eu.evermine.it.configs.yamls.AbstractYaml;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
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
 * @author just
 * @version 2.1
 * @see AbstractYaml
 */
public class YamlManager {

    /**
     * Istanza di YamlManager. Se definita, viene utilizzata, altrimenti viene creata una nuova istanza
     * dal metodo {@link #getInstance}.
     */
    private static YamlManager yamlManager;
    private final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());


    /**
     * Costruttore privato della classe.
     * Non essendo istanziabile, nel caso si voglia ottenere un'istanza, utilizzare il metodo {@link #getInstance}.
     * Se la classe viene istanziata una seconda volta, viene lanciata un'eccezione.
     *
     * @throws IllegalAccessException Se la classe viene istanziata una seconda volta.
     */
    private YamlManager() throws IllegalAccessException {
        if (yamlManager != null)
            throw new IllegalAccessException("YamlLoader non può essere istanziato.");
        yamlManager = this;
    }

    /**
     * Restituisce l'istanza di YamlManager. Se non è definita, viene creata una nuova istanza.
     *
     * @return L'istanza di YamlManager.
     */
    public static YamlManager getInstance() {
        if (yamlManager == null) {
            try {
                return new YamlManager();
            } catch (IllegalAccessException ignored) {
            }
        }
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
     * Carica un file Yaml utilizzando la classe fornita come modello.
     * La classe fornita deve essere estensione della classe astratta {@link AbstractYaml},
     * in modo da identificare una rappresentazione di un file Yaml.
     * Un'istanza viene creata a partire dalla classe fornita.
     * Il nome del file viene fornito dal metodo {@link AbstractYaml#getFilename()}, e se il file
     * "configs/filename" non esiste, il metodo copia nella stessa path il file presente in "resources/filename".
     * Se il file non è presente nella cartella resources, viene lanciata un'eccezione.
     * Copiato il file, il metodo carica il file Yaml nell'oggetto di tipo "T".
     * L'oggetto viene verificato dal metodo {@link #validateConfig}, e in caso di errore, viene lanciata un'eccezione.
     * Il metodo restituisce poi l'oggetto popolato con i dati del file Yaml.
     *
     * @param clazz Classe che rappresenta il modello del config Yaml da caricare.
     * @throws IOException              Se il file Yaml non è presente nella cartella resources.
     * @throws IllegalArgumentException Se il file Yaml non è valido, o se la creazione dell'istanza di "clazz" fallisce.
     */
    public void loadYaml(Class<? extends AbstractYaml> clazz) throws IOException, IllegalArgumentException {
        try {
            String filename = clazz.getConstructor().newInstance().getFilename();
            this.validateConfig(filename);
            mapper.readValue(Files.newBufferedReader(Path.of("config/" + filename)), clazz).checkConfigValidity();
        } catch (NoSuchMethodException ignored) { // Jackson impedisce l'utilizzo di una classe senza costruttore vuoto.

        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Carica i valori di una classe all'interno di un file Yaml.
     * La classe fornita deve essere estensione della classe astratta {@link AbstractYaml},
     * in modo da identificare una rappresentazione di un file Yaml.
     * Un istanza viene creata a partire dalla classe fornita.
     * Il nome del file viene fornito dal metodo {@link AbstractYaml#getFilename()}.
     * I dati da caricare all'interno del file Yaml vengono forniti dal metodo {@link AbstractYaml#getDumpableData}.
     * Se il metodo getDumpableData restituisce null, e quindi la classe non ha dati da salvare,
     * viene lanciata un'eccezione.
     * La stringa di valori del file Yaml viene scritta nel file "config/filename".
     *
     * @param clazz Classe che rappresenta il modello del config Yaml da caricare.
     * @throws IOException              In caso di errore nella scrittura su file.
     * @throws IllegalArgumentException Se l'oggetto non presenta dati da scrivere.
     */
    public void dumpYaml(Class<? extends AbstractYaml> clazz) throws IOException, IllegalArgumentException {
        try {
            AbstractYaml abstractYaml = clazz.getConstructor().newInstance();
            if (abstractYaml.getDumpableData() == null)
                throw new IllegalArgumentException("Dumpable data non definito");
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File("config/" + abstractYaml.getFilename()), abstractYaml.getDumpableData());
        } catch (
                InvocationTargetException ignored) { // Jackson impedisce l'utilizzo di una classe senza costruttore vuoto.

        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Verifica che il file Yaml sia valido.
     * Se il file non esiste nella cartella "config/filename", viene copiato il file di default "resources/filename".
     * Se non viene trovato il file "resources/filename", viene lanciata un'eccezione.
     * Se non è possibile leggere il file o creare la cartella config, viene lanciata un'eccezione.
     *
     * @param filename Nome del file Yaml.
     * @throws IllegalArgumentException Se il file Yaml non è trovato nella cartella resources.
     * @throws IOException              In caso di errori nella scrittura su file/creazione delle cartelle.
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
