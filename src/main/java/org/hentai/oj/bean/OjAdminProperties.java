package org.hentai.oj.bean;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OjAdminProperties {

    private String runSh;
    private String codePath;
    private String fileNameSign;
    private Long runTime;
    private Map<String, List<String>> codeTypeAndRun;

    private OjAdminProperties(OjProperties properties) {
        this.runSh = properties.getRunSh();
        this.codePath = properties.getCodePath();
        this.fileNameSign = properties.getFileNameSign();
        this.runTime = properties.getRunTime();
        this.codeTypeAndRun = properties.getCodeTypeAndRun();
    }

    public static OjAdminProperties getOjAdminProperties(OjProperties properties) {
        return new OjAdminProperties(properties);
    }

    public String getRunSh() {
        return runSh;
    }

    public void setRunSh(String runSh) {
        this.runSh = runSh;
    }

    public String getCodePath() {
        return codePath;
    }

    public void setCodePath(String codePath) {
        this.codePath = codePath;
    }

    public String getFileNameSign() {
        return fileNameSign;
    }

    public void setFileNameSign(String fileNameSign) {
        this.fileNameSign = fileNameSign;
    }

    public Long getRunTime() {
        return runTime;
    }

    public void setRunTime(Long runTime) {
        this.runTime = runTime;
    }

    public Map<String, List<String>> getCodeTypeAndRun() {
        return codeTypeAndRun;
    }

    public void setCodeTypeAndRun(Map<String, List<String>> codeTypeAndRun) {
        this.codeTypeAndRun = codeTypeAndRun;
    }

    @Override
    public String toString() {
        return "OjAdminProperties{" +
                "runSh='" + runSh + '\'' +
                ", codePath='" + codePath + '\'' +
                ", fileNameSign='" + fileNameSign + '\'' +
                ", runTime=" + runTime +
                ", codeTypeAndRun=" + codeTypeAndRun +
                '}';
    }
}
