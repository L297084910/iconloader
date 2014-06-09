package icongetter.core;


import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: 47
 * Date: 2014/6/2
 * Time: 10:16
 */
public class BingEngine implements icongetter.core.SearchEngine {
    public static final String SEARCH_URL = "http://cn.bing.com/search?q=";
    public static final String URL_START_HEAD = "https://itunes.apple.com";
    @Override
    public String getSearchUrl(String keyword) {
        return SEARCH_URL+keyword;
    }

    @Override
    public List<String> getNextUrl(String searchWebHtml, int resultCount) {
        List<String> nextStepLinks = new LinkedList<String>();
        for(int i = 0,startIndex = 0 ; i < resultCount && startIndex != -1 ; i++){
            startIndex = searchWebHtml.indexOf(URL_START_HEAD,startIndex);
            //确定开始位置
            String url = searchWebHtml.substring(startIndex,searchWebHtml.indexOf("\"",
                    startIndex+URL_START_HEAD.length()));
            MyLog.i(startIndex+"");
            MyLog.i(url);
            nextStepLinks.add(url);
            startIndex += url.length() + 1;//跳过链接头
        }
        return nextStepLinks;
    }
}
