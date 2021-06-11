package com.example.ozeronews.controllers;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;

public class ExchangeRate {

    public void getExchangeRate() {
        String linkExchangeRate = "http://www.cbr.ru/scripts/XML_daily.asp"; //https://openweathermap.org/
        String Valute = null;
        String NumCode = null;
        String CharCode = null;
        String Nominal = null;
        String Name = null;
        String Value = null;

//        <Valute ID="R01820">
//        <NumCode>392</NumCode>
//        <CharCode>JPY</CharCode>
//        <Nominal>100</Nominal>
//        <Name>Японских иен</Name>
//        <Value>69,1584</Value>
//        </Valute>


        //api.openweathermap.org/data/2.5/weather?q={city name},{state code},{country code}&lang=ru&appid=fc96152a5db18b9caad02923602ae692
        //api.openweathermap.org/data/2.5/weather?q=moscow&mode=xml&lang=ru&appid=fc96152a5db18b9caad02923602ae692
        try {
            // Create URL object
            URL url = new URL(linkExchangeRate);
            BufferedReader readr = new BufferedReader(new InputStreamReader(url.openStream()));

            // Enter filename in which you want to download
            BufferedWriter writer = new BufferedWriter(new FileWriter("ExchangeRate.xml"));

            // read each line from stream till end
            String line;
            while ((line = readr.readLine()) != null) {
                writer.write(line);
            }
            readr.close();
            writer.close();
            System.out.println("Successfully ExchangeRate.xml");
        }
        // Exceptions
        catch (MalformedURLException mue) {
            System.out.println("Malformed URL Exception raised");
        }
        catch (IOException ie) {
            System.out.println("IOException raised");
        }

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = null;
            builder = factory.newDocumentBuilder();

            Document document = null;
            document = builder.parse(new File("ExchangeRate.xml"));
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
    }
}
