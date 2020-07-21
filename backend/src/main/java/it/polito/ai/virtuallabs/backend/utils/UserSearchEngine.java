package it.polito.ai.virtuallabs.backend.utils;

import org.apache.commons.text.similarity.JaroWinklerSimilarity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UserSearchEngine {

    public static final int SHUFFLES = 16;

    public static Double getSimilarity(String query, String username, String firstName, String lastName) {
        double maxScore = Double.MIN_VALUE;
        JaroWinklerSimilarity jarowinkler = new JaroWinklerSimilarity();

        List<String> a = new ArrayList<>(List.of(query.toLowerCase().split("\\s+")));
        List<String> b = List.of((username + " " + firstName + " " + lastName).toLowerCase().split("\\s+"));
        a.addAll(Collections.nCopies(b.size() - a.size(), ""));

        for(int i = 0; i < SHUFFLES; i++) {
            double score = 0;
            Collections.shuffle(a);

            for(int j = 0; j < a.size(); j++) {
                score += jarowinkler.apply(a.get(j), b.get(j));
            }

            maxScore = Math.max(maxScore, score);
        }

        return maxScore;
    }

}
