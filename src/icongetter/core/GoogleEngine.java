package icongetter.core;

import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: 47
 * Date: 14-5-28
 * Time: 下午6:19
 * 谷歌引擎嘻嘻
 */
public class GoogleEngine implements SearchEngine {

    public static final String SEARCH_URL = "https://www.google.com.hk/#newwindow=1&q=";
    public static final String URL_START_HEAD = "https://itunes.apple.com";
    @Override
    public String getSearchUrl(String keyword) {
        return SEARCH_URL+ keyword;
    }

    @Override
    public List<String> getNextUrl(String searchWebHtml, int resultCount) {
        List<String> nextStepLinks = new LinkedList<String>();
        for(int i = 0,startIndex = 0 ; i < resultCount ; i++){
            startIndex = searchWebHtml.indexOf(URL_START_HEAD,startIndex);
            if(startIndex == -1 ) return nextStepLinks;
            //确定开始位置
            int firstEnd = searchWebHtml.indexOf("\"",startIndex + URL_START_HEAD.length());
            String url = searchWebHtml.substring(startIndex,firstEnd);
            MyLog.i(url);
            nextStepLinks.add(url);
            //由于google 会出现三次同样的地址，最后一次是在类似结果之前，所以跳过就可以了
            startIndex= searchWebHtml.indexOf("类似结果",firstEnd);
        }
        return nextStepLinks;
    }
}
