package simple.util.net.observable;

import simple.bean.CommonBean;

/**
 * 用于捕获服务器约定的错误类型
 */
public class ResultException extends RuntimeException {

    private CommonBean cb;

    public ResultException(int errCode) {
        this(errCode, "OK code= " + errCode);
    }

    public ResultException(int errCode, String msg) {
        this(new CommonBean(errCode, msg), msg);
    }

    public ResultException(CommonBean cb, String msg) {
        super(msg);
        this.cb = cb;
    }

    public int getErrCode() {
        return cb.code;
    }

    public CommonBean getCb() {
        return cb;
    }
}
