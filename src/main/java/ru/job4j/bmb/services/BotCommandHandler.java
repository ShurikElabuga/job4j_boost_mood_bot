package ru.job4j.bmb.services;


import org.telegram.telegrambots.meta.api.objects.Message;
import ru.job4j.bmb.model.User;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import ru.job4j.bmb.content.Content;
import ru.job4j.bmb.repository.UserRepository;

import java.util.Optional;

@Service
public class BotCommandHandler {
    private final UserRepository userRepository;
    private final MoodService moodService;
    private final TgUI tgUI;

    public BotCommandHandler(UserRepository userRepository,
                             MoodService moodService,
                             TgUI tgUI) {
        this.userRepository = userRepository;
        this.moodService = moodService;
        this.tgUI = tgUI;
    }

   public Optional<Content> commands(Message message) {
        Optional<Content> result;
        result = switch (message.getText()) {
            case "/start" -> handleStartCommand(message.getChatId(), message.getFrom().getId());
            case "/week_mood_log" -> moodService.weekMoodLogCommand(message.getChatId(), message.getFrom().getId());
            case "/month_mood_log" -> moodService.monthMoodLogCommand(message.getChatId(), message.getFrom().getId());
            case "/award" -> moodService.awards(message.getChatId(), message.getFrom().getId());
            default -> Optional.empty();
        };
        return result;
    }

   public Optional<Content> handleCallback(CallbackQuery callback) {
        var moodId = Long.valueOf(callback.getData());
        var user = userRepository.findByClientId(callback.getFrom().getId());
        return Optional.of(moodService.chooseMood(user, moodId));
    }

    private Optional<Content> handleStartCommand(long chatId, Long clientId) {
        var user = new User();
        if (user.getClientId() != clientId) {
            user.setClientId(clientId);
        }

        user.setChatId(chatId);
        userRepository.save(user);
        var content = new Content(user.getChatId());
        content.setText("Как настроение?");
        content.setMarkup(tgUI.buildButtons());
        return Optional.of(content);
    }
}
