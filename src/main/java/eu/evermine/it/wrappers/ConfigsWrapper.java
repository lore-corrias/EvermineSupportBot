package eu.evermine.it.wrappers;

import eu.evermine.it.configs.YamlManager;
import eu.evermine.it.configs.yamls.ConfigsYaml;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.List;


final public class ConfigsWrapper {

    private static ConfigsWrapper configsWrapper;
    private static ConfigsYaml configsYaml;


    private ConfigsWrapper() {
        if(configsWrapper != null)
            throw new IllegalStateException("ConfigsWrapper è già stato inizializzato.");
        configsWrapper = this;
    }

    public static ConfigsWrapper getInstance() throws IOException {
        if(configsWrapper == null)
            new ConfigsWrapper();
        configsYaml = (ConfigsYaml) YamlManager.getInstance().loadYaml(new ConfigsYaml());
        return configsWrapper;
    }

    public List<Long> getAdmins() {
        return configsYaml.getAdmins();
    }

    public boolean isAdmin(Long id) {
        return configsYaml.isAdmin(id);
    }

    public @Nullable Long getAdminGroupID() {
        return configsYaml.getAdminGroupID();
    }

    public boolean isAdminGroupSet() {
        return configsYaml.isAdminGroupSet();
    }

    public @Nullable String getBotToken() {
        return configsYaml.getBotToken();
    }

    public @Nullable String getBotUsername() {
        return configsYaml.getBotUsername();
    }

    public void reloadConfigs() throws IOException {
        configsYaml = (ConfigsYaml) YamlManager.getInstance().loadYaml(new ConfigsYaml());
    }
}
