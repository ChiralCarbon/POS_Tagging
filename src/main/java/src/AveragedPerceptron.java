package src;

import java.util.*;

/**
 * Created by eclipse on 25/04/2016.
 */
public class AveragedPerceptron {

    private int i = 0;
    //index number for remembering number of training carried out
    public final Map<String, Map<String, Float>> weights;
    //Map of words to their features Features.
    private HashMap<String, Features> wordToFeatures;
    public final Set<String> labels;
    private final Map<String, Map<String, Float>> totals = new HashMap<>();
    private final Map<String, Map<String, Integer>> timestamps = new HashMap<>();

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

    public void train(String word, String trueType, Features features) {
        i++;
        String prediction = predict(word);
        if (!prediction.equals(trueType)) {
            for(String feature: features.keySet()){
                Map<String, Float> weights = getOrInitialize(this.weights, feature);
                updateFeature(trueType, feature, weights.getOrDefault(trueType, 0.f), 1.f);
                updateFeature(prediction, feature, weights.getOrDefault(prediction, 0.f), -1.f);
            }
        }
    }

    private void updateFeature(String label, String feature, Float weight, float v) {
        // TODO: 2016/5/1
        int i = this.i;
        getOrInitialize(this.totals, feature)
                .merge(label, 0.f,
                        (current, initial) -> {
                            int timestamp = this.timestamps.get(feature).get(label);
                            return current + (i - timestamp) * weight;
                        });
        getOrInitialize(this.timestamps, feature).put(label, i);
        this.weights.get(feature).put(label, weight + v);
    }

    private static <T> Map<String, T> getOrInitialize
            (Map<String, Map<String, T>> weights, String feature) {
        return weights.computeIfAbsent(feature, missingFeature -> new HashMap<>());
    }

    public void averageWeights(){
        int index = this.i;
        for(Map.Entry<String,Map<String,Float>> featureEntry: this.weights.entrySet()){
            String feature = featureEntry.getKey();
            Map<String,Float> curWeights = featureEntry.getValue();
            Map<String,Float> newWeights = new HashMap<>();
            Map<String,Integer> timestamps = this.timestamps.get(feature);
            Map<String, Float> totals = this.totals.get(feature);
            for(Map.Entry<String, Float> weightEntry: curWeights.entrySet()){
                String label = weightEntry.getKey();
                float weight = weightEntry.getValue();

                int timestamp = timestamps.get(label);
                float total = totals.get(label) +(i - timestamp)* weight;
                float averaged = Math.round(total/(float)i*1000)/1000.f;
                if(averaged>0.f) newWeights.put(label,averaged);
            }
            this.weights.put(feature,newWeights);
        }
    }



}
