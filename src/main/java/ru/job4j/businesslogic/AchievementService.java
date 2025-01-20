package ru.job4j.businesslogic;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Component
public class AchievementService {

    @PostConstruct
    public void init() {
        System.out.println("Bean is going through @PostConstruct init.");
    }

    @PreDestroy
    public void destroy() {
        System.out.println("Bean will be destroyed via @PreDestroy.");
    }
}
