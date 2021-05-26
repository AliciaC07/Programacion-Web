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
        Integer selectedOption = 0;

        do {
            System.out.println("\nSeleccione una de las opciones o seleccione 0 si desea salir: \n 1-) Cantidad de líneas retornadas. \n 2-) Cantidad de párrafos (p).\n" +
                    " 3-) Cantidad de imágenes. \n 4-) Cantidad de formularios por Metodos(POST y GET). \n 5-) Cada formulario mostrar los campos de input y su tipo." +
                    "\n 6-) Identificar que el método de envío del formulario sea POST y enviar una petición al servidor. \n");
            Scanner scan = new Scanner(System.in);
            selectedOption = Integer.parseInt(scan.next());
            switch (selectedOption){
                case 1:
                    Integer cantLines = CountLines(url);
                    System.out.println("La cantidad es de: "+cantLines);
                    break;
                case 2:
                    Integer cantParagraphs = CountParagraphs(doc_url);
                    System.out.println("La cantidad es de: "+cantParagraphs);
                    break;
                case 3:
                    Integer cantImages = CountImages(doc_url);
                    System.out.println("La cantidad es de: "+cantImages);
                    break;
                case 4:
                    Integer cantFormsByPost = CountForms(doc_url, "post");
                    Integer cantFormsByGet = CountForms(doc_url, "get");
                    System.out.println("La cantidad de forms por metodo POST: "+cantFormsByPost);
                    System.out.println("La cantidad de forms por metodo GET: "+cantFormsByGet);
                    break;
                case 5:
                    ShowFormInputs(doc_url);
                    break;
                case 6:
                    FormParamsHeader(doc_url, "20170465");
                    break;

                default:
                    System.out.println("\n");
            }
        }while (selectedOption != 0);












    }

    ///Cantidad de lineas de la url dada
    public static Integer CountLines(String url) throws IOException {
        String body = Jsoup.connect(url).execute().body();
        ///System.out.println(body);
        String[] bodyArr = body.split("\n");
        //System.out.println(bodyArr.length);
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
        //System.out.println(cantParagraphs);
        return cantParagraphs;
    }

    //Cantidad de imagenes dentro de parrafos
    public static Integer CountImages(Document document){
        Integer cantImages = 0;
        Elements images = document.select("p img");
        cantImages = images.size();
        //System.out.println(cantImages);
        return cantImages;
    }

    //Cantidad de Forms que hay en el HTML por Metodo POST y GET
    public static  Integer CountForms(Document document, String type){
        Integer cantFormsByMethod = 0;
        Elements formElements = new Elements();
        formElements = document.select("form[method="+type.toLowerCase()+"]");
        cantFormsByMethod = formElements.size();
        //System.out.println(cantFormsByMethod);
        //System.out.println(formElements);
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
            System.out.println("Input-"+cantInputForm+ " "+ inputType.get("Input-"+cantInputForm));
        }
        //System.out.println(cantInputForm +"  "+inputType);
    }

    //Realizar el envio de datos para un form con metodo post y enviar header
    public static void FormParamsHeader(Document document, String matricula) throws IOException {
        Elements postForms = document.select("form[method=post]");
        Map<String, Document> formsSave = new HashMap<>();
        Integer countForms = 0;
        //Verificar el form obtenido
        for (Element formSelected : postForms) {
            countForms++;
            String abosluteUrl = formSelected.absUrl("action");
            Document documentResponse = Jsoup.connect(abosluteUrl).data("asignatura", "practica-1").header("matricula", matricula).post();
            formsSave.put("Form-"+countForms, documentResponse);
            System.out.println(formsSave.get("Form-"+countForms).body());
        }



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
