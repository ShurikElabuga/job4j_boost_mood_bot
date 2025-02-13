package ru.job4j.bmb.services;

import org.junit.jupiter.api.Test;
import ru.job4j.bmb.content.Content;
import ru.job4j.bmb.model.Mood;
import ru.job4j.bmb.model.MoodLog;
import ru.job4j.bmb.model.User;
import ru.job4j.bmb.test.MoodFakeRepository;
import ru.job4j.bmb.test.MoodLogFakeRepository;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.*;

class ReminderServiceTest {

    @Test
    public void whenMoodGood() {
        var result = new ArrayList<Content>();
        var sentContent = new SentContent() {
            @Override
            public void sent(Content content) {
                result.add(content);
            }
        };
        var moodRepository = new MoodFakeRepository();
        moodRepository.save(new Mood("Good", true));
        var moodLogRepository = new MoodLogFakeRepository();
        var moodLogService = new MoodLogService(moodLogRepository);
        var user = new User();
        user.setChatId(100);
        var moodLog = new MoodLog();
        moodLog.setUser(user);
        var yesterday = LocalDate.now()
                .minusDays(10)
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli() - 1;
        moodLog.setCreatedAt(yesterday);
        moodLogRepository.save(moodLog);
        var tgUI = new TgUI(moodRepository);
        new ReminderService(tgUI, moodLogService, sentContent)
                .remindUsers();
        assertThat(result.iterator().next().getMarkup().getKeyboard()
                .iterator().next().iterator().next().getText()).isEqualTo("Good");
    }

    @Test
    public void whenMoodBad() {
        var result = new ArrayList<Content>();
        var sentContent = new SentContent() {
            @Override
            public void sent(Content content) {
                result.add(content);
            }
        };
        var moodRepository = new MoodFakeRepository();
        moodRepository.save(new Mood("Bad", false));
        var moodLogRepository = new MoodLogFakeRepository();
        var moodLogService = new MoodLogService(moodLogRepository);
        var user = new User();
        user.setChatId(100);
        var moodLog = new MoodLog();
        moodLog.setUser(user);
        var yesterday = LocalDate.now()
                .minusDays(1)
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli() - 1;
        moodLog.setCreatedAt(yesterday);
        moodLogRepository.save(moodLog);
        var tgUI = new TgUI(moodRepository);
        new ReminderService(tgUI, moodLogService, sentContent)
                .remindUsers();
        assertThat(result.iterator().next().getMarkup().getKeyboard()
                .iterator().next().iterator().next().getText()).isEqualTo("Bad");
    }

    @Test
    public void whenMoodGoodButVotedTodayThenResultIsEmpty() {
        var result = new ArrayList<Content>();
        var sentContent = new SentContent() {
            @Override
            public void sent(Content content) {
                result.add(content);
            }
        };
        var moodRepository = new MoodFakeRepository();
        moodRepository.save(new Mood("Good", true));
        var moodLogRepository = new MoodLogFakeRepository();
        var moodLogService = new MoodLogService(moodLogRepository);
        var user = new User();
        user.setChatId(100);
        var moodLog = new MoodLog();
        moodLog.setUser(user);
        var yesterday = LocalDate.now()
                .plusDays(1)
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli() - 100;
        moodLog.setCreatedAt(yesterday);
        moodLogRepository.save(moodLog);
        var tgUI = new TgUI(moodRepository);
        new ReminderService(tgUI, moodLogService, sentContent)
                .remindUsers();
        assertThat(result).isEmpty();
    }
}