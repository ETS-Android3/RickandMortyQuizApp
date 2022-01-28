package com.atuvwapps.rickandmortyultimatecharacterquiz;

import java.util.List;

public class Question {
    String title;
    String character_id;
    List<String> options;
    String answer;

    public Question(String title, String character_id, List<String> options, String answer) {
        this.title = title;
        this.character_id = character_id;
        this.options = options;
        this.answer = answer;
    }

    public Question(){}

    public String getCharacter_id() {
        return character_id;
    }

    public void setCharacter_id(String character_id) {
        this.character_id = character_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
