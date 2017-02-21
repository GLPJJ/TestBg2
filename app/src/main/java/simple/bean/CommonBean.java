package simple.bean;

/**
 * Created by glp on 2016/7/29.
 */

public class CommonBean {
    public int code;
    public String message = "";

    public <T> CommonBean(CommonBeanT<T> o) {
        this(o.code, o.message);
    }

    public CommonBean(int code) {
        this(code, "");
    }

    public CommonBean(int code, String msg) {
        this.code = code;
        this.message = msg;
    }
}
