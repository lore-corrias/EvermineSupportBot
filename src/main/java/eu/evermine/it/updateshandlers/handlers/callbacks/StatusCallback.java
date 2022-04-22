package eu.evermine.it.updateshandlers.handlers.callbacks;

import eu.evermine.it.configs.yamls.LanguageYaml;
import eu.evermine.it.updateshandlers.handlers.CallbacksHandler;
import eu.evermine.it.wrappers.LanguageWrapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.util.List;
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
    public void handleCallback(Update update) {
        boolean status = false;
        int players = 0;

        OkHttpClient client = new OkHttpClient().newBuilder().build();
        Request request = new Request.Builder().url("https://mcapi.us/server/status?ip=mc.evermine.eu").build();

        try {
            try(Response response = client.newCall(request).execute()) {
                ResponseBody responseBody = response.body();
                JSONObject jsonObject = new JSONObject(Objects.requireNonNull(responseBody).string());
                status = jsonObject.getBoolean("online");
                players = jsonObject.getJSONObject("players").getInt("now");
            }
        } catch (IOException | NullPointerException e) {
            logger.error(language.getLanguageString(LanguageYaml.LANGUAGE_INDEXES.ERROR_STATUS_REQUEST), e);
        }

        String messageText = language.getLanguageString(LanguageYaml.LANGUAGE_INDEXES.STATUS_SERVER_MESSAGE, List.of(status ? "<i>ONLINE</i>" : "<i>OFFLINE</i>", String.valueOf(players)));

        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(getCallbackChatID(update));
        editMessageText.setMessageId(getCallbackMessageID(update));
        editMessageText.setText(messageText);
        editMessageText.setParseMode("HTML");
        InlineKeyboardMarkup keyboardMarkup = language.getKeyboard(LanguageYaml.KEYBOARDS_INDEXES.BACK_KEYBOARD);
        if(keyboardMarkup == null) {
            logger.error(language.getLanguageString(LanguageYaml.LANGUAGE_INDEXES.NOT_MATCHING_BUTTONS));
        } else {
            editMessageText.setReplyMarkup(keyboardMarkup);
        }

        try {
            getCallbacksHandler().execute(editMessageText);
        } catch (TelegramApiException e) {
            logger.error(language.getLanguageString(LanguageYaml.LANGUAGE_INDEXES.ERROR_SEND_STATUS_MESSAGE), e);
        }
    }

    @Override
    public String getCallback() {
        return "status";
    }

    @Override
    public String getCallbackDescription() {
        return "Ottieni lo status del server.";
    }
}
