package com.sig.videoplayer.activity;

import java.util.ArrayList;

import com.sig.videoplayer.R;
import com.sig.videoplayer.bean.VideoBean;
import com.sig.videoplayer.util.Constant;
import com.sig.videoplayer.util.Player;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 播放流视频的Activity，实现OnTouchListener，OnClickListener接口
 * 
 * @author Sig
 * @version 1.0
 */
public class MainActivity extends Activity implements OnTouchListener, OnClickListener {
	/**
	 * 控制面板隐藏间隔时间
	 */
	private static final int INTERVAL = 5 * 1000;

	/**
	 * 视频画面容器
	 */
	private SurfaceView mSurfaceView = null;
	/**
	 * 拖动条
	 */
	private SeekBar mSeekBar = null;
	/**
	 * 当前播放的时间位置
	 */
	private TextView mCurrentPosTv = null;
	/**
	 * 视频播放的时长
	 */
	private TextView mDurationTv = null;
	/**
	 * 控制面板布局
	 */
	private LinearLayout mLinearLayout = null;
	/**
	 * 流视频加载按钮
	 */
	private Button mPlayBtn = null;
	/**
	 * 播放上一个流视频按钮
	 */
	private Button mLastBtn = null;
	/**
	 * 流视频快退按钮
	 */
	private Button mBackwardBtn = null;
	/**
	 * 流视频暂停按钮
	 */
	private Button mPauseBtn = null;
	/**
	 * 流视频继续按钮
	 */
	private Button mContinueBtn = null;
	/**
	 * 流视频快进按钮
	 */
	private Button mForwardBtn = null;
	/**
	 * 播放下一个流视频按钮
	 */
	private Button mNextBtn = null;
	/**
	 * 流视频停止按钮
	 */
	private Button mStopBtn = null;
	/**
	 * 流视频快退按钮
	 */
	private Player mPlayer = null;
	/**
	 * 视频名称
	 */
	private String videoName = null;
	/**
	 * 视频List集合
	 */
	private ArrayList<VideoBean> mList = null;

	/**
	 * 选中的条目
	 */
	private static int position = 0;
	/**
	 * 处理者
	 */
	private Handler mHandler = null;

	/**
	 * 控制面板可视标志
	 */
	private static boolean isVisibility = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);// 设置无标题，必须在setContentView()之前设置
		setContentView(R.layout.activity_main);
		initView();
		initData();
	}

	@Override
	protected void onStart() {
		super.onStart();
		mHandler.postDelayed(new Runnable() {// 延时500ms加载流视频

			@Override
			public void run() {
				mPlayer.playUrl(videoName);
			}
		}, 500);
		mHandler.postDelayed(mDelay, INTERVAL);// 延时5秒隐藏控制面板
	}

	@Override
	protected void onStop() {
		mPlayer.onStop();
		super.onStop();
	}

	/**
	 * 延时Runnable
	 */
	private Runnable mDelay = new Runnable() {

		@Override
		public void run() {
			if (!isVisibility)
				mLinearLayout.setVisibility(View.GONE);
		}
	};

	/**
	 * 初始化View
	 */
	private void initView() {
		mSurfaceView = (SurfaceView) this.findViewById(R.id.surface_view);
		mSeekBar = (SeekBar) this.findViewById(R.id.seek_bar);
		mCurrentPosTv = (TextView) this.findViewById(R.id.tv_current_position);
		mDurationTv = (TextView) this.findViewById(R.id.tv_duration);
		mLinearLayout = (LinearLayout) this.findViewById(R.id.linear_layout);
		mPlayBtn = (Button) this.findViewById(R.id.btn_play);
		mLastBtn = (Button) this.findViewById(R.id.btn_last);
		mBackwardBtn = (Button) this.findViewById(R.id.btn_backward);
		mPauseBtn = (Button) this.findViewById(R.id.btn_pause);
		mContinueBtn = (Button) this.findViewById(R.id.btn_continue);
		mForwardBtn = (Button) this.findViewById(R.id.btn_forward);
		mNextBtn = (Button) this.findViewById(R.id.btn_next);
		mStopBtn = (Button) this.findViewById(R.id.btn_stop);

	}

	/**
	 * 初始化数据
	 */
	@SuppressWarnings("unchecked")
	private void initData() {
		mSurfaceView.setOnTouchListener(this);
		mSeekBar.setOnSeekBarChangeListener(new SeekBarChangeListener());
		mPlayBtn.setOnClickListener(this);
		mLastBtn.setOnClickListener(this);
		mBackwardBtn.setOnClickListener(this);
		mPauseBtn.setOnClickListener(this);
		mContinueBtn.setOnClickListener(this);
		mForwardBtn.setOnClickListener(this);
		mNextBtn.setOnClickListener(this);
		mStopBtn.setOnClickListener(this);
		mPlayer = new Player(mSurfaceView, mSeekBar, mCurrentPosTv, mDurationTv);// 实例化Player
		videoName = this.getIntent().getStringExtra("video_name");// 接收Intent跳转传递过来的参数
		mList = (ArrayList<VideoBean>) this.getIntent().getSerializableExtra("video_list");
		position = this.getIntent().getIntExtra("position", 0);
		mHandler = new Handler();
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN) {// 捕获按下动作，可实现控制面板的显示or隐藏
			if (!isVisibility) {
				isVisibility = !isVisibility;
				mLinearLayout.setVisibility(View.GONE);
			} else {
				isVisibility = !isVisibility;
				mLinearLayout.setVisibility(View.VISIBLE);
			}
		}
		return false;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {// 捕获各种按钮动作
		case R.id.btn_play:
			mPlayer.playUrl(Constant.BASE_URL + mList.get(position).getName());
			mContinueBtn.setVisibility(View.GONE);
			mPauseBtn.setVisibility(View.VISIBLE);
			break;
		case R.id.btn_last:
			if (position < 1) {
				Toast.makeText(getApplicationContext(), R.string.toast_start, Toast.LENGTH_SHORT).show();
				return;
			}
			mPlayer.playUrl(Constant.BASE_URL + mList.get(--position).getName());
			mContinueBtn.setVisibility(View.GONE);
			mPauseBtn.setVisibility(View.VISIBLE);
			break;
		case R.id.btn_backward:
			mPlayer.backward();
			break;
		case R.id.btn_pause:
			mPlayer.pause();
			mContinueBtn.setVisibility(View.VISIBLE);
			mPauseBtn.setVisibility(View.GONE);
			break;
		case R.id.btn_continue:
			mPlayer.start();
			mContinueBtn.setVisibility(View.GONE);
			mPauseBtn.setVisibility(View.VISIBLE);
			break;
		case R.id.btn_forward:
			mPlayer.forward();
			break;
		case R.id.btn_next:
			if (position == mList.size() - 1) {
				Toast.makeText(getApplicationContext(), R.string.toast_end, Toast.LENGTH_SHORT).show();
				return;
			}
			mPlayer.playUrl(Constant.BASE_URL + mList.get(++position).getName());
			mContinueBtn.setVisibility(View.GONE);
			mPauseBtn.setVisibility(View.VISIBLE);
			break;
		case R.id.btn_stop:
			mPlayer.stop();
			this.finish();
		default:
			break;
		}
	}

	private class SeekBarChangeListener implements OnSeekBarChangeListener {
		int position = 0;

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			if (mPlayer.mMediaPlayer != null)
				position = progress * mPlayer.mMediaPlayer.getDuration() / seekBar.getMax();
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {

		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			mPlayer.mMediaPlayer.seekTo(position);
		}
	}
}
