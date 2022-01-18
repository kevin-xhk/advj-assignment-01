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
        List<List<String>> lines = null;
        Path path = Paths.get("/home/kev/Downloads/owid-covid-data.csv");

        try(BufferedReader bf = Files.newBufferedReader(path)) {
            lines = bf.lines()
                    .map(x -> Arrays.asList(x.split(",", -1)))
                    .toList();
        }catch(Exception e) {
            e.printStackTrace();
        }

        //get column indices
        List<String> cols = lines.stream().findFirst().get();

        //save indices needed by Country's constructor
        int cod = cols.indexOf("iso_code");
        int cnt = cols.indexOf("continent");
        int loc = cols.indexOf("location");
        int pop = cols.indexOf("population");
        int ma  = cols.indexOf("median_age");

        //save indices needed by CovidReport's constructor
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

        //get list of countries
        Map<Integer, Country> countries = lines.stream()
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
        System.out.println("COUNTRIES: " + countries.size());

        //get covid-reports
        Map<Integer, CovidReport> covidReports = lines.stream()
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
        System.out.println("REPORTS: " + covidReports.size());
    }
}
