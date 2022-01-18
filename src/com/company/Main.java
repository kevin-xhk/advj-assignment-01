package com.company;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Main {

    //courtesy of https://stackoverflow.com/a/27872852
    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }

    public static void main(String[] args) {
        //TODO: MAKE PATH AS A COMMAND-LINE ARG
	    //read csv
        String filepath = "/home/kev/Downloads/owid-covid-data.csv";
        List<List<String>> lines = getLines(filepath);

        //get column indices
        List<String> cols = lines.stream().findFirst().get();

        //get list of countries
        Map<Integer, Country> countries        = getCountries(lines, cols);

        //get covid-reports
        Map<Integer, CovidReport> covidReports = getCovidReports(lines,cols);

    }

    private static Map<Integer, CovidReport> getCovidReports(List<List<String>> lines, List<String> cols) {
        //save indices needed by CovidReport's constructor
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

        Map<Integer, CovidReport> output =                 lines.stream()
                .skip(1)
                .filter(x -> !x.get(cnt).equals(""))
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

    private static Map<Integer, Country> getCountries(List<List<String>> lines, List<String> cols) {
        //save indices needed by Country's constructor
        int cod = cols.indexOf("iso_code");
        int cnt = cols.indexOf("continent");
        int loc = cols.indexOf("location");
        int pop = cols.indexOf("population");
        int ma  = cols.indexOf("median_age");

        Map<Integer,Country> output = lines.stream()
                .skip(1)                                                //skip column names
                .filter(distinctByKey(x -> x.get(cod)))                 //pick only 1 entry per iso_code
                .filter(x -> !x.get(cnt).equals(""))                    //entries w/out continent are not countries
                .map(x -> new Country (                                 //create a Country from List<String> fields
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
