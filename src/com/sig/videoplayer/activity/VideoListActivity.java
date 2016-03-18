package com.sig.videoplayer.activity;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONException;

import com.sig.videoplayer.R;
import com.sig.videoplayer.adapter.VideoAdapter;
import com.sig.videoplayer.bean.VideoBean;
import com.sig.videoplayer.util.Constant;
import com.sig.videoplayer.util.JsonParser;

import android.app.ListActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;

/**
 * 显示视频列表的Activity，直接继承ListActivity
 * 
 * @author Sig
 * @version 1.0
 */
public class VideoListActivity extends ListActivity {
	/**
	 * 调试用标签
	 */
	private static final String TAG = VideoListActivity.class.getSimpleName();

	/**
	 * 进度条刷新列表
	 */
	private ProgressBar mProgressBar = null;
	/**
	 * 视频List集合
	 */
	private ArrayList<VideoBean> mList = null;
	/**
	 * 自定义的视频列表适配器
	 */
	private VideoAdapter mVideoAdapter = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_video_list);
		mProgressBar = (ProgressBar) this.findViewById(R.id.progress_bar);
		mList = new ArrayList<>();
		mVideoAdapter = new VideoAdapter(this, mList);
		this.setListAdapter(mVideoAdapter);// 设置适配器
		new JsonDownloader().execute(Constant.REQUEST_URL);// 执行JSON下载器线程
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Intent intent = new Intent(this, MainActivity.class);// Intent可实现从当前Activity跳转到另一个Activity
		String videoUrl = Constant.BASE_URL + mList.get(position).getName();// 获取点击条目的视频地址
		intent.putExtra("video_name", videoUrl);// 跳转过程可传递参数
		intent.putExtra("video_list", mList);
		intent.putExtra("position", position);
		this.startActivity(intent);// 启动跳转
		super.onListItemClick(l, v, position, id);
	}

	/**
	 * Handler负责处理消息
	 */
	private Handler mHandler = new Handler(new Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			if (msg.what == 0) {
				mProgressBar.setVisibility(View.GONE); // 隐藏刷新进度条
				if (mList != null)
					mVideoAdapter.refresh(mList);// 刷新列表
			}
			return false;
		}
	});

	/**
	 * JsonDownloader继承异步任务，带泛型参数String，Integer，Void
	 * 
	 * @author Sig
	 * @version 1.0
	 */
	private class JsonDownloader extends AsyncTask<String, Integer, Void> {

		@Override
		protected Void doInBackground(String... params) {// 该方法执行线程数据，参数是可变长度数组
			try {
				mList = JsonParser.getList(VideoListActivity.this, params[0]);
				mHandler.sendEmptyMessage(0);// Handler发送消息
			} catch (IOException e) {
				Log.e(TAG, "输入输出异常", e);
			} catch (JSONException e) {
				Log.e(TAG, "JSON解析异常", e);
			}
			return null;
		}
	}
}
