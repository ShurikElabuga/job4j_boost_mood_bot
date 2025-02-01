package ru.job4j.bmb.repository;

import org.springframework.data.repository.CrudRepository;
import ru.job4j.bmb.model.MoodLog;

import java.time.Period;
import java.util.List;

public interface MoodLogRepository extends CrudRepository<MoodLog, Long> {
    List<MoodLog> findAll();
    List<MoodLog> findForPeriod(Long clientId, Period period);
}
