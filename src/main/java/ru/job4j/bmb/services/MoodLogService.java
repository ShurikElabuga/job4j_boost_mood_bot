package ru.job4j.bmb.services;

import org.springframework.stereotype.Service;
import ru.job4j.bmb.model.MoodLog;
import ru.job4j.bmb.model.User;
import ru.job4j.bmb.repository.MoodLogRepository;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class MoodLogService {
    private final MoodLogRepository moodLogRepository;

    public MoodLogService(MoodLogRepository moodLogRepository) {
        this.moodLogRepository = moodLogRepository;
    }

    List<MoodLog> findAll() {
        return moodLogRepository.findAll();
    }

    List<MoodLog> findByUserId(Long userId) {
        return moodLogRepository.findByUserId(userId);
    }

    List<User> findUsersWhoDidNotVoteToday(long startOfDay, long endOfDay) {
        List<MoodLog> list = findAll();
        return  list.stream()
                .filter(moodLog -> moodLog.getCreatedAt() < startOfDay || moodLog.getCreatedAt() > endOfDay)
                .map(MoodLog::getUser)
                .distinct()
                .collect(Collectors.toList());
    }

    Stream<MoodLog> findByUserIdOrderByCreatedAtDesc(Long userId) {
        List<MoodLog> list = findAll();
        return list.stream()
                .filter(moodLog -> moodLog.getUser().getId().equals(userId))
                .sorted(Comparator.comparing(MoodLog::getCreatedAt).reversed());
    }

    List<MoodLog> findMoodLogsForWeek(Long userId, long weekStart) {
        List<MoodLog> list = findAll();
        return list.stream()
                .filter(moodLog -> moodLog.getUser().getId().equals(userId))
                .filter(moodLog -> moodLog.getCreatedAt() >= weekStart)
                .collect(Collectors.toList());
    }

    List<MoodLog> findMoodLogsForMonth(Long userId, long monthStart) {
        List<MoodLog> list = findAll();
        return list.stream()
                .filter(moodLog -> moodLog.getUser().getId().equals(userId))
                .filter(moodLog -> moodLog.getCreatedAt() >= monthStart)
                .collect(Collectors.toList());
    }
}
