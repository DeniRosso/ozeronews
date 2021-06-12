package com.example.ozeronews.service;

import com.example.ozeronews.models.NewsResource;
import com.example.ozeronews.repo.NewsResourceRepository;
import com.example.ozeronews.service.parsing.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ArticlesCollectScheduler {

//    https://michael-smirnov.ru/ - RSS каналы
//    https://marketmedia.ru/rss/ - marketmedia
//    https://novayagazeta.ru/news - novayagazeta

    private NewsResourceRepository newsResourceRepository;
    private ParsingRIA parsingRIA;
    private ParsingTsargrad parsingTsargrad;
    private ParsingLentaru parsingLentaru;
    private ParsingMeduza parsingMeduza;
    private ParsingINOSMI parsingINOSMI;
    private ParsingXTrue parsingXTrue;
    private ParsingLife parsingLife;
    private ParsingFontanka parsingFontanka;
    private ParsingDniru parsingDniru;
    private ParsingRegnum parsingRegnum;
    private ParsingRBC parsingRBC;
    private ParsingVzglyad parsingVzglyad;
    private ParsingRT parsingRT;
    private ParsingIzvestia parsingIzvestia;
    private ParsingMK parsingMK;
    private ParsingNewsru parsingNewsru;
    private ParsingVedomosti parsingVedomosti;
    private ParsingGazetaRu parsingGazetaRu;
    private ParsingRG parsingRG;
    private ParsingForbes parsingForbes;
    private ParsingFinam parsingFinam;
    private ParsingDW parsingDW;
    private ParsingNewsUN parsingNewsUN;
    private ParsingSportExpress parsingSportExpress;
    private ParsingTASS parsingTASS;
    private ParsingInvesting parsingInvesting;
    private ParsingKommersant parsingKommersant;
    private ParsingRetailRu parsingRetailRu;
    private ParsingActualNews parsingActualNews;
    private ParsingSvoboda parsingSvoboda;
    private ParsingLifehacker parsingLifehacker;

//    @Value("${schedule.work}")
//    private String scheduleTime;

    public ArticlesCollectScheduler(NewsResourceRepository newsResourceRepository,
                                    ParsingRIA parsingRIA,
                                    ParsingTsargrad parsingTsargrad,
                                    ParsingLentaru parsingLentaru,
                                    ParsingMeduza parsingMeduza,
                                    ParsingINOSMI parsingINOSMI,
                                    ParsingXTrue parsingXTrue,
                                    ParsingLife parsingLife,
                                    ParsingFontanka parsingFontanka,
                                    ParsingDniru parsingDniru,
                                    ParsingRegnum parsingRegnum,
                                    ParsingRBC parsingRBC,
                                    ParsingVzglyad parsingVzglyad,
                                    ParsingRT parsingRT,
                                    ParsingIzvestia parsingIzvestia,
                                    ParsingMK parsingMK,
                                    ParsingNewsru parsingNewsru,
                                    ParsingVedomosti parsingVedomosti,
                                    ParsingGazetaRu parsingGazetaRu,
                                    ParsingRG parsingRG,
                                    ParsingForbes parsingForbes,
                                    ParsingFinam parsingFinam,
                                    ParsingDW parsingDW,
                                    ParsingNewsUN parsingNewsUN,
                                    ParsingSportExpress parsingSportExpress,
                                    ParsingTASS parsingTASS,
                                    ParsingInvesting parsingInvesting,
                                    ParsingKommersant parsingKommersant,
                                    ParsingRetailRu parsingRetailRu,
                                    ParsingActualNews parsingActualNews,
                                    ParsingSvoboda parsingSvoboda,
                                    ParsingLifehacker parsingLifehacker) {

        this.newsResourceRepository = newsResourceRepository;
        this.parsingRIA = parsingRIA;
        this.parsingTsargrad = parsingTsargrad;
        this.parsingLentaru = parsingLentaru;
        this.parsingMeduza = parsingMeduza;
        this.parsingINOSMI = parsingINOSMI;
        this.parsingXTrue = parsingXTrue;
        this.parsingLife = parsingLife;
        this.parsingFontanka = parsingFontanka;
        this.parsingDniru = parsingDniru;
        this.parsingRegnum = parsingRegnum;
        this.parsingRBC = parsingRBC;
        this.parsingVzglyad = parsingVzglyad;
        this.parsingRT = parsingRT;
        this.parsingIzvestia = parsingIzvestia;
        this.parsingMK = parsingMK;
        this.parsingNewsru = parsingNewsru;
        this.parsingVedomosti = parsingVedomosti;
        this.parsingGazetaRu = parsingGazetaRu;
        this.parsingRG = parsingRG;
        this.parsingForbes = parsingForbes;
        this.parsingFinam = parsingFinam;
        this.parsingDW = parsingDW;
        this.parsingNewsUN = parsingNewsUN;
        this.parsingSportExpress = parsingSportExpress;
        this.parsingTASS = parsingTASS;
        this.parsingInvesting = parsingInvesting;
        this.parsingKommersant = parsingKommersant;
        this.parsingRetailRu = parsingRetailRu;
        this.parsingActualNews = parsingActualNews;
        this.parsingSvoboda = parsingSvoboda;
        this.parsingLifehacker = parsingLifehacker;
    }

    @Scheduled(initialDelay = 10000, fixedRateString = "${article.collection.schedule}")
//    - Запуск операции через 1 сек. после старта приложени
//    - Время между заверщение одной операции и запуском следующей
//    @Scheduled(fixedRateString = "${schedule.work}")
//    - Фиксированное время между вызовами
    public void runCollectArticles() {
        System.out.println(LocalDateTime.now() + ": Start collecting articles");

//        parsingRIA.getArticles();
//        parsingTsargrad.getArticles();
//        parsingLentaru.getArticles();
//        parsingMeduza.getArticles();
//        parsingINOSMI.getArticles();
//        parsingXTrue.getArticles();
//        parsingLife.getArticles();
//        parsingFontanka.getArticles();
//        parsingDniru.getArticles();
//        parsingRegnum.getArticles();
//        parsingRBC.getArticles();
//        parsingVzglyad.getArticles();
//        parsingRT.getArticles();
//        parsingIzvestia.getArticles();
//        parsingMK.getArticles();
//        parsingNewsru.getArticles();
//        parsingVedomosti.getArticles();
//        parsingGazetaRu.getArticles();
//        parsingRG.getArticles();
//        parsingForbes.getArticles();
//        parsingFinam.getArticles();
//        parsingDW.getArticles();
//        parsingNewsUN.getArticles();
//        parsingSportExpress.getArticles();
//        parsingTASS.getArticles();
////        parsingInvesting.getArticles();
//        parsingKommersant.getArticles();
//        parsingRetailRu.getArticles();
//        parsingActualNews.getArticles();
//        parsingSvoboda.getArticles();
//        parsingLifehacker.getArticles();

        Iterable<NewsResource> newsResources = newsResourceRepository.findAll();
        for (NewsResource newsResource : newsResources) {
            if (newsResource.isActive() && true) {
                switch (newsResource.getResourceKey()) {
                    case "ria":
                        parsingRIA.getArticles();
                        System.out.println(LocalDateTime.now() + ": Collection articles RIA completed");
                        break;
                    case "tsargrad":
                        parsingTsargrad.getArticles();
                        System.out.println(LocalDateTime.now() + ": Collection articles Tsargrad completed");
                        break;
                    case "lentaru":
                        parsingLentaru.getArticles();
                        System.out.println(LocalDateTime.now() + ": Collection articles LentaRu completed");
                        break;
                    case "meduza":
                        parsingMeduza.getArticles();
                        System.out.println(LocalDateTime.now() + ": Collection articles Meduza completed");
                        break;
                    case "inosmi":
                        parsingINOSMI.getArticles();
                        System.out.println(LocalDateTime.now() + ": Collection articles Inosmi completed");
                        break;
                    case "xtrue":
                        parsingXTrue.getArticles();
                        System.out.println(LocalDateTime.now() + ": Collection articles X-True completed");
                        break;
                    case "life":
                        parsingLife.getArticles();
                        System.out.println(LocalDateTime.now() + ": Collection articles Life completed");
                        break;
                    case "fontanka":
                        parsingFontanka.getArticles();
                        System.out.println(LocalDateTime.now() + ": Collection articles Fontanka completed");
                        break;
                    case "dniru":
                        parsingDniru.getArticles();
                        System.out.println(LocalDateTime.now() + ": Collection articles DniRu completed");
                        break;
                    case "regnum":
                        parsingRegnum.getArticles();
                        System.out.println(LocalDateTime.now() + ": Collection articles Regnum completed");
                        break;
                    case "rbc":
                        parsingRBC.getArticles();
                        System.out.println(LocalDateTime.now() + ": Collection articles RBC completed");
                        break;
                    case "rt":
                        parsingRT.getArticles();
                        System.out.println(LocalDateTime.now() + ": Collection articles RT completed");
                        break;
                    case "izvestia":
                        parsingIzvestia.getArticles();
                        System.out.println(LocalDateTime.now() + ": Collection articles Izvestia completed");
                        break;
                    case "vzglyad":
                        parsingVzglyad.getArticles();
                        System.out.println(LocalDateTime.now() + ": Collection articles Vzglyad completed");
                        break;
                    case "newsru":
                        parsingNewsru.getArticles();
                        System.out.println(LocalDateTime.now() + ": Collection articles NewsRu completed");
                        break;
                    case "vedomosti":
                        parsingVedomosti.getArticles();
                        System.out.println(LocalDateTime.now() + ": Collection articles Vedomosti completed");
                        break;
                    case "mk":
                        parsingMK.getArticles();
                        System.out.println(LocalDateTime.now() + ": Collection articles MK completed");
                        break;
                    case "gazetaru":
                        parsingGazetaRu.getArticles();
                        System.out.println(LocalDateTime.now() + ": Collection articles GazetaRu completed");
                        break;
                    case "rg":
                        parsingRG.getArticles();
                        System.out.println(LocalDateTime.now() + ": Collection articles RG completed");
                        break;
                    case "forbes":
                        parsingForbes.getArticles();
                        System.out.println(LocalDateTime.now() + ": Collection articles Forbes completed");
                        break;
                    case "finam":
                        parsingFinam.getArticles();
                        System.out.println(LocalDateTime.now() + ": Collection articles Finam completed");
                        break;
                    case "newsun":
                        parsingNewsUN.getArticles();
                        System.out.println(LocalDateTime.now() + ": Collection articles NewsUN completed");
                        break;
                    case "sportexpress":
                        parsingSportExpress.getArticles();
                        System.out.println(LocalDateTime.now() + ": Collection articles SportExpress completed");
                        break;
                    case "tass":
                        parsingTASS.getArticles();
                        System.out.println(LocalDateTime.now() + ": Collection articles TASS completed");
                        break;
                    case "investing-":
                        parsingInvesting.getArticles();
                        System.out.println(LocalDateTime.now() + ": Collection articles Investing completed");
                        break;
                    case "kommersant":
                        parsingKommersant.getArticles();
                        System.out.println(LocalDateTime.now() + ": Collection articles Kommersant completed");
                        break;
                    case "retailru":
                        parsingRetailRu.getArticles();
                        System.out.println(LocalDateTime.now() + ": Collection articles RetailRu completed");
                        break;
                    case "actualnews":
                        parsingActualNews.getArticles();
                        System.out.println(LocalDateTime.now() + ": Collection articles ActualNews completed");
                        break;
                    case "svoboda":
                        parsingSvoboda.getArticles();
                        System.out.println(LocalDateTime.now() + ": Collection articles Svoboda completed");
                        break;
                    case "lifehacker":
                        parsingLifehacker.getArticles();
                        System.out.println(LocalDateTime.now() + ": Collection articles Lifehacker completed");
                        break;
                }
            }
        }
        System.out.println(LocalDateTime.now() + ": Collection of articles completed");
    }
}
