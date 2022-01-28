package com.s2dioapps.admindivinepolytechnic.ui.lesson;

public class LessonModel {

    public LessonModel(String le_id, String nameModules, int noOfModules) {
        this.le_id = le_id;
        this.nameModules = nameModules;
        this.noOfModules = noOfModules;
    }

    public String getLe_id() {
        return le_id;
    }

    public void setLe_id(String le_id) {
        this.le_id = le_id;
    }

    public String getNameModules() {
        return nameModules;
    }

    public void setNameModules(String nameModules) {
        this.nameModules = nameModules;
    }

    public int getNoOfModules() {
        return noOfModules;
    }

    public void setNoOfModules(int noOfModules) {
        this.noOfModules = noOfModules;
    }

    String le_id;
    String nameModules;
    int noOfModules;

}
