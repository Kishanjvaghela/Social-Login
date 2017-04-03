package com.social.google.downloader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;

/**
 * Created by CS02 on 11/8/2016.
 */
public class ImageDownloaderTask extends AsyncTask<Void, Void, Exception> {
  private String imageUrl;
  private String imageName;
  private String finalImagePath;
  private String dirPath;

  public ImageDownloaderTask(String imageUrl, String dirPath, String imageName) {
    this.imageUrl = imageUrl;
    this.imageName = imageName;
    this.dirPath = dirPath;
  }

  @Override
  protected Exception doInBackground(Void... params) {
    URL img_value = null;
    try {
      img_value = new URL(imageUrl);

      Bitmap mIcon1 = null;
      mIcon1 = BitmapFactory.decodeStream(img_value.openConnection().getInputStream());
      if (mIcon1 != null) {
        File dir = new File(dirPath);
        dir.mkdir();
        File imageFile = new File(dir, imageName + ".jpg");
        if (imageFile.exists()) imageFile.delete();
        finalImagePath = imageFile.getAbsolutePath();
        try {
          FileOutputStream out = new FileOutputStream(imageFile);
          mIcon1.compress(Bitmap.CompressFormat.JPEG, 100, out);
          out.flush();
          out.close();
          return null;
        } catch (Exception e) {
          e.printStackTrace();
          return e;
        }
      } else {
        return new ImageException("Image not found");
      }
    } catch (Exception e) {
      e.printStackTrace();
      return e;
    }
  }

  public String getFinalImagePath() {
    return finalImagePath;
  }
}