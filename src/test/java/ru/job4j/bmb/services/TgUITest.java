package ru.job4j.bmb.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import ru.job4j.bmb.model.Mood;
import ru.job4j.bmb.test.MoodFakeRepository;
import ru.job4j.bmb.repository.MoodRepository;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ContextConfiguration(classes = {TgUI.class, MoodFakeRepository.class})
class TgUITest {

    @Autowired
    @Qualifier("moodFakeRepository")
    private MoodRepository moodRepository;
    @Autowired
    private TgUI tgUI;

    @BeforeEach
    void setUp() {
        moodRepository.deleteAll();
    }

    @Test
    public void whenBtnGood() {
        assertThat(moodRepository).isNotNull();
        String expected = "Good Day";
        moodRepository.save(new Mood("Good Day", true));
        var markup = tgUI.buildButtons();
        var actual = markup.getKeyboard().iterator().next().iterator().next().getText();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void whenMultipleMoods() {
        moodRepository.save(new Mood("Happy", true));
        moodRepository.save(new Mood("Sad", true));
        var markup = tgUI.buildButtons();
        assertThat(markup.getKeyboard()).hasSize(2);
        assertThat(markup.getKeyboard().get(0).get(0).getText()).isEqualTo("Happy");
        assertThat(markup.getKeyboard().get(1).get(0).getText()).isEqualTo("Sad");
    }

    @Test
    public void whenNoMoods() {
        var markup = tgUI.buildButtons();
        assertThat(markup.getKeyboard()).isEmpty();
    }
}