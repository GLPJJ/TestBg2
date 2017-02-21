package simple.bean;

import simple.config.SimpleCode;

/**
 * Created by glp on 2017/1/10.
 */

public class SynchronizeBean {
    public int code = SimpleCode.CODE_GO_END;
    private boolean finish = false;
    public Object data = null;

    public boolean isFinish() {
        synchronized (this) {
            return finish;
        }
    }

    public void setData(int code) {
        setData(code, null);
    }

    public void setData(int code, Object data) {
        synchronized (this) {
            this.finish = true;
            this.code = code;
            this.data = data;
        }
    }
}
