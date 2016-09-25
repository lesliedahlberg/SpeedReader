package com.lesliedahlberg.speedreader;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {

    Handler mHandler;
    int wpm = 100;
    boolean playing = false;

    int FILE_REQUEST = 1;
    String dummyText = "Lorem ipsum dolor sit amet.";
    Document document;
    TextView wordView, prevWord, nextWord, wpmView;
    ImageButton playButton;
    SeekBar seekBar;



    Runnable mAutoNextWord = new Runnable() {
        @Override
        public void run() {
            try {
                next(null);
            }finally {
                mHandler.postDelayed(mAutoNextWord, WPMToMilliseconds(wpm));
            }
        }
    };

    private int WPMToMilliseconds(int wpm){
        return 60000/wpm;
    }

    private void OpenFile(){
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        startActivityForResult(intent, FILE_REQUEST);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        OpenFile();

        mHandler = new Handler();
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(this);
        playButton = (ImageButton) findViewById(R.id.buttonPlay);
        document = new Document(dummyText);
        wordView = (TextView) findViewById(R.id.wordView);
        wpmView = (TextView) findViewById(R.id.wpmView);
        updateTextView();
        updateWPMView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == FILE_REQUEST){
            if(resultCode == RESULT_OK){
                Uri uri = null;
                if (data != null) {
                    uri = data.getData();
                    try {
                        document.load(readTextFromUri(uri));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    updateTextView();
                    seekBar.setMax(document.getWordCount());

                }
            }
        }
    }

    private String readTextFromUri(Uri uri) throws IOException {
        InputStream inputStream = getContentResolver().openInputStream(uri);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            stringBuilder.append("\n");
            stringBuilder.append(line);
        }
        inputStream.close();
        return stringBuilder.toString();
    }


    private void updateTextView(){

        wordView.setText(document.current());
        seekBar.setProgress(document.getPosition());
    }

    public void next(View view){
        document.moveNext();
        updateTextView();
    }

    public void prev(View view){
        document.movePrev();
        updateTextView();
    }



    public void play(View view){
        if(playing){
            stop(null);
            playButton.setBackgroundResource(R.drawable.ic_play_circle_filled_black_24dp);
        }else{
            start(null);
            playButton.setBackgroundResource(R.drawable.ic_pause_circle_filled_black_24dp);
        }
        playing = !playing;
    }

    public void start(View view){
        mAutoNextWord.run();
    }

    public void stop (View view){
        mHandler.removeCallbacks(mAutoNextWord);
    }


    public void faster(View view){
        wpm+=100;
        updateWPMView();
    }

    public void slower(View view){
        if(wpm >= 100){
            wpm-=100;
            updateWPMView();
        }
    }

    private void updateWPMView(){
        wpmView.setText(String.valueOf(wpm));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_open:
                OpenFile();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        document.setPosition(progress);
        updateTextView();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
