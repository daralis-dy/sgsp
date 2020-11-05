package com.whrailway.sgsp.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * ClassName: R
 * Description:
 *
 * @author honghh
 * @date 2019/02/20 15:14
 */
public class R extends HashMap<String, Object> {
    private static final long serialVersionUID = 1L;

    public R() {
        put("code", 0);
        put("msg", "success");
    }

    public static com.whrailway.sgsp.utils.R error() {
        return error(500, "未知异常，请联系管理员");
    }

    public static com.whrailway.sgsp.utils.R error(String msg) {
        return error(500, msg);
    }

    public static com.whrailway.sgsp.utils.R error(int code, String msg) {
        com.whrailway.sgsp.utils.R r = new com.whrailway.sgsp.utils.R();
        r.put("code", code);
        r.put("msg", msg);
        return r;
    }

    public static com.whrailway.sgsp.utils.R error(String code, String msg) {
        com.whrailway.sgsp.utils.R r = new com.whrailway.sgsp.utils.R();
        r.put("code", code);
        r.put("msg", msg);
        return r;
    }

    public static com.whrailway.sgsp.utils.R ok(String msg) {
        com.whrailway.sgsp.utils.R r = new com.whrailway.sgsp.utils.R();
        r.put("msg", msg);
        return r;
    }

    public static com.whrailway.sgsp.utils.R ok(Map<String, Object> map) {
        com.whrailway.sgsp.utils.R r = new com.whrailway.sgsp.utils.R();
        r.putAll(map);
        return r;
    }

    public static com.whrailway.sgsp.utils.R ok() {
        return new com.whrailway.sgsp.utils.R();
    }

    @Override
    public com.whrailway.sgsp.utils.R put(String key, Object value) {
        super.put(key, value);
        return this;
    }
}
