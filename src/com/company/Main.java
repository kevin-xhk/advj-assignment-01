package com.company;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;


public class Main {
    //parameters are hardcoded for the time being
    static String file    = "/home/kev/Downloads/owid-covid-data.csv";
    static String stat    = "max";     //either "min" or "max"
    static int    limit   = 10;        //from 1 to 100
    static String by      = "COUNTRY"; //either "DATE", "COUNTRY" or "CONTINENT"
    static String display = "NC";      //"NC" "NCS" "ND" "NDS" "NT" "NDPC???"

    //courtesy of https://stackoverflow.com/a/27872852
    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }

    public static void main(String[] args) {
        //start program execution time measurement
        long startTime = System.nanoTime();

        //deal with arguments
//        processArgs(args);

	    //read csv once and save it to a list of string-lists
        List<List<String>> lines = getLines(file);

        //get Maps of countries, continents and world-entities
        Map<Integer, Entity> countries     = getCountries(lines);
        Map<Integer, Entity> continents    = getContinents(lines);
        Map<Integer, Entity> worldEntities = getWorldEntities(lines);

        //get Map of covid-reports
        Map<Integer, CovidReport> covidReports = getCovidReports(lines);

        //process user request based on parameters
        if(by.equalsIgnoreCase("date"))      processRequest(worldEntities, covidReports);
        if(by.equalsIgnoreCase("continent")) processRequest(continents, covidReports);
        if(by.equalsIgnoreCase("country"))   processRequest(countries, covidReports);

        //processRequest(countries, covidReports);

        //end and process program execution time
        long endTime = System.nanoTime();
        System.out.println("\nEXECUTION TIME: " + ((endTime - startTime) / (1_000_000_000 + 0.00f)));
    }

    private static Map<Integer,Entity> getContinents(List<List<String>> lines) {
        List<String> cols = getColumns(lines);
        int cod = cols.indexOf("iso_code");
        int cnt = cols.indexOf("continent");
        int loc = cols.indexOf("location");
        int pop = cols.indexOf("population");
        int ma  = cols.indexOf("median_age");

        Map<Integer,Entity> output = lines.stream()
                .skip(1)                                //skip column names
                .filter(distinctByKey(x -> x.get(cod))) //pick only 1 entry per iso_code
                .filter(x -> x.get(cnt).equals(""))     //entries w/out continent are not countries
                .filter(x -> !(x.get(loc).contains("income") ||
                        x.get(loc).contains("International") ||
                        x.get(loc).contains("Union") ||
                        x.get(loc).contains("World")))
                .map(x -> new Continent (               //create a Country from List<String> fields
                        x.get(cod),
                        x.get(loc),
                        x.get(pop),
                        x.get(ma)))
                .collect(Collectors.toMap(
                        (x -> x.getIsocode().hashCode()), //make isocode + date as composite key
                        Function.identity()));
        System.out.println("CONTINENTS: " + output.size());
        return output;
    }

    private static Map<Integer,Entity> getWorldEntities(List<List<String>> lines) {
        List<String> cols = getColumns(lines);
        int cod = cols.indexOf("iso_code");
        int cnt = cols.indexOf("continent");
        int loc = cols.indexOf("location");
        int pop = cols.indexOf("population");
        int ma  = cols.indexOf("median_age");

        Map<Integer,Entity> output = lines.stream()
                .skip(1)                                //skip column names
                .filter(distinctByKey(x -> x.get(cod))) //pick only 1 entry per iso_code
                .filter(x -> x.get(cnt).equals(""))     //entries w/out continent are not countries
                .filter(x -> (x.get(loc).contains("income") ||
                        x.get(loc).contains("International") ||
                        x.get(loc).contains("Union") ||
                        x.get(loc).contains("World")))
                .map(x -> new WorldEntity (               //create a Country from List<String> fields
                        x.get(cod),
                        x.get(loc),
                        x.get(pop),
                        x.get(ma)))
                .collect(Collectors.toMap(
                        (x -> x.getIsocode().hashCode()), //make isocode + date as composite key
                        Function.identity()));
        System.out.println("WORLD ENTITIES: " + output.size());

        return output;
    }


    private static <T> void processRequest(Map<Integer, Entity> entities, Map<Integer, CovidReport> covidReports) {
        System.out.println("hello");
        List<String> entityNames = entities.values().stream()
                .map(x -> x.getLocation())
                .toList();
        entityNames.stream().forEach(System.out::println);
        //        covidReports.entrySet().stream()
//                .sorted((rep1, rep2) -> rep1.getValue().get)
    }

    private static void processArgs(String[] args) {
        //TODO: write the actual implementation of argument-processing
    }

    private static void printUsageError() {
        System.out.println("USAGE: -file pathToFile -param1 value1 -param2 value2 -paramM valueN …");
        return;
    }

    private static List<String> getColumns(List<List<String>> lines) {
        return lines.stream().findFirst().get();
    }

    private static Map<Integer, CovidReport> getCovidReports(List<List<String>> lines) {
        //save indices needed by CovidReport's constructor
        List<String> cols = getColumns(lines);
        int cod = cols.indexOf("iso_code");
        int cnt = cols.indexOf("continent");
        int dt  = cols.indexOf("date");
        int tc  = cols.indexOf("total_cases");
        int nc  = cols.indexOf("new_cases");
        int ncs = cols.indexOf("new_cases_smoothed");
        int td  = cols.indexOf("total_deaths");
        int nd  = cols.indexOf("new_deaths");
        int nds = cols.indexOf("new_deaths_smoothed");
        int rr  = cols.indexOf("reproduction_rate");
        int nt  = cols.indexOf("new_tests");
        int tt  = cols.indexOf("total_tests");
        int si  = cols.indexOf("stringency_index");

        Map<Integer, CovidReport> output = lines.stream()
                .skip(1)
                //.filter(x -> !x.get(cnt).equals(""))
                .map(x -> new CovidReport(
                        x.get(cod),
                        x.get(dt),
                        x.get(tc),
                        x.get(nc),
                        x.get(ncs),
                        x.get(td),
                        x.get(nd),
                        x.get(nds),
                        x.get(rr),
                        x.get(nt),
                        x.get(tt),
                        x.get(si)))
                .collect(Collectors.toMap(
                        (x -> (x.getIsocode() + x.getDate()).hashCode()), //make isocode + date as composite key
                        Function.identity()));
        System.out.println("REPORTS: " + output.size());

        return output;
    }

    private static Map<Integer, Entity> getCountries(List<List<String>> lines) {
        //save indices needed by Country's constructor
        List<String> cols = getColumns(lines);
        int cod = cols.indexOf("iso_code");
        int cnt = cols.indexOf("continent");
        int loc = cols.indexOf("location");
        int pop = cols.indexOf("population");
        int ma  = cols.indexOf("median_age");

        Map<Integer, Entity> output = lines.stream()
                .skip(1)                                 //skip column names
                .filter(distinctByKey(x -> x.get(cod)))  //pick only 1 entry per iso_code
                .filter(x -> !x.get(cnt).equals(""))     //entries w/out continent are not countries
                .map(x -> new Country (                  //create a Country from List<String> fields
                        x.get(cod),
                        x.get(cnt),
                        x.get(loc),
                        x.get(pop),
                        x.get(ma)))
                .collect(Collectors.toMap(
                        (x -> x.getIsocode().hashCode()), //make isocode + date as composite key
                        Function.identity()));
        System.out.println("COUNTRIES: " + output.size());

        return output;
    }

    private static List<List<String>> getLines(String filepath) {
        Path path = Paths.get(filepath);
        List<List<String>> output = null;

        try(BufferedReader bf = Files.newBufferedReader(path)) {
            output = bf.lines()
                    .map(x -> Arrays.asList(x.split(",", -1)))
                    .toList();
        }catch(Exception e) {
            e.printStackTrace();
        }

        return output;
    }
}
