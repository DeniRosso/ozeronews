package com.example.ozeronews.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Component
public class ArticlesDeleteScheduler {

    @Autowired
    ArticleDeleteService articleDeleteService;

    @Value("${article.archive.day}")
    private long articleArchiveDay;

//    @Scheduled(cron = "0 0 4 * * ?") // Удаление статей в 4:00 ежедневно
    @Scheduled(cron = "0 0 * * * ?") // Удаление статей каждый в час
//    @Scheduled(initialDelay = 10000, fixedDelay = 1000000000)
//    @Scheduled(initialDelay = 10000) // так не работает
    public void deleteArticles() {

        ZonedDateTime date = ZonedDateTime.now(ZoneId.of("UTC")).minusDays(articleArchiveDay);

        System.out.println(LocalDateTime.now() + ": Starting deleting articles");
        articleDeleteService.deleteArticles(date);
        System.out.println(LocalDateTime.now() + ": Deletion of articles completed");
    }
}
