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
    //parameters that will be used to fit query to user needs
    static String file;     //filepath
    static String stat;     //either "min" or "max"
    static int    limit;    //from 1 to 100
    static String by;       //either "DATE", "COUNTRY" or "CONTINENT"
    static String display;  //"NC" "NCS" "ND" "NDS" "NT"

    //contains the comparators needed for sorting, [key=stat+by, value=appropriate comparator]
    static Map<String, Comparator<? super CovidReport>> filterOptions;

    //courtesy of https://stackoverflow.com/a/27872852
    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }

    public static void main(String[] args) {
        // make sure we have the number of args needed
        if(args.length <= 9){
            printUsageError();
            return;
        }

        // start program execution time measurement
        long startTime = System.nanoTime();

        // deal with arguments
        processArgs(args);

	    // read csv once, store it in a list of string-lists
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

        //end and process program execution time
        long endTime = System.nanoTime();
        System.out.println("\nEXECUTION TIME: " + ((endTime - startTime) / (1_000_000_000 + 0.00f)));
    }

    private static Map<Integer, Entity> getContinents(List<List<String>> lines) {
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
    private static Map<Integer, Entity> getWorldEntities(List<List<String>> lines) {
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


    private static void processRequest(Map<Integer, Entity> entities, Map<Integer, CovidReport> covidReports) {
        //get isocodes of our entities of interest
        List<String> entityIsoCodes = entities.values().stream()
                .map(Entity::getIsocode)
                .sorted()
                .toList();

        List<CovidReport> output;
        String optionCode = display.toLowerCase()+"-"+stat.toLowerCase();
        
        output = covidReports.values().stream()
                .filter(x -> entityIsoCodes.contains(x.getIsocode())) //filter based on BY
                .sorted(filterOptions.get(optionCode))                //sort   based on DISPLAY + STAT
                .limit(limit)                                         //limit  based on LIMIT
                .toList();

        //output to console
        System.out.println("OUTPUT: ");
        output.stream().forEach(System.out::println);
    }

    private static void processArgs(String[] args) {
        //i'm cheating a little by using a small loop to deal with flags
        for (int i = 0 ; i < args.length-1; ++i){
            if(args[i].equalsIgnoreCase("-file"))
                file = args[i+1];
            if(args[i].equalsIgnoreCase("-stat"))
                stat = args[i+1];
            if(args[i].equalsIgnoreCase("-limit"))
                limit = Integer.parseInt(args[i+1]);
            if(args[i].equalsIgnoreCase("-by"))
                by = args[i+1];
            if(args[i].equalsIgnoreCase("-display"))
                display = args[i+1];
        }

        //create and populate filteroptions to include all possible combinations of
        filterOptions = new HashMap<>();
        filterOptions.put("nc-max",  Comparator.comparingInt(CovidReport::getNewCases).reversed());
        filterOptions.put("nc-min",  Comparator.comparingInt(CovidReport::getNewCases));
        filterOptions.put("ncs-max", Comparator.comparingDouble(CovidReport::getNewCasesSmoothed).reversed());
        filterOptions.put("ncs-min", Comparator.comparingDouble(CovidReport::getNewCasesSmoothed));
        filterOptions.put("nd-max",  Comparator.comparingInt(CovidReport::getNewDeaths).reversed());
        filterOptions.put("nd-min",  Comparator.comparingInt(CovidReport::getNewDeaths));
        filterOptions.put("nds-max", Comparator.comparingDouble(CovidReport::getNewDeathsSmoothed).reversed());
        filterOptions.put("nds-min", Comparator.comparingDouble(CovidReport::getNewDeathsSmoothed));
        filterOptions.put("nt-max",  Comparator.comparingInt(CovidReport::getNewTests).reversed());
        filterOptions.put("nt-min",  Comparator.comparingInt(CovidReport::getNewTests));
    }

    private static void printUsageError() {
        System.out.println("""
                USAGE: -file [filepath] -param1 [value1] -param2 [value2] -paramN valueN …\s
                -file:    path to file
                -stat:    either 'min' or 'max'
                -limit:   integer included in [1, 100]\s
                -by:      'nc', 'ncs', 'nd', 'nds', 'nt'
                -display: 'date', 'continent', 'country'""");
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
        System.out.println("REPORTS: " + output.size() + "\n");

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
