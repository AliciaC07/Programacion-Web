package org.practica;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.FormElement;
import org.jsoup.select.Elements;

import javax.print.Doc;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException {
        String url = EnterUrl();
        System.out.println(url);
        Document doc_url = ConnectionUrl(url);
        System.out.println(doc_url.title());
        Integer cantLines = CountLines(url);
        Integer cantParagraphs = CountParagraphs(doc_url);
        Integer cantImages = CountImages(doc_url);
        Integer cantFormsByPost = CountForms(doc_url, "post");
        Integer cantFormsByGet = CountForms(doc_url, "get");
        ShowFormInputs(doc_url);








    }

    ///Cantidad de lineas de la url dada
    public static Integer CountLines(String url) throws IOException {
        String body = Jsoup.connect(url).execute().body();
        ///System.out.println(body);
        String[] bodyArr = body.split("\n");
        System.out.println(bodyArr.length);
        return bodyArr.length;

    }
    //Cantidad de parrafos que contiene el HTML
    public static Integer CountParagraphs(Document document){
        Integer cantParagraphs = 0;
        Elements paragraphs = document.select("p");
        //System.out.println(paragraphs.size());
        for (Element para:  paragraphs) {
            //aumentar
            cantParagraphs++;
        }
        System.out.println(cantParagraphs);
        return cantParagraphs;
    }

    //Cantidad de imagenes dentro de parrafos
    public static Integer CountImages(Document document){
        Integer cantImages = 0;
        Elements images = document.select("p img");
        cantImages = images.size();
        System.out.println(cantImages);
        return cantImages;
    }

    //Cantidad de Forms que hay en el HTML por Metodo POST y GET
    public static  Integer CountForms(Document document, String type){
        Integer cantFormsByMethod = 0;
        Elements formElements = new Elements();
        formElements = document.select("form[method="+type.toLowerCase()+"]");
        cantFormsByMethod = formElements.size();
        System.out.println(cantFormsByMethod);
        System.out.println(formElements);
        return cantFormsByMethod;

    }
    //Mostrar los inputs de los formularios y su tipo
    public static void ShowFormInputs(Document document){
        Elements inputsForms = document.select("form input");
        System.out.println(inputsForms);
        Map<String, String> inputType = new HashMap<>();
        Integer cantInputForm = 0;
        for (Element input : inputsForms) {
            cantInputForm++;
            inputType.put("Input-"+cantInputForm, input.attr("type"));
        }
        System.out.println(cantInputForm +"  "+inputType);
    }


    public static Document ConnectionUrl(String url) throws IOException {
        return Jsoup.connect(url).get();
    }

    public static String EnterUrl(){
        System.out.println("Introduzca la URL que desea examinar: ");
        Scanner inputUrl = new Scanner(System.in);
        String Url = inputUrl.next();
        Boolean state = true;
        if (Url != null){
            return Url;
        }else {
            System.out.println("Debe insertar una URL!");
            return null;
        }

    }

}
