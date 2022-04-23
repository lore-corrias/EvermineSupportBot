package eu.evermine.it.updateshandlers.handlers.callbacks;

import eu.evermine.it.configs.yamls.LanguageYaml;
import eu.evermine.it.updateshandlers.handlers.CallbacksHandler;
import eu.evermine.it.wrappers.LanguageWrapper;
import org.slf4j.Logger;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class ServerIpCallback extends AbstractCallback {

    private final Logger logger;
    private final LanguageWrapper language;


    public ServerIpCallback(CallbacksHandler handler, Logger logger, LanguageWrapper language) {
        super(handler);
        this.logger = logger;
        this.language = language;
    }

    @Override
    public void handleCallback(Update update) {
        AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
        answerCallbackQuery.setCallbackQueryId(update.getCallbackQuery().getId());
        answerCallbackQuery.setText(language.getLanguageString(LanguageYaml.LANGUAGE_INDEXES.SERVER_IP_CALLBACK_TEXT));
        answerCallbackQuery.setShowAlert(true);
        try {
            super.getCallbacksHandler().execute(answerCallbackQuery);
        } catch (TelegramApiException e) {
            logger.error(language.getLanguageString(LanguageYaml.LANGUAGE_INDEXES.ERROR_CALLBACK_SERVER_IP), e);
        }
    }

    @Override
    public String getCallback() {
        return "serverip";
    }

    @Override
    public String getCallbackDescription() {
        return "Mostra un popup che contiene l'indirizzo IP del server.";
    }
}
