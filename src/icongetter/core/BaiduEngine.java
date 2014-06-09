package icongetter.core;

import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: 47
 * Date: 14-5-28
 * Time: 上午11:11
 * 百度搜索引擎信息
 */
public class BaiduEngine implements SearchEngine {

    public static final String SEARCH_ENGINE_ROOT = "http://www.baidu.com/s?wd=";
    public static final String BAIDU_SEARCH_RESULT_HEAD = "href=\"http://www.baidu.com/link?";

    public static final String BAIDU_SEARCH_LINK_HEAD = "http://www.baidu.com/link?";
    @Override
    public String getSearchUrl(String keyword) {
        return SEARCH_ENGINE_ROOT + keyword;
    }

    @Override
    public List<String> getNextUrl(String searchWebHtml, int resultCount) {
        List<String> nextStepLinks = new LinkedList<String>();
        int startIndex = 0;
        for (int i = 0; i < resultCount; i++) {
            //搜索链接头
            startIndex = searchWebHtml.indexOf(BAIDU_SEARCH_RESULT_HEAD, startIndex);
            if (startIndex == -1) return nextStepLinks;
            //确定开始位置
            startIndex += (BAIDU_SEARCH_RESULT_HEAD.length() - BAIDU_SEARCH_LINK_HEAD
                    .length());
            String webLink = searchWebHtml.substring(startIndex, searchWebHtml.indexOf("\"",
                    startIndex + 1));
            nextStepLinks.add(webLink);
            MyLog.i(webLink);
            startIndex += webLink.length();//跳过连接头
        }
        return nextStepLinks;
    }
}
