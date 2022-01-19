package com.company;

public class WorldEntity<T> {
    String isocode;
    String location;
    String population;
    String medianage;

    public WorldEntity(String isocode, String location, String population, String medianage) {
        this.isocode = isocode;
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
        return "WorldEntity{" +
                "isocode='" + isocode + '\'' +
                ", location='" + location + '\'' +
                ", population='" + population + '\'' +
                ", medianage='" + medianage + '\'' +
                '}';
    }
}
