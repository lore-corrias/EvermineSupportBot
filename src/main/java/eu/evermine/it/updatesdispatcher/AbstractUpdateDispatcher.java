package eu.evermine.it.updatesdispatcher;

import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;

import java.util.List;

public abstract class AbstractUpdateDispatcher implements UpdatesListener {

    public int process(List<Update> updates) {
        for (Update update : updates) {
            handleUpdate(update);
        }
        return CONFIRMED_UPDATES_ALL;
    }

    public abstract boolean handleUpdate(Update update);
}
