package src;

import java.util.HashMap;
import java.util.List;

/**
 * Created by eclipse on 27/04/2016.
 */
public class Features extends HashMap<String, Integer> {

    private static String suffix(String word) {
        return word.substring(Math.max(0, word.length() - 3));
    }

    public void addFeature(String... arguments) {
        String key = String.join(" ", arguments);
        merge(key, 1, (current, initial) -> current + 1);
    }

    public static Features getFeatures(int i, String word, List<String> context, String prev, String prev2) {
        Features features = new Features();
        features.addFeature("bias");
        features.addFeature("i suffix", suffix(word));
        features.addFeature("i pref1", String.valueOf(word.charAt(0)));
        features.addFeature("i-1 tag", prev);
        features.addFeature("i-2 tag", prev2);
        features.addFeature("i word", context.get(i));
        features.addFeature("i-1 tag+i word", prev, context.get(i));
        features.addFeature("i-1 word", context.get((i - 1)));
        features.addFeature("i-1 suffix", suffix(context.get(i - 1)));
        features.addFeature("i-2 word", context.get(i - 2));
        features.addFeature("i+1 word", context.get(i + 1));
        features.addFeature("i+1 suffix", suffix(context.get(i + 1)));
        features.addFeature("i+2 word",context.get(i+2));
        return features;
    }
}
