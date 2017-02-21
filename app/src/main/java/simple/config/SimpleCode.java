package simple.config;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.example.administrator.testbg.R;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.net.HttpURLConnection;

import rx.functions.Action1;
import simple.bean.CommonBean;
import simple.bean.CommonBeanT;
import simple.util.gson.GsonUtils;
import simple.util.net.observable.ResultException;
import simple.util.til.ToolUtil;
import simple.util.tools.LogUtil;

/**
 * Created by glp on 2017/2/14.
 */

public abstract class SimpleCode {
    public static final String Tag = "SimpleConfigCode";

    public final static int CODE_USER_DEFAULT = -6;//使用默认的文本提示
    public final static int CODE_GO_END = -5;//提供RxJava Code支持，跳过中间过程，直接结束,不需要打印信息
    //新增客户端本地ERROR CODE
    public final static int CODE_CANCEL = -4;//请求已经被取消
    public final static int CODE_JSON_PARSE_ERROR = -3;//客户端新增,服务器JSON解析失败
    public final static int CODE_FAIL_INLIST = -2;//请求已经再队列中 //客户端新增，请求点击过快
    public final static int CODE_NET_ERROR = -1;
    //GameStatusCode
    public final static int CODE_SUCCESS = 0;//
    //读取本地缓存数据库成功
    public final static int CODE_SUCCESS_FROMDB = 1;//有些请求如果会读取本地数据库，需要加这个判断

    //通用 rx java 错误处理
    public static Action1<Throwable> sErrorDeal = new Action1<Throwable>() {
        @Override
        public void call(Throwable throwable) {
            throwable.printStackTrace();
            Simple.Config.reportError("rx42 exception = " + ToolUtil.GetInfoFromThrowable(throwable));
        }
    };

    public static boolean IsCodeSucess(int code) {
        return IsCodeSucessNet(code) || code == CODE_SUCCESS_FROMDB;
    }

    public static boolean IsCodeInList(int code) {
        return code == CODE_FAIL_INLIST;
    }

    public static boolean IsCodeSucessNet(CommonBean o) {
        if (o == null)
            return false;
        return IsCodeSucessNet(o.code);
    }

    public static boolean IsCodeSucessNet(int code) {
        return code == CODE_SUCCESS;
    }

    public ResultException createException() {
        return createException(CODE_GO_END);
    }

    public ResultException createException(int code) {
        return createException(new CommonBean(code));
    }

    public <T> ResultException createException(CommonBeanT<T> o) {
        return createException(o, o.message);
    }

    public <T> ResultException createException(CommonBeanT<T> o, String def) {
        return createException(new CommonBean(o.code, switchCode(o, def)), def);
    }

    public ResultException createException(CommonBean o) {
        return createException(o, o.message);
    }

    public ResultException createException(CommonBean o, String def) {
        return new ResultException(o, switchCode(o, def));
    }

    /**
     * 这里返回的结果保证是非null的，但是data可能是null数据，处理的时候需要判断data是否为空
     */
    public abstract <T> CommonBeanT<T> getCom(String s, Type type);

    public abstract CommonBean getCom(String s);

    public <T> CommonBeanT<T> getComCode(String s) {
        CommonBeanT<T> data = new CommonBeanT<>();
        CommonBean ret = getCom(s);
        data.code = ret.code;
        data.message = ret.message;
        return data;
    }

    /**
     * 不能用于检查Http的回调
     *
     * @param s
     * @param type
     * @param <T>
     * @return
     */
    @Nullable
    public static <T> T ParseJson(String s, Type type) {
        try {
            T ret = GsonUtils.getGson().fromJson(s, type);
            return ret;
        } catch (Throwable e) {

            String error = "ParseJson : string=" + s + "\ntype="
                    + type.toString() + "\nexception=" + ToolUtil.GetInfoFromThrowable(e);
            LogUtil.e(Tag, error);
            //记录Json解析错误
            Simple.Config.reportError(error);
            return null;
        }
    }

    /**
     * @param code
     * @param def
     * @return
     * @see #switchCode(CommonBeanT, String)
     */
    @Deprecated
    public String switchCode(int code, String def) {
        CommonBeanT<Object> o = new CommonBeanT<>();
        o.code = code;
        return switchCode(o, def);
    }

    public String switchCode(CommonBean o, String def) {
        CommonBeanT<Object> n = new CommonBeanT<>();
        n.code = o.code;
        n.message = o.message;
        return switchCode(n, def);
    }

    public <T> String switchCode(CommonBeanT<T> o, String def) {
        String temp;
        if (TextUtils.isEmpty(def))
            temp = ToolUtil.GetString(R.string.request_error);
        else {
            try {
                JSONObject jsonObject = new JSONObject(def);
                temp = jsonObject.optString("message");
            } catch (Throwable e) {
                temp = def;
            }
        }

        String ret = temp;
        switch (o.code) {
            case CODE_SUCCESS:
                ret = ToolUtil.GetString(R.string.deal_success);
                break;
            case CODE_JSON_PARSE_ERROR://客户端本地判断
                ret = ToolUtil.GetString(R.string.error_json_parse);
                break;
            case CODE_USER_DEFAULT://使用默认
                if (TextUtils.isEmpty(def))//如果默认是空，那么使用message
                    ret = o.message;
                break;
            case CODE_GO_END://不需要提示
            case CODE_CANCEL:
                ret = null;
                break;
            case CODE_FAIL_INLIST://客户端本地判断，请求点击过快
                ret = ToolUtil.GetString(R.string.quick_tip);
                break;
            case CODE_NET_ERROR:
                ret = ToolUtil.GetString(R.string.request_error);
                break;
            case HttpURLConnection.HTTP_NOT_FOUND:
                ret = ToolUtil.GetString(R.string.error_404);
                break;
            case HttpURLConnection.HTTP_GATEWAY_TIMEOUT:
                ret = ToolUtil.GetString(R.string.request_nonet_nocache);
                break;
            default:
                return switchInChild(new CommonBean(o.code, o.message), ret);
        }
        return ret;
    }

    protected abstract String switchInChild(CommonBean o, String def);
}
