package simple.util.net.observable;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import simple.bean.CommonBean;
import simple.config.Simple;
import simple.config.SimpleCode;
import simple.util.net.NetWork;
import simple.util.til.AppDirUtil;
import simple.util.tools.LogUtil;

/**
 * Created by glp on 2016/7/1.
 * <p>
 * 使用规范说明：
 * <p>
 * 对于某一个请求，在特定的Activity上，如果这个操作是后台加载的（即不显示等待对话框），而我们的订阅者需要对这个Activity做一些界面操作，
 * 那么我们在订阅者方法里需要先判断这个Activity是否还活着。否则界面相关的代码会导致崩溃。for example:
 * <p>
 * <pre><code>
 * HttpApi.CreateBuilder()
 *  .methodGet(Url)
 *  .param(msp)
 *  .param("a", 1)
 *  .param("b", 2)
 *  .buildForResult(null)//这里我们没有加入等待对话框
 *  .map(new Func1<String, CommonBeanT<Object>>() {
 *      @Overridepublic
 *      CommonBeanT<List<GameBillEntity>> call(String s)
 *      {return ApiCode.GetCom(s,new TypeToken<CommonBeanT<Object>>{}.getType());})
 *  .subscribe(new Action1<CommonBeanT<Object>>()
 *      @Override
 *      public void call(CommonBeanT<Object> o) {
 *          //这里，需要判断Activity是否已经Finish。
 *      )
 *  </code></pre>
 * <p>
 * 同理也适用于Fragment
 */

public class HttpApi {

    public static final String Tag = "HttpApi";

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    //默认10秒超时
    static final int DEFAULT_TIMEOUT = 5;
    //http 缓存文件最大大小
    static final long DEFAULT_CACHE_FILESIZE = 100 * 1024 * 1024;//100MB

    static Cache sCache;

//    /**
//     * 替代参考使用 {@link Builder#buildForResult(Action0)}
//     *
//     * @param map
//     * @return
//     */
//    @Deprecated
//    public static Retrofit GetRetrofitIns(Map<String, String> map) {
    //手动创建一个OkHttpClient并设置超时时间
//        OkHttpClient client = new OkHttpClient.Builder()
//                .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
////                .writeTimeout(DEFAULT_TIMEOUT/2,TimeUnit.SECONDS)
////                .readTimeout(DEFAULT_TIMEOUT/2,TimeUnit.SECONDS)
//                .addNetworkInterceptor(new AuthInterceptor(SignStringRequest.getCommAuth(map)))
//                .build();
//
//        Retrofit retrofit = new Retrofit.Builder()
//                .client(client)
//                .baseUrl(Simple.Config.getHost())
//                .addConverterFactory(GsonConverterFactory.create())//转换Json
//                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())//返回被观察者
//                .build();
//        return retrofit;
//    }

    static Cache GetHttpCache() {
        if (sCache == null)
            sCache = new Cache(AppDirUtil.getTempCacheDirFile(), DEFAULT_CACHE_FILESIZE);
        return sCache;
    }

    /**
     * HTTP 模板
     *
     * @param builder Builder对象
     * @param action0 请求开始前，可以做一些操作，比如显示加载对话框
     * @return
     */
    private static Observable<String> GetNetObservable(@NonNull Builder builder, @Nullable Action0 action0) {

        if (builder == null)
            return Observable.just("");//返回空字符串

        /**
         * .doOnSubscribe 跟在最后面最近的一个subscribeOn指定的线程执行,不管前面指定多少个subscribeOn
         * ，和后面多少个subscribeOn，后面没指定，默认跟just一个线程
         */
        Observable<Builder> observable;
        if (action0 != null) {
            observable = Observable.just(builder)
                    .doOnSubscribe(action0)
                    .subscribeOn(AndroidSchedulers.mainThread());
        } else {
            observable = Observable.just(builder);
        }

        return observable
                .observeOn(Schedulers.io())
                .map(new Func1<Builder, String>() {
                    @Override
                    public String call(Builder builder) {
                        return GetNetResult(builder);
                    }
                });
    }

    /**
     * 同步获取网络请求结果
     *
     * @param builder
     * @return
     */
    private static String GetNetResult(Builder builder) {
        String retStr = "";

        //如果正在请求队列里面,阻止这个请求！！！
        String urlId = builder.toString();
        if (OkHttpCallMgr.GetInstance().isInCallList(urlId)) {
            LogUtil.w(Tag, "Too Frequently URL id = " + urlId);
            retStr = BuildResponseData(SimpleCode.CODE_FAIL_INLIST);
            return retStr;
        }
        //LogUtil.e("HttpAPi",urlId);

        //同步 OKHttp
        OkHttpClient client = new OkHttpClient.Builder()
                .cache(GetHttpCache())
                .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                //.writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                //.readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                //.authenticator()//服务器返回401,407可以使用这个
                //Application Interceptor
                .addInterceptor(new AuthInterceptor(Simple.Config.getHttpHeadAuth(builder.getParams())))
                //Network Interceptor
                //.addNetworkInterceptor()
                .build();

        String url;
        if (TextUtils.isEmpty(builder.getBaseUrl()))
            url = Simple.Config.getHost() + builder.getMethodUrl();
        else
            url = builder.getBaseUrl() + builder.getMethodUrl();

        CacheControl.Builder cacheBuilder = new CacheControl.Builder();
        if (builder.getCacheTime() == -1)
            cacheBuilder.noStore();//不缓存任何数据
        else
            cacheBuilder.maxAge(builder.getCacheTime(), TimeUnit.SECONDS);//缓存数据
        Request.Builder requestBuilder = new Request.Builder()
                .cacheControl(cacheBuilder.build());

        if (builder.getMethod() == Builder.METHOD_GET) {
            url += NetWork.getRequestParams(builder.getParams());
            requestBuilder.url(url);
        } else if (builder.getMethod() == Builder.METHOD_DELETE) {
            url += NetWork.getRequestParams(builder.getParams());
            requestBuilder.url(url);

            requestBuilder.delete();
        } else {
            requestBuilder.url(url);

            Set<String> stringSet = builder.getParams().keySet();
            FormBody.Builder fbBuilder = new FormBody.Builder();
            for (String name : stringSet) {
                fbBuilder.add(name, builder.getParams().get(name));
            }

            switch (builder.getMethod()) {
                case Builder.METHOD_POST:
                    requestBuilder.post(fbBuilder.build());
                    break;
                case Builder.METHOD_DELETE:
                    if (stringSet.isEmpty())
                        requestBuilder.delete();
                    else
                        requestBuilder.delete(fbBuilder.build());
                    break;
                case Builder.METHOD_PUT:
                    requestBuilder.put(fbBuilder.build());
                    break;
                case Builder.METHOD_PATCH:
                    requestBuilder.patch(fbBuilder.build());
                    break;
                case Builder.METHOD_HEAD:
                    requestBuilder.head();
            }
        }

        LogUtil.i(Tag, "URL=" + url + "  ;  Param=" + builder.getParams().toString());

        Request request = requestBuilder.build();
        Response response;

        try {

            Call call = client.newCall(request);
            //记录我们当前的Call
            OkHttpCallMgr.GetInstance().addCall(urlId, call);
            response = call.execute();

            if (call.isExecuted()) {
//                Headers responseHeaders = response.headers();
//                for (int i = 0; i < responseHeaders.size(); i++) {
//                    LogUtil.e("HttpApi", responseHeaders.name(i) + ":" + responseHeaders.value(i));
//                }
                //无网络，无缓存的时候，修改一下我们的code
                if (response.code() == HttpURLConnection.HTTP_GATEWAY_TIMEOUT ||
                        response.code() == HttpURLConnection.HTTP_NOT_FOUND) {
                    retStr = BuildResponseData(response.code());
                } else {
                    //如果希望是下载到文件
                    if (builder.file != null) {
                        if (response.code() == HttpURLConnection.HTTP_OK) {
                            //if (response.body().contentLength() > Integer.MAX_VALUE) {//大文件需要循环读取
                            byte[] bytes = new byte[1024];
                            InputStream is = response.body().byteStream();
                            FileOutputStream fos = null;

                            try {
                                fos = new FileOutputStream(builder.file);
                                if (!builder.file.exists() || builder.file.isDirectory())
                                    builder.file.createNewFile();

                                int readBytes = 0;
                                while (-1 != (readBytes = is.read(bytes))) {
                                    fos.write(bytes, 0, readBytes);
                                    fos.flush();
                                }

                                fos.close();
                                //data指定下载文件的地址
                                retStr = "{\"code\":" + SimpleCode.CODE_SUCCESS + ",\"message\":\"\",\"data\":\"" + builder.file.getPath() + "\"}";
                            } catch (Throwable e) {
                                e.printStackTrace();
                                if (fos != null)
                                    fos.close();

                                retStr = BuildResponseData(SimpleCode.CODE_NET_ERROR);
                            }

                            response.body().close();
                        } else {
                            retStr = BuildResponseData(SimpleCode.CODE_NET_ERROR);
                        }
                    } else {
                        retStr = response.body().string();
                        //关闭body流
                        response.body().close();

                        if (TextUtils.isEmpty(retStr)) {
                            LogUtil.e(Tag, "Empty Result Error");
                            retStr = BuildResponseData(SimpleCode.CODE_NET_ERROR);
                        } else if (TextUtils.equals(retStr, "404")) {//检查是否是404
                            LogUtil.e(Tag, "404 Error");
                            retStr = BuildResponseData(HttpURLConnection.HTTP_NOT_FOUND);
                        }

                        LogUtil.i(Tag, "URL=" + url + "  ;  ResponseBody=" + retStr);
                    }
                }
            } else if (call.isCanceled()) {
                LogUtil.e(Tag, "Cancel Request");
                retStr = BuildResponseData(SimpleCode.CODE_CANCEL);
            } else {
                LogUtil.e(Tag, "Error Request");
                retStr = BuildResponseData(SimpleCode.CODE_NET_ERROR);
            }
        } catch (ConnectException e) {
            retStr = BuildResponseData(HttpURLConnection.HTTP_GATEWAY_TIMEOUT);
        } catch (SocketTimeoutException e) {
            retStr = BuildResponseData(HttpURLConnection.HTTP_GATEWAY_TIMEOUT);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            //移除我们的Call
            OkHttpCallMgr.GetInstance().removeCall(urlId);
        }
        return retStr;
    }

    private static String BuildResponseData(int code) {
        return "{\"code\":" + code + ",\"message\":\"\",\"data\":\"{}\"}";
    }

    public static Builder CreateBuilder() {
        return CreateBuilder(null);
    }

    public static Builder CreateBuilder(@Nullable String baseUrl) {
        return new Builder(baseUrl);
    }

    /**
     * Http请求参数构建类
     */
    public static final class Builder {
        public static final int METHOD_GET = 0;
        public static final int METHOD_POST = 1;
        public static final int METHOD_DELETE = 2;
        public static final int METHOD_PUT = 3;
        public static final int METHOD_PATCH = 4;
        public static final int METHOD_HEAD = 5;

        String baseUrl;//基础访问地址，可以为空
        String methodUrl;//方法
        /**
         * @see Builder#METHOD_GET
         */
        int method = 0;//默认GET
        int cacheTime = -1;//请求缓存时间，单位秒，默认不缓存
        /**
         * 一旦指定了下载文件，那么我们获取到的数据将不是http请求下来的body数据了,而是下载是否成功的提示
         */
        File file = null;
        Map<String, String> params = new HashMap<>();//参数

        private Builder(@Nullable String baseUrl) {
            if (baseUrl == null)
                baseUrl = "";
            this.baseUrl = baseUrl;
        }

        public String getBaseUrl() {
            return baseUrl;
        }

        public String getMethodUrl() {
            return methodUrl;
        }

        public int getMethod() {
            return method;
        }

        public int getCacheTime() {
            return cacheTime;
        }

        public Map<String, String> getParams() {
            return params;
        }

        /**
         * Get 请求
         *
         * @param methodUrl
         * @return
         */
        public Builder methodGet(@NonNull String methodUrl) {
            return methodUrl(METHOD_GET, methodUrl);
        }

        /**
         * Post 请求
         *
         * @param methodUrl
         * @return
         */
        public Builder methodPost(@NonNull String methodUrl) {
            return methodUrl(METHOD_POST, methodUrl);
        }

        public Builder methodDelete(@NonNull String methodUrl) {
            return methodUrl(METHOD_DELETE, methodUrl);
        }

        public Builder methodPut(@NonNull String methodUrl) {
            return methodUrl(METHOD_PUT, methodUrl);
        }

        public Builder methodPatch(@NonNull String methodUrl) {
            return methodUrl(METHOD_PATCH, methodUrl);
        }

        public Builder methodHead(@NonNull String methodUrl) {
            return methodUrl(METHOD_HEAD, methodUrl);
        }

        /**
         * 指定请求方式，以及方法名
         *
         * @param method
         * @param methodUrl
         * @return
         */
        public Builder methodUrl(int method, @NonNull String methodUrl) {
            this.method = method;
            this.methodUrl = methodUrl;
            return this;
        }

        /**
         * 指定缓存时间
         *
         * @param cacheTime
         * @return
         */
        public Builder cacheTime(int cacheTime) {
            this.cacheTime = cacheTime;
            return this;
        }

        /**
         * 指定下载body到指定文件路径,支持大文件下载，
         *
         * @param path
         * @return
         */
        public Builder downloadFile(String path) {
            return downloadFile(new File(path));
        }

        /**
         * 指定下载body到指定文件路径,支持大文件下载，
         *
         * @param file
         * @return
         */
        public Builder downloadFile(File file) {
            this.file = file;
            return this;
        }

        /**
         * @param params 如果为null，则清空this.params，否则加入params
         * @return
         */
        public Builder param(@Nullable Map<String, String> params) {
            if (params == null)
                this.params.clear();
            else
                this.params.putAll(params);
            return this;
        }

        /**
         * @param key
         * @param value 如果value为null，则不会添加该参数
         * @return
         */
        public Builder param(@NonNull String key, @Nullable String value) {
            if (TextUtils.isEmpty(key) || TextUtils.isEmpty(value))
                return this;
            else {
                this.params.put(key, value);
                return this;
            }
        }

        public Builder param(@NonNull String key, int value) {
            return this.param(key, String.valueOf(value));
        }

        public Builder param(@NonNull String key, long value) {
            return this.param(key, String.valueOf(value));
        }

        public Builder param(@NonNull String key, double value) {
            return this.param(key, String.valueOf(value));
        }

        public Builder param(@Nullable String key, boolean value) {
            return this.param(key, value ? "1" : "0");
        }


        /**
         * 构建网络请求，异步返回 结果被观察者(只含有结果Code)
         *
         * @param action0
         * @return
         */
        public Observable<CommonBean> buildForCodeInMain(@Nullable Action0 action0) {
            return buildForCode(action0).observeOn(AndroidSchedulers.mainThread());
        }

        /**
         * 构建网络请求，异步返回 结果被观察者(只含有结果Code)
         *
         * @param action0
         * @return
         * @注意， 如需要在主线程返回结果使用 {@link this#buildForCodeInMain(Action0)}
         */
        public Observable<CommonBean> buildForCode(@Nullable Action0 action0) {
            return GetNetObservable(this, action0)
                    .map(new Func1<String, CommonBean>() {
                        @Override
                        public CommonBean call(String s) {
                            return Simple.Code.getCom(s);
                        }
                    });
        }

        /**
         * 构建网络请求，异步返回 结果被观察者
         *
         * @param action0
         * @return
         */
        public Observable<String> buildForResult(@Nullable Action0 action0) {
            return GetNetObservable(this, action0);
        }

        /**
         * 构建网络请求，同步返回 结果
         *
         * @return
         */
        public String buildForString() {
            return GetNetResult(this);
        }

        /**
         * 用于唯一标识我们的请求
         *
         * @return
         */
        @Override
        public String toString() {
            StringBuffer ret = new StringBuffer();
            ret.append(baseUrl)
                    .append("|")
                    .append(methodUrl)
                    .append("|")
                    .append(method)
                    .append("|")
                    .append(cacheTime)
                    .append("|");
            for (Map.Entry<String, String> entry : params.entrySet()) {
                ret.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }
            return ret.toString();
        }

        @Override
        public boolean equals(Object obj) {
            Builder build = (Builder) obj;
            if (build == null)
                return super.equals(obj);
            else {
                return obj == this || this.toString().equals(build.toString());
            }
        }
    }
}
