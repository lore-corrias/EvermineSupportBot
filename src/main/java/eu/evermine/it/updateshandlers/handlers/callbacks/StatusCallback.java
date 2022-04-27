package eu.evermine.it.updateshandlers.handlers.callbacks;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import eu.evermine.it.configs.yamls.LanguageYaml;
import eu.evermine.it.updateshandlers.handlers.CallbacksHandler;
import eu.evermine.it.wrappers.LanguageWrapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class StatusCallback extends AbstractCallback {

    private final Logger logger;
    private final LanguageWrapper language;


    public StatusCallback(CallbacksHandler callbacksHandler, Logger logger, LanguageWrapper language) {
        super(callbacksHandler);
        this.logger = logger;
        this.language = language;
    }

    @Override
    public boolean handleCallback(Update update) {
        boolean status = false;
        int players = 0;

        OkHttpClient client = new OkHttpClient().newBuilder().build();
        Request request = new Request.Builder().url("https://mcapi.us/server/status?ip=mc.evermine.eu").build();

        try {
            try(Response response = client.newCall(request).execute()) {
                ResponseBody responseBody = response.body();
                if(responseBody == null)
                    throw new NullPointerException();
                JsonElement parser = JsonParser.parseReader(responseBody.charStream());
                if(!parser.isJsonObject())
                    throw new IOException();
                status = parser.getAsJsonObject().get("online").getAsBoolean();
                players = parser.getAsJsonObject().getAsJsonObject("players").get("now").getAsInt();
            }
        } catch (IOException | NullPointerException e) {
            logger.error(language.getLanguageString(LanguageYaml.LANGUAGE_INDEXES.ERROR_STATUS_REQUEST), e);
        }

        String messageText = language.getLanguageString(LanguageYaml.LANGUAGE_INDEXES.STATUS_SERVER_MESSAGE, List.of(status ? "ONLINE" : "OFFLINE", String.valueOf(players)));
        InlineKeyboardMarkup keyboardMarkup = language.getKeyboard(LanguageYaml.KEYBOARDS_INDEXES.BACK_KEYBOARD);
        if(keyboardMarkup == null) {
            logger.error(language.getLanguageString(LanguageYaml.LANGUAGE_INDEXES.NOT_MATCHING_BUTTONS));
        }

        editMessage(messageText, getCallbackChatID(update), getCallbackMessageID(update), keyboardMarkup);
        return true;
    }

    @Override
    public String getCallback() {
        return "status";
    }
}
