package org.hentai.oj.bean;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@ConfigurationProperties(prefix = "hentai.oj")
public class OjProperties {

    private String codePath;
    private String fileNameSign;
    private Long runTime;
    private Map<String, String> codeTypeAndRun;


    public OjProperties() {
    }

    public OjProperties(String codePath, String fileNameSign, Long runTime, Map<String, String> codeTypeAndRun) {
        this.codePath = codePath;
        this.fileNameSign = fileNameSign;
        this.runTime = runTime;
        this.codeTypeAndRun = codeTypeAndRun;
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

    public Map<String, String> getCodeTypeAndRun() {
        return codeTypeAndRun;
    }

    public void setCodeTypeAndRun(Map<String, String> codeTypeAndRun) {
        this.codeTypeAndRun = codeTypeAndRun;
    }
}
