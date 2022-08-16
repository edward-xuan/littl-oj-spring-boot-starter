package org.hentai.oj.bean;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.*;

@ConfigurationProperties(prefix = "hentai.oj")
public class OjProperties {

    private String runSh = "/bin/sh";
    private String codePath;
    private String fileNameSign = "<f>";
    private Long runTime = 2000L;
    private Map<String, List<String>> codeTypeAndRun = new HashMap<String, List<String>>() {{
        put("c", Arrays.asList("c", "gcc <f>.c -o <f>", "./<f>"));
        put("python2", Arrays.asList("py", "python2 <f>.py"));
        put("python3", Arrays.asList("py", "python3 <f>.py"));
        put("go", Arrays.asList("go", "go build <f>.go", "./<f>"));
    }};

    public OjProperties() {
    }

    public OjProperties(String runSh, String codePath, String fileNameSign, Long runTime, Map<String, List<String>> codeTypeAndRun) {
        this.runSh = runSh;
        this.codePath = codePath;
        this.fileNameSign = fileNameSign;
        this.runTime = runTime;
        this.codeTypeAndRun = codeTypeAndRun;
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
}
