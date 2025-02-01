package ru.job4j.bmb.services;

import org.springframework.stereotype.Service;
import ru.job4j.bmb.content.Content;
import ru.job4j.bmb.model.*;
import ru.job4j.bmb.repository.AchievementRepository;
import ru.job4j.bmb.repository.MoodLogRepository;
import ru.job4j.bmb.repository.MoodRepository;
import ru.job4j.bmb.repository.UserRepository;
import java.time.Instant;
import java.time.Period;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class MoodService {
    private final MoodLogRepository moodLogRepository;
    private final RecommendationEngine recommendationEngine;
    private final UserRepository userRepository;
    private final AchievementRepository achievementRepository;
    private final MoodRepository moodRepository;
    private final DateTimeFormatter formatter = DateTimeFormatter
            .ofPattern("dd-MM-yyyy HH:mm")
            .withZone(ZoneId.systemDefault());

    public MoodService(MoodLogRepository moodLogRepository,
                       RecommendationEngine recommendationEngine,
                       UserRepository userRepository,
                       AchievementRepository achievementRepository,
                       MoodRepository moodRepository) {
        this.moodLogRepository = moodLogRepository;
        this.recommendationEngine = recommendationEngine;
        this.userRepository = userRepository;
        this.achievementRepository = achievementRepository;
        this.moodRepository = moodRepository;
    }

    public Content chooseMood(User user, Long moodId) {
        Mood mood = moodRepository.findById(moodId)
                .orElseThrow(() -> new IllegalArgumentException("Mood not found"));
        moodLogRepository.save(new MoodLog(user, mood, System.currentTimeMillis() / 1000));
        return recommendationEngine.recommendFor(user.getChatId(), moodId);
    }

    public Optional<Content> weekMoodLogCommand(long chatId, Long clientId) {
        List<MoodLog> logs = moodLogRepository.findForPeriod(clientId, Period.ofWeeks(1));
        String formatLogs = formatMoodLogs(logs, "Logs for the past week");
        var content = new Content(chatId);
        content.setText(formatLogs);
        return Optional.of(content);
    }

    public Optional<Content> monthMoodLogCommand(long chatId, Long clientId) {
        List<MoodLog> logs = moodLogRepository.findForPeriod(clientId, Period.ofMonths(1));
        String formatLogs = formatMoodLogs(logs, "Logs for the past month");
        var content = new Content(chatId);
        content.setText(formatLogs);
        return Optional.of(content);
    }

    private String formatMoodLogs(List<MoodLog> logs, String title) {
        if (logs.isEmpty()) {
            return title + ":\nNo mood logs found.";
        }
        var sb = new StringBuilder(title + ":\n");
        logs.forEach(log -> {
            String formattedDate = formatter.format(Instant.ofEpochSecond(log.getCreatedAt()));
            sb.append(formattedDate).append(": ").append(log.getMood().getText()).append("\n");
        });
        return sb.toString();
    }

    public Optional<Content> awards(long chatId, Long clientId) {
        List<Achievement> achievements = achievementRepository.findByUserId(clientId);
        if (achievements.isEmpty()) {
            var content = new Content(chatId);
            content.setText("You haven't awards yet!");
            return Optional.of(content);
        }
         var txt = new StringBuilder("You awards: \n");
        achievements.forEach(achievement -> txt.append("- ").append(achievement.getAward().getTitle()).append("\n"));
        var content = new Content(chatId);
        content.setText(txt.toString());
        return Optional.of(content);
    }
}
