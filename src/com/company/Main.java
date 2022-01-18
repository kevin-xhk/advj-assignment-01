package com.company;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

public class Main {

    //courtesy of https://stackoverflow.com/a/27872852
    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }

    public static void main(String[] args) {
	    //read csv
        Path path = Paths.get("/home/kev/IdeaProjects/advj-ass1/owid-covid-data.csv");
        try(BufferedReader bf = Files.newBufferedReader(path)){

            //get column indices
            List<String> cols = bf.lines()
                    .findFirst()
                    .map(x-> Arrays.asList(x.split(",")))
                    .get();

            //save indices needed by Country's constructor
            int cod = cols.indexOf("iso_code");
            int cnt = cols.indexOf("continent");
            int loc = cols.indexOf("location");
            int pop = cols.indexOf("population");
            int ma  = cols.indexOf("median_age");

            //get list of countries
            List<Country> countries = bf.lines()
                    .skip(1)                                                //skip column names
                    .map(x -> Arrays.asList(x.split(",", -1)))  //convert csv row to List<String> including empty fields
                    .filter(distinctByKey(x -> x.get(cod)))                 //pick only 1 entry per iso_code
                    .filter(x -> !x.get(cnt).equals(""))                    //entries w/out continent are not countries
                    .map(x -> new Country (                                 //create a Country from List<String> fields
                            x.get(cod),
                            x.get(cnt),
                            x.get(loc),
                            x.get(pop),
                            x.get(ma)))
                    .toList();                                              //save as List<Countries>
            System.out.println("COUNTRIES: " + countries.size());

        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
