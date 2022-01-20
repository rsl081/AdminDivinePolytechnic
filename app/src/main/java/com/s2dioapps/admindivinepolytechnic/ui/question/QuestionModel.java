package com.s2dioapps.admindivinepolytechnic.ui.question;

public class QuestionModel {


    public QuestionModel(String optionA, int correctAns, String optionB, String optionC,
                         String category, String optionD, String question, String test, String uid) {
        this.optionA = optionA;
        this.correctAns = correctAns;
        this.optionB = optionB;
        this.optionC = optionC;
        this.category = category;
        this.optionD = optionD;
        this.question = question;
        this.test = test;
        this.uid = uid;
    }

    public String getOptionA() {
        return optionA;
    }

    public void setOptionA(String optionA) {
        this.optionA = optionA;
    }

    public int getCorrectAns() {
        return correctAns;
    }

    public void setCorrectAns(int correctAns) {
        this.correctAns = correctAns;
    }

    public String getOptionB() {
        return optionB;
    }

    public void setOptionB(String optionB) {
        this.optionB = optionB;
    }

    public String getOptionC() {
        return optionC;
    }

    public void setOptionC(String optionC) {
        this.optionC = optionC;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getOptionD() {
        return optionD;
    }

    public void setOptionD(String optionD) {
        this.optionD = optionD;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getTest() {
        return test;
    }

    public void setTest(String test) {
        this.test = test;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    private String optionA;
    private int correctAns;
    private String optionB;
    private String optionC;
    private String category;
    private String optionD;
    private String question;
    private String test;
    private String uid;


}
