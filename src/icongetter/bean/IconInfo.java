package icongetter.bean;

import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Auggie Liang
 * Date: 13-12-22
 * Time: 下午4:11
 */
public class IconInfo {

    public String name ;

    public String getName(){
        return name;
    }

    /** 图片URl*/
    public List<String> urls = new LinkedList<String>();

    /** 后缀名*/
    public List<String> suffixs = new LinkedList<String>();

    /**
     * 添加一个图片的URL
     * @param url
     */
    public void addUrl(String url){
        if(url == null ||"".equals(url)) return;
        urls.add(url);
        //获取后缀名
        int startIndex = url.lastIndexOf(".");
        String suffix = ".png";
        if(startIndex != -1){
            suffix = url.substring(startIndex,url.length());
        }
        suffixs.add(suffix);
    }
}
