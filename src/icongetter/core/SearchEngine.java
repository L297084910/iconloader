/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package icongetter.core;

import java.util.List;

/**
 * 获取搜索工具的地址
 * @author 47
 */
public interface SearchEngine {

    /** 获取搜索的URL
     *
     * @param keyword 搜索关键字
     * @return
     */
    public String getSearchUrl(String keyword);


    /**
     * 获取下一步的链接地址
     * @param searchWebHtml 搜索后的HTML
     * @param resultCount 返回下一步的链接个数
     * @return
     */
    public List<String> getNextUrl(String searchWebHtml,int resultCount);

}
