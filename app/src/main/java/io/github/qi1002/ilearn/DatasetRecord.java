package io.github.qi1002.ilearn;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xmlpull.v1.XmlSerializer;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.util.Xml;

/**
 * Created by QI on 2017/2/12.
 */
public class DatasetRecord {
    // real data fields
    public String name;
    public int lookup_cnt;
    public long timestamp;

    public int mean_correct_cnt;
    public int mean_fail_cnt;
    public int voice_correct_cnt;
    public int voice_fail_cnt;
    public int category;

    // download dataset XML
    private static final int BUFFER_SIZE = 4096;
    private static boolean bInitialized = false;
    private static boolean bDirty = false;
    public static final String dataset_filename = "data.xml";
    public static final String output_filename = "output.xml";

    // XML node names
    static final String NODE_WORD = "w";
    static final String ATTR_NAME = "n";
    static final String ATTR_LOOKUP_CNT = "c";
    static final String ATTR_TIMESTAMP = "t";
    static private ArrayList<DatasetRecord> dataset = new ArrayList<DatasetRecord>();
    static private IDictionaryProvider teachbase = new IchachaProvider();

    public static ArrayList<DatasetRecord> getDataset()
    {
        return dataset;
    }

    public static IDictionaryProvider getDictionaryProvider()
    {
        return teachbase;
    }

    public static boolean isInitialized() {
        return bInitialized;
    }

    public static boolean isDirty() {
        return bDirty;
    }

    public static boolean checkFile(String filename)
    {
        File file = new File(Environment.getExternalStorageDirectory(), filename);
        return file.exists();
    }

    public static void resetDataset() {
        dataset = new ArrayList<DatasetRecord>();
    }

    public static void initialDataset(Context context) {
        if (!checkFile(dataset_filename)) {
            downloadDataset(context, dataset_filename);
        }
        else {
            parseDataset(context, dataset_filename);
        }
    }

    public static void downloadDataset(Context context, String filename) {

        boolean dataset_update = Helper.getPreferenceBoolean(context, R.string.pref_key_dataset_update);
        String dataset_url_location = Helper.getPreferenceString(context, R.string.pref_key_dataset_url_location);

        if (dataset_update == false || Helper.isNullOrEmpty(dataset_url_location))
        {
            resetDataset();
            bInitialized = true;
            return;
        }

        // avoid NetworkOnMainThreadException ( not use http download in UI thread)
        Object[] arguments = { dataset_url_location,  filename };
        Thread downloadThread = new Thread(new DataPassThread(context, arguments) {
            @Override
            public void run() {
                try {
                    assert((inner_arguments != null) && (inner_arguments.length == 2));
                    // avoid exception "can't create handler inside thread that has not called looper.prepare()"
                    Looper.prepare();
                    String urllink = (String)inner_arguments[0];
                    String filename = (String)inner_arguments[1];
                    downloadDatasetImpl(inner_context, urllink, filename);
                    DatasetRecord.parseDataset(inner_context, filename);
                } catch (Exception e) {
                    Helper.GenericExceptionHandler(inner_context, e);
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
            Helper.GenericExceptionHandler(context, e);
        }
    }

    private static DatasetRecord checkWord(String word)
    {
        DatasetRecord record = null;

        for (int i = 0; i<dataset.size(); i++) {
            record = dataset.get(i);
            if (record.name.compareTo(word) == 0)
                break;
            else
                record = null;
        }

        return record;
    }

    public static void parseDataset(final Context context, String inputfile) {

        boolean dataset_check = Helper.getPreferenceBoolean(context, R.string.pref_key_dataset_check);
        String duplicateWords = "";

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
                record.timestamp = Long.parseLong(element.getAttributeNode(ATTR_TIMESTAMP).getValue());
                record.mean_correct_cnt = record.mean_fail_cnt = 0;
                record.voice_correct_cnt = record.voice_fail_cnt = 0;
                record.category = 0; // undefined

                if (dataset_check) {
                    if (checkWord(record.name) != null)
                        duplicateWords += (record.name + ":");
                }

                dataset.add(record);
                //Log.d("Test", "name = " + record.name + " count = " + record.lookup_cnt + " stamp = " + new Date(record.timestamp));
            }

            bInitialized = true;
        }
        catch (Exception e) {
            Helper.GenericExceptionHandler(context, e);
        }

         if (duplicateWords.length() != 0) {

            DialogInterface.OnClickListener positiveListner =
            new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    DatasetRecord.resetDataset();
                    DatasetRecord.downloadDataset(context, DatasetRecord.dataset_filename);
                }
            };

            Helper.SelectionBox(context, "Duplicated words\n" + duplicateWords + "\nFound\nDo you want to reset dataset ?", "Reset", "Keep",
                    "Dataset Check Selection Box", positiveListner, null);
        }
    }

    public static void mergeDataset(ArrayList<DatasetRecord> mergedData, ArrayList<DatasetRecord> appenedData)
    {
        for (int i = 0; i < appenedData.size(); i++) {
            DatasetRecord newRecord = appenedData.get(i);
            DatasetRecord record = checkWord(newRecord.name);

            if (record == null)
            {
                mergedData.add(newRecord);
            }else
            {
                assert(record.name.compareTo(newRecord.name) == 0);
                if (newRecord.timestamp > record.timestamp) {
                    record.timestamp = newRecord.timestamp;
                    record.lookup_cnt += newRecord.lookup_cnt;
                }
            }

            bDirty = true;
        }
    }

    public static ArrayList<DatasetRecord> parseECDICT(Context context, String inputpath, String inputfile) {

        ArrayList<DatasetRecord> ecdictData = new ArrayList<DatasetRecord>();
        ArrayList<DatasetRecord> result = ecdictData;

        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            File filename = new File(inputpath, inputfile);
            Document doc = db.parse(filename);
            doc.getDocumentElement().normalize();

            NodeList nodeList = doc.getElementsByTagName("long");
            for (int i = 0; i < nodeList.getLength(); i++) {
                Element element = (Element) nodeList.item(i);
                DatasetRecord record = new DatasetRecord();
                record.name = element.getAttributeNode("name").getValue();
                record.lookup_cnt = 1;
                record.timestamp = Long.parseLong(element.getAttributeNode("value").getValue());
                record.mean_correct_cnt = record.mean_fail_cnt = 0;
                record.voice_correct_cnt = record.voice_fail_cnt = 0;
                record.category = 0; // undefined

                ecdictData.add(record);
                Log.d("ecDict", "name = " + record.name + " stamp = " + new Date(record.timestamp));
            }
        }
        catch (Exception e) {
            ecdictData = null;
            Helper.GenericExceptionHandler(context, e);
        }

        return ecdictData;   }

    public static void writeDataset(Context context, String outputfile) {

        synchronized (dataset) {
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
                for (int i = 0; i < dataset.size(); i++) {
                    DatasetRecord record = dataset.get(i);
                    xmlSerializer.startTag(null, NODE_WORD);
                    xmlSerializer.attribute(null, ATTR_NAME, record.name);
                    xmlSerializer.attribute(null, ATTR_LOOKUP_CNT, String.valueOf(record.lookup_cnt));
                    xmlSerializer.attribute(null, ATTR_TIMESTAMP, String.valueOf(record.timestamp));
                    xmlSerializer.endTag(null, NODE_WORD);
                }

                xmlSerializer.endTag(null, "collection");
                xmlSerializer.endDocument();
                xmlSerializer.flush();
                fileos.write(writer.toString().getBytes());
                fileos.close();
            } catch (Exception e) {
                Helper.GenericExceptionHandler(context, e);
            }
        }

        bDirty = false;
    }

    public static void updateDataset(String outputfile, String inputfile)
    {
        File output_file = new File(Environment.getExternalStorageDirectory(), outputfile);

        if (output_file.exists())
        {
            File dataset_file = new File(Environment.getExternalStorageDirectory(), inputfile);
            if (dataset_file.exists()) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("-yy-MM-dd-HH-mm-ss");
                String backupfile = inputfile.substring(0, inputfile.length() - 4)+dateFormat.format(new Date())+".xml";
                File backup_file = new File(Environment.getExternalStorageDirectory(), backupfile);
                dataset_file.renameTo(backup_file);
            }

            File dataset2_file = new File(Environment.getExternalStorageDirectory(), inputfile);
            output_file.renameTo(dataset2_file);
        }
    }

    public static void updateRecord(String word)
    {
        DatasetRecord record = checkWord(word);

        if (record == null)
        {
            record = new DatasetRecord();
            record.name = word;
            record.lookup_cnt = 1;
            record.timestamp = (new Date()).getTime();
            dataset.add(record);
        }else
        {
            assert(record.name.compareTo(word) == 0);
            record.lookup_cnt++;
            record.timestamp = (new Date()).getTime();
        }

        bDirty = true;
    }

    public static void updateMeanExamResult(String word, boolean correct)
    {
        DatasetRecord record = checkWord(word);

        if (record != null)
        {
            if (correct)
                record.mean_correct_cnt++;
            else
                record.mean_fail_cnt++;
        }
    }

    public static void updateVoiceExamResult(String word, boolean correct)
    {
        DatasetRecord record = checkWord(word);

        if (record != null)
        {
            if (correct)
                record.voice_correct_cnt++;
            else
                record.voice_fail_cnt++;
        }
    }

    public static IEnumerable getEnumerator(ArrayList<DatasetRecord> dataset, String wayname)
    {
        EnumerableWay way = EnumerableWay.valueOf(wayname);
        switch (way)
        {
            case Sequence:
                return new SequenceEnumerable(dataset);
            case Random:
                return new RandomEnumerable(dataset);
            case Shuffle:
                return new ShuffleEnumerable(dataset);
            case LookupCount:
                return new LookupCountEnumerable(dataset);
            case TimeStamp:
                return new TimeStampEnumerable(dataset);
            case MeanExamScore:
                return new MeanScoreEnumerable(dataset);
            case MeanExamCount:
                return new MeanCountEnumerable(dataset);
            case VoiceExamScore:
                return new VoiceScoreEnumerable(dataset);
            case VoiceExamCount:
                return new VoiceCountEnumerable(dataset);
            case AllExamScore:
                return new AllScoreEnumerable(dataset);
            case AllExamCount:
                return new AllCountEnumerable(dataset);
        }

        return null;
    }
}

enum EnumerableWay {
    None,
    Sequence,
    Random,
    Shuffle,
    LookupCount,
    TimeStamp,
    MeanExamScore,
    MeanExamCount,
    VoiceExamScore,
    VoiceExamCount,
    AllExamScore,
    AllExamCount,
};

class ArrayIndexComparator implements Comparator<Integer>
{
    private ArrayList<DatasetRecord> dataset;
    private EnumerableWay compareWay;

    public ArrayIndexComparator(ArrayList<DatasetRecord> dataset, EnumerableWay way)
    {
        this.dataset = dataset;
        this.compareWay = way;
    }

    public ArrayList<Integer> createIndexArray()
    {
        ArrayList<Integer> indexes = new ArrayList<Integer>();
        for (int i = 0; i < dataset.size(); i++)
            indexes.add(i);

        return indexes;
    }

    @Override
    public int compare(Integer index1, Integer index2)
    {
        Integer count1, count2;
        Integer score1, score2;

        switch (compareWay)
        {
            case LookupCount:
                count1 = dataset.get(index1).lookup_cnt;
                count2 = dataset.get(index2).lookup_cnt;
                // descending order
                return count2.compareTo(count1);
            case TimeStamp:
                Long stamp1 = dataset.get(index1).timestamp;
                Long stamp2 = dataset.get(index2).timestamp;
                // descending order
                return stamp2.compareTo(stamp1);
            case MeanExamScore:
                score1 = dataset.get(index1).mean_correct_cnt - dataset.get(index1).mean_fail_cnt;
                score2 = dataset.get(index2).mean_correct_cnt - dataset.get(index2).mean_fail_cnt;
                // descending order
                return score2.compareTo(score1);
            case MeanExamCount:
                count1 = dataset.get(index1).mean_correct_cnt + dataset.get(index1).mean_fail_cnt;
                count2 = dataset.get(index2).mean_correct_cnt + dataset.get(index2).mean_fail_cnt;
                // descending order
                return count2.compareTo(count1);
            case VoiceExamScore:
                score1 = dataset.get(index1).voice_correct_cnt - dataset.get(index1).voice_fail_cnt;
                score2 = dataset.get(index2).voice_correct_cnt - dataset.get(index2).voice_fail_cnt;
                // descending order
                return score2.compareTo(score1);
            case VoiceExamCount:
                count1 = dataset.get(index1).voice_correct_cnt + dataset.get(index1).voice_fail_cnt;
                count2 = dataset.get(index2).voice_correct_cnt + dataset.get(index2).voice_fail_cnt;
                // descending order
                return count2.compareTo(count1);
            case AllExamScore:
                score1 = dataset.get(index1).mean_correct_cnt - dataset.get(index1).mean_fail_cnt +
                         dataset.get(index1).voice_correct_cnt - dataset.get(index1).voice_fail_cnt;
                score2 = dataset.get(index2).mean_correct_cnt - dataset.get(index2).mean_fail_cnt +
                         dataset.get(index2).voice_correct_cnt - dataset.get(index2).voice_fail_cnt;
                // descending order
                return score2.compareTo(score1);
            case AllExamCount:
                count1 = dataset.get(index1).mean_correct_cnt + dataset.get(index1).mean_fail_cnt +
                         dataset.get(index1).voice_correct_cnt + dataset.get(index1).voice_fail_cnt;
                count2 = dataset.get(index2).mean_correct_cnt + dataset.get(index2).mean_fail_cnt +
                         dataset.get(index2).voice_correct_cnt + dataset.get(index2).voice_fail_cnt;
                // descending order
                return count2.compareTo(count1);
            default:
                // default is ascending order in sequence
                return index1.compareTo(index2);
        }
    }
}

/* enumerate dataset by sequence order */
class SequenceEnumerable implements IEnumerable {

    private int currentIndex;
    private ArrayList<DatasetRecord> dataset;

    public SequenceEnumerable(ArrayList<DatasetRecord> dataset)
    {
        currentIndex = 0;
        this.dataset = dataset;
    }

    public DatasetRecord getCurrent()
    {
        if (currentIndex >= dataset.size())
            return null;

        return dataset.get(currentIndex);
    }

    public DatasetRecord moveNext()
    {
        if (currentIndex >= dataset.size())
            return null;

        return dataset.get(currentIndex++);
    }

    public void reset()
    {
        currentIndex = 0;
    }
}

/* enumerate dataset by random order */
class RandomEnumerable implements IEnumerable {

    private int currentIndex;
    private Random generator = null;
    private ArrayList<DatasetRecord> dataset;

    public RandomEnumerable(ArrayList<DatasetRecord> dataset)
    {
        generator = new Random();
        currentIndex = generator.nextInt(dataset.size());
        this.dataset = dataset;
    }

    public RandomEnumerable(ArrayList<DatasetRecord> dataset, long seed)
    {
        generator = new Random(seed);
        currentIndex = generator.nextInt(dataset.size());
        this.dataset = dataset;
    }

    public DatasetRecord getCurrent()
    {
        return dataset.get(currentIndex);
    }

    public DatasetRecord moveNext()
    {
        currentIndex = generator.nextInt(dataset.size());
        return dataset.get(currentIndex);
    }

    public void reset()
    {
        // do nothing
    }
}

/* enumerate dataset by shuffle order */
class ShuffleEnumerable implements IEnumerable {

    protected int currentIndex;
    protected ArrayList<Integer> indexes = null;
    protected ArrayList<DatasetRecord> dataset;

    public ShuffleEnumerable(ArrayList<DatasetRecord> dataset)
    {
        this.dataset = dataset;
        reset();
    }

    public DatasetRecord getCurrent()
    {
        if (currentIndex >= dataset.size())
            return null;

        return dataset.get(indexes.get(currentIndex));
    }

    public DatasetRecord moveNext()
    {
        if (currentIndex >= dataset.size())
            return null;

        return dataset.get(indexes.get(currentIndex++));
    }

    public void reset()
    {
        ArrayIndexComparator comparator = new ArrayIndexComparator(dataset, EnumerableWay.Shuffle);
        indexes = comparator.createIndexArray();
        Collections.shuffle(indexes);
    }
}

/* enumerate dataset by some value order */
class ValueEnumerable implements IEnumerable {

    protected int currentIndex;
    protected ArrayList<Integer> indexes = null;
    protected ArrayList<DatasetRecord> dataset;

    protected EnumerableWay getEnumerableWay()
    {
        return EnumerableWay.None;
    }

    public ValueEnumerable(ArrayList<DatasetRecord> dataset)
    {
        this.dataset = dataset;
        reset();
    }

    public DatasetRecord getCurrent()
    {
        if (currentIndex >= dataset.size())
            return null;

        return dataset.get(indexes.get(currentIndex));
    }

    public DatasetRecord moveNext()
    {
        if (currentIndex >= dataset.size())
            return null;

        return dataset.get(indexes.get(currentIndex++));
    }

    public void reset()
    {
        ArrayIndexComparator comparator = new ArrayIndexComparator(dataset, getEnumerableWay());
        indexes = comparator.createIndexArray();
        Collections.sort(indexes, comparator);
        currentIndex = 0;
    }
}

/* enumerate dataset by lookup count value order */
class LookupCountEnumerable extends ValueEnumerable {

    @Override
    protected EnumerableWay getEnumerableWay()
    {
        return EnumerableWay.LookupCount;
    }

    public LookupCountEnumerable(ArrayList<DatasetRecord> dataset)
    {
        super(dataset);
    }
}

/* enumerate dataset by timestamp value order */
class TimeStampEnumerable extends ValueEnumerable {

    @Override
    protected EnumerableWay getEnumerableWay()
    {
        return EnumerableWay.TimeStamp;
    }

    public TimeStampEnumerable(ArrayList<DatasetRecord> dataset)
    {
        super(dataset);
    }
}

/* enumerate dataset by mean score value order */
class MeanScoreEnumerable extends ValueEnumerable {

    @Override
    protected EnumerableWay getEnumerableWay()
    {
        return EnumerableWay.MeanExamScore;
    }

    public MeanScoreEnumerable(ArrayList<DatasetRecord> dataset)
    {
        super(dataset);
    }
}

/* enumerate dataset by mean count value order */
class MeanCountEnumerable extends ValueEnumerable {

    @Override
    protected EnumerableWay getEnumerableWay()
    {
        return EnumerableWay.MeanExamCount;
    }

    public MeanCountEnumerable(ArrayList<DatasetRecord> dataset)
    {
        super(dataset);
    }
}

/* enumerate dataset by voice score value order */
class VoiceScoreEnumerable extends ValueEnumerable {

    @Override
    protected EnumerableWay getEnumerableWay()
    {
        return EnumerableWay.VoiceExamScore;
    }

    public VoiceScoreEnumerable(ArrayList<DatasetRecord> dataset)
    {
        super(dataset);
    }
}

/* enumerate dataset by voice count value order */
class VoiceCountEnumerable extends ValueEnumerable {

    @Override
    protected EnumerableWay getEnumerableWay()
    {
        return EnumerableWay.VoiceExamCount;
    };

    public VoiceCountEnumerable(ArrayList<DatasetRecord> dataset)
    {
        super(dataset);
    }
}

/* enumerate dataset by all  score value order */
class AllScoreEnumerable extends ValueEnumerable {

    @Override
    protected EnumerableWay getEnumerableWay()
    {
        return EnumerableWay.AllExamScore;
    }

    public AllScoreEnumerable(ArrayList<DatasetRecord> dataset)
    {
        super(dataset);
    }
}

/* enumerate dataset by all count value order */
class AllCountEnumerable extends ValueEnumerable {

    @Override
    protected EnumerableWay getEnumerableWay()
    {
        return EnumerableWay.AllExamCount;
    }

    public AllCountEnumerable(ArrayList<DatasetRecord> dataset)
    {
        super(dataset);
    }
}