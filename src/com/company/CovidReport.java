package com.company;

public class CovidReport {
    String isocode;
    String date;
    int totalCases;
    int newCases;
    float newCasesSmoothed;
    int totalDeaths;
    int newDeaths;
    float newDeathsSmoothed;
    float reproductionRate;
    int newTests;
    int totalTests;
    float stringencyIndex;

    public CovidReport(String isocode, String date, String totalCases, String newCases, String newCasesSmoothed, String totalDeaths, String newDeaths, String newDeathsSmoothed, String reproductionRate, String newTests, String totalTests, String stringencyIndex) {
        this.isocode = isocode;
        this.date = date;
        this.totalCases = totalCases.equals("") ? 0 : (int) Float.parseFloat(totalCases);
        this.newCases = newCases.equals("") ? 0 : (int) Float.parseFloat(newCases);
        this.newCasesSmoothed = newCasesSmoothed.equals("") ? 0.0f : Float.parseFloat(newCasesSmoothed);
        this.totalDeaths = totalDeaths.equals("") ? 0 : (int) Float.parseFloat(totalDeaths);
        this.newDeaths = newDeaths.equals("") ? 0 : (int) Float.parseFloat(newDeaths);
        this.newDeathsSmoothed = newDeathsSmoothed.equals("") ? 0.0f : Float.parseFloat(newDeathsSmoothed);
        this.reproductionRate = reproductionRate.equals("") ? 0.0f : Float.parseFloat(reproductionRate);
        this.newTests = newTests.equals("") ? 0 : (int) Float.parseFloat(newTests);
        this.totalTests = totalTests.equals("") ? 0 : (int) Float.parseFloat(totalTests);
        this.stringencyIndex = stringencyIndex.equals("") ? 0.0f : Float.parseFloat(stringencyIndex);
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

    public int getTotalCases() {
        return totalCases;
    }

    public void setTotalCases(String totalCases) {
        this.totalCases = totalCases.equals("") ? 0 : (int) Float.parseFloat(totalCases);
    }

    public int getNewCases() {
        return newCases;
    }

    public void setNewCases(String newCases) {
        this.newCases = newCases.equals("") ? 0 : (int) Float.parseFloat(newCases);;
    }

    public float getNewCasesSmoothed() {
        return newCasesSmoothed;
    }

    public void setNewCasesSmoothed(String newCasesSmoothed) {
        this.newCasesSmoothed = newCasesSmoothed.equals("") ? 0 : Float.parseFloat(newCasesSmoothed);
    }

    public int getTotalDeaths() {
        return totalDeaths;
    }

    public void setTotalDeaths(String totalDeaths) {
        this.totalDeaths = totalDeaths.equals("") ? 0 : (int) Float.parseFloat(totalDeaths);
    }

    public int getNewDeaths() {
        return newDeaths;
    }

    public void setNewDeaths(String newDeaths) {
        this.newDeaths = newDeaths.equals("") ? 0 : (int) Float.parseFloat(newDeaths);
    }

    public float getNewDeathsSmoothed() {
        return newDeathsSmoothed;
    }

    public void setNewDeathsSmoothed(String newDeathsSmoothed) {
        this.newDeathsSmoothed = newDeathsSmoothed.equals("") ? 0 : Float.parseFloat(newDeathsSmoothed);
    }

    public float getReproductionRate() {
        return reproductionRate;
    }

    public void setReproductionRate(String reproductionRate) {
        this.reproductionRate = reproductionRate.equals("") ? 0 : Float.parseFloat(reproductionRate);
    }

    public int getNewTests() {
        return newTests;
    }

    public void setNewTests(String newTests) {
        this.newTests = newTests.equals("") ? 0 : (int) Float.parseFloat(newTests);
    }

    public int getTotalTests() {
        return totalTests;
    }

    public void setTotalTests(String totalTests) {
        this.totalTests = totalTests.equals("") ? 0 : (int) Float.parseFloat(totalTests);
    }

    public float getStringencyIndex() {
        return stringencyIndex;
    }

    public void setStringencyIndex(String stringencyIndex) {
        this.stringencyIndex = stringencyIndex.equals("") ? 0 : Float.parseFloat(stringencyIndex);
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
