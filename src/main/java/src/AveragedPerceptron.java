package src;

import java.util.*;

/**
 * Created by eclipse on 25/04/2016.
 */
public class AveragedPerceptron {

    public final Map<String, Map<String, Float>> weights;
    //Map of words to their features Features.
    private HashMap<String, Features> wordToFeatures;
    public final Set<String> labels;

    public AveragedPerceptron(Map<String, Map<String, Float>> weights, Set<String> labels) {
        this.weights = weights;
        this.labels = labels;
    }

    public String predict(String word) {
        return predict(wordToFeatures.get(word));
    }

    public String predict(Features features) {
        Map<String, Float> scores = new HashMap<>();
        for (Map.Entry<String, Integer> featureEntry : features.entrySet()) {
            String feature = featureEntry.getKey();
            int value = featureEntry.getValue();
            Map<String, Float> curWeights = weights.get(feature);
            if (curWeights == null || value == 0) {
                continue;
            }
            for (Map.Entry<String, Float> curEntry : curWeights.entrySet()) {
                String label = curEntry.getKey();
                Float weight = curEntry.getValue();
                scores.merge(label, 0f, (current, initial) -> current + value * weight);
            }
        }
        return Collections.max(this.labels, (String label1, String label2) -> Float.compare(scores.getOrDefault(label1, Float.MIN_VALUE),
                scores.getOrDefault(label2, Float.MIN_VALUE)));
    }

    public void train(String word, String trueType, DataReader data) {
        // TODO: 27/04/2016
        String prediction = predict(word);
        if (!prediction.equals(trueType)) {

        }
    }


}
