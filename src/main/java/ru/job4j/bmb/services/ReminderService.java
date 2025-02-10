package ru.job4j.bmb.services;

import org.jvnet.hk2.annotations.Service;
import org.springframework.scheduling.annotation.Scheduled;
import ru.job4j.bmb.content.Content;

import java.time.LocalDate;
import java.time.ZoneId;

@Service
public class ReminderService {
    private final TgUI tgUI;
    private final MoodLogService moodLogService;
    private final SentContent sentContent;

    public ReminderService(TgUI tgUI, MoodLogService moodLogService, SentContent sentContent) {
        this.tgUI = tgUI;
        this.moodLogService = moodLogService;
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
       for (var user : moodLogService.findUsersWhoDidNotVoteToday(startOfDay, endOfDay)) {
           var content = new Content(user.getChatId());
           content.setText("Как настроение?");
           content.setMarkup(tgUI.buildButtons());
           sentContent.sent(content);
       }
    }
}
