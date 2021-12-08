package org.hentai.oj.service;

import org.hentai.oj.bean.OjParamName;
import org.hentai.oj.bean.OjProperties;
import org.hentai.oj.err.CodeTypeException;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class OjService {

    @Autowired
    OjProperties ojProperties;

    /**
     * 文件保存
     *
     * @param fileName   纯文件名 没有文件后缀
     * @param code       代码内容
     * @param fileSuffix 文件后缀
     * @return 代码是否保存成功
     */
    public boolean saveCodeFile(String fileName, String code, String fileSuffix) throws IOException {

        if (!ojProperties.getCodeTypeAndRun().containsKey(fileSuffix)) {
            return false;
        }

        File dir = new File(ojProperties.getCodePath());

        if (!dir.exists()) {
            dir.mkdirs();
        }

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
        String[] orders = ojProperties.getCodeTypeAndRun().get(codeType).split(" && ");
        if (orders.length == 1) {
            return new HashMap<String, String>() {{
                put(OjParamName.RUN, orders[0].replace(ojProperties.getFileNameSign(), fileName));
            }};
        } else if (orders.length == 2) {
            return new HashMap<String, String>() {{
                put(OjParamName.COMPILE, orders[0].replace(ojProperties.getFileNameSign(), fileName));
                put(OjParamName.RUN, orders[1].replace(ojProperties.getFileNameSign(), fileName));
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
            process.waitFor();
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

    //-------------------------------------------以下为编译运行未分离代码，仍可使用----------------------

    /**
     * 获取指令
     *
     * @param filename 文件名 无后缀
     * @param codeType 代码类型
     * @return 编译运行一体指令
     */
    public String getCommand(String filename, String codeType) throws CodeTypeException {
        String cmd = ojProperties.getCodeTypeAndRun().get(codeType).replace(ojProperties.getFileNameSign(), filename);

        cmd = String.format("cd %s && %s", ojProperties.getCodePath(), cmd);

        return cmd;
    }

    /**
     * 编译并运行 有传入参数
     *
     * @param command 指令
     * @param in      传入参数
     * @param out     目标输出
     * @return 答题结果
     */
    public Map<Boolean, String> runCommand(String command, String in, String out) throws IOException, InterruptedException {

        Process exec = Runtime.getRuntime().exec(ojProperties.getRunSh());

        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(exec.getOutputStream()));
        bw.write(command);
        bw.write(in);
        bw.newLine();
        bw.flush();
        bw.close();

        return judgeCommand(exec, out);
    }

    /**
     * 编译并运行 无传入参数
     *
     * @param command 指令
     * @param out     目标输出
     * @return 答题结果
     */
    public Map<Boolean, String> runCommand(String command, String out) throws IOException, InterruptedException {
        Process exec = Runtime.getRuntime().exec(ojProperties.getRunSh());
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(exec.getOutputStream()));
        bw.write(command);
        bw.flush();
        bw.close();
        return judgeCommand(exec, out);
    }

    /**
     * 读取输出并判断
     *
     * @param exec 子进程
     * @param out  目标输出
     * @return 答题结果
     */
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
                answerType.put(false, String.format("你的答案: %s\t正确答案: %s", sb.toString(), out));
            }
            return answerType;
        }

        return null;
    }

}
