package com.sig.videoplayer.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sig.videoplayer.bean.VideoBean;

import android.content.Context;
import android.util.Log;

/**
 * JSON解析器
 * 
 * @author Sig
 * @version 1.0
 */
public class JsonParser {
	/**
	 * 调试用标签
	 */
	private static final String TAG = JsonParser.class.getSimpleName();

	/**
	 * 获取网络中的JSON数据
	 * 
	 * @param path
	 * @return
	 * @throws IOException
	 * @throws JSONException
	 */
	public static ArrayList<VideoBean> getList(Context context, String path) throws IOException, JSONException {
		ArrayList<VideoBean> list = null;
		// URL url = new URL(path);
		// HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		// conn.setConnectTimeout(15 * 1000);
		// conn.setRequestMethod("GET");
		// if (conn.getResponseCode() == 200) {
		// InputStream is = conn.getInputStream();
		list = new ArrayList<VideoBean>();
		// 获取工程"assets"目录下指定文件输入流
		InputStream is = context.getAssets().open("video_list.json");
		byte[] data = readStream(is);
		String json = new String(data);// String构造器，将字节数组作为参数转化
		Log.d(TAG, json); // 打印调试信息
		JSONObject jsonObject = new JSONObject(json); // 封装JSON对象
		JSONArray jsonArray = jsonObject.getJSONArray("videoList"); // 根据Key值获取JSON数组
		for (int i = 0, len = jsonArray.length(); i < len; i++) {// 遍历数组
			VideoBean videoBean = new VideoBean();
			JSONObject item = jsonArray.getJSONObject(i);// 数组的每一个元素也是JSON对象
			int id = item.getInt("id");
			videoBean.setId(id);
			String videoName = item.getString("videoName");
			videoBean.setName(videoName);
			list.add(videoBean);// 将每一个实体存放进List集合
			// }
		}
		return list;
	}

	/**
	 * 将输入流转换成字节数组
	 * 
	 * @param inputStream
	 * @return
	 */
	public static byte[] readStream(InputStream inputStream) {
		ByteArrayOutputStream bout = null;// 流视频是字节流传输
		try {
			bout = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int len = 0;
			while ((len = inputStream.read(buffer)) != -1) {// -1表示流结束
				bout.write(buffer, 0, len);
			}
		} catch (IOException e) {
			Log.e(TAG, "输入输出异常", e);
		} finally {
			try {
				if (bout != null)
					bout.close();
				if (inputStream != null)
					inputStream.close();
			} catch (IOException e) {
				Log.e(TAG, "输入输出异常", e);
			}
		}
		return bout.toByteArray(); // 返回字节数组
	}

}
