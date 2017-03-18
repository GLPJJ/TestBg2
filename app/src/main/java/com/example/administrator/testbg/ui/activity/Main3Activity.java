package com.example.administrator.testbg.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.example.administrator.testbg.R;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.schedulers.Timestamped;

//分页1
public class Main3Activity extends BaseMyActivity {

	static final String TAG = "Main3Activity";

	@BindView(R.id.btn)
	Button mButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main3);
		setSwipeBackEnable(false);
	}

	public void onGo(View v) {
		Observable.just("btn")
				.subscribeOn(Schedulers.newThread())//为每个任务创建一个新线程
				.doOnSubscribe(new Action0() {
					@Override
					public void call() {
						//线程紧跟在后面第一个subscribeOn 指定的线程
						Log.i(TAG, "第0步 : " + String.valueOf(Thread.currentThread() + ",初始化操作;"));
					}
				})
				.subscribeOn(Schedulers.computation()) //紧跟的第一个指定线程
				.subscribeOn(Schedulers.io())//用于IO密集型任务
				.observeOn(AndroidSchedulers.mainThread())
				.flatMap(new Func1<String, Observable<String>>() {
					@Override
					public Observable<String> call(String s) {
						String[] arra = new String[s.length()];
						for (int i = 0; i < s.length(); i++) {
							arra[i] = s.substring(i, i + 1);
						}
						Log.i(TAG, "第1步 : " + String.valueOf(Thread.currentThread() + "," + String.valueOf(arra)));
						return Observable.from(arra);
					}
				})
				//累积1秒的事件，然后一次性传值,
				//调用完毕后，指定等待2秒，生成新的累积buffer
//				.buffer(1, 10, TimeUnit.SECONDS) // window 类似 buffer window返回的是被观察者
//				.buffer(2)//每次发送两个值
//				.map(new Func1<List<String>, String>() {
//					@Override
//					public String call(List<String> strings) {
//						Log.i(TAG, "第2步 : " + String.valueOf(Thread.currentThread()) + "," + strings + " ;");
//						return strings.get(0);
//					}
//				})
//				.toSortedList(new Func2<List<String>, List<String>, Integer>() { //默认等待10个，指定排序，然后一起向下发射
//					@Override
//					public Integer call(List<String> strings, List<String> strings2) {
//						return null;
//					}
//				})
//				.throttleFirst(10, TimeUnit.SECONDS)
				.timestamp()//增加时间戳
				.take(1)//只取第一个。
//				.take(1000, TimeUnit.SECONDS)//take 指定时间内的子弹
				.map(new Func1<Timestamped<String>, String>() {
					@Override
					public String call(Timestamped<String> stringTimestamped) {
						Log.i(TAG, "第3步 : " + String.valueOf(Thread.currentThread()) + "," + stringTimestamped + " ;");
						return stringTimestamped.getValue();
					}
				})
				.throttleFirst(1000, TimeUnit.MILLISECONDS)
				.observeOn(Schedulers.trampoline())//当其它排队的任务完成后，在当前线程排队开始执行
				.filter(new Func1<String, Boolean>() {//过滤操作
					@Override
					public Boolean call(String s) {
						Log.i(TAG, "第3.1步 : " + String.valueOf(Thread.currentThread()) + "," + s + " ;");
//						return false;//表示这个链式不需要再继续下去了。
//						throw new IllegalArgumentException("end");
						return true;
					}
				})
				.observeOn(Schedulers.io())
				.map(new Func1<String, String>() {
					@Override
					public String call(String s) {
						Log.i(TAG, "第4步 : " + String.valueOf(Thread.currentThread()) + "," + s + " ;");
						return s;
					}
				})
				.observeOn(Schedulers.newThread())
				.map(new Func1<String, String>() {
					@Override
					public String call(String s) {
						Log.i(TAG, "第5步 : " + String.valueOf(Thread.currentThread()) + "," + s + " ;");
						return s;
					}
				})
				.observeOn(Schedulers.immediate())//默认指定线程方式
				.map(new Func1<String, String>() {
					@Override
					public String call(String s) {
						Log.i(TAG, "第6步 : " + String.valueOf(Thread.currentThread()) + "," + s + " ;");
						return s;
					}
				})
				.observeOn(AndroidSchedulers.mainThread())
				.map(new Func1<String, String>() {
					@Override
					public String call(String s) {
						Log.i(TAG, "第7步 : " + String.valueOf(Thread.currentThread()) + "," + s + " ;");
						return s;
					}
				})
				.subscribe(new Action1<String>() {
					@Override
					public void call(String s) {
						Log.i(TAG, "第8步 : " + String.valueOf(Thread.currentThread()) + "," + s + " next;");
					}
				}, new Action1<Throwable>() {
					@Override
					public void call(Throwable throwable) {
						Log.i(TAG, "第8步 : " + String.valueOf(Thread.currentThread()) + "," + String.valueOf(throwable) + ", exception;");
					}
				}, new Action0() {
					@Override
					public void call() {
						Log.i(TAG, "第8步 : " + String.valueOf(Thread.currentThread()) + "," + " complete;");
					}
				});

	}

	public void onGo1(View view) {
		startActivity(new Intent(this, Main4Activity.class));
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		Log.i(TAG, "dispatchTouchEvent" + "," + "Activity" + ",action=" + ev.getAction());

		//return true;//返回true，表示我们要处理事件
		return super.dispatchTouchEvent(ev);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		Log.i(TAG, "onTouchEvent" + "," + "Activity" + ",action=" + event.getAction());

//		return true;
		return super.onTouchEvent(event);
	}


}
