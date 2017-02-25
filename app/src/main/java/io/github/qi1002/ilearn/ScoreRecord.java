package io.github.qi1002.ilearn;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.util.Xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.StringWriter;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by QI on 2017/2/25.
 */
public class ScoreRecord {

    // real data fields
    public String items = "";
    public int scores = 0;
    public int type;
    public int test_cnt;
    public double timestamp;

    // download dataset XML
    private static final int BUFFER_SIZE = 4096;
    private static boolean bInitialized = false;
    public static final int MEAN = 0;
    public static final int PRONUNCIATION = 1;
    public static final String score_filename = "score*.xml";

    // XML node names
    static final String NODE_EXAM = "e";
    static final String ATTR_ITEMS = "i";
    static final String ATTR_SCORES = "s";
    static final String ATTR_TYPE = "y";
    static final String ATTR_EXAM_CNT = "c";
    static final String ATTR_TIMESTAMP = "t";
    static private int loadingYear = 0;
    static private ArrayList<ScoreRecord> scoreHistory = new ArrayList<ScoreRecord>();

    public static ArrayList<ScoreRecord> getYearHistory(int year)
    {
        return scoreHistory;
    }

    public static boolean isInitialized() {
        return bInitialized;
    }

    public static boolean checkFile(String filename)
    {
        File file = new File(Environment.getExternalStorageDirectory(), filename);
        return file.exists();
    }

    public static void initialScoreHistory(Context context, int year) {
        loadingYear = year;
        String filename = score_filename.replace("*", String.valueOf(year));
        if (checkFile(filename)) {
            parseHistory(context, filename);
        }
    }

    public static void parseHistory(Context context, String inputfile) {

        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            File filename = new File(Environment.getExternalStorageDirectory(), inputfile);
            Document doc = db.parse(filename);
            doc.getDocumentElement().normalize();

            NodeList nodeList = doc.getElementsByTagName(NODE_EXAM);
            for (int i = 0; i < nodeList.getLength(); i++) {
                Element element = (Element) nodeList.item(i);
                ScoreRecord record = new ScoreRecord();

                record.items = element.getAttributeNode(ATTR_ITEMS).getValue();
                record.scores =  Integer.parseInt(element.getAttributeNode(ATTR_SCORES).getValue());
                record.type =  Integer.parseInt(element.getAttributeNode(ATTR_TYPE).getValue());
                record.test_cnt = Integer.parseInt(element.getAttributeNode(ATTR_EXAM_CNT).getValue());
                record.timestamp = Double.parseDouble(element.getAttributeNode(ATTR_TIMESTAMP).getValue());

                int correctCount = Integer.bitCount(record.scores);
                assert(correctCount <= record.test_cnt );
                int score = (record.test_cnt == 0) ? 0 : correctCount * 100 / record.test_cnt;

                scoreHistory.add(record);
                Log.d("Test", "testitems = " + record.items + " scores = " + record.scores + " type = " + record.type
                        + " count = " + record.test_cnt + " stamp = " + new Date((long) record.timestamp));
            }

            bInitialized = true;
        }
        catch (Exception e) {
            Helper.GenericExceptionHandler(context, e);
        }
    }

    public static void writeHistory(Context context, String outputfile) {

        synchronized (scoreHistory) {
            NumberFormat nf = DecimalFormat.getInstance();
            nf.setMaximumFractionDigits(2);
            nf.setGroupingUsed(false);

            try {
                File filename = new File(Environment.getExternalStorageDirectory(), outputfile);
                FileOutputStream fileos = new FileOutputStream(filename);
                XmlSerializer xmlSerializer = Xml.newSerializer();
                xmlSerializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
                StringWriter writer = new StringWriter();
                xmlSerializer.setOutput(writer);
                xmlSerializer.startDocument("UTF-8", true);
                xmlSerializer.startTag(null, "score");
                for (int i = 0; i < scoreHistory.size(); i++) {
                    ScoreRecord record = scoreHistory.get(i);
                    xmlSerializer.startTag(null, NODE_EXAM);
                    xmlSerializer.attribute(null, ATTR_ITEMS, record.items);
                    xmlSerializer.attribute(null, ATTR_SCORES, String.valueOf(record.scores));
                    xmlSerializer.attribute(null, ATTR_TYPE, String.valueOf(record.type));
                    xmlSerializer.attribute(null, ATTR_EXAM_CNT, String.valueOf(record.test_cnt));
                    xmlSerializer.attribute(null, ATTR_TIMESTAMP, nf.format(record.timestamp));
                    xmlSerializer.endTag(null, NODE_EXAM);
                }

                xmlSerializer.endTag(null, "score");
                xmlSerializer.endDocument();
                xmlSerializer.flush();
                fileos.write(writer.toString().getBytes());
                fileos.close();
            } catch (Exception e) {
                Helper.GenericExceptionHandler(context, e);
            }
        }
    }

    public void applyResult(String item, int index, boolean correct)
    {
        if (items.length() == 0)
            items += item;
        else
            items += (":" + item);

        assert(index < test_cnt);

        if (correct) {
            scores |= (1 << index);
        }
    }

    public void updateRecord(Context context)
    {
        timestamp = (double)(new Date()).getTime();
        scoreHistory.add(this);

        String filename = score_filename.replace("*", String.valueOf(loadingYear));
        writeHistory(context, filename);
    }
}

