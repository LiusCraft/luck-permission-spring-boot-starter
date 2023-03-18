package com.liuscraft.luckpermission.utils;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author LiusCraft
 * @date 2023/3/6 9:49
 */
@Getter
public class LuckR {
    private Integer code;
    private Boolean status;
    private String msg;
    private Object data;
    private boolean isPut = false;

    /**
     * 设置Data属性对象，若之前使用了put再使用该方法则会清除Put的内容
     * @param data 对象
     * @return
     */
    public LuckR setData(Object data) {
        this.isPut = false;
        this.data = data;
        return this;
    }

    public LuckR setCode(Integer code) {
        this.code = code;
        return this;
    }

    public LuckR setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public LuckR setStatus(Boolean status) {
        this.status = status;
        return this;
    }

    /**
     * 如果使用了put则setData的内容将会消失
     * @param key 键名
     * @param value 键值
     */
    public LuckR put(String key, String value) {
        if (this.data == null || !this.isPut) {
            this.data = new HashMap<String, Object>();
        }
        HashMap<String, Object> map = (HashMap<String, Object>) (this.data);
        map.put(key,value);
        return this;
    }

    public static LuckR ok() {
        return new LuckR().setCode(200).setStatus(true).setMsg("成功");
    }

    public static LuckR error() {
        return new LuckR().setCode(404).setStatus(false).setMsg("失败");
    }

}
