package com.kkwinter.floatbar;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.MediaScannerConnection;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.widget.Toast;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.nio.ByteBuffer;

/**
 * Created by jiantao.tu on 2018/9/20.
 */
public class ShotTer {

    private final SoftReference<Context> mRefContext;
    private ImageReader mImageReader;

    private MediaProjection mMediaProjection;
    private VirtualDisplay mVirtualDisplay;

    private String mLocalUrl = "";

    private OnShotListener mOnShotListener;


    public ShotTer(Context context, int reqCode, Intent data) {
        this.mRefContext = new SoftReference<>(context);

        mMediaProjection = getMediaProjectionManager().getMediaProjection(reqCode, data);

        mImageReader = ImageReader.newInstance(
            getScreenWidth(),
            getScreenHeight(),
            PixelFormat.RGBA_8888,//此处必须和下面 buffer处理一致的格式 ，RGB_565在一些机器上出现兼容问题。
            1);
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void virtualDisplay() {

        mVirtualDisplay = mMediaProjection.createVirtualDisplay("screen-mirror",
            getScreenWidth(),
            getScreenHeight(),
            Resources.getSystem().getDisplayMetrics().densityDpi,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
            mImageReader.getSurface(), null, null);

    }


    public void startScreenShot(OnShotListener onShotListener, String loc_url) {
        mLocalUrl = loc_url;
        startScreenShot(onShotListener);
    }


    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void startScreenShot(OnShotListener onShotListener) {

        mOnShotListener = onShotListener;

        virtualDisplay();

        App.HANDLER.postDelayed(new Runnable() {
            @Override
            public void run() {
                Image image = mImageReader.acquireLatestImage();
                new SaveTask(mLocalUrl, mVirtualDisplay, mOnShotListener).executeOnExecutor(
                    AsyncTask.THREAD_POOL_EXECUTOR, image);
                //                    AsyncTaskCompat.executeParallel(new SaveTask(), image);
            }
        }, 400);
        //这里delay 时间过短容易导致 系统权限弹窗的阴影还没消失就完成了截图。 @see<a href="https://github.com/weizongwei5/AndroidScreenShot_SysApi/issues/4">issues</a>

    }


    public static class SaveTask extends AsyncTask<Image, Void, File> {

        private String mLocalUrl;

        private VirtualDisplay mVirtualDisplay;

        private OnShotListener mOnShotListener;
        private MediaScannerConnection msc;

        SaveTask(String localUrl, VirtualDisplay virtualDisplay, OnShotListener onShotListener) {
            mLocalUrl = localUrl;
            mVirtualDisplay = virtualDisplay;
            mOnShotListener = onShotListener;
        }


        @TargetApi(Build.VERSION_CODES.KITKAT)
        @Override
        protected File doInBackground(Image... params) {

            if (params == null || params.length < 1 || params[0] == null) {

                return null;
            }

            Image image = params[0];

            int width = image.getWidth();
            int height = image.getHeight();
            final Image.Plane[] planes = image.getPlanes();
            final ByteBuffer buffer = planes[0].getBuffer();
            //每个像素的间距
            int pixelStride = planes[0].getPixelStride();
            //总的间距
            int rowStride = planes[0].getRowStride();
            int rowPadding = rowStride - pixelStride * width;
            Bitmap bitmap = Bitmap.createBitmap(width + rowPadding / pixelStride, height,
                Bitmap.Config.ARGB_8888);//虽然这个色彩比较费内存但是 兼容性更好
            bitmap.copyPixelsFromBuffer(buffer);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height);
            image.close();
            File fileImage = null;
            if (bitmap != null) {
                try {
                    mLocalUrl = Utils.getSDPath() + "/Pictures/Screenshots";
                    YeLog.i("ShotTer:"+ mLocalUrl);
                    String fileName = System.currentTimeMillis() + ".png";
                    mLocalUrl += "/" + fileName;
                    fileImage = new File(mLocalUrl);
                    File parentFile = fileImage.getParentFile();
                    if (!parentFile.exists()) {
                        if (!parentFile.mkdirs()) {
                            Toast.makeText(App.getApp(), "screenshot failed.", Toast.LENGTH_SHORT)
                                .show();
                            return null;
                        }
                    }

                    if (!fileImage.exists()) {
                        if (!fileImage.createNewFile()) {
                            Toast.makeText(App.getApp(), "screenshot failed.", Toast.LENGTH_SHORT)
                                .show();
                            return null;
                        }
                    }
                    FileOutputStream out = new FileOutputStream(fileImage);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
                    out.flush();
                    out.close();
                    // 最后通知图库更新
                    // Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    // Uri uri = Uri.fromFile(fileImage);
                    // intent.setData(uri);
                    // App.getApp().sendBroadcast(intent);
                    // 其次把文件插入到系统图库
                    String absolutePath = MediaStore.Images.Media.insertImage(
                        App.getApp().getContentResolver(),
                        fileImage.getAbsolutePath(), fileName, null);
                    msc = new MediaScannerConnection(App.getApp(), new ScannerClient(absolutePath));
                    msc.connect();
                } catch (IOException e) {
                    e.printStackTrace();
                    fileImage = null;
                }
            }
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
            }
            if (fileImage != null) {
                return fileImage;
            }
            return null;
        }

        @TargetApi(Build.VERSION_CODES.KITKAT)
        @Override
        protected void onPostExecute(File file) {
            super.onPostExecute(file);
            if (mVirtualDisplay != null) {
                mVirtualDisplay.release();
            }

            if (mOnShotListener != null) {
                mOnShotListener.onFinish(file);
            }

        }
        class ScannerClient implements MediaScannerConnection.MediaScannerConnectionClient {
            private static final String TAG = "ScannerClient";
            private String absolutePath;
            ScannerClient(String absolutePath) {
                this.absolutePath = absolutePath;
            }

            public void onMediaScannerConnected() {
                if (msc != null && msc.isConnected()) {
                    msc.scanFile(absolutePath, "image/png");
                }
            }

            public void onScanCompleted(String path, Uri uri) {
                msc.disconnect();
            }
        }
    }

    private MediaProjectionManager getMediaProjectionManager() {

        return (MediaProjectionManager) getContext().getSystemService(
                Context.MEDIA_PROJECTION_SERVICE);
    }

    private Context getContext() {
        return mRefContext.get();
    }


    private int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    private int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }


    // a  call back listener
    public interface OnShotListener {
        void onFinish(File file);
    }
}