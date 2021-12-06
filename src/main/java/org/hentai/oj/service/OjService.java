package org.hentai.oj.service;

import org.hentai.oj.bean.OjProperties;
import org.hentai.oj.err.CodeTypeException;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class OjService {

    @Autowired
    OjProperties ojProperties;

    public boolean saveCodeFile(String username, String code, String codeType) throws IOException {

        if (!ojProperties.getCodeTypes().contains(codeType)) {
            return false;
        }

        File dir = new File(ojProperties.getCodePath());
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String allPath = String.format("%s/%s.%s", ojProperties.getCodePath(), username, codeType);

        File f = new File(allPath);
        if (!f.exists()) {
            f.createNewFile();
        }

        BufferedWriter writer = new BufferedWriter(new FileWriter(allPath));

        writer.write(code);
        writer.flush();
        writer.close();

        return true;
    }

    public String getCommand(String username, String codeType) throws CodeTypeException {
//        String cmd = ojProperties.getCodePath();
        String cmd = String.format("chmod 777 %s && %s", ojProperties.getCodePath(), ojProperties.getCodePath());
        switch (codeType) {
            case "c":
                cmd = String.format("%s && gcc %s.c -o %s && ./%s",
                        cmd, username, username, username);
                break;
            case "py":
                cmd = String.format("%s && python3 %s.py", cmd, username);
                break;
            case "go":
                cmd = String.format("%s && go build %s.go && ./%s",
                        cmd, username, username);
                break;
            case "java":
                cmd = String.format("%s && java %s.java && javac %s",
                        cmd, username, username);
                break;
            default:
                throw new CodeTypeException();
        }
        return cmd;
    }

    public Map<Boolean, String> runCommand(String command, String in, String out) throws IOException, InterruptedException {
        Process exec = Runtime.getRuntime().exec(command);

        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(exec.getOutputStream()));
        bw.write(in);
        bw.newLine();
        bw.flush();
        bw.close();

        return judgeCommand(exec, out);
    }

    public Map<Boolean, String> runCommand(String command, String out) throws IOException, InterruptedException {
        Process exec = Runtime.getRuntime().exec(command);
        return judgeCommand(exec, out);
    }

    private Map<Boolean, String> judgeCommand(Process exec, String out) throws IOException, InterruptedException {
        Thread.sleep(ojProperties.getRunTime());
        BufferedReader br;
        if (exec.exitValue() == 0) {
            br = new BufferedReader(new InputStreamReader(exec.getInputStream()));
        } else {
            br = new BufferedReader(new InputStreamReader(exec.getErrorStream()));
        }

        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
            sb.append("\n");
        }
        br.close();
        exec.destroy();

        Map<Boolean, String> answerType = new HashMap<>();
        if (out != null && !out.equals("")) {
            if (sb.toString().trim().equals(out.trim())) {
                answerType.put(true, "回答正确");
            } else {
                answerType.put(false, "回答错误");
            }
            return answerType;
        }

        return null;
    }

}
