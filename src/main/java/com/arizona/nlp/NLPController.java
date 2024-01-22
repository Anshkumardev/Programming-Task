package com.arizona.nlp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class NLPController {

    private final NLPService nlpService;

    @Autowired
    public NLPController(NLPService nlpService) {
        this.nlpService = nlpService;
    }

    @GetMapping("/")
    public String showForm() {
        return "index"; // Thymeleaf template for the input form
    }

    @PostMapping("/analyze")
    public String analyzeText(@RequestParam("text") String text, Model model) {
        int sentenceCount = nlpService.countSentences(text);
        int wordCount = nlpService.countWords(text);
        int nounCount = nlpService.countNouns(text);
        int verbCount = nlpService.countVerbs(text); // Counting verbs
        int stopwordCount = nlpService.countStopwords(text); // Counting stopwords

        // Add the statistics to the model for Thymeleaf
        model.addAttribute("sentenceCount", sentenceCount);
        model.addAttribute("wordCount", wordCount);
        model.addAttribute("nounCount", nounCount);
        model.addAttribute("verbCount", verbCount); // Add verb count
        model.addAttribute("stopwordCount", stopwordCount); // Add stopword count

        return "result"; // Thymeleaf template for displaying the results
    }








}
