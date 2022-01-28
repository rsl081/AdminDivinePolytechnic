package com.s2dioapps.admindivinepolytechnic.ui.module;

public class ModuleModel {
    public ModuleModel(String moduleId, String modulePdf) {
        this.moduleId = moduleId;
        this.modulePdf = modulePdf;
    }

    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    public String getModulePdf() {
        return modulePdf;
    }

    public void setModulePdf(String modulePdf) {
        this.modulePdf = modulePdf;
    }

    String moduleId;
    String modulePdf;
}
