package com.lesliedahlberg.speedreader;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Handler mHandler;
    int interval = 1000;

    int FILE_REQUEST = 1;
    String dummyText = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Curabitur eleifend nec erat eu dictum. Praesent ac vulputate nisi. Integer volutpat, lacus id placerat dignissim, nisi magna vulputate ligula, sit amet dignissim dui nunc non arcu. Vivamus facilisis vel arcu vitae dictum. Praesent sagittis, diam a varius ultrices, turpis felis dapibus ipsum, a congue ex nulla nec arcu. Proin at lorem non quam luctus sollicitudin eu id lectus. Aenean feugiat lacus quis eros feugiat, vel faucibus nunc posuere. Praesent est quam, efficitur eu condimentum eget, consequat ut nunc. Morbi aliquam nec enim at tincidunt. Quisque lorem mauris, tristique ut odio non, elementum rutrum nunc. Praesent fermentum erat sit amet sapien aliquam consequat id in nunc. Nulla ultrices molestie pellentesque. Suspendisse in risus viverra, rutrum ipsum vel, aliquam nunc.";

    Document document;
    TextView wordView, prevWord, nextWord;
    Spinner spinner;



    Runnable mAutoNextWord = new Runnable() {
        @Override
        public void run() {
            try {
                next(null);
            }finally {
                mHandler.postDelayed(mAutoNextWord, interval);
            }
        }
    };

    private int WPMToMilliseconds(int wpm){
        return 60000/wpm;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        startActivityForResult(intent, FILE_REQUEST);

        mHandler = new Handler();

        document = new Document(dummyText);
        wordView = (TextView) findViewById(R.id.wordView);
        nextWord = (TextView) findViewById(R.id.nextWord);
        prevWord = (TextView) findViewById(R.id.prevWord);
        updateTextView();
        spinner = (Spinner) findViewById(R.id.spinnerSpeeds);
        spinner.setOnItemSelectedListener(this);
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
                        updateTextView();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

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
            stringBuilder.append(line);
        }
        inputStream.close();
        return stringBuilder.toString();
    }


    private void updateTextView(){
        prevWord.setText(document.prev());
        wordView.setText(document.current());
        nextWord.setText(document.next());
    }

    public void next(View view){
        document.moveNext();
        updateTextView();
    }

    public void previous(View view){
        document.movePrev();
        updateTextView();
    }

    public void start(View view){
        mAutoNextWord.run();
    }

    public void stop (View view){
        mHandler.removeCallbacks(mAutoNextWord);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        interval = WPMToMilliseconds(Integer.valueOf(spinner.getSelectedItem().toString()));
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

}
