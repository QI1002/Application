package io.github.qi1002.ilearn;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xmlpull.v1.XmlSerializer;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.util.Xml;

/**
 * Created by QI on 2017/2/12.
 */
public class DatasetRecord {
    // real data fields
    public String name;
    public int lookup_cnt;
    public double timestamp;

    // download dataset XML
    private static final int BUFFER_SIZE = 4096;
    private static final String dataset_filename = "data.xml";
    private static final String output_filename = "output.xml";
    private static boolean bInitialized = false;

    // XML node names
    static final String NODE_WORD = "w";
    static final String ATTR_NAME = "n";
    static final String ATTR_LOOKUP_CNT = "c";
    static final String ATTR_TIMESTAMP = "t";
    static private ArrayList<DatasetRecord> dataset = new ArrayList<DatasetRecord>();

    public static ArrayList<DatasetRecord> getDataset()
    {
        return dataset;
    }

    public static boolean isInitialized() {
        return bInitialized;
    }

    public static boolean checkFile(String filename)
    {
        File file = new File(Environment.getExternalStorageDirectory(), filename);
        return file.exists();
    }

    public static void initialDataset(Context context) {
        if (!DatasetRecord.checkFile(dataset_filename))
            DatasetRecord.downloadDataset(context, "https://raw.githubusercontent.com/QI1002/qi1002.github.io/master/data/" + dataset_filename, dataset_filename);
        else {
            DatasetRecord.parseDataset(context, dataset_filename);
            //DatasetRecord.writeDataset(context, output_filename);
        }
    }

    public static void downloadDataset(Context context, final String urllink, final String filename) {

        // avoid NetworkOnMainThreadException ( not use http download in UI thread)
        final Object[] arguments = { urllink,  filename };
        Thread downloadThread = new Thread(new DataPassThread(context, arguments) {
            @Override
            public void run() {
                try {
                    assert((inner_arguments != null) && (inner_arguments.length == 2));
                    String urllink = (String)inner_arguments[0];
                    String filename = (String)inner_arguments[1];
                    downloadDatasetImpl(inner_context, urllink, filename);
                    DatasetRecord.parseDataset(inner_context, filename);
                } catch (Exception e) {
                    Helper.MessageBox(inner_context, e.getLocalizedMessage());
                }
            }
        });

        downloadThread.start();
    }

    private static void downloadDatasetImpl(Context context, String urllink, String filename)
    {
        try {
            URL url = new URL(urllink);
            File file = new File(Environment.getExternalStorageDirectory(), filename);

            long startTime = System.currentTimeMillis();
            Log.d("DownloadDataset", "download url: " + url + " to " + filename + " beginning");

            /* Open a connection to that URL. */
            URLConnection urlConn = url.openConnection();

            /* Define InputStreams to read from the URLConnection. */
            InputStream inputStream = urlConn.getInputStream();
            FileOutputStream outputStream = new FileOutputStream(file);

            int bytesRead = -1;
            byte[] buffer = new byte[BUFFER_SIZE];
            /* Read bytes to the Buffer until there is nothing more to read(-1). */
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.flush();
            outputStream.close();
            inputStream.close();

            Log.d("DownloadDataset", "download ready in "
                    + ((System.currentTimeMillis() - startTime) / 1000)
                    + " sec");
        }
        catch (Exception e) {
            Helper.MessageBox(context, e.getLocalizedMessage());
        }
    }

    public static void parseDataset(Context context, String inputfile) {

        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            File filename = new File(Environment.getExternalStorageDirectory(), inputfile);
            Document doc = db.parse(filename);
            doc.getDocumentElement().normalize();

            NodeList nodeList = doc.getElementsByTagName(NODE_WORD);
            for (int i = 0; i < nodeList.getLength(); i++) {
                Element element = (Element) nodeList.item(i);
                DatasetRecord record = new DatasetRecord();
                record.name = element.getAttributeNode(ATTR_NAME).getValue();
                record.lookup_cnt = Integer.parseInt(element.getAttributeNode(ATTR_LOOKUP_CNT).getValue());
                record.timestamp = Double.parseDouble(element.getAttributeNode(ATTR_TIMESTAMP).getValue());
                dataset.add(record);
                Log.d("Test", "name = " + record.name + " count = " + record.lookup_cnt + " stamp = " + new java.util.Date((long)record.timestamp));
            }

            bInitialized = true;
        }
        catch (Exception e) {
            Helper.MessageBox(context, e.getLocalizedMessage());
        }
    }

    public static void writeDataset(Context context, String outputfile) {

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
            xmlSerializer.startTag(null, "collection");
            for (int i = 0; i<dataset.size(); i++)
            {
                DatasetRecord record = dataset.get(i);
                xmlSerializer.startTag(null, "w");
                xmlSerializer.attribute(null, "n", record.name);
                xmlSerializer.attribute(null, "c", String.valueOf(record.lookup_cnt));
                xmlSerializer.attribute(null, "t", nf.format(record.timestamp));
                xmlSerializer.endTag(null, "w");
            }

            xmlSerializer.endTag(null, "collection");
            xmlSerializer.endDocument();
            xmlSerializer.flush();
            fileos.write(writer.toString().getBytes());
            fileos.close();
        }
        catch (Exception e) {
            Helper.MessageBox(context, e.getLocalizedMessage());
        }
    }
}
