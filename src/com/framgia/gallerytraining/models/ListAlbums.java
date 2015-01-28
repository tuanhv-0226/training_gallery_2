package com.framgia.gallerytraining.models;

import java.util.ArrayList;

public class ListAlbums {

	private ArrayList<Album> listAlbums;

	public ListAlbums() {
		super();
		this.listAlbums = new ArrayList<Album>();
	}

	public ArrayList<Album> getListAlbums() {
		return listAlbums;
	}

	public void setListAlbums(ArrayList<Album> listAlbums) {
		this.listAlbums = listAlbums;
	}

	public void addAlbum(Album album) {
		this.listAlbums.add(album);
	}

}
