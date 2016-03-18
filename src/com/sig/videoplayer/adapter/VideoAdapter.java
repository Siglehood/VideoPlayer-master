package com.sig.videoplayer.adapter;

import java.util.List;

import com.sig.videoplayer.R;
import com.sig.videoplayer.bean.VideoBean;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * 视频列表适配器类
 * 
 * @author Sig
 * @version 1.0
 */
public class VideoAdapter extends BaseAdapter {
	/**
	 * 布局映射器
	 */
	private LayoutInflater mLayoutInflater = null;
	/**
	 * 视频List集合
	 */
	private List<VideoBean> mList = null;

	/**
	 * 带两个参数的构造器
	 * 
	 * @param context
	 * @param list
	 */
	public VideoAdapter(Context context, List<VideoBean> list) {
		mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);// 根据系统服务获取单例
		mList = list;
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (convertView == null) {// 若缓冲View为null，则用LayoutInflater的inflate()方法映射布局
			convertView = mLayoutInflater.inflate(R.layout.list_item, parent, false);
			viewHolder = new ViewHolder();
			viewHolder.videoId = (TextView) convertView.findViewById(R.id.video_id);
			viewHolder.videoName = (TextView) convertView.findViewById(R.id.video_name);
			convertView.setTag(viewHolder); // 给缓冲View设置viewHolder标签
		} else
			viewHolder = (ViewHolder) convertView.getTag();// 若缓冲View不为null，则直接取出标签
		viewHolder.videoId.setText(mList.get(position).getId() + "、");// 设置视频编号文本
		viewHolder.videoName.setText(mList.get(position).getName());// 设置视频名称文本
		return convertView;
	}

	/**
	 * 刷新视频列表
	 * 
	 * @param list
	 */
	public void refresh(List<VideoBean> list) {
		mList.clear();
		mList.addAll(list);
		this.notifyDataSetChanged();
	}

	/**
	 * 利用ViewHolder类能高效刷新列表
	 * 
	 * @author Sig
	 * @version 1.0
	 *
	 */
	private static class ViewHolder {
		/**
		 * 视频编号
		 */
		private TextView videoId = null;
		/**
		 * 视频名称
		 */
		private TextView videoName = null;
	}

}
