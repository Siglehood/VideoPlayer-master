package com.sig.videoplayer.util;

/**
 * 常量
 * 
 * @author Sig
 * @version 1.0
 */
public interface Constant {
	/**
	 * 服务器IP地址
	 */
	public static final String SERVER_IP = "192.168.1.143";
	/**
	 * 服务器端口号
	 */
	public static final String SERVER_PORT = "8080";
	/**
	 * HTTP头协议
	 */
	public static final String SCHEMA = "http://";
	/**
	 * 拼接基本路径
	 */
	public static final String BASE_URL = SCHEMA + SERVER_IP + ":" + SERVER_PORT + "/VODServer/videos/";
	/**
	 * 播放列表路径
	 */
	public static final String REQUEST_URL = BASE_URL + "video_list.json";
}
