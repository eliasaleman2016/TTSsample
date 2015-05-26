package com.example.sharma.ttssample;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


/**
 * Created by sharma on 24/5/15.
 * this class have several attempts to capture users voise
 */
public class MainActivity extends ActionBarActivity {

    //This class provides access to the speech recognition service
    private SpeechRecognizer mSpeechRecognizer;
    private Intent mSpeechRecognizerIntent;

    //checking whether the system have ability to listen
    private boolean islistening;

    //handler to repeat the task(listening)
    private Handler handler;

    //this is for handler
    private Runnable runnable;

    //boolean flag to start the task(listening) depending on the result
    private  boolean isItReady;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        isItReady = true;
//        promptSpeechInput();

        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
                this.getPackageName());


        SpeechRecognitionListener listener = new SpeechRecognitionListener();
        mSpeechRecognizer.setRecognitionListener(listener);

       if (mSpeechRecognizer.isRecognitionAvailable(this)) {
            islistening = true;
        }

        /**
         * here am repeating handler for every  seconds, you can change this according your need. thanks
         */
        runnable = new Runnable() {
            @Override
            public void run() {
                if (islistening && isItReady) {
                    mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
                    isItReady = false;
                }
                handler.removeCallbacks(runnable);
                handler.postDelayed(runnable, 2000);
            }
        };
        handler = new Handler();
        handler.removeCallbacks(runnable);
        handler.postDelayed(runnable, 2000);


    }

    //button click event
    public void apply(View view) {
        String str = mSpeechRecognizer.isRecognitionAvailable(this) ? "Yes" : "No";
        showToastMessage("is this device supports voice recognition: " + str);
        if (islistening) {
           // mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
        }
    }

    //toast message
    void showToastMessage(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSpeechRecognizer != null)
        {
            mSpeechRecognizer.destroy();
        }
        handler.removeCallbacks(runnable);
    }


    /**
     * Created by sharma on 24/5/15.
     * This is the class with which we will get required out put results from RecognitionListener
     */
    public class SpeechRecognitionListener implements RecognitionListener {

        private String TAG = "GVsTTS";

        @Override
        public void onError(int error) {
            Log.d(TAG, "onError " + error); //$NON-NLS-1$
            isItReady = true;
        }

        @Override
        public void onPartialResults(Bundle partialResults) {
            ArrayList<String>results = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            Log.d(TAG, "onPartialResults "+ results.get(0));
        }

        @Override
        public void onBufferReceived(byte[] buffer) {
            Log.d(TAG, "onBufferReceived " + buffer);

        }

        @Override
        public void onEvent(int eventType, Bundle params) {
            Log.d(TAG, "onEvent "+eventType +" : "+params);

        }

        @Override
        public void onResults(Bundle results) {
            Log.d(TAG, "onResults " + results);
            ArrayList<String> speech = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            showToastMessage(speech.get(0));
            isItReady = true;
        }

        @Override
        public void onReadyForSpeech(Bundle params) {
            Log.d(TAG, "onPartialResults ");
        }

        @Override
        public void onBeginningOfSpeech() {
            Log.d(TAG, "onBeginningOfSpeech");
            showToastMessage(" ready start..!");
        }

        @Override
        public void onEndOfSpeech() {
            Log.d(TAG, "onEndOfSpeech");
            showToastMessage(" end of speech");
        }

        @Override
        public void onRmsChanged(float rmsdB) {
            Log.d(TAG, "onRmsChanged " + rmsdB);
        }
    }


}
