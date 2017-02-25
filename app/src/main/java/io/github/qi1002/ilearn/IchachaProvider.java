package io.github.qi1002.ilearn;

import android.content.Context;

/**
 * Created by QI on 2017/2/19.
 */
public class IchachaProvider implements IDictionaryProvider {

    public String getEntrance()
    {
        return "http://tw.ichacha.net/m/";
    }

    public String getWordMeanLink(String word)
    {
        return "http://tw.ichacha.net/m/" + word + ".html";
    }

    public String getWordVoiceLink(String word)
    {
        return "document.getElementsByClassName('laba')[0].click();";
    }

    public boolean isTranslate()
    {
        return true;
    }

    public boolean isInvalidLookup(String html)
    {
        return html.contains("未收錄此詞條，如要使用機器自動翻譯整個詞條，請點");
    }

    public String getWordMean(Context context, String html, String word) {

        String result = "";

        try
        {
            // get // <div class="base"> .. </div>
            int find1 = html.indexOf("<div class=\"base\">");
            int find2 = html.indexOf("</div>", (find1 < 0) ? 0 : find1);
            if (find1 > 0 && find2 > 0)
            {
                String subHTML = html.substring(find1 + 19, find2);
                int findLess = 0;
                int findMore = 0;
                int inSpanCnt = 0;
                int inSkipSpanCnt = 0;
                int inSpanLess = -1;
                while (findLess >= 0 && findMore >= 0)
                {
                    findLess = subHTML.indexOf('<');
                    findMore = subHTML.indexOf('>');
                    if (findLess < findMore)
                    {
                        result = "";
                        if (findLess > 0)
                        {
                            if (inSpanLess == -1 || inSkipSpanCnt != inSpanCnt)
                                result += subHTML.substring(0, findLess);
                            else
                                result += subHTML.substring(0, inSpanLess);
                        }

                        if (findLess >= 0)
                        {
                            if (subHTML.substring(findLess).startsWith("<span"))
                            {
                                inSpanCnt++;
                                if (subHTML.substring(findLess, findMore).indexOf("toggle") > 0 ||
                                        subHTML.substring(findLess, findMore).indexOf("display:none") > 0)
                                {
                                    inSkipSpanCnt = inSpanCnt;
                                    inSpanLess = findLess;
                                }
                            }
                            if (subHTML.substring(findLess).startsWith("</span"))
                            {
                                if (inSkipSpanCnt == inSpanCnt)
                                    inSkipSpanCnt = 0;
                                inSpanCnt--;
                            }
                        }

                        if ((findMore + 1) < subHTML.length())
                            result += subHTML.substring(findMore + 1);
                        subHTML = result;
                    }
                }
            }
        }catch (Exception e)
        {
            Helper.GenericExceptionHandler(context, e);
        }

        return result.replace("&nbsp;","");
    }
}
