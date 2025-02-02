package ru.job4j.bmb.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.job4j.bmb.content.Content;
import ru.job4j.logging.SentContentException;

@Service
public class TelegramBotService extends TelegramLongPollingBot implements SentContent {

    private final BotCommandHandler handler;
    private final String botName;

    public TelegramBotService(@Value("${telegram.bot.name}") String botName,
                              @Value("${telegram.bot.token}") String botToken,
                              BotCommandHandler handler) {
        super(botToken);
        this.handler = handler;
        this.botName = botName;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasCallbackQuery()) {
            handler.handleCallback(update.getCallbackQuery())
                    .ifPresent(this::sent);
        } else if (update.hasMessage() && update.getMessage().getText() != null) {
            handler.commands(update.getMessage())
                    .ifPresent(this::sent);
        }
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public void sent(Content content) {
        try {
        if (content.getAudio() != null) {
            if (content.getText() != null) {
                execute(new SendAudio(String.valueOf(content.getChatId()), content.getAudio())).setText(content.getText());
            }
            execute(new SendAudio(String.valueOf(content.getChatId()), content.getAudio()));
        }
        if (content.getText() != null && content.getMarkup() != null) {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(content.getChatId());
            sendMessage.setText(content.getText());
            sendMessage.setReplyMarkup(content.getMarkup());
            execute(sendMessage);
        }
        if (content.getText() != null) {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(content.getChatId());
            sendMessage.setText(content.getText());
            execute(sendMessage);
        }
        if (content.getPhoto() != null) {
            if (content.getText() != null) {
                execute(new SendPhoto(String.valueOf(content.getChatId()), content.getPhoto())).setText(content.getText());
            }
            execute(new SendPhoto(String.valueOf(content.getChatId()), content.getPhoto()));
        }
        } catch (TelegramApiException e) {
            throw new SentContentException(e.getMessage(), e);
        }
    }
}
