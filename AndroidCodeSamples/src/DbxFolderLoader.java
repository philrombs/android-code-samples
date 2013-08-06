


import java.io.*;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.DropBoxManager;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;
import android.widget.Toast;
import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.exception.DropboxUnlinkedException;


public class DbxFolderLoader extends AsyncTaskLoader<ArrayList<FileInfo>>{

	private String refpath;

	private String mDbxPath = null;
	private ArrayList<FileInfo> mDbxInfosArray;
	private ArrayList<FileInfo> mCachedContents;
	private int id;
	private String newFolderName;
	private String mSort;

    private DropboxAPI<?> mApi;
    Context mContext;

	public DbxFolderLoader(Context context, DropboxAPI<?> api, String dbxPath, String sort) {
        super(context);

        mContext = context.getApplicationContext();
        mApi = api;
        mDbxPath = dbxPath;
        mSort = sort;

        //Log.i("DbxFolderLoader:constructor", "path: "+mDbxPath.toString());
    }
	
	

/************************************************//**/
/******** Implement the Loader Behaviors ********//**/
/************************************************//**/

    //onStartLoading, onForceLoad, onStopLoading, onReset
    
    
    //Automatically called when the fragment/activity is being started.
    //Brings in Cached Contents if available.
    @SuppressLint("NewApi")
	protected void onStartLoading(){
        //Log.i("Dropbox", "onStartLoading");

    	if (mCachedContents != null) {
            deliverResult(mCachedContents);
        }
        if (takeContentChanged() || mCachedContents == null) {
            forceLoad();
        }

        forceLoad();
    }
    
    
    //Forces an asynchronous load.
    @SuppressLint("NewApi")
	@Override
    protected void onForceLoad() {
        super.onForceLoad();
    }
    
    
    //Automatically called when the fragment/activity is being stopped.
    @SuppressLint("NewApi")
	@Override
    protected void onStopLoading() {
        cancelLoad();
    }
    
    
    //Automatically called when destroying the Loader.
    @Override
    protected void onReset() {
        onStopLoading();
        mCachedContents = null;
    }
    
 

/********************************************/
/*********** ASyncTask functions ************/
/********************************************/

    
    
	//Main worker.  Delivers result to deliverResult(ArrayList<FileInfo>)
	@Override
	public ArrayList<FileInfo> loadInBackground() {
		
		//mDbxFs = getDbxFileSystem();

        ArrayList<FileInfo> dbxInfosArray = new ArrayList<FileInfo>();

        try {
            Entry dirEntry = mApi.metadata(mDbxPath, 1000, null, true, null);

            for(Entry ent: dirEntry.contents){

                File thumbnailFolder = new File(mContext.getExternalCacheDir().getAbsolutePath(), "thumbnails");

                if(!thumbnailFolder.exists()){
                    //Log.i("FolderLoader", "creating thumbnail folder");
                    thumbnailFolder.mkdir();


                }
                File thumbFile = new File(thumbnailFolder, ent.fileName());

                if(ent.thumbExists && !thumbFile.exists()){

                    //Log.i("DbxFolderFolder", "Downloading Image to: " + thumbFile.getAbsolutePath());
                    FileOutputStream mFos = new FileOutputStream(thumbFile.getAbsolutePath());
                    mApi.getThumbnail(ent.path, mFos, DropboxAPI.ThumbSize.ICON_64x64,
                            DropboxAPI.ThumbFormat.JPEG, null);


                    final int THUMBNAIL_SIZE = 64;

                    //Log.i("ImageAdapter:getMediaThumbnail", " Thumbnail Paths: " + thumbFile.getAbsolutePath());

                    FileInputStream fis = new FileInputStream(thumbFile);
                    Bitmap imageBitmap = BitmapFactory.decodeStream(fis);
                    imageBitmap = Bitmap.createScaledBitmap(imageBitmap, THUMBNAIL_SIZE, THUMBNAIL_SIZE, false);
                    FileInfo info = new FileInfo(ent.fileName(), ent.bytes, ent.isDir, ent.path, ent, imageBitmap, ent.modified);
                    dbxInfosArray.add(info);
                } else if(ent.thumbExists && thumbFile.exists()){

                    final int THUMBNAIL_SIZE = 64;
                    Log.i("DbxFolderFolder", "Cresting Image from: " + thumbFile.getAbsolutePath());

                    Bitmap bp = BitmapFactory.decodeFile(thumbFile.getAbsolutePath());

                    FileInfo info = new FileInfo(ent.fileName(), ent.bytes, ent.isDir, ent.path, ent, bp, ent.modified);
                    dbxInfosArray.add(info);

                } else{

                    //Log.i("DbxFolderFolder", "Using regular icons");
                    Bitmap imageBitmap = null;
                    FileInfo info = new FileInfo(ent.fileName(), ent.bytes, ent.isDir, ent.path, ent, imageBitmap, ent.modified);
                    dbxInfosArray.add(info);
                }

            }
        } catch (DropboxUnlinkedException e){
            //Log.i("DBXLoader", "Hitting Unlinked Exception");
            //e.printStackTrace();
            return null;
        } catch (DropboxException e) {
            //e.printStackTrace();
        } catch (FileNotFoundException e) {
            //e.printStackTrace();
        }

        if(mSort != null){
        	//Log.i("DbxFolderLoader:loadInBackground", "Inside mSort != null");
        	
            if(mSort.equalsIgnoreCase("SNA")) {
                dbxInfosArray = FileComparator.sortNameAscending(dbxInfosArray);
            } else if (mSort.equalsIgnoreCase("SND")) {
                dbxInfosArray = FileComparator.sortNameDescending(dbxInfosArray);
            } else if (mSort.equalsIgnoreCase("SSD")) {
                dbxInfosArray = FileComparator.sortSizeDescending(dbxInfosArray);
            } else if (mSort.equalsIgnoreCase("SSA")) {
                dbxInfosArray = FileComparator.sortSizeAscending(dbxInfosArray);
            } else if (mSort.equalsIgnoreCase("SDLM")) {
                dbxInfosArray = FileComparator.sortDateLastModified(dbxInfosArray);
            } else if (mSort.equalsIgnoreCase("SDFM")) {
                dbxInfosArray = FileComparator.sortDateFirstModified(dbxInfosArray);
            }
        }
        return dbxInfosArray;
	}
	
	

/****************************************************/
/** Deliver the Results to the Registered Listener **/
/****************************************************/

	
	
	//Delivers Result back to UI thread.
	@SuppressLint("NewApi")
	public void deliverResult(ArrayList<FileInfo> dbxInfosArray) {

        //Log.i("Dropbox", "deliverResult");
		if (isReset()) {
            // An async result came back after the loader is stopped
			releaseResources(dbxInfosArray);
            return;
        }

        mCachedContents = dbxInfosArray;

        if (isStarted()) {
            super.deliverResult(mCachedContents);
        }
	}

/********************************************/
/***** Various other Loader Functions *******/
/********************************************/

	//Need to release the Resources of the Loader
	private void releaseResources(ArrayList<FileInfo> dbxInfosArray){

        Log.i("Dropbox", "releaseResources");
        dbxInfosArray = null;
		
	}

}
