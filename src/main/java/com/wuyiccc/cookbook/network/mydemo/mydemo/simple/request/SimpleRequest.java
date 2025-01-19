package com.wuyiccc.cookbook.network.mydemo.mydemo.simple.request;

import java.io.Serializable;

/**
 * @author wuyiccc
 * @date 2025/1/19 15:57
 */
public class SimpleRequest implements Serializable {

    private String content;

    private Exception e;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Exception getE() {
        return e;
    }

    public void setE(Exception e) {
        this.e = e;
    }

    @Override
    public String toString() {
        return "SimpleRequest{" +
                "content='" + content + '\'' +
                ", e=" + e +
                '}';
    }
}
