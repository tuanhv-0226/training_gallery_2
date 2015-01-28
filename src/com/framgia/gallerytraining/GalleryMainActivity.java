package com.framgia.gallerytraining;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.framgia.gallerytraining.adapters.AlbumAdapter;
import com.framgia.gallerytraining.controllers.ControllerGallery;
import com.framgia.gallerytraining.models.ListAlbums;

public class GalleryMainActivity extends ActionBarActivity {

	private ListView lvAlbums;
	private ListAlbums listAlbums;
	private ControllerGallery ctrGallery;
	private AlbumAdapter albumAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gallery_main);

		lvAlbums = (ListView) findViewById(R.id.fragment_gallery_listAlbums);
		ctrGallery = new ControllerGallery(this);
		fetchListAlbum();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.gallery_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void fetchListAlbum() {
		//Take list album in Device
		listAlbums = ctrGallery.fetchListAlbums();
		if (listAlbums != null) {
			showListAlbum(listAlbums);
		}
	}

	public void showListAlbum(ListAlbums lsAlbums) {
		//Show list album by ListView
		if (lsAlbums.getListAlbums().size() > 0) {
			if (albumAdapter == null) {
				albumAdapter = new AlbumAdapter(getBaseContext());
			} else {
				albumAdapter.notifyDataSetChanged();
			}
			albumAdapter.setData(lsAlbums.getListAlbums());
			lvAlbums.setAdapter(albumAdapter);
		}
	}
}
