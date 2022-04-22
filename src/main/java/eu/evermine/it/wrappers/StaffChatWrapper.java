package eu.evermine.it.wrappers;

import eu.evermine.it.configs.YamlManager;
import eu.evermine.it.configs.yamls.StaffChatYaml;

import java.io.IOException;

final public class StaffChatWrapper {

    private static StaffChatWrapper staffChatWrapper;
    private static StaffChatYaml staffChatYaml;


    private StaffChatWrapper() {
        if(staffChatYaml != null)
            throw new IllegalStateException("StaffChatYaml è già stato inizializzato.");
        staffChatWrapper = this;
    }

    public static StaffChatWrapper getInstance() throws IOException {
        if(staffChatWrapper == null)
            staffChatWrapper = new StaffChatWrapper();
        staffChatYaml = (StaffChatYaml) YamlManager.getInstance().loadYaml(new StaffChatYaml());
        return staffChatWrapper;
    }

    public boolean isUserInChat(Long userId) {
        return staffChatYaml.isInChatUser(userId);
    }

    public boolean isBannedUser(Long userId) {
        return staffChatYaml.isBannedUser(userId);
    }

    public void addBannedUser(Long userId) throws IOException {
        staffChatYaml.addBannedUser(userId);
        YamlManager.getInstance().dumpYaml(staffChatYaml);
    }

    public void removeBannedUser(Long userId) throws IOException {
        staffChatYaml.removeBannedUser(userId);
        YamlManager.getInstance().dumpYaml(staffChatYaml);
    }

    public void addInChatUser(Long userId) throws IOException {
        staffChatYaml.addInChatUser(userId);
        YamlManager.getInstance().dumpYaml(staffChatYaml);
    }

    public void removeInChatUser(Long userId) throws IOException {
        staffChatYaml.removeInChatUser(userId);
        YamlManager.getInstance().dumpYaml(staffChatYaml);
    }
}
