package com.framgia.gallerytraining.models;


public class Album {

	private long idAlbum;
	private String nameAlbum;
	private String dateAlbum;
	private ListPictures listPicture;
	private int totalPicture;
	private int sizeAlbum;
	private String dataAlbum;
	private String srcAlbum;

	public long getIdAlbum() {
		return idAlbum;
	}

	public void setIdAlbum(long idAlbum) {
		this.idAlbum = idAlbum;
	}

	public String getNameAlbum() {
		return nameAlbum;
	}

	public void setNameAlbum(String nameAlbum) {
		this.nameAlbum = nameAlbum;
	}

	public String getDateAlbum() {
		return dateAlbum;
	}

	public void setDateAlbum(String dateAlbum) {
		this.dateAlbum = dateAlbum;
	}

	public ListPictures getListPicture() {
		return listPicture;
	}

	public void setListPicture(ListPictures listPicture) {
		this.listPicture = listPicture;
	}

	public int getTotalPicture() {
		return totalPicture;
	}

	public void setTotalPicture(int totalPicture) {
		this.totalPicture = totalPicture;
	}

	public int getSizeAlbum() {
		return sizeAlbum;
	}

	public void setSizeAlbum(int sizeAlbum) {
		this.sizeAlbum = sizeAlbum;
	}

	public String getDataAlbum() {
		return dataAlbum;
	}

	public void setDataAlbum(String dataAlbum) {
		this.dataAlbum = dataAlbum;
	}

	public String getSrcAlbum() {
		return srcAlbum;
	}

	public void setSrcAlbum(String srcAlbum) {
		this.srcAlbum = srcAlbum;
	}

}
