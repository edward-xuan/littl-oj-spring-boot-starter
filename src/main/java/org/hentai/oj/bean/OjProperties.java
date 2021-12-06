package org.hentai.oj.bean;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "hentai.oj")
public class OjProperties {

    private String codePath;

    private List<String> codeTypes;

    private Long runTime;


    public OjProperties() {
    }

    public OjProperties(String codePath, List<String> codeTypes, Long runTime) {
        this.codePath = codePath;
        this.codeTypes = codeTypes;
        this.runTime = runTime;
    }

    public String getCodePath() {
        return codePath;
    }

    public void setCodePath(String codePath) {
        this.codePath = codePath;
    }

    public List<String> getCodeTypes() {
        return codeTypes;
    }

    public void setCodeTypes(List<String> codeTypes) {
        this.codeTypes = codeTypes;
    }

    public Long getRunTime() {
        return runTime;
    }

    public void setRunTime(Long runTime) {
        this.runTime = runTime;
    }
}
