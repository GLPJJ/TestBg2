package com.example.administrator.testbg;

import android.content.Context;
import android.support.multidex.MultiDexApplication;

import java.lang.reflect.Type;
import java.util.Map;

import simple.bean.CommonBean;
import simple.bean.CommonBeanT;
import simple.config.Simple;
import simple.config.SimpleCode;
import simple.config.SimpleConfig;

/**
 * Created by glp on 2017/2/17.
 */

public class App extends MultiDexApplication {

	@Override
	public void onCreate() {
		super.onCreate();

		Simple.InitSimpleModule(
				new SimpleConfig() {
					@Override
					public String getHost() {
						return null;
					}

					@Override
					public String getWBSocket() {
						return null;
					}

					@Override
					public String getToken() {
						return null;
					}

					@Override
					public String getCCode() {
						return null;
					}

					@Override
					public String getUser() {
						return null;
					}

					@Override
					public String getAndroidId() {
						return null;
					}

					@Override
					public boolean isTextNeedFilter() {
						return false;
					}

					@Override
					public boolean isLogOpen() {
						return true;
					}

					@Override
					public void goWebview(Context context, String title, String url) {

					}

					@Override
					public void reportError(String error) {

					}

					@Override
					public Map<String, String> getHttpHeadAuth(Map<String, String> params) {
						return null;
					}
				}, new SimpleCode() {
					@Override
					public <T> CommonBeanT<T> getCom(String s, Type type) {
						return null;
					}

					@Override
					public CommonBean getCom(String s) {
						return null;
					}

					@Override
					protected String switchInChild(CommonBean o, String def) {
						return null;
					}
				}, this);
	}
}
