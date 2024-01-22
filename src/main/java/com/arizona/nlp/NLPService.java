package com.arizona.nlp;

import jakarta.annotation.PostConstruct;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

@Service
public class NLPService {

    private SentenceModel sentenceModel;
    private TokenizerModel tokenizerModel;
    private POSModel posModel;
    private Set<String> stopWords;

    @PostConstruct
    public void initModels() {
        try {
            sentenceModel = new SentenceModel(getResourceAsStream("/models/en-sent.bin"));
            tokenizerModel = new TokenizerModel(getResourceAsStream("/models/en-token.bin"));
            posModel = new POSModel(getResourceAsStream("/models/en-pos-maxent.bin"));
            loadStopWords();
        } catch (IOException e) {
            throw new IllegalStateException("Model files not found!", e);
        }
    }

    private void loadStopWords() {
        // Initialize the stopwords set. You can expand this list or load it from a file.
        stopWords = new HashSet<>(Arrays.asList("a", "an", "the", "and", "or", "but", "because", "if", "when"));
    }

    private InputStream getResourceAsStream(String name) {
        return this.getClass().getResourceAsStream(name);
    }

    public int countSentences(String text) {
        SentenceDetectorME sentenceDetector = new SentenceDetectorME(sentenceModel);
        return sentenceDetector.sentDetect(text).length;
    }

    public int countWords(String text) {
        TokenizerME tokenizer = new TokenizerME(tokenizerModel);
        return tokenizer.tokenize(text).length;
    }

    public int countNouns(String text) {
        return countPOS(text, "NN");
    }

    public int countVerbs(String text) {
        return countPOS(text, "VB");
    }


    private int countPOS(String text, String posPrefix) {
        TokenizerME tokenizer = new TokenizerME(tokenizerModel);
        String[] tokens = tokenizer.tokenize(text);
        POSTaggerME posTagger = new POSTaggerME(posModel);
        String[] tags = posTagger.tag(tokens);

        int count = 0;
        for (String tag : tags) {
            if (tag.startsWith(posPrefix)) {
                count++;
            }
        }
        return count;
    }

    public int countStopwords(String text) {
        TokenizerME tokenizer = new TokenizerME(tokenizerModel);
        String[] tokens = tokenizer.tokenize(text);

        int stopwordsCount = 0;
        for (String token : tokens) {
            if (stopWords.contains(token.toLowerCase())) {
                stopwordsCount++;
            }
        }
        return stopwordsCount;
    }


    public Map<String, Integer> readCSVStats() throws IOException {
        Map<String, Integer> csvStats = new HashMap<>();
        ClassPathResource resource = new ClassPathResource("aggregated_results.csv");
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            reader.readLine(); // Skip header line
            String line = reader.readLine();
            if (line != null) {
                String[] values = line.split(",");
                csvStats.put("sentenceCount", Integer.parseInt(values[0]));
                csvStats.put("wordCount", Integer.parseInt(values[1]));
                csvStats.put("nounCount", Integer.parseInt(values[2]));
                csvStats.put("verbCount", Integer.parseInt(values[3]));
                csvStats.put("stopwordCount", Integer.parseInt(values[4]));
            }
        }
        return csvStats;
    }

}

