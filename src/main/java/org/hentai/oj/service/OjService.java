package org.hentai.oj.service;

import org.hentai.oj.bean.OjAdminProperties;
import org.hentai.oj.bean.OjOrder;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OjService {

    private OjAdminProperties ojAdminProperties;
    private ProcessBuilder pb = new ProcessBuilder();

    public OjService(OjAdminProperties ojAdminProperties) {
        this.ojAdminProperties = ojAdminProperties;
        pb.directory(new File(ojAdminProperties.getCodePath()));
    }

    public void setOjAdminProperties(OjAdminProperties ojAdminProperties) {
        this.ojAdminProperties = ojAdminProperties;
    }

    public OjAdminProperties getOjAdminProperties() {
        return ojAdminProperties;
    }

    /**
     * 文件保存
     *
     * @param fileName 纯文件名 没有文件后缀
     * @param code     代码内容
     * @param codeType 代码类型
     * @return 代码是否保存成功
     */
    public boolean saveCodeFile(String fileName, String code, String codeType) throws IOException {

        Map<String, List<String>> codeTypeAndRun = ojAdminProperties.getCodeTypeAndRun();

        if (!codeTypeAndRun.containsKey(codeType)) {
            return false;
        }

        File dir = new File(ojAdminProperties.getCodePath());

        if (!dir.exists()) {
            dir.mkdirs();
        }

        String fileSuffix = codeTypeAndRun.get(codeType).get(0);

        String allPath = String.format("%s%s%s.%s", ojAdminProperties.getCodePath(), File.separator, fileName, fileSuffix);

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

        List<String> orders = ojAdminProperties.getCodeTypeAndRun().get(codeType);
        if (orders.size() == 2) {
            return new HashMap<String, String>() {{
                put(OjOrder.RUN, orders.get(1).replace(ojAdminProperties.getFileNameSign(), fileName));
            }};
        } else if (orders.size() == 3) {
            return new HashMap<String, String>() {{
                put(OjOrder.COMPILE, orders.get(1).replace(ojAdminProperties.getFileNameSign(), fileName));
                put(OjOrder.RUN, orders.get(2).replace(ojAdminProperties.getFileNameSign(), fileName));
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
        Process exec = pb.command(command.split("\\s+")).start();
        exec.waitFor();
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
        System.out.println(Arrays.asList(command.split("\\s+")));
        Process process = null;
        try {
            process = pb.command(command.split("\\s+")).start();
            process.waitFor();
            Thread.sleep(ojAdminProperties.getRunTime());
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
            process = Runtime.getRuntime().exec(ojAdminProperties.getRunSh());
            process = pb.command(Arrays.asList(command.split("\\s+"))).start();
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
            bw.write(in);
            bw.close();
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

}
