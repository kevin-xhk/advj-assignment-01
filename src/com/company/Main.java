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
    // parameters that will be used to fit query to user needs
    static String file;     // filepath
    static String stat;     // "min" or "max"
    static int    limit;    // from 1 to 100
    static String by;       // "NC" "NCS" "ND" "NDS" "NT" "NDPC"
    static String display;  // "DATE", "COUNTRY" or "CONTINENT"

    // contains the comparators needed for sorting
    // the relevant reports based on BY and STAT parameters
    // [key=by-stat, value=appropriate comparator]
    static Map<String, Comparator<? super CovidReport>> sortingOptions;

    public static void main(String[] args) {
        // make sure we have the number of args needed
        if(args.length <= 9){
            printUsageError();
            return;
        }

        // start execution time measurement
        long startTime = System.nanoTime();

        // deal with arguments, populate static fields
        processArgs(args);

	    // read csv once, store it in a list of rows
        // where each row is itself a list of entries
        List<List<String>> lines = getLines(file);

        // get Map of covid-reports
        Map<String, CovidReport> covidReports = getCovidReports(lines);

        // get Maps of countries, continents and world-entities
        // to satisfy the 3NF condition
        Map<String, Entity> worldEntities = getWorldEntities(lines);
        Map<String, Entity> continents    = getContinents(lines);
        Map<String, Entity> countries     = getCountries(lines, continents);

        // process user request based on scope defined by display parameter
        if(display.equalsIgnoreCase("date"))        //scope: world entities
            processRequest(worldEntities, covidReports);
        if(display.equalsIgnoreCase("continent"))   //scope: continents
            processRequest(continents, covidReports);
        if(display.equalsIgnoreCase("country"))     //scope: countries
            processRequest(countries, covidReports);

        // end and process program execution time
        long endTime = System.nanoTime();
        System.out.println("\nEXECUTION TIME: " + ((endTime - startTime) / (1_000_000_000 + 0.00f)));
    }

    // outputs desired query to the console
    private static void processRequest(Map<String, Entity> entities,
                                       Map<String, CovidReport> covidReports) {
        List<CovidReport> output;
        String optionCode;

        optionCode = by.toLowerCase()+"-"+stat.toLowerCase();
        output = covidReports.values().stream()
                .filter(x -> entities.keySet().contains(x.getIsocode())) //filter based on DISPLAY
                .sorted(sortingOptions.get(optionCode))                //sort   based on BY + STAT
                .limit(limit)                                         //limit  based on LIMIT
                .toList();

        //output to console
        System.out.println("OUTPUT: ");
        if(display.equalsIgnoreCase("date"))
            output.stream()
                    .forEach(x -> System.out.println(x.getDate()));
        else
            output.stream()
                    .forEach(x -> System.out.println(
                            entities.get(x.getIsocode()).getLocation()));

        //uncomment this for verbose output
        //output.stream().forEach(System.out::println);
    }

    // methods for getting the needed maps
    private static Map<String, CovidReport> getCovidReports(List<List<String>> lines) {
        //save indices needed by CovidReport's constructor
        Map<String, CovidReport> output;
        List<String> cols;

        //get the indices of our columns of interest
        cols = getColumns(lines);
        int cod = cols.indexOf("iso_code");
        int dt =  cols.indexOf("date");
        int tc =  cols.indexOf("total_cases");
        int nc =  cols.indexOf("new_cases");
        int ncs = cols.indexOf("new_cases_smoothed");
        int td =  cols.indexOf("total_deaths");
        int nd =  cols.indexOf("new_deaths");
        int nds = cols.indexOf("new_deaths_smoothed");
        int rr =  cols.indexOf("reproduction_rate");
        int nt =  cols.indexOf("new_tests");
        int tt =  cols.indexOf("total_tests");
        int si =  cols.indexOf("stringency_index");

        output = lines.stream()
                .skip(1)        //skips 'column-names' row
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
                        (x -> (x.getIsocode() + x.getDate())), //make isocode + date as composite key
                        Function.identity()));
        System.out.println("REPORTS: " + output.size() + "\n");

        //uncomment this for verbose output
        //output.entrySet().stream().forEach(System.out::println);

        return output;
    }
    private static Map<String, Entity> getWorldEntities(List<List<String>> lines) {
        Map<String,Entity> output;
        List<String> cols;

        //get the indices of our columns of interest
        cols = getColumns(lines);
        int cod = cols.indexOf("iso_code");
        int cnt = cols.indexOf("continent");
        int loc = cols.indexOf("location");
        int pop = cols.indexOf("population");
        int ma  = cols.indexOf("median_age");

        output = lines.stream()
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
                        (WorldEntity::getIsocode), //make isocode + date as composite key
                        Function.identity()));
        System.out.println("WORLD ENTITIES: " + output.size());

        //uncomment this for verbose output
        //output.entrySet().stream().forEach(System.out::println);

        return output;
    }
    private static Map<String, Entity> getContinents(List<List<String>> lines) {
        Map<String,Entity> output;
        List<String> cols;

        // get the indices of our columns of interest
        cols = getColumns(lines);
        int cod = cols.indexOf("iso_code");
        int cnt = cols.indexOf("continent");
        int loc = cols.indexOf("location");
        int pop = cols.indexOf("population");
        int ma  = cols.indexOf("median_age");

        output = lines.stream()
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
                        (Continent::getIsocode), //make isocode + date as composite key
                        Function.identity()));
        System.out.println("CONTINENTS: " + output.size());

        //uncomment this for verbose output
        //output.entrySet().stream().forEach(System.out::println);

        return output;
    }
    private static Map<String, Entity> getCountries(List<List<String>> lines, Map<String, Entity> continents) {
        Map<String, Entity> output;
        List<String> cols;
        Map<String, String> continentsIsocodes;

        // get the indices of our columns of interest
        cols = getColumns(lines);
        int cod = cols.indexOf("iso_code");
        int cnt = cols.indexOf("continent");
        int loc = cols.indexOf("location");
        int pop = cols.indexOf("population");
        int ma  = cols.indexOf("median_age");

        // get continent isocodes as they'll be used as a 'foreign-key' of Continents map
        // [key=continent location, value=continent isocode]
        continentsIsocodes = continents.values().stream()
                .collect(Collectors.toMap(Entity::getLocation, Entity::getIsocode) );

        output = lines.stream()
                .skip(1)                                 //skip column names
                .filter(distinctByKey(x -> x.get(cod)))  //pick only 1 entry per iso_code
                .filter(x -> !x.get(cnt).equals(""))     //entries w/out continent are not countries
                .map(x -> new Country (                  //create a Country from List<String> fields
                        x.get(cod),
                        continentsIsocodes.get(x.get(cnt)),
                        x.get(loc),
                        x.get(pop),
                        x.get(ma)))
                .collect(Collectors.toMap(
                        (Country::getIsocode), //make isocode + date as composite key
                        Function.identity()));
        System.out.println("COUNTRIES: " + output.size());

        //uncomment this for verbose output
        //output.entrySet().stream().forEach(System.out::println);

        return output;
    }



    // helpers for getting lines
    private static List<String> getColumns(List<List<String>> lines) {
        return lines.stream().findFirst().get();
    }
    private static List<List<String>> getLines(String filepath) {
        List<List<String>> output;
        Path path;

        path = Paths.get(filepath);
        try(BufferedReader bf = Files.newBufferedReader(path)) {
            output = bf.lines()
                    .map(x -> Arrays.asList(x.split(",", -1)))
                    .toList();
        }catch(Exception e) {
            e.printStackTrace();
            output = null;
        }

        return output;
    }

    // filtering method, needed by methods that populate maps
    // courtesy of https://stackoverflow.com/a/27872852
    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }

    // methods to deal with arguments/usage
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
        sortingOptions = new HashMap<>();
        sortingOptions.put("nc-max",   Comparator.comparingInt(CovidReport::getNewCases).reversed());
        sortingOptions.put("nc-min",   Comparator.comparingInt(CovidReport::getNewCases));
        sortingOptions.put("ncs-max",  Comparator.comparingDouble(CovidReport::getNewCasesSmoothed).reversed());
        sortingOptions.put("ncs-min",  Comparator.comparingDouble(CovidReport::getNewCasesSmoothed));
        sortingOptions.put("nd-max",   Comparator.comparingInt(CovidReport::getNewDeaths).reversed());
        sortingOptions.put("nd-min",   Comparator.comparingInt(CovidReport::getNewDeaths));
        sortingOptions.put("nds-max",  Comparator.comparingDouble(CovidReport::getNewDeathsSmoothed).reversed());
        sortingOptions.put("nds-min",  Comparator.comparingDouble(CovidReport::getNewDeathsSmoothed));
        sortingOptions.put("nt-max",   Comparator.comparingInt(CovidReport::getNewTests).reversed());
        sortingOptions.put("nt-min",   Comparator.comparingInt(CovidReport::getNewTests));
        sortingOptions.put("ndpc-max", Comparator.comparingDouble(CovidReport::getNewDeathsPerCase).reversed());
        sortingOptions.put("ndpc-min", Comparator.comparingDouble(CovidReport::getNewDeathsPerCase));
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
}
