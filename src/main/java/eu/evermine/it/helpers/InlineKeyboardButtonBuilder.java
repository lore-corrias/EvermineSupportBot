package eu.evermine.it.helpers;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;

public class InlineKeyboardButtonBuilder {

    private String text;
    private String url;
    private String callbackData;

    private InlineKeyboardButtonBuilder() {
    }

    public static InlineKeyboardButtonBuilder getBuilder() {
        return new InlineKeyboardButtonBuilder();
    }

    public InlineKeyboardButtonBuilder setText(String text) {
        this.text = text;
        return this;
    }

    public InlineKeyboardButtonBuilder setUrl(String url) {
        this.url = url;
        return this;
    }

    public InlineKeyboardButtonBuilder setCallbackData(String callbackData) {
        this.callbackData = callbackData;
        return this;
    }

    public InlineKeyboardButton buildButton() {
        if (text == null || (callbackData == null && url == null))
            throw new IllegalArgumentException();
        if (callbackData != null) {
            return new InlineKeyboardButton(text).callbackData(callbackData);
        } else {
            return new InlineKeyboardButton(text).url(url);
        }
    }
}
