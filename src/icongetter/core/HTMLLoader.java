package icongetter.core;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: 47
 * Date: 14-5-31
 * Time: 下午9:42
 * 网络工具包
 * html页面下载工具
 */
public class HTMLLoader {
    //失败重试次数
    public static final int REPEAT_COUNT = 4;

    private HttpClient httpClient;


    public HTMLLoader() {
        createHttpClient();
    }

    /**
     * 获得搜索引擎的HMTL数据
     */
    public String loadHtml(String url) {
        HttpGet get = new HttpGet(url);
        HttpResponse response;
        for (int i = 0; i < REPEAT_COUNT; i++) {
            try {
                response = httpClient.execute(get);
                if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    MyLog.i("网络正常，正在返回数据");
                    return EntityUtils.toString(response.getEntity(), "utf-8");
                } else {
                    MyLog.i("访问网络失败错误代码:" + response.getStatusLine().getStatusCode());
                }
            } catch (IOException e) {
                MyLog.i("访问网络失败:"+e.getMessage());
            }
        }
        return "";
    }

    /** 创建一个httpcliend;**/
    private void createHttpClient(){
        httpClient = HttpClients.createDefault();
    }

}
