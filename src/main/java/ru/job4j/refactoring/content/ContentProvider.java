package ru.job4j.refactoring.content;

public interface ContentProvider {
    Content byMood(Long chatId, Long moodId);
}
