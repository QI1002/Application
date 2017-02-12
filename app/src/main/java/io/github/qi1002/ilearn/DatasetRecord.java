package io.github.qi1002.ilearn;

import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

/**
 * Created by QI on 2017/2/12.
 */
public class DatasetRecord {
    public String name;
    public int lookup_cnt;
    public double timestamp;

    // XML node names
    static final String NODE_WORD = "w";
    static final String ATTR_NAME = "n";
    static final String ATTR_LOOKUP_CNT = "c";
    static final String ATTR_TIMESTAMP = "t";

    public static void parseDataset(Context context) {

        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            File filename = new File(Environment.getExternalStorageDirectory(), "data.xml");
            Document doc = db.parse(filename);
            doc.getDocumentElement().normalize();

            NodeList nodeList = doc.getElementsByTagName(NODE_WORD);
            for (int i = 0; i < nodeList.getLength(); i++) {
                Element element = (Element) nodeList.item(i);
                DatasetRecord record = new DatasetRecord();
                record.name = element.getAttributeNode(ATTR_NAME).getValue();
                record.lookup_cnt = Integer.parseInt(element.getAttributeNode(ATTR_LOOKUP_CNT).getValue());
                record.timestamp = Double.parseDouble(element.getAttributeNode(ATTR_TIMESTAMP).getValue());
                Log.d("Test", "name = " + record.name + " count = " + record.lookup_cnt + " stamp = " + new java.util.Date((long)record.timestamp));
            }
        }
        catch (Exception e) {
            Helper.MessageBox(context, e.toString());
        }
    }
}
