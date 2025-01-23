package ru.job4j;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import ru.job4j.bmb.content.Content;
import ru.job4j.bmb.services.TelegramBotService;

public class Main {
    Long aLong = 64363636363636L;
    public static void main(String[] args) {
        SpringApplication.run(ru.job4j.bmb.Main.class, args);
    }

    @Bean
    public CommandLineRunner initTelegramApi(ApplicationContext ctx) {
        return args -> {
            var bot = ctx.getBean(TelegramBotService.class);
            bot.receive(new Content(aLong));
        };
    }
}
