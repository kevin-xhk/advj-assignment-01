package com.company;

public class CovidReport {
    String isocode;
    String date;
    String totalCases;
    String newCases;
    String newCasesSmoothed;
    String totalDeaths;
    String newDeaths;
    String newDeathsSmoothed;
    String reproductionRate;
    String newTests;
    String totalTests;
    String stringencyIndex;

    public CovidReport(String isocode, String date, String totalCases, String newCases, String newCasesSmoothed, String totalDeaths, String newDeaths, String newDeathsSmoothed, String reproductionRate, String newTests, String totalTests, String stringencyIndex) {
        this.isocode = isocode;
        this.date = date;
        this.totalCases = totalCases;
        this.newCases = newCases;
        this.newCasesSmoothed = newCasesSmoothed;
        this.totalDeaths = totalDeaths;
        this.newDeaths = newDeaths;
        this.newDeathsSmoothed = newDeathsSmoothed;
        this.reproductionRate = reproductionRate;
        this.newTests = newTests;
        this.totalTests = totalTests;
        this.stringencyIndex = stringencyIndex;
    }

    public String getIsocode() {
        return isocode;
    }

    public void setIsocode(String isocode) {
        this.isocode = isocode;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTotalCases() {
        return totalCases;
    }

    public void setTotalCases(String totalCases) {
        this.totalCases = totalCases;
    }

    public String getNewCases() {
        return newCases;
    }

    public void setNewCases(String newCases) {
        this.newCases = newCases;
    }

    public String getNewCasesSmoothed() {
        return newCasesSmoothed;
    }

    public void setNewCasesSmoothed(String newCasesSmoothed) {
        this.newCasesSmoothed = newCasesSmoothed;
    }

    public String getTotalDeaths() {
        return totalDeaths;
    }

    public void setTotalDeaths(String totalDeaths) {
        this.totalDeaths = totalDeaths;
    }

    public String getNewDeaths() {
        return newDeaths;
    }

    public void setNewDeaths(String newDeaths) {
        this.newDeaths = newDeaths;
    }

    public String getNewDeathsSmoothed() {
        return newDeathsSmoothed;
    }

    public void setNewDeathsSmoothed(String newDeathsSmoothed) {
        this.newDeathsSmoothed = newDeathsSmoothed;
    }

    public String getReproductionRate() {
        return reproductionRate;
    }

    public void setReproductionRate(String reproductionRate) {
        this.reproductionRate = reproductionRate;
    }

    public String getNewTests() {
        return newTests;
    }

    public void setNewTests(String newTests) {
        this.newTests = newTests;
    }

    public String getTotalTests() {
        return totalTests;
    }

    public void setTotalTests(String totalTests) {
        this.totalTests = totalTests;
    }

    public String getStringencyIndex() {
        return stringencyIndex;
    }

    public void setStringencyIndex(String stringencyIndex) {
        this.stringencyIndex = stringencyIndex;
    }

    @Override
    public String toString() {
        return "CovidReport{" +
                "isocode='" + isocode + '\'' +
                ", date='" + date + '\'' +
                ", totalCases='" + totalCases + '\'' +
                ", newCases='" + newCases + '\'' +
                ", newCasesSmoothed='" + newCasesSmoothed + '\'' +
                ", totalDeaths='" + totalDeaths + '\'' +
                ", newDeaths='" + newDeaths + '\'' +
                ", newDeathsSmoothed='" + newDeathsSmoothed + '\'' +
                ", reproductionRate='" + reproductionRate + '\'' +
                ", newTests='" + newTests + '\'' +
                ", totalTests='" + totalTests + '\'' +
                ", stringencyIndex='" + stringencyIndex + '\'' +
                '}';
    }
}
