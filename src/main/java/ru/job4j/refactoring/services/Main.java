package ru.job4j.refactoring.services;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.job4j.refactoring.services.TgRemoteService;

@EnableScheduling
@SpringBootApplication
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) { /*ctx — это объект типа ApplicationContext, который содержит информацию обо всех бинах в приложении*/
        return args -> {
            var bot = ctx.getBean(TgRemoteService.class); /*получение экземпляра бота из контекста Spring. Переменная bot теперь содержит экземпляр бота, готового к регистрации*/
            var botsApi = new TelegramBotsApi(DefaultBotSession.class); /*TelegramBotsApi — это класс из библиотеки telegrambots,
            который отвечает за регистрацию ботов и обработку их сессий. В данном случае создаётся объект botsApi с использованием DefaultBotSession
            DefaultBotSession — это сессия по умолчанию, которая используется для взаимодействия с Telegram API*/
            try { /*выполняется регистрация бота в системе с помощью метода registerBot()*/
                botsApi.registerBot(bot); /*регистрация бота в Telegram API.
                Это означает, что бот начинает получать обновления (сообщения и другие события) от Telegram.
                bot — это объект вашего класса TgRemoteService, который реализует логику бота*/
                System.out.println("Бот успешно зарегистрирован");
                /*Если регистрация проходит успешно, то выводится сообщение в консоль: "Бот успешно зарегистрирован"
                 Если возникает исключение (например, проблемы с подключением к Telegram API), оно будет перехвачено в блоке catch, и с помощью e.printStackTrace() информация об исключении будет выведена в консоль */
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        };
    }
}
