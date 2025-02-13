package ru.job4j.bmb.test;

import org.springframework.test.fake.CrudRepositoryFake;
import ru.job4j.bmb.model.Mood;
import ru.job4j.bmb.repository.MoodRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MoodFakeRepository extends CrudRepositoryFake<Mood, Long> implements MoodRepository {

    @Override
    public List<Mood> findAll() {
        return new ArrayList<>(memory.values());
    }

    @Override
    public Mood save(Mood mood) {
        if (mood.getId() == null) {
            Long lastId = memory.keySet().stream()
                            .max(Comparator.naturalOrder())
                                    .orElse(0L);
            mood.setId(lastId + 1);
        }
        return super.save(mood);
    }

}
