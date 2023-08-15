package ru.netology;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class Main {
    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException, TransformerException {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";
        List<Employee> list = parseCSV(columnMapping, fileName);
        String json = listToJson(list);
        String jsonFilename = "data.json";
        writeString(json, jsonFilename);
        String xmlFileName = "data.xml";
        createXml(xmlFileName);
        List<Employee> list2 = parseXML(xmlFileName);
        String json2 = listToJson(list2);
        String jsonFilename2 = "data2.json";
        writeString(json2, jsonFilename2);

    }

    private static String listToJson(List<Employee> list) {
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        Gson gson = builder.create();
        Type listType = new TypeToken<List<Employee>>() {
        }.getType();
        return gson.toJson(list, listType);
    }

    private static void writeString(String json, String jsonFilename) {
        try (FileWriter file = new FileWriter(jsonFilename)) {
            file.write(json);
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        var employee = "1,John,Smith,USA,25" .split(",");
        var employee1 = "2,Inav,Petrov,RU,23" .split(",");

        try (CSVReader csvReader = new CSVReader(new FileReader("data.csv"))) {
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping("id", "firstName", "lastName", "country", "age");

            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(csvReader)
                    .withMappingStrategy(strategy)
                    .build();
            return csv.parse();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    private static List<Employee> parseXML(String xmlFileName) throws ParserConfigurationException, IOException, SAXException {
        List<Employee> list = new ArrayList<>();
        //  File file = new File("data.xml");
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(new File(xmlFileName));
        Node root = document.getDocumentElement();
        NodeList nodeList = root.getChildNodes();
        document.getDocumentElement().normalize();

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);

            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;

                long id = Long.parseLong(element.getElementsByTagName("id").item(0).getTextContent());
                String firstName = element.getElementsByTagName("firstName").item(0).getTextContent();
                String lastName = element.getElementsByTagName("lastName").item(0).getTextContent();
                String country = element.getElementsByTagName("country").item(0).getTextContent();
                int age = Integer.parseInt(element.getElementsByTagName("age").item(0).getTextContent());

                Employee employee = new Employee(id, firstName, lastName, country, age);
                list.add(employee);
            }
        }
        return list;
    }


    private static void createXml(String xmlFileName) throws ParserConfigurationException, TransformerException, IOException, SAXException {
        final List<Employee> employees = List.of(
                new Employee(1, "John", "Smith", "USA", 25),
                new Employee(2, "Ivan", "Petrov", "RU", 23));

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document document = dBuilder.newDocument();

        Element rootElement = document.createElement("staff");
        document.appendChild(rootElement);



        for (Employee employee : employees) {
            Element employeeElement = document.createElement("employee");
            rootElement.appendChild(employeeElement);

            Element id = document.createElement("id");
            id.appendChild(document.createTextNode(String.valueOf(employee.id)));
            employeeElement.appendChild(id);

            Element firstName = document.createElement("firstName");
            firstName.appendChild(document.createTextNode(employee.firstName));
            employeeElement.appendChild(firstName);

            Element lastName = document.createElement("lastName");
            lastName.appendChild(document.createTextNode(employee.lastName));
            employeeElement.appendChild(lastName);

            Element country = document.createElement("country");
            country.appendChild(document.createTextNode(employee.country));
            employeeElement.appendChild(country);

            Element age = document.createElement("age");
            age.appendChild(document.createTextNode(String.valueOf(employee.age)));
            employeeElement.appendChild(age);
        }
        DOMSource domSource = new DOMSource(document);
        StreamResult streamResult = new StreamResult(new File("data.xml"));
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.transform(domSource, streamResult);
    }
}


