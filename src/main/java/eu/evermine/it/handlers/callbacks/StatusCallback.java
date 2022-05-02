package eu.evermine.it.handlers.callbacks;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import eu.evermine.it.configs.yamls.LanguageYaml;
import eu.evermine.it.handlers.models.AbstractCallback;
import eu.evermine.it.helpers.ActionsAPIHelper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.List;

public class StatusCallback extends AbstractCallback {

    private final Logger logger;
    private final LanguageYaml language;


    public StatusCallback(Logger logger, LanguageYaml language) {
        this.logger = logger;
        this.language = language;
    }

    @Override
    public boolean handleUpdate(Update update) {
        boolean status = false;
        int players = 0;

        OkHttpClient client = new OkHttpClient().newBuilder().build();
        Request request = new Request.Builder().url("https://mcapi.us/server/status?ip=mc.evermine.eu").build();

        try {
            try (Response response = client.newCall(request).execute()) {
                ResponseBody responseBody = response.body();
                if (responseBody == null)
                    throw new NullPointerException();
                JsonElement parser = JsonParser.parseReader(responseBody.charStream());
                if (!parser.isJsonObject())
                    throw new IOException();
                status = parser.getAsJsonObject().get("online").getAsBoolean();
                players = parser.getAsJsonObject().getAsJsonObject("players").get("now").getAsInt();
            }
        } catch (IOException | NullPointerException e) {
            logger.error(language.getLanguageString("error-status-request"), e);
        }

        String messageText = language.getLanguageString("status-server-message", List.of(status ? "ONLINE" : "OFFLINE", String.valueOf(players)));
        InlineKeyboardMarkup keyboardMarkup = language.getKeyboard("back-keyboard");
        if (keyboardMarkup == null) {
            logger.error(language.getLanguageString("not-matching-buttons"));
        }

        ActionsAPIHelper.editMessage(messageText, getCallbackChatID(update), getCallbackMessageID(update), keyboardMarkup);
        return true;
    }

    @Override
    public String getCallback() {
        return "status";
    }
}