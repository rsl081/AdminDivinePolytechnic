package com.s2dioapps.admindivinepolytechnic.ui.subject;

public class SubjectModel {

    public SubjectModel(String id, String name, int noOfTests, String testCounter) {
        this.id = id;
        this.name = name;
        this.noOfTests = noOfTests;
        this.testCounter = testCounter;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNoOfTests() {
        return noOfTests;
    }

    public void setNoOfTests(int noOfTests) {
        this.noOfTests = noOfTests;
    }

    private String id;
    private String name;
    private int noOfTests;
    private String testCounter;

}
