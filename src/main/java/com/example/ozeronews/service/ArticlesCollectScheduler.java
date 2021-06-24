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
//    https://novayagazeta.ru/news - novayagazeta
//    https://team29.org/ - Команда 29

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
    private ParsingMarketMedia parsingMarketMedia;
    private ParsingPrime parsingPrime;
    private ParsingRussian7 parsingRussian7;
    private ParsingVersia parsingVersia;
    private ParsingUra parsingUra;
    private ParsingRepublic parsingRepublic;
    private ParsingPravdaRu parsingPravdaRu;
    private ParsingJPGazeta parsingJPGazeta;
    private ParsingNewsLab parsingNewsLab;
    private Parsing5TV parsing5TV;
    private ParsingBBCRussian parsingBBCRussian;
    private ParsingMetroNews parsingMetroNews;
    private ParsingYtro parsingYtro;
    private ParsingAIF parsingAIF;
    private ParsingCurrentTime parsingCurrentTime;
    private ParsingCNews parsingCNews;
    private ParsingMediazona parsingMediazona;
    private ParsingSnob parsingSnob;
    private ParsingZnak parsingZnak;
    private ParsingProekt parsingProekt;
    private ParsingNG parsingNG;
    private ParsingNovayaGazeta parsingNovayaGazeta;

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
                                    ParsingABNews parsingABNews,
                                    ParsingMarketMedia parsingMarketMedia,
                                    ParsingPrime parsingPrime,
                                    ParsingRussian7 parsingRussian7,
                                    ParsingVersia parsingVersia,
                                    ParsingUra parsingUra,
                                    ParsingRepublic parsingRepublic,
                                    ParsingPravdaRu parsingPravdaRu,
                                    ParsingJPGazeta parsingJPGazeta,
                                    ParsingNewsLab parsingNewsLab,
                                    Parsing5TV parsing5TV,
                                    ParsingBBCRussian parsingBBCRussian,
                                    ParsingMetroNews parsingMetroNews,
                                    ParsingYtro parsingYtro,
                                    ParsingAIF parsingAIF,
                                    ParsingCurrentTime parsingCurrentTime,
                                    ParsingCNews parsingCNews,
                                    ParsingMediazona parsingMediazona,
                                    ParsingSnob parsingSnob,
                                    ParsingZnak parsingZnak,
                                    ParsingProekt parsingProekt,
                                    ParsingNG parsingNG,
                                    ParsingNovayaGazeta parsingNovayaGazeta) {

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
        this.parsingMarketMedia = parsingMarketMedia;
        this.parsingPrime = parsingPrime;
        this.parsingRussian7 = parsingRussian7;
        this.parsingVersia = parsingVersia;
        this.parsingUra = parsingUra;
        this.parsingRepublic = parsingRepublic;
        this.parsingPravdaRu = parsingPravdaRu;
        this.parsingJPGazeta = parsingJPGazeta;
        this.parsingNewsLab = parsingNewsLab;
        this.parsing5TV = parsing5TV;
        this.parsingBBCRussian = parsingBBCRussian;
        this.parsingMetroNews = parsingMetroNews;
        this.parsingYtro = parsingYtro;
        this.parsingAIF = parsingAIF;
        this.parsingCurrentTime = parsingCurrentTime;
        this.parsingCNews = parsingCNews;
        this.parsingMediazona = parsingMediazona;
        this.parsingSnob = parsingSnob;
        this.parsingZnak = parsingZnak;
        this.parsingProekt = parsingProekt;
        this.parsingNG = parsingNG;
        this.parsingNovayaGazeta = parsingNovayaGazeta;
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
////        parsingLife.getArticles();
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
//        parsingMarketMedia.getArticles();
//        parsingPrime.getArticles();
//        parsingRussian7.getArticles();
////        parsingVersia.getArticles();
//        parsingUra.getArticles();
//        parsingRepublic.getArticles();
//        parsingPravdaRu.getArticles();
//        parsingJPGazeta.getArticles();
//        parsingNewsLab.getArticles();
//        parsing5TV.getArticles();
//        parsingBBCRussian.getArticles();
//        parsingMetroNews.getArticles();
//        parsingYtro.getArticles();
//        parsingAIF.getArticles();
//        parsingCurrentTime.getArticles();
//        parsingCNews.getArticles();
//        parsingMediazona.getArticles();
//        parsingSnob.getArticles();
//        parsingZnak.getArticles();
////        parsingProekt.getArticles();
//        parsingNG.getArticles();
////        parsingNovayaGazeta.getArticles();

        Iterable<NewsResource> newsResources = newsResourceRepository.findAll();
        for (NewsResource newsResource : newsResources) {
            if (newsResource.isActive() && true) {
                switch (newsResource.getResourceKey()) {
                    case "ria":
                        System.out.print(LocalDateTime.now() + ": " + String.format("%1$15s", newsResource.getResourceKey()) + " collection ");
                        articleCount = parsingRIA.getArticles();
                        System.out.println(articleCount + " articles completed");
                        break;
                    case "tsargrad":
                        System.out.print(LocalDateTime.now() + ": " + String.format("%1$15s", newsResource.getResourceKey()) + " collection ");
                        articleCount = parsingTsargrad.getArticles();
                        System.out.println(articleCount + " articles completed");
                        break;
                    case "lentaru":
                        System.out.print(LocalDateTime.now() + ": " + String.format("%1$15s", newsResource.getResourceKey()) + " collection ");
                        articleCount = parsingLentaru.getArticles();
                        System.out.println(articleCount + " articles completed");
                        break;
                    case "meduza":
                        System.out.print(LocalDateTime.now() + ": " + String.format("%1$15s", newsResource.getResourceKey()) + " collection ");
                        articleCount = parsingMeduza.getArticles();
                        System.out.println(articleCount + " articles completed");
                        break;
                    case "inosmi":
                        System.out.print(LocalDateTime.now() + ": " + String.format("%1$15s", newsResource.getResourceKey()) + " collection ");
                        articleCount = parsingINOSMI.getArticles();
                        System.out.println(articleCount + " articles completed");
                        break;
                    case "xtrue":
                        System.out.print(LocalDateTime.now() + ": " + String.format("%1$15s", newsResource.getResourceKey()) + " collection ");
                        articleCount = parsingXTrue.getArticles();
                        System.out.println(articleCount + " articles completed");
                        break;
                    case "life":
                        System.out.print(LocalDateTime.now() + ": " + String.format("%1$15s", newsResource.getResourceKey()) + " collection ");
                        articleCount = parsingLife.getArticles();
                        System.out.println(articleCount + " articles completed");
                        break;
                    case "fontanka":
                        System.out.print(LocalDateTime.now() + ": " + String.format("%1$15s", newsResource.getResourceKey()) + " collection ");
                        articleCount = parsingFontanka.getArticles();
                        System.out.println(articleCount + " articles completed");
                        break;
                    case "dniru":
                        System.out.print(LocalDateTime.now() + ": " + String.format("%1$15s", newsResource.getResourceKey()) + " collection ");
                        articleCount = parsingDniru.getArticles();
                        System.out.println(articleCount + " articles completed");
                        break;
                    case "regnum":
                        System.out.print(LocalDateTime.now() + ": " + String.format("%1$15s", newsResource.getResourceKey()) + " collection ");
                        articleCount = parsingRegnum.getArticles();
                        System.out.println(articleCount + " articles completed");
                        break;
                    case "rbc":
                        System.out.print(LocalDateTime.now() + ": " + String.format("%1$15s", newsResource.getResourceKey()) + " collection ");
                        articleCount = parsingRBC.getArticles();
                        System.out.println(articleCount + " articles completed");
                        break;
                    case "rt":
                        System.out.print(LocalDateTime.now() + ": " + String.format("%1$15s", newsResource.getResourceKey()) + " collection ");
                        articleCount = parsingRT.getArticles();
                        System.out.println(articleCount + " articles completed");
                        break;
                    case "izvestia":
                        System.out.print(LocalDateTime.now() + ": " + String.format("%1$15s", newsResource.getResourceKey()) + " collection ");
                        articleCount = parsingIzvestia.getArticles();
                        System.out.println(articleCount + " articles completed");
                        break;
                    case "vzglyad":
                        System.out.print(LocalDateTime.now() + ": " + String.format("%1$15s", newsResource.getResourceKey()) + " collection ");
                        articleCount = parsingVzglyad.getArticles();
                        System.out.println(articleCount + " articles completed");
                        break;
                    case "newsru":
                        System.out.print(LocalDateTime.now() + ": " + String.format("%1$15s", newsResource.getResourceKey()) + " collection ");
                        articleCount = parsingNewsru.getArticles();
                        System.out.println(articleCount + " articles completed");
                        break;
                    case "vedomosti":
                        System.out.print(LocalDateTime.now() + ": " + String.format("%1$15s", newsResource.getResourceKey()) + " collection ");
                        articleCount = parsingVedomosti.getArticles();
                        System.out.println(articleCount + " articles completed");
                        break;
                    case "mk":
                        System.out.print(LocalDateTime.now() + ": " + String.format("%1$15s", newsResource.getResourceKey()) + " collection ");
                        articleCount = parsingMK.getArticles();
                        System.out.println(articleCount + " articles completed");
                        break;
                    case "gazetaru":
                        System.out.print(LocalDateTime.now() + ": " + String.format("%1$15s", newsResource.getResourceKey()) + " collection ");
                        articleCount = parsingGazetaRu.getArticles();
                        System.out.println(articleCount + " articles completed");
                        break;
                    case "rg":
                        System.out.print(LocalDateTime.now() + ": " + String.format("%1$15s", newsResource.getResourceKey()) + " collection ");
                        articleCount = parsingRG.getArticles();
                        System.out.println(articleCount + " articles completed");
                        break;
                    case "forbes":
                        System.out.print(LocalDateTime.now() + ": " + String.format("%1$15s", newsResource.getResourceKey()) + " collection ");
                        articleCount = parsingForbes.getArticles();
                        System.out.println(articleCount + " articles completed");
                        break;
                    case "finam":
                        System.out.print(LocalDateTime.now() + ": " + String.format("%1$15s", newsResource.getResourceKey()) + " collection ");
                        articleCount = parsingFinam.getArticles();
                        System.out.println(articleCount + " articles completed");
                        break;
                    case "dw":
                        System.out.print(LocalDateTime.now() + ": " + String.format("%1$15s", newsResource.getResourceKey()) + " collection ");
                        articleCount = parsingDW.getArticles();
                        System.out.println(articleCount + " articles completed");
                        break;
                    case "newsun":
                        System.out.print(LocalDateTime.now() + ": " + String.format("%1$15s", newsResource.getResourceKey()) + " collection ");
                        articleCount = parsingNewsUN.getArticles();
                        System.out.println(articleCount + " articles completed");
                        break;
                    case "sportexpress":
                        System.out.print(LocalDateTime.now() + ": " + String.format("%1$15s", newsResource.getResourceKey()) + " collection ");
                        articleCount = parsingSportExpress.getArticles();
                        System.out.println(articleCount + " articles completed");
                        break;
                    case "tass":
                        System.out.print(LocalDateTime.now() + ": " + String.format("%1$15s", newsResource.getResourceKey()) + " collection ");
                        articleCount = parsingTASS.getArticles();
                        System.out.println(articleCount + " articles completed");
                        break;
                    case "investing-":
                        System.out.print(LocalDateTime.now() + ": " + String.format("%1$15s", newsResource.getResourceKey()) + " collection ");
                        articleCount = parsingInvesting.getArticles();
                        System.out.println(articleCount + " articles completed");
                        break;
                    case "kommersant":
                        System.out.print(LocalDateTime.now() + ": " + String.format("%1$15s", newsResource.getResourceKey()) + " collection ");
                        articleCount = parsingKommersant.getArticles();
                        System.out.println(articleCount + " articles completed");
                        break;
                    case "retailru":
                        System.out.print(LocalDateTime.now() + ": " + String.format("%1$15s", newsResource.getResourceKey()) + " collection ");
                        articleCount = parsingRetailRu.getArticles();
                        System.out.println(articleCount + " articles completed");
                        break;
                    case "actualnews":
                        System.out.print(LocalDateTime.now() + ": " + String.format("%1$15s", newsResource.getResourceKey()) + " collection ");
                        articleCount = parsingActualNews.getArticles();
                        System.out.println(articleCount + " articles completed");
                        break;
                    case "svoboda":
                        System.out.print(LocalDateTime.now() + ": " + String.format("%1$15s", newsResource.getResourceKey()) + " collection ");
                        articleCount = parsingSvoboda.getArticles();
                        System.out.println(articleCount + " articles completed");
                        break;
                    case "lifehacker":
                        System.out.print(LocalDateTime.now() + ": " + String.format("%1$15s", newsResource.getResourceKey()) + " collection ");
                        articleCount = parsingLifehacker.getArticles();
                        System.out.println(articleCount + " articles completed");
                        break;
                    case "fan":
                        System.out.print(LocalDateTime.now() + ": " + String.format("%1$15s", newsResource.getResourceKey()) + " collection ");
                        articleCount = parsingFAN.getArticles();
                        System.out.println(articleCount + " articles completed");
                        break;
                    case "topnewsru":
                        System.out.print(LocalDateTime.now() + ": " + String.format("%1$15s", newsResource.getResourceKey()) + " collection ");
                        articleCount = parsingTopnewsru.getArticles();
                        System.out.println(articleCount + " articles completed");
                        break;
                    case "newsinfo":
                        System.out.print(LocalDateTime.now() + ": " + String.format("%1$15s", newsResource.getResourceKey()) + " collection ");
                        articleCount = parsingNewsinfo.getArticles();
                        System.out.println(articleCount + " articles completed");
                        break;
                    case "m24":
                        System.out.print(LocalDateTime.now() + ": " + String.format("%1$15s", newsResource.getResourceKey()) + " collection ");
                        articleCount = parsingM24.getArticles();
                        System.out.println(articleCount + " articles completed");
                        break;
                    case "matchtv":
                        System.out.print(LocalDateTime.now() + ": " + String.format("%1$15s", newsResource.getResourceKey()) + " collection ");
                        articleCount = parsingMatchTV.getArticles();
                        System.out.println(articleCount + " articles completed");
                        break;
                    case "interfax":
                        System.out.print(LocalDateTime.now() + ": " + String.format("%1$15s", newsResource.getResourceKey()) + " collection ");
                        articleCount = parsingInterfax.getArticles();
                        System.out.println(articleCount + " articles completed");
                        break;
                    case "zarulem":
                        System.out.print(LocalDateTime.now() + ": " + String.format("%1$15s", newsResource.getResourceKey()) + " collection ");
                        articleCount = parsingZaRulem.getArticles();
                        System.out.println(articleCount + " articles completed");
                        break;
                    case "tvrain":
                        System.out.print(LocalDateTime.now() + ": " + String.format("%1$15s", newsResource.getResourceKey()) + " collection ");
                        articleCount = parsingTVRain.getArticles();
                        System.out.println(articleCount + " articles completed");
                        break;
                    case "bagnet":
                        System.out.print(LocalDateTime.now() + ": " + String.format("%1$15s", newsResource.getResourceKey()) + " collection ");
                        articleCount = parsingBagnet.getArticles();
                        System.out.println(articleCount + " articles completed");
                        break;
                    case "abnews":
                        System.out.print(LocalDateTime.now() + ": " + String.format("%1$15s", newsResource.getResourceKey()) + " collection ");
                        articleCount = parsingABNews.getArticles();
                        System.out.println(articleCount + " articles completed");
                        break;
                    case "prime":
                        System.out.print(LocalDateTime.now() + ": " + String.format("%1$15s", newsResource.getResourceKey()) + " collection ");
                        articleCount = parsingPrime.getArticles();
                        System.out.println(articleCount + " articles completed");
                        break;
                    case "russian7":
                        System.out.print(LocalDateTime.now() + ": " + String.format("%1$15s", newsResource.getResourceKey()) + " collection ");
                        articleCount = parsingRussian7.getArticles();
                        System.out.println(articleCount + " articles completed");
                        break;
                    case "versia":
                        System.out.print(LocalDateTime.now() + ": " + String.format("%1$15s", newsResource.getResourceKey()) + " collection ");
                        articleCount = parsingVersia.getArticles();
                        System.out.println(articleCount + " articles completed");
                        break;
                    case "ura":
                        System.out.print(LocalDateTime.now() + ": " + String.format("%1$15s", newsResource.getResourceKey()) + " collection ");
                        articleCount = parsingUra.getArticles();
                        System.out.println(articleCount + " articles completed");
                        break;
                    case "republic":
                        System.out.print(LocalDateTime.now() + ": " + String.format("%1$15s", newsResource.getResourceKey()) + " collection ");
                        articleCount = parsingRepublic.getArticles();
                        System.out.println(articleCount + " articles completed");
                        break;
                    case "pravdaru":
                        System.out.print(LocalDateTime.now() + ": " + String.format("%1$15s", newsResource.getResourceKey()) + " collection ");
                        articleCount = parsingPravdaRu.getArticles();
                        System.out.println(articleCount + " articles completed");
                        break;
                    case "jpgazeta":
                        System.out.print(LocalDateTime.now() + ": " + String.format("%1$15s", newsResource.getResourceKey()) + " collection ");
                        articleCount = parsingJPGazeta.getArticles();
                        System.out.println(articleCount + " articles completed");
                        break;
                    case "newslab":
                        System.out.print(LocalDateTime.now() + ": " + String.format("%1$15s", newsResource.getResourceKey()) + " collection ");
                        articleCount = parsingNewsLab.getArticles();
                        System.out.println(articleCount + " articles completed");
                        break;
                    case "5tv":
                        System.out.print(LocalDateTime.now() + ": " + String.format("%1$15s", newsResource.getResourceKey()) + " collection ");
                        articleCount = parsing5TV.getArticles();
                        System.out.println(articleCount + " articles completed");
                        break;
                    case "bbcrussian":
                        System.out.print(LocalDateTime.now() + ": " + String.format("%1$15s", newsResource.getResourceKey()) + " collection ");
                        articleCount = parsingBBCRussian.getArticles();
                        System.out.println(articleCount + " articles completed");
                        break;
                    case "metronews":
                        System.out.print(LocalDateTime.now() + ": " + String.format("%1$15s", newsResource.getResourceKey()) + " collection ");
                        articleCount = parsingMetroNews.getArticles();
                        System.out.println(articleCount + " articles completed");
                        break;
                    case "ytro":
                        System.out.print(LocalDateTime.now() + ": " + String.format("%1$15s", newsResource.getResourceKey()) + " collection ");
                        articleCount = parsingYtro.getArticles();
                        System.out.println(articleCount + " articles completed");
                        break;
                    case "aif":
                        System.out.print(LocalDateTime.now() + ": " + String.format("%1$15s", newsResource.getResourceKey()) + " collection ");
                        articleCount = parsingAIF.getArticles();
                        System.out.println(articleCount + " articles completed");
                        break;
                    case "currenttime":
                        System.out.print(LocalDateTime.now() + ": " + String.format("%1$15s", newsResource.getResourceKey()) + " collection ");
                        articleCount = parsingCurrentTime.getArticles();
                        System.out.println(articleCount + " articles completed");
                        break;
                    case "cnews":
                        System.out.print(LocalDateTime.now() + ": " + String.format("%1$15s", newsResource.getResourceKey()) + " collection ");
                        articleCount = parsingCNews.getArticles();
                        System.out.println(articleCount + " articles completed");
                        break;
                    case "mediazona":
                        System.out.print(LocalDateTime.now() + ": " + String.format("%1$15s", newsResource.getResourceKey()) + " collection ");
                        articleCount = parsingMediazona.getArticles();
                        System.out.println(articleCount + " articles completed");
                        break;
                    case "snob":
                        System.out.print(LocalDateTime.now() + ": " + String.format("%1$15s", newsResource.getResourceKey()) + " collection ");
                        articleCount = parsingSnob.getArticles();
                        System.out.println(articleCount + " articles completed");
                        break;
                    case "znak":
                        System.out.print(LocalDateTime.now() + ": " + String.format("%1$15s", newsResource.getResourceKey()) + " collection ");
                        articleCount = parsingZnak.getArticles();
                        System.out.println(articleCount + " articles completed");
                        break;
                    case "proekt":
                        System.out.print(LocalDateTime.now() + ": " + String.format("%1$15s", newsResource.getResourceKey()) + " collection ");
                        articleCount = parsingProekt.getArticles();
                        System.out.println(articleCount + " articles completed");
                        break;
                    case "ng":
                        System.out.print(LocalDateTime.now() + ": " + String.format("%1$15s", newsResource.getResourceKey()) + " collection ");
                        articleCount = parsingNG.getArticles();
                        System.out.println(articleCount + " articles completed");
                        break;
                    case "novayagazeta":
                        System.out.print(LocalDateTime.now() + ": " + String.format("%1$15s", newsResource.getResourceKey()) + " collection ");
                        articleCount = parsingNovayaGazeta.getArticles();
                        System.out.println(articleCount + " articles completed");
                        break;
                }
            }
        }
        System.out.println(LocalDateTime.now() + ": Collection of articles completed");
    }
}
