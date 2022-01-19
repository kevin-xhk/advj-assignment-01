package com.company;

public class Country<T> {
    String isocode;
    String continent;
    String location;
    String population;
    String medianage;

    public Country(String isocode, String continent, String location, String population, String medianage) {
        this.isocode = isocode;
        this.continent = continent;
        this.location = location;
        this.population = population;
        this.medianage = medianage;
    }

    public String getIsocode() {
        return isocode;
    }

    public void setIsocode(String isocode) {
        this.isocode = isocode;
    }

    public String getContinent() {
        return continent;
    }

    public void setContinent(String continent) {
        this.continent = continent;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPopulation() {
        return population;
    }

    public void setPopulation(String population) {
        this.population = population;
    }

    public String getMedianage() {
        return medianage;
    }

    public void setMedianage(String medianage) {
        this.medianage = medianage;
    }


    @Override
    public String toString() {
        return "Country{" +
                "isocode='" + isocode + '\'' +
                ", continent='" + continent + '\'' +
                ", location='" + location + '\'' +
                ", population='" + population + '\'' +
                ", medianage='" + medianage + '\'' +
                '}';
    }
}
