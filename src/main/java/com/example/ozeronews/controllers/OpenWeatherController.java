package com.example.ozeronews.controllers;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;

public class OpenWeatherController {

    public void getForecastWeather() {
        //https://openweathermap.org/
        String linkSource = "api.openweathermap.org/data/2.5/weather?q=moscow&mode=xml&lang=ru&appid=fc96152a5db18b9caad02923602ae692";
        String cityName = "moscow";
        String stateCode = "";
        String CountryCode = "";
        String keyAPI = "fc96152a5db18b9caad02923602ae692";
        String format = "mode=xml";

        String CharCode = null;
        String Nominal = null;
        String Name = null;
        String Value = null;

//        <current>
//        <city id="524901" name="Москва">
//        <coord lon="37.6156" lat="55.7522"/>
//        <country>RU</country>
//        <timezone>10800</timezone>
//        <sun rise="2021-03-30T03:05:29" set="2021-03-30T16:02:14"/>
//        </city>
//        <temperature value="275.48" min="275.15" max="276.15" unit="kelvin"/>
//        <feels_like value="268.52" unit="kelvin"/>
//        <humidity value="52" unit="%"/>
//        <pressure value="1030" unit="hPa"/>
//        <wind>
//        <speed value="6" unit="m/s" name="Moderate breeze"/>
//        <gusts/>
//        <direction value="200" code="SSW" name="South-southwest"/>
//        </wind>
//        <clouds value="0" name="ясно"/>
//        <visibility value="10000"/>
//        <precipitation mode="no"/>
//        <weather number="800" value="ясно" icon="01d"/>
//        <lastupdate value="2021-03-30T06:37:36"/>
//        </current>

        //api.openweathermap.org/data/2.5/weather?q={city name},{state code},{country code}&lang=ru&appid=fc96152a5db18b9caad02923602ae692
        //api.openweathermap.org/data/2.5/weather?q=moscow&mode=xml&lang=ru&appid=fc96152a5db18b9caad02923602ae692

        try {
            // Create URL object
            URL url = new URL(linkSource);
            BufferedReader readr = new BufferedReader(new InputStreamReader(url.openStream()));

            // Enter filename in which you want to download
            BufferedWriter writer = new BufferedWriter(new FileWriter("ForecastWeather.xml"));

            // read each line from stream till end
            String line;
            while ((line = readr.readLine()) != null) {
                writer.write(line);
            }
            readr.close();
            writer.close();
            System.out.println("Successfully ForecastWeather.xml");
        }
        // Exceptions
        catch (MalformedURLException mue) {
            System.out.println("Malformed URL Exception raised");
        }
        catch (IOException ie) {
            System.out.println("IOException raised");
        }
        /*
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = null;
            builder = factory.newDocumentBuilder();

            Document document = null;
            document = builder.parse(new File("ForecastWeather.xml"));
            document.getDocumentElement().normalize();
            NodeList nList = document.getElementsByTagName("Valute");

            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node node = nList.item(temp);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) node;
                    if (eElement.getElementsByTagName("CharCode").item(0).getTextContent().equals("JPY")) {

//                        NumCode = eElement.getElementsByTagName("NumCode").item(0).getTextContent();
//                        CharCode = eElement.getElementsByTagName("CharCode").item(0).getTextContent();
//                        Nominal = eElement.getElementsByTagName("Nominal").item(0).getTextContent();
//                        Name = eElement.getElementsByTagName("Name").item(0).getTextContent();
                        Value = eElement.getElementsByTagName("Value").item(0).getTextContent();
                    }
                }
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }

//        System.out.println("NumCode = " + NumCode);
//        System.out.println("CharCode = " + CharCode);
//        System.out.println("Nominal = " + Nominal);
//        System.out.println("Name = " + Name);
//        System.out.println("Value = " + Value);
        System.out.println("Курс Японской иены к Российскому рублю = " + Value);

         */
    }
}
