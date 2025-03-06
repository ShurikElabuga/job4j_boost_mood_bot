package ru.job4j.bmb.services;

import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.job4j.bmb.content.Content;
import ru.job4j.bmb.model.MoodLog;
import ru.job4j.bmb.repository.AwardRepository;
import ru.job4j.bmb.repository.MoodLogRepository;

import java.util.List;

@Service
public class AchievementService implements ApplicationListener<UserEvent> {
    private final MoodLogRepository moodLogRepository;
    private final AwardRepository awardRepository;

    public AchievementService(MoodLogRepository moodLogRepository, AwardRepository awardRepository) {
        this.moodLogRepository = moodLogRepository;
        this.awardRepository = awardRepository;
    }

    @Transactional
    @Override
    public void onApplicationEvent(UserEvent event) {
        var user = event.getUser();
        List<MoodLog> list = moodLogRepository.findByUserId(user.getId());
        int counter = 0;
        for (MoodLog log : list) {
            if (log.getMood().isGood()) {
                counter++;
            } else {
                counter--;
            }
        }
        StringBuilder message = new StringBuilder();
        int currentCounter = counter;
        if (counter > 0) {
            message.append("Ваши достижения: \n");
            awardRepository.findAll().stream()
                    .filter(award -> award.getDays() <= currentCounter)
                    .forEach(award -> message.append(award.getTitle())
                            .append(" : ").append(award.getDescription()));
        } else {
            message.append("У вас пока нет достижений.");
        }
        var content = new Content(user.getChatId());
        content.setText(message.toString());
    }
}
