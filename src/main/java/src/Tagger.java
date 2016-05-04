package src;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Yang Xu on 2016/5/1.
 */
public class Tagger {

    public interface TrainingListener {
        void onIterationStart(int iterationIndex, int iterationCount);

        void onTrainedSentence(int sentenceIndex, int sentenceCount, int correct, int total);

        void onIterationEnd(int iterationIndex, int iterationCount);

        void onAveraging();
    }

    protected static final String[] Start = {"-Start-","-Start2-"};
    protected static final String[] END = {"-END-", "-END2-"};

    protected final Map<String, String> tags;
    protected final AveragedPerceptron perceptron;

    private DataReader DR = new DataReader(Paths.get("C:\\Users\\Yang Xu\\Documents\\GitHub\\POS_Tagging\\src\\main\\resources\\wsj00-18.pos"));
    private static final Pattern NUMBER = Pattern.compile("[0-9][0-9,.]*");

    public Tagger(Map<String, Map<String, Float>> weights, Map<String, String> tags, Set<String> labels) {
        this(tags,new AveragedPerceptron(weights,labels));
    }

    private static boolean isNumber(String string) {
        return NUMBER.matcher(string).matches();
    }

    public static Tagger loadTags(Path inputPath){
        DataReader reader = new DataReader(inputPath);
        Map<String,String> tags= reader.getWordTypePairs();
        Set<String> labels = reader.getAllWordTypes();
        Map<String,Map<String,Float>> weights = reader.getWeights();
        return new Tagger(weights,tags,labels);
    }

    private static String normalize(String word) {
        if (isNumber(word))
            return "!NUMBER";
        return word.toLowerCase();
    }

    protected Tagger(Map<String,String> tags,AveragedPerceptron perceptron){
        this.tags = tags;
        this.perceptron = perceptron;
    }
    protected static List<String> getContext(List<String> words) {
        return Stream.concat(Stream.concat(Arrays.stream(Start),
                words.stream().map(Tagger::normalize)),
                Arrays.stream(END))
                .collect(Collectors.toList());
    }

    public void saveTo(Path outputPath) throws IOException{
        File output = outputPath.toFile();
        FileOutputStream fileStream = new FileOutputStream((output));
        {

        }
    }

    public List<String> tag(List<String> words){
        List<String> context = getContext(words);
        String prev = Start[0];
        String prev2 = Start[1];
        int offset = Start.length;
        List<String> res = new ArrayList<>();
        for(int i = 0;i<words.size();i++){
            String cur = words.get(i);
            String tag = this.tags.get(cur);
            if(tag == null){
                Features features = Features.getFeatures(offset+i,cur,context,prev,prev2);
                tag = this.perceptron.predict(features);
            }
            res.add(tag);
            prev2 = prev;
            prev = tag;
        }
        return res;
    }

    private  static Map<String,String> getTags(Map<String,Map<String,Integer>> counts, int frequencyThreshold, float ambiguityThreshold){
        Map<String,String> tags = new HashMap<>();
        for(Map.Entry<String,Map<String,Integer>> countEntry: counts.entrySet()){
            String word = countEntry.getKey();
            Map<String,Integer> tagCounts = countEntry.getValue();

            Map.Entry<String, Integer> maxEntry = Collections.max(tagCounts.entrySet(),
                    (entry1,entry2)->
                            Integer.compare(entry1.getValue(),entry2.getValue()));

            String tag = maxEntry.getKey();
            int mode = maxEntry.getValue();
            int n = tagCounts.values().stream().mapToInt(Integer::intValue).sum();

            if (n > frequencyThreshold
                    && (((float)mode) / n) >= ambiguityThreshold)
            {
                tags.put(word, tag);
            }
        }
        return tags;

    }


}
