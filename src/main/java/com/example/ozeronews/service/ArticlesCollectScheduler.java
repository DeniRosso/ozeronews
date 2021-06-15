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

    int articleCount;

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
    private ParsingFAN parsingFAN;
    private ParsingTopnewsru parsingTopnewsru;
    private ParsingNewsinfo parsingNewsinfo;
    private ParsingM24 parsingM24;
    private ParsingMatchTV parsingMatchTV;
    private ParsingInterfax parsingInterfax;
    private ParsingZaRulem parsingZaRulem;
    private ParsingTVRain parsingTVRain;
    private ParsingBagnet parsingBagnet;
    private ParsingABNews parsingABNews;

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
                                    ParsingLifehacker parsingLifehacker,
                                    ParsingFAN parsingFAN,
                                    ParsingTopnewsru parsingTopnewsru,
                                    ParsingNewsinfo parsingNewsinfo,
                                    ParsingM24 parsingM24,
                                    ParsingMatchTV parsingMatchTV,
                                    ParsingInterfax parsingInterfax,
                                    ParsingZaRulem parsingZaRulem,
                                    ParsingTVRain parsingTVRain,
                                    ParsingBagnet parsingBagnet,
                                    ParsingABNews parsingABNews) {

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
        this.parsingFAN = parsingFAN;
        this.parsingTopnewsru = parsingTopnewsru;
        this.parsingNewsinfo = parsingNewsinfo;
        this.parsingM24 = parsingM24;
        this.parsingMatchTV = parsingMatchTV;
        this.parsingInterfax = parsingInterfax;
        this.parsingZaRulem = parsingZaRulem;
        this.parsingTVRain = parsingTVRain;
        this.parsingBagnet = parsingBagnet;
        this.parsingABNews = parsingABNews;
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
//        parsingFAN.getArticles();
//        parsingTopnewsru.getArticles();
//        parsingNewsinfo.getArticles();
//        parsingM24.getArticles();
//        parsingMatchTV.getArticles();
//        parsingInterfax.getArticles();
//        parsingZaRulem.getArticles();
//        parsingTVRain.getArticles();
//        parsingBagnet.getArticles();
//        parsingABNews.getArticles();

        Iterable<NewsResource> newsResources = newsResourceRepository.findAll();
        for (NewsResource newsResource : newsResources) {
            if (newsResource.isActive() && true) {
                switch (newsResource.getResourceKey()) {
                    case "ria":
                        articleCount = parsingRIA.getArticles();
                        System.out.println(LocalDateTime.now() + ": Collection " + articleCount + " articles RIA completed");
                        break;
                    case "tsargrad":
                        articleCount = parsingTsargrad.getArticles();
                        System.out.println(LocalDateTime.now() + ": Collection " + articleCount + " articles Tsargrad completed");
                        break;
                    case "lentaru":
                        articleCount = parsingLentaru.getArticles();
                        System.out.println(LocalDateTime.now() + ": Collection " + articleCount + " articles LentaRu completed");
                        break;
                    case "meduza":
                        articleCount = parsingMeduza.getArticles();
                        System.out.println(LocalDateTime.now() + ": Collection " + articleCount + " articles Meduza completed");
                        break;
                    case "inosmi":
                        articleCount = parsingINOSMI.getArticles();
                        System.out.println(LocalDateTime.now() + ": Collection " + articleCount + " articles Inosmi completed");
                        break;
                    case "xtrue":
                        articleCount = parsingXTrue.getArticles();
                        System.out.println(LocalDateTime.now() + ": Collection " + articleCount + " articles X-True completed");
                        break;
                    case "life":
                        articleCount = parsingLife.getArticles();
                        System.out.println(LocalDateTime.now() + ": Collection " + articleCount + " articles Life completed");
                        break;
                    case "fontanka":
                        articleCount = parsingFontanka.getArticles();
                        System.out.println(LocalDateTime.now() + ": Collection " + articleCount + " articles Fontanka completed");
                        break;
                    case "dniru":
                        articleCount = parsingDniru.getArticles();
                        System.out.println(LocalDateTime.now() + ": Collection " + articleCount + " articles DniRu completed");
                        break;
                    case "regnum":
                        articleCount = parsingRegnum.getArticles();
                        System.out.println(LocalDateTime.now() + ": Collection " + articleCount + " articles Regnum completed");
                        break;
                    case "rbc":
                        articleCount = parsingRBC.getArticles();
                        System.out.println(LocalDateTime.now() + ": Collection " + articleCount + " articles RBC completed");
                        break;
                    case "rt":
                        articleCount = parsingRT.getArticles();
                        System.out.println(LocalDateTime.now() + ": Collection " + articleCount + " articles RT completed");
                        break;
                    case "izvestia":
                        articleCount = parsingIzvestia.getArticles();
                        System.out.println(LocalDateTime.now() + ": Collection " + articleCount + " articles Izvestia completed");
                        break;
                    case "vzglyad":
                        articleCount = parsingVzglyad.getArticles();
                        System.out.println(LocalDateTime.now() + ": Collection " + articleCount + " articles Vzglyad completed");
                        break;
                    case "newsru":
                        articleCount = parsingNewsru.getArticles();
                        System.out.println(LocalDateTime.now() + ": Collection " + articleCount + " articles NewsRu completed");
                        break;
                    case "vedomosti":
                        articleCount = parsingVedomosti.getArticles();
                        System.out.println(LocalDateTime.now() + ": Collection " + articleCount + " articles Vedomosti completed");
                        break;
                    case "mk":
                        articleCount = parsingMK.getArticles();
                        System.out.println(LocalDateTime.now() + ": Collection " + articleCount + " articles MK completed");
                        break;
                    case "gazetaru":
                        articleCount = parsingGazetaRu.getArticles();
                        System.out.println(LocalDateTime.now() + ": Collection " + articleCount + " articles GazetaRu completed");
                        break;
                    case "rg":
                        articleCount = parsingRG.getArticles();
                        System.out.println(LocalDateTime.now() + ": Collection " + articleCount + " articles RG completed");
                        break;
                    case "forbes":
                        articleCount = parsingForbes.getArticles();
                        System.out.println(LocalDateTime.now() + ": Collection " + articleCount + " articles Forbes completed");
                        break;
                    case "finam":
                        articleCount = parsingFinam.getArticles();
                        System.out.println(LocalDateTime.now() + ": Collection " + articleCount + " articles Finam completed");
                        break;
                    case "dw":
                        articleCount = parsingDW.getArticles();
                        System.out.println(LocalDateTime.now() + ": Collection " + articleCount + " articles dw completed");
                        break;
                    case "newsun":
                        articleCount = parsingNewsUN.getArticles();
                        System.out.println(LocalDateTime.now() + ": Collection " + articleCount + " articles NewsUN completed");
                        break;
                    case "sportexpress":
                        articleCount = parsingSportExpress.getArticles();
                        System.out.println(LocalDateTime.now() + ": Collection " + articleCount + " articles SportExpress completed");
                        break;
                    case "tass":
                        articleCount = parsingTASS.getArticles();
                        System.out.println(LocalDateTime.now() + ": Collection " + articleCount + " articles TASS completed");
                        break;
                    case "investing-":
                        articleCount = parsingInvesting.getArticles();
                        System.out.println(LocalDateTime.now() + ": Collection " + articleCount + " articles Investing completed");
                        break;
                    case "kommersant":
                        articleCount = parsingKommersant.getArticles();
                        System.out.println(LocalDateTime.now() + ": Collection " + articleCount + " articles Kommersant completed");
                        break;
                    case "retailru":
                        articleCount = parsingRetailRu.getArticles();
                        System.out.println(LocalDateTime.now() + ": Collection " + articleCount + " articles RetailRu completed");
                        break;
                    case "actualnews":
                        articleCount = parsingActualNews.getArticles();
                        System.out.println(LocalDateTime.now() + ": Collection " + articleCount + " articles ActualNews completed");
                        break;
                    case "svoboda":
                        articleCount = parsingSvoboda.getArticles();
                        System.out.println(LocalDateTime.now() + ": Collection " + articleCount + " articles Svoboda completed");
                        break;
                    case "lifehacker":
                        articleCount = parsingLifehacker.getArticles();
                        System.out.println(LocalDateTime.now() + ": Collection " + articleCount + " articles Lifehacker completed");
                        break;
                    case "fan":
                        articleCount = parsingFAN.getArticles();
                        System.out.println(LocalDateTime.now() + ": Collection " + articleCount + " articles FAN completed");
                        break;
                    case "topnewsru":
                        articleCount = parsingTopnewsru.getArticles();
                        System.out.println(LocalDateTime.now() + ": Collection " + articleCount + " articles TopNewsru completed");
                        break;
                    case "newsinfo":
                        articleCount = parsingNewsinfo.getArticles();
                        System.out.println(LocalDateTime.now() + ": Collection " + articleCount + " articles newsinfo completed");
                        break;
                    case "m24":
                        articleCount = parsingM24.getArticles();
                        System.out.println(LocalDateTime.now() + ": Collection " + articleCount + " articles M24 completed");
                        break;
                    case "matchtv":
                        articleCount = parsingMatchTV.getArticles();
                        System.out.println(LocalDateTime.now() + ": Collection " + articleCount + " articles MatchTV completed");
                        break;
                    case "interfax":
                        articleCount = parsingInterfax.getArticles();
                        System.out.println(LocalDateTime.now() + ": Collection " + articleCount + " articles Interfax completed");
                        break;
                    case "zarulem":
                        articleCount = parsingZaRulem.getArticles();
                        System.out.println(LocalDateTime.now() + ": Collection " + articleCount + " articles ZaRulem completed");
                        break;
                    case "tvrain":
                        articleCount = parsingTVRain.getArticles();
                        System.out.println(LocalDateTime.now() + ": Collection " + articleCount + " articles TVRain completed");
                        break;
                    case "bagnet":
                        articleCount = parsingBagnet.getArticles();
                        System.out.println(LocalDateTime.now() + ": Collection " + articleCount + " articles Bagnet completed");
                        break;
                    case "abnews":
                        articleCount = parsingABNews.getArticles();
                        System.out.println(LocalDateTime.now() + ": Collection " + articleCount + " articles ABNews completed");
                        break;
                }
            }
        }
        System.out.println(LocalDateTime.now() + ": Collection of articles completed");
    }
}
