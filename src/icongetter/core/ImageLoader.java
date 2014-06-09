package icongetter.core;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created with IntelliJ IDEA.
 * User: 47
 * Date: 14-5-29
 * Time: 上午9:53
 * 图像加载器
 */
public class ImageLoader {

    private ExecutorService mExecutorService;
    /**
     * 下载图片监听器
     */
    private OnImageLoadingListener mImageLoadingListener;
    /**
     * 保存地址*
     */
    private String mSavePath;
    /**
     * 线程数*
     */
    private int mThreadCount;

    /**
     * @param savePath 保存路径
     */
    public ImageLoader(String savePath) {
        this(4, savePath);
    }

    /**
     * 保存路径*
     */
    private ImageLoader(int threadCount, String savePath) {
        mThreadCount = threadCount;
        mSavePath = savePath;
    }

    public void init() {
        if (mExecutorService == null)
            mExecutorService = Executors.newFixedThreadPool(mThreadCount);
    }

    /** 取消下载**/
    public void cancelAll(){
        mExecutorService.shutdown();
    }

    public void setOnImageLoadingListener(OnImageLoadingListener listener){
        mImageLoadingListener = listener;
    }

    /** 添加到下载队列下载**/
    public void putToDownload(final String url, final String name) {
        final ImageInfo iconInfo = new ImageInfo(url, name);
        putToDownload(iconInfo);

    }

    public void putToDownload(final ImageInfo iconInfo) {
    //加入到下载队列
        mExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                if (mImageLoadingListener != null) {
                    mImageLoadingListener.onStart(iconInfo);
                    if (downloadImage(iconInfo)) {
                        mImageLoadingListener.onSucceed(iconInfo);
                    } else {
                        mImageLoadingListener.onFail(iconInfo);
                    }
                    mImageLoadingListener.onComplete(iconInfo);
                }else{
                    downloadImage(iconInfo);
                }
            }
        });
    }


    private boolean downloadImage(ImageInfo imageInfo) {
        String url = imageInfo.url, fileName = imageInfo.name;
        byte[] buffer = new byte[1024 * 4];//4k的缓冲
        URLConnection urlConnection;
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            URL imageUrl = new URL(url);
            urlConnection = imageUrl.openConnection();
            urlConnection.setReadTimeout(3000);
            inputStream = urlConnection.getInputStream();
            int length = 0;
            File file = new File(mSavePath == null ? fileName : mSavePath + "\\" + fileName);
            //如果文件已经存在将会覆盖
            outputStream = new FileOutputStream(file);
            while ((length = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, length);
            }
        } catch (MalformedURLException e) {
            System.out.println("下载失败!" + e.getMessage());
        } catch (IOException e) {
            System.out.println("下载失败!" + e.getMessage());
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;

    }

    public static class ImageInfo {
        public String name;
        public String url;

        public ImageInfo(String url, String name) {
            this.name = name;
            this.url = url;

        }
    }

    public interface OnImageLoadingListener {
        public void onStart(ImageInfo iconInfo);

        public void onSucceed(ImageInfo iconInfo);

        public void onFail(ImageInfo iconInfo);

        /**
         * 无论失败还是成功都会调用*
         */
        public void onComplete(ImageInfo iconInfo);
    }


}
