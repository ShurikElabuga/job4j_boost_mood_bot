package ru.job4j.bmb.services;

import org.jvnet.hk2.annotations.Service;
import org.springframework.scheduling.annotation.Scheduled;
import ru.job4j.bmb.content.Content;
import ru.job4j.bmb.repository.MoodLogRepository;

import java.time.LocalDate;
import java.time.ZoneId;

@Service
public class ReminderService {
    private final TgUI tgUI;
    private final MoodLogRepository moodLogRepository;
    private final SentContent sentContent;

    public ReminderService(TgUI tgUI, MoodLogRepository moodLogRepository, SentContent sentContent) {
        this.tgUI = tgUI;
        this.moodLogRepository = moodLogRepository;
        this.sentContent = sentContent;
    }

    @Scheduled(fixedRateString = "${recommendation.alert.period}")
    public void remindUsers() {
       var startOfDay = LocalDate.now()
               .atStartOfDay(ZoneId.systemDefault())
               .toInstant()
               .toEpochMilli();
       var endOfDay = LocalDate.now()
               .plusDays(1)
               .atStartOfDay(ZoneId.systemDefault())
               .toInstant()
               .toEpochMilli() - 1;
       for (var user : moodLogRepository.findUsersWhoDidNotVoteToday(startOfDay, endOfDay)) {
           var content = new Content(user.getChatId());
           content.setText("Как настроение?");
           content.setMarkup(tgUI.buildButtons());
           sentContent.sent(content);
       }
    }
}
