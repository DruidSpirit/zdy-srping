package com.lagou.edu.enums;

public enum ProxyType {

    JDK_PROXY("jdk","jdk动态代理"),
    CGLIB_PROXY("cglib","cglib动态代理");

    private String type;
    private String message;

    ProxyType(String type, String message) {
        this.type = type;
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }
}
