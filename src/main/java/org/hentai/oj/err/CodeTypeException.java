package org.hentai.oj.err;

public class CodeTypeException extends Exception {
    public CodeTypeException() {
        super("找不到这种代码类型");
    }
}
