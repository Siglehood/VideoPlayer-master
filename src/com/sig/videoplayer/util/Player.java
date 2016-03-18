package com.sig.videoplayer.util;

import java.io.IOException;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * Player是核心类，其实现了“进度条更新“、“数据缓冲”、“SurfaceHolder生命周期”等功能，其中“
 * SurfaceHolder生命周期”是视频与音频播放的最大区别，通过surfaceCreated()、surfaceDestroyed()、
 * surfaceChanged()可以创建或释放某些资源
 * 
 * @author Sig
 * @version 1.0
 */
public class Player implements Callback, OnBufferingUpdateListener, OnPreparedListener, OnCompletionListener {
	/**
	 * 流视频播放器核心对象
	 */
	public MediaPlayer mMediaPlayer = null;

	/**
	 * 调试用标签
	 */
	private static final String TAG = Player.class.getSimpleName();
	/**
	 * 快进和快退的间隔时间
	 */
	private static final int INTERVAL = 10 * 1000;

	/**
	 * 视频管理器
	 */
	private SurfaceHolder mSurfaceHolder = null;
	/**
	 * 拖动条
	 */
	private SeekBar mSeekBar = null;
	/**
	 * 当前播放的时间位置
	 */
	private TextView mCurrentPosTv = null;
	/**
	 * 视频播放时长
	 */
	private TextView mDurationTv = null;

	/**
	 * 定时器
	 */
	private Timer mTimer = null;

	/**
	 * 视频宽度
	 */
	private int mVideoWidth = 0;
	/**
	 * 视频高度
	 */
	private int mVideoHeight = 0;

	public Player(SurfaceView surfaceView, SeekBar seekBar, TextView currentPosTv, TextView durationTv) {
		mSurfaceHolder = surfaceView.getHolder();// 从SurfaceView的getHolder()获取单例
		mSurfaceHolder.addCallback(this);// 添加回调
		mSeekBar = seekBar;
		mCurrentPosTv = currentPosTv;
		mDurationTv = durationTv;
		mTimer = new Timer();
		mTimer.schedule(mTimerTask, 0, 1000);// 设置定时器，每隔1s发送一个消息
	}

	/**
	 * 通过Timer和Handler来更新进度条
	 */
	private TimerTask mTimerTask = new TimerTask() {

		@Override
		public void run() {
			if (mMediaPlayer == null)
				return;
			if (mMediaPlayer.isPlaying() && !mSeekBar.isPressed())
				mHandler.sendEmptyMessage(0);
		}
	};

	/**
	 * 处理者负责处理消息
	 */
	private Handler mHandler = new Handler(new Handler.Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			int position = mMediaPlayer.getCurrentPosition();// 获取播放位置
			int duration = mMediaPlayer.getDuration();// 获取播放时长
			if (duration > 0)
				// progress/max = position/duration
				mSeekBar.setProgress(mSeekBar.getMax() * position / duration);
			return false;
		}
	});

	public void backward() {
		if (mMediaPlayer != null) {
			mMediaPlayer.seekTo(mMediaPlayer.getCurrentPosition() - INTERVAL);// MediaPlayer的seekTo()方法可定位
			mMediaPlayer.start();// 启动播放
		}
	}

	public void playUrl(String url) {
		if (mMediaPlayer != null) {
			mMediaPlayer.stop();// 停止播放
			mMediaPlayer.reset();// 视频加载前重置资源
		}
		try {
			mMediaPlayer.setDataSource(url);// 设置流视频源
			mMediaPlayer.prepareAsync();// 异步准备状态（因为是异步状态，有时需要摁两次加载按钮）
		} catch (IllegalArgumentException e) {
			Log.e(TAG, "非法参数异常", e);
		} catch (SecurityException e) {
			Log.e(TAG, "安全异常", e);
		} catch (IllegalStateException e) {
			Log.e(TAG, "非法状态异常", e);
		} catch (IOException e) {
			Log.e(TAG, "输入输出异常", e);
		}
	}

	public void pause() {
		if (mMediaPlayer != null)
			mMediaPlayer.pause();// 暂停播放
	}

	public void start() {
		if (mMediaPlayer != null)
			mMediaPlayer.start();
	}

	public void forward() {
		if (mMediaPlayer != null) {
			mMediaPlayer.seekTo(mMediaPlayer.getCurrentPosition() + INTERVAL);// MediaPlayer的seekTo()方法可定位
			mMediaPlayer.start();// 启动播放
		}
	}

	public void stop() {
		if (mMediaPlayer != null)
			mMediaPlayer.stop();// 停止播放
	}

	public void onStop() {
		if (mMediaPlayer != null) {
			mMediaPlayer.stop();// 停止播放
			mMediaPlayer.release();// 释放资源
			mMediaPlayer = null; // 使MediaPlayer对象指向null，以确保完全释放
		}
		if (mTimer != null)
			mTimer.cancel();// 取消定时器
	}

	/**
	 * 获取视频当前播放位置
	 * 
	 * @return
	 */
	public String getCurrentPosition() {
		int min = mMediaPlayer.getCurrentPosition() / 1000;
		return format(min / 60) + ":" + format(min % 60);
	}

	/**
	 * 获取视频播放时长
	 * 
	 * @return
	 */
	public String getDuration() {
		int sec = mMediaPlayer.getDuration() / 1000;
		return format(sec / 60) + ":" + format(sec % 60);
	}

	/**
	 * 格式化：两位数不足补零
	 * 
	 * @param num
	 * @return
	 */
	public String format(int num) {
		return String.format(Locale.getDefault(), "%02d", num);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		Log.d(TAG, "surfaceCreated()");
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		Log.d(TAG, "surfaceChanged()");
		mMediaPlayer = new MediaPlayer(); // 实例化MediaPlayer对象
		mMediaPlayer.setDisplay(mSurfaceHolder);// 设置SurfaceHolder对象
		mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);// 设置音频流类型
		mMediaPlayer.setOnPreparedListener(this);// 设置OnPreparedListener()监听器
		mMediaPlayer.setOnBufferingUpdateListener(this);// 设置OnBufferingUpdateListener()监听器
		mMediaPlayer.setOnCompletionListener(this);// 设置OnCompletionListener()监听器
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.d(TAG, "surfaceDestroyed()");
	}

	@Override
	public void onBufferingUpdate(MediaPlayer mp, int percent) {
		mSeekBar.setSecondaryProgress(percent);// 设置次进度条，即缓冲条
		if (mMediaPlayer == null)
			return;
		int currentProgress = mSeekBar.getMax() * mMediaPlayer.getCurrentPosition() / mMediaPlayer.getDuration();
		Log.d(TAG, "onBufferingUpdate() --> " + currentProgress + "% play --> " + percent + "% buffer");
		mCurrentPosTv.setText(getCurrentPosition());// 设置当前播放位置文本
	}

	@Override
	public void onPrepared(MediaPlayer mp) {// 有些视频格式不支持，不能播放时mVideoWidth=0，mVideoHeight=0，以此来判断是否能播放
		Log.d(TAG, "onPrepared()");
		mVideoWidth = mMediaPlayer.getVideoWidth();
		mVideoHeight = mMediaPlayer.getVideoHeight();
		if (mVideoHeight != 0 && mVideoWidth != 0) {
			mp.start();// 启动播放
			mDurationTv.setText(getDuration());// 设置播放时长文本
		}
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		Log.d(TAG, "onCompletion()");
		if (mMediaPlayer != null)
			mMediaPlayer.start();// 启动播放
	}
}
