package com.company;

public class Country implements Entity {
    String isocode;
    String continentIsocode;
    String location;
    String population;
    String medianage;

    public Country(String isocode, String continent, String location, String population, String medianage) {
        this.isocode = isocode;
        this.continentIsocode = continent;
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

    public String getContinentIsocode() {
        return continentIsocode;
    }

    public void setContinentIsocode(String continent) {
        this.continentIsocode = continent;
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
                ", continentIsoCode='" + continentIsocode + '\'' +
                ", location='" + location + '\'' +
                ", population='" + population + '\'' +
                ", medianage='" + medianage + '\'' +
                '}';
    }
}
