package com.google.hackathon.museum.model;

import com.google.gson.annotations.SerializedName;

public class Art {

	private String id;
	private String minor;
	private Region region;
	
	@SerializedName("image")
	private String imagePath;
	private String name;
	
	public Art() {
		
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Art(String id, String name, String minor, Region region,
			String imagePath) {
		super();
		this.id = id;
		this.name = name;
		this.minor = minor;
		this.region = region;
		this.imagePath = imagePath;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMinor() {
		return minor;
	}

	public void setMinor(String minor) {
		this.minor = minor;
	}

	public Region getRegion() {
		return region;
	}

	public void setRegion(Region region) {
		this.region = region;
	}

}
