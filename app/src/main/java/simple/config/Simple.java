package simple.config;

import android.content.Context;

/**
 * Created by glp on 2017/2/14.
 * <p>
 * <p>
 * Java 类型	符号
 * boolean	    Z
 * byte	        B
 * char	        C
 * short	    S
 * int	        I
 * long	        L
 * float	    F
 * double	    D
 * void	        V
 * objects对象	Lfully-qualified-class-name;L类名    Ljava/lang/String;
 * Arrays数组	[array-type [数组类型               [I
 * methods方法	(argument-types)return-type(参数类型)返回类型    ()V - 无参数，返回Void
 */

public class Simple {
    //参数配置
    public static SimpleConfig Config = null;

    //配置错误码获取
    public static SimpleCode Code = null;

    public static Context Context = null;

    //当前CPU数量*2+1
    public static int sMaxThread = 1;
    /**
     * aes密钥
     */
    public static final String APP_SECRET_KEY = "a0c4c3h4r8f6ghijklm3o1qysauvwzyz";

    public static void InitSimpleModule(SimpleConfig config, SimpleCode code, Context context) {

        if (config == null || code == null || context == null)
            throw new IllegalArgumentException("InitSimpleModule with null arguments");

        Config = config;
        Code = code;
        Context = context.getApplicationContext();
        sMaxThread = Runtime.getRuntime().availableProcessors() * 2 + 1;
    }
}
