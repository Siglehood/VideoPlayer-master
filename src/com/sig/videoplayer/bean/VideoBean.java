package com.sig.videoplayer.bean;

import java.io.Serializable;

/**
 * 封装视频列表的实体类
 * 
 * @author Sig
 * @version 1.0
 */
public class VideoBean implements Serializable {
	private static final long serialVersionUID = -4977383178402043178L;
	private int id = 0;
	private String name = null;

	public VideoBean() {

	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "VideoBean [id=" + id + ", name=" + name + "]";
	}
}
