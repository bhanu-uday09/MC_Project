#include <jni.h>
#include <string>
#include <vector>
#include <sstream>
#include <iostream>
#include <algorithm>
#include <cctype>
#include <unordered_set>
#include <map>

// Set of stopwords to be removed
const std::unordered_set<std::string> stopwords =
        {"i", "you", "he", "she", "it", "we", "they", "me", "him", "her", "us", "them",
         "my", "your", "his", "her", "its", "our", "their", "mine", "yours", "hers",
         "ours", "theirs", "myself", "yourself", "himself", "herself", "itself",
         "ourselves", "yourselves", "themselves", "this", "that", "these", "those",
         "here", "there", "where", "when", "why", "how", "which", "who"
        };


// Function to preprocess text (remove stopwords, convert to lowercase, etc.)
std::string preprocessText(const std::string& text) {
    std::string result = text;

    // Convert text to lowercase
    std::transform(result.begin(), result.end(), result.begin(), ::tolower);

    // Remove stopwords
    std::istringstream iss(result);
    std::string word;
    result = "";
    while (iss >> word) {
        if (stopwords.find(word) == stopwords.end()) {
            result += word + " ";
        }
    }

    return result;
}


// Function to split text into sentences
std::vector<std::string> splitIntoSentences(const std::string& text) {
    std::vector<std::string> sentences;
    std::istringstream iss(text);
    std::string sentence;
    while (std::getline(iss, sentence, '.')) {
        sentences.push_back(sentence);
    }
    return sentences;
}

// Function to summarize text into simple points
std::string summarizeText(const std::string& inputText, int maxSummarySize) {
    // Preprocess text
    std::string preprocessedText = preprocessText(inputText);

    // Split preprocessed text into sentences
    std::vector<std::string> sentences = splitIntoSentences(preprocessedText);

    // Create a map of word frequencies
    std::map<std::string, int> wordFrequencies;
    for (const auto& sentence : sentences) {
        std::istringstream iss(sentence);
        std::string word;
        while (iss >> word) {
            wordFrequencies[word]++;
        }
    }

    // Filter stopwords from word frequencies
    for (const auto& stopword : stopwords) {
        wordFrequencies.erase(stopword);
    }

    // Sort word frequencies by frequency
    std::vector<std::pair<std::string, int>> sortedWordFrequencies(wordFrequencies.begin(), wordFrequencies.end());
    std::sort(sortedWordFrequencies.begin(), sortedWordFrequencies.end(), [](const auto& a, const auto& b) {
        return a.second > b.second;
    });

    // Creation of summary
    std::string summary;
    int sentencesAdded = 0;
    for (const auto& sentence : sentences) {
        // Add sentence if it contains a high-frequency word
        for (const auto& [word, _] : sortedWordFrequencies) {
            if (sentence.find(word) != std::string::npos) {
                summary += sentence + ". ";
                sentencesAdded++;
                break;
            }
        }
        // Break if reached max summary size
        if (sentencesAdded >= maxSummarySize) {
            break;
        }
    }

    return summary;
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_msr_1mc24_mc_1newsapp_DescriptionKt_preprocessText(JNIEnv *env, jclass clazz,
                                                            jstring input_text) {
    const char *inputTextPtr = env->GetStringUTFChars(input_text, nullptr);
    if (inputTextPtr == nullptr) {
        return env->NewStringUTF("");
    }

    std::string result = summarizeText(inputTextPtr, 2);
    env->ReleaseStringUTFChars(input_text, inputTextPtr);
    return env->NewStringUTF(result.c_str());
}

