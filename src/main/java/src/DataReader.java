package src;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * Created by eclipse on 25/04/2016.
 */
@SuppressWarnings("MalformedRegex")
public class DataReader {
    private List<String> stringList;
    public DataReader(String url){
        stringList = new ArrayList<String>();
        loadFile(url);
    }
    public void loadFile(String url){
        try {
            Scanner input = new Scanner(new File(url));
            while (input.hasNext()){
                String s = input.next();
                stringList.add(s);
            }
            input.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    public Map<String,String> getWordTypePairs(){
        Map<String,String> res = new HashMap<String, String>();

        for(String curStr:stringList){
            String[] parts = curStr.split("/");
            res.put(parts[0].toLowerCase(),parts[1]);
        }
        return res;
    }
    public List<String> getWords(){
        List<String> res= new ArrayList<String>();
        for(String curStr:stringList){
            String[] parts = curStr.split("/");
            res.add(parts[0]);
        }
        return res;
    }


}
