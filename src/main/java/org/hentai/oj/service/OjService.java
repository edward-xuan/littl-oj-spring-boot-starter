package org.hentai.oj.service;

import org.hentai.oj.bean.OjParamName;
import org.hentai.oj.bean.OjProperties;
import org.hentai.oj.err.CodeTypeException;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OjService {

    @Autowired
    OjProperties ojProperties;

    /**
     * 文件保存
     *
     * @param fileName 纯文件名 没有文件后缀
     * @param code     代码内容
     * @param codeType 代码类型
     * @return 代码是否保存成功
     */
    public boolean saveCodeFile(String fileName, String code, String codeType) throws IOException {

        Map<String, List<String>> codeTypeAndRun = ojProperties.getCodeTypeAndRun();

        if (!codeTypeAndRun.containsKey(codeType)) {
            return false;
        }

        File dir = new File(ojProperties.getCodePath());

        if (!dir.exists()) {
            dir.mkdirs();
        }

        String fileSuffix = codeTypeAndRun.get(codeType).get(0);

        String allPath = String.format("%s%s%s.%s", ojProperties.getCodePath(), File.separator, fileName, fileSuffix);

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

    /**
     * 获取文件编译和运行的指令
     *
     * @param fileName 文件名 没有文件后缀
     * @param codeType 代码类型
     * @return 文件编译和运行的指令
     */
    public Map<String, String> getCompileAndRunOrder(String fileName, String codeType) {

        List<String> orders = ojProperties.getCodeTypeAndRun().get(codeType);
        if (orders.size() == 2) {
            return new HashMap<String, String>() {{
                put(OjParamName.RUN, orders.get(1).replace(ojProperties.getFileNameSign(), fileName));
            }};
        } else if (orders.size() == 3) {
            return new HashMap<String, String>() {{
                put(OjParamName.COMPILE, orders.get(1).replace(ojProperties.getFileNameSign(), fileName));
                put(OjParamName.RUN, orders.get(2).replace(ojProperties.getFileNameSign(), fileName));
            }};
        }
        return null;
    }

    /**
     * 编译
     *
     * @param command 编译指令
     * @return 编译状态
     */
    public Map<Boolean, String> compile(String command) throws IOException, InterruptedException {
        Process exec = Runtime.getRuntime().exec(ojProperties.getRunSh());

        execWrite(new BufferedWriter(new OutputStreamWriter(exec.getOutputStream())), command);
        exec.waitFor();
//        Thread.sleep(ojProperties.getRunTime());
        BufferedReader br;
        Map<Boolean, String> map = new HashMap<>();
        if (exec.exitValue() == 0) {
            new BufferedReader(new InputStreamReader(exec.getInputStream()));
            map.put(true, "编译成功");
        } else {
            br = new BufferedReader(new InputStreamReader(exec.getErrorStream()));
            map.put(false, execRead(br));
        }

        exec.destroy();
        return map;
    }

    /**
     * 无传入参数运行
     *
     * @param command 运行指令
     * @param out     目标输出
     * @return 答题状态
     */
    public Map<Boolean, String> run(String command, String out) {
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(ojProperties.getRunSh());
            execWrite(new BufferedWriter(new OutputStreamWriter(process.getOutputStream())), command);
//            process.waitFor();
            Thread.sleep(ojProperties.getRunTime());
            return judge(process, out);

        } catch (Exception e) {
            return new HashMap<Boolean, String>() {{
                put(false, e.toString());
            }};
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
    }

    /**
     * 有传入参数运行
     *
     * @param command 运行指令
     * @param in      传入参数
     * @param out     目标输出
     * @return 答题状态
     */
    public Map<Boolean, String> run(String command, String in, String out) {
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(ojProperties.getRunSh());
            execWrite(new BufferedWriter(new OutputStreamWriter(process.getOutputStream())), command, in);
            process.waitFor();
            return judge(process, out);

        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap<Boolean, String>() {{
                put(false, e.toString());
            }};
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
    }

    /**
     * 判断参数
     *
     * @param process 子进程 用于读取输出
     * @param out     目标输出
     * @return 答题状态
     */
    private Map<Boolean, String> judge(Process process, String out) throws IOException, InterruptedException {
        BufferedReader br;
        Map<Boolean, String> map = new HashMap<>();
        int i = process.exitValue();
        if (i == 0) {
            br = new BufferedReader(new InputStreamReader(process.getInputStream()));
        } else {
            br = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        }

        String answer = execRead(br).trim();
        return new HashMap<Boolean, String>() {{
            put(answer.equals(out.trim()), answer);
        }};
    }

    /**
     * 读取输出
     *
     * @param br 输入流
     * @return 实际输出
     */
    private String execRead(BufferedReader br) throws IOException {
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
            sb.append("\n");
        }

        return sb.toString();
    }

    /**
     * 传入指令
     *
     * @param bw      输出流
     * @param command 指令
     */
    private void execWrite(BufferedWriter bw, String command) throws IOException {
        bw.write(String.format("cd %s && %s", ojProperties.getCodePath(), command));
        bw.newLine();
        bw.flush();
        bw.close();
    }

    /**
     * 传入指令
     *
     * @param bw      输出流
     * @param command 指令
     * @param in      传入参数
     */
    private void execWrite(BufferedWriter bw, String command, String in) throws IOException {
        bw.write(String.format("cd %s && %s", ojProperties.getCodePath(), command));
        bw.newLine();
        bw.write(in);
        bw.newLine();
        bw.flush();
        bw.close();
    }

}
