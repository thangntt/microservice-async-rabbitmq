package com.thangntt.microservice.object.message;

import java.io.Serializable;

public class DataQueueRequest implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private int id;
	private String data;
	
	public DataQueueRequest(int id, String data) {
		this.id = id;
		this.data = data;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	
	
}
