package icongetter.core;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.SwingWorker;

import icongetter.bean.IconInfo;
import icongetter.ui.PrintableUi;

/**
 * Created with IntelliJ IDEA.
 * User: Auggie Liang
 * Date: 13-12-1 Time: 上午11:14
 * 分析数据的核心代码
 */
public class Core {

    public static final int MESSAGE_PRINT = 1;

    //"site:itunes.apple.com%20"
    private final List<IconInfo> mIconInfoList;
    private final String mSavePath;

    /** 搜索前缀**/
    private final String mSearchForward;

    /** 搜索引擎**/
    private final SearchEngine mEngine;

    /** html加载器**/
    private HTMLLoader mHTMLLoader = new HTMLLoader();

    private boolean isCancel;

    /** 图像加载器**/
    private ImageLoader mImageLoader;
    /**
     * 搜索的个数,默认为前四条
     */
    private int mSearchCount = 4;

    public Core(List<IconInfo> iconInfoList,
                String searchForward,
                String savePath,
                int searchCount,
                SearchEngine engine) {
        this.mIconInfoList = iconInfoList;//图标信息
        this.mSavePath = savePath;//保存路径
        this.mSearchForward = searchForward;//搜索前缀
        this.mSearchCount = searchCount;//搜索个数
        mEngine = engine;//搜索引擎
        mImageLoader = new ImageLoader(savePath);//图像加载器
        //图像下载过程监听器
        mImageLoader.setOnImageLoadingListener(new ImageLoader.OnImageLoadingListener() {
            @Override
            public void onStart(ImageLoader.ImageInfo iconInfo) {
                MyLog.i("download:" + iconInfo.name);
            }

            @Override
            public void onSucceed(ImageLoader.ImageInfo iconInfo) {
                MyLog.i("download succeed:"+iconInfo.name);
            }

            @Override
            public void onFail(ImageLoader.ImageInfo iconInfo) {
                MyLog.e("download fail:" + iconInfo.name + ":" + iconInfo.url);
            }

            @Override
            public void onComplete(ImageLoader.ImageInfo iconInfo) {

            }
        });
    }


    /** 取消操作**/
    public void cancel(){
        isCancel = true;
    }


    /** 开始运行**/
    public void start(){
        mImageLoader.init();
        isCancel = false;
        for (IconInfo iconInfo : mIconInfoList) {
            String searchHtml = searchKeyWord(iconInfo.name);
            getIconLink(searchHtml, iconInfo);
            if (isCancel) {
                MyLog.i("cancel");
                shutdown();
                break;
            }
            int i = 0;
            for (String url : iconInfo.urls) {
                if (isCancel) {
                    shutdown();
                    break;
                }
                //加入到下载队列
                mImageLoader.putToDownload(url,
                        iconInfo.name + (i + 1) + iconInfo.suffixs.get(i++)//输入的名字命名
                );
            }
        }
    }

    private void shutdown(){
        mImageLoader.cancelAll();
    }


    /**
     * 根据关键词搜索
     * @param iconName
     * @return 返回关键词搜索后的页面的string,失败返回空字符串
     */
    public String searchKeyWord(String iconName) {
//        return "name";
        String searchUrl = mEngine.getSearchUrl(mSearchForward+iconName);
        MyLog.i(searchUrl);
        return mHTMLLoader.loadHtml(searchUrl);
    }

    /**
     * 分析网页代码，获取图片下的下载地址
     *
     * @param htmlStr
     * @return
     */
    public void getIconLink(String htmlStr, IconInfo iconInfo) {
        //获取搜索结果下一步的链接
        List<String> nextStepLinks = mEngine.getNextUrl(htmlStr,mSearchCount);
        if (nextStepLinks.size() == 0) {
            MyLog.w("没有找到对应的图片:" + iconInfo.name);
            return;
        }
        //开始分析图片的位置
        for (String link : nextStepLinks) {
            String resultStr = mHTMLLoader.loadHtml(link);
            //使用正则表达式进行匹配
            Matcher matcher = bigImagePattern.matcher(resultStr);
            if (matcher.find()) {
                String group = matcher.group();
                if (group.length() > IMAGE_HEAD.length()) {//搜索结果是否大于头，不大于表示没有结果
                    String imgLinks = group.substring(IMAGE_HEAD.length());
                    MyLog.i("match:" + iconInfo.name + ":" + imgLinks);
                    iconInfo.addUrl(imgLinks);//添加到一个下载链接到图标中
                } else MyLog.w("no match-->" + iconInfo.name + ":baiduLink:" + link);
            } else {
                MyLog.w("no match-->" + iconInfo.name + ":baiduLink:" + link);
            }
        }
    }

    public static class IconWorker extends SwingWorker<String, Message> {



        /**
         * 下载核心*
         */
        private Core mCore;

        /**
         * 完成监听器*
         */
        private OnDoneListener mOnDoneListener;

        private PrintableUi mPrintableUi;

        /**
         *
         * @param iconInfoList 图标搜索列表
         * @param searchForward
         * @param savePath
         * @param printUi
         * @param searchCount
         * @param engine
         */
        public IconWorker(List<IconInfo> iconInfoList, String searchForward, String savePath,
                          PrintableUi printUi, int searchCount,SearchEngine engine) {
            mCore = new Core(iconInfoList,
                    searchForward,
                    savePath,
                    searchCount,
                    engine);
            mPrintableUi = printUi;
        }

        public Core getCore(){
            return mCore;
        }

        /**
         * 设置完成监听器
         *
         * @param listener
         */
        public void setOnDoneListener(OnDoneListener listener) {
            mOnDoneListener = listener;
        }

        @Override
        protected String doInBackground() throws Exception {
            mCore.start();
            return null;
        }




        @Override
        protected void process(List<Message> chunks) {
            if (chunks.size() > 0) {
                Message msg = chunks.get(0);
                switch (msg.what) {
                    case MESSAGE_PRINT://print information
                        mPrintableUi.print(msg.object.toString());
                        break;
                }
            }
            super.process(chunks);
        }

        @Override
        protected void done() {
            super.done();
            if (mOnDoneListener != null) {
                mOnDoneListener.done();
            }
        }

    }


    /**高清图像url匹配正则表达式**/
    private final Pattern bigImagePattern
            = Pattern.compile("<meta content=\"http://a\\d\\.mzstatic\\.com.+(\\.png|\\.jpg|\\.jpeg|\\.gif){1}");


    public static final String BIG_IMAGE_START = "<meta content=\"http://a1.mzstatic.com/";

    public static final String BIG_IMAGE_URL_START = "http://a1.mzstatic.com/";

    public static final String IMAGE_HEAD = "<meta content=\"";
}
