package com.framgia.gallerytraining.models;

import java.util.ArrayList;

public class ListPictures {

	private ArrayList<Picture> listPictures;

	public ListPictures() {
		super();
		this.listPictures = new ArrayList<Picture>();
	}

	public ArrayList<Picture> getListPictures() {
		return listPictures;
	}

	public void setListPictures(ArrayList<Picture> listPictures) {
		this.listPictures = listPictures;
	}

}
