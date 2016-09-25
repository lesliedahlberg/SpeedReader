package com.lesliedahlberg.speedreader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

/**
 * Created by lesliedahlberg on 2016-09-24.
 */
public class Document {
    String text;
    ArrayList<String> words;
    int position;

    private int nextPosition(){
        if(position+1 >= words.size()){
            return 0;
        }else{
            return position+1;
        }
    }

    private int prevPosition(){
        if(position == 0){
            return words.size()-1;
        }else{
            return position-1;
        }
    }

    public void load(String text){
        this.text = text;
        String[] words_array = this.text.split("\\s+");
        words = new ArrayList<>(Arrays.asList(words_array));
        position = 0;
    }

    public Document(String text){
        load(text);
    }

    public int getWordCount(){
        return words.size()-1;
    }

    public int getPosition(){
        return position;
    }

    public String current(){
        return words.get(position);
    }

    public void moveNext(){
        position = nextPosition();
    }

    public void movePrev(){
        position = prevPosition();
    }


    public String next(){
        return words.get(nextPosition());
    }

    public String prev(){
        nextPosition();
        return words.get(prevPosition());
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
