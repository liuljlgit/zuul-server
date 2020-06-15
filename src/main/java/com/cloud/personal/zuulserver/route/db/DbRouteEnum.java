package com.cloud.personal.zuulserver.route.db;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum  DbRouteEnum {
    ROUTE_FALSE((byte)0,false),
    ROUTE_TURE((byte)1,true);

    private Byte code;

    private Boolean bl;

    static Map<Byte,Boolean> codeMap;

    static {
        codeMap = new HashMap<>();
        Arrays.stream(DbRouteEnum.values()).forEach(e -> {
            codeMap.put(e.getCode(),e.getBl());
        });
    }

    DbRouteEnum(Byte code, Boolean bl) {
        this.code = code;
        this.bl = bl;
    }

    public Byte getCode() {
        return code;
    }

    public void setCode(Byte code) {
        this.code = code;
    }

    public Boolean getBl() {
        return bl;
    }

    public void setBl(Boolean bl) {
        this.bl = bl;
    }
}
