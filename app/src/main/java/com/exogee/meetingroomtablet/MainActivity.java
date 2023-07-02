package com.exogee.meetingroomtablet;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    public static final int STATUS_CHECKS_PER_SECOND = 2;
    public static final int POLL_DELAY = 1000 / STATUS_CHECKS_PER_SECOND;

    protected WebView browser;
    protected Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Hide the status bar and navigation bars
        getWindow().getDecorView().getWindowInsetsController().hide(
                android.view.WindowInsets.Type.statusBars()
                        | android.view.WindowInsets.Type.navigationBars()
        );

        browser = new WebView(getApplicationContext());

        // Needed for the app to work.
        browser.getSettings().setDomStorageEnabled(true);
        browser.getSettings().setJavaScriptEnabled(true);

        // This should work to enable prompt() to work according to the docs but doesn't:
        // browser.setWebChromeClient(new WebChromeClient());

        setContentView(browser);

        // TODO: Handle JS Prompt events so we don't have to hard code the key here.
        browser.loadUrl("https://app.meetingroom365.com/?key=[KEY HERE]");

        // Show yellow on startup
        LEDCommand.execute(LEDCommand.SHOW_YELLOW);

        pollForMeetingRoomStatusUpdates();
    }

    protected void pollForMeetingRoomStatusUpdates() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run()
            {
                applyMeetingRoomStatusToLEDs();
            }
        },
                0, // Start immediately
                POLL_DELAY // Check as configured above
        );
    }

    protected final static String JS_EVAL = "({" +
            "available: $('#iframe').contents().find('body.available').length ? true : false," +
            "occupied: $('#iframe').contents().find('body.occupied').length ? true : false," +
            "loading: $('#iframe').contents().find('body.loading').length ? true : false," +
            "});";

    protected void applyMeetingRoomStatusToLEDs() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                browser.evaluateJavascript(JS_EVAL, new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String s) {
                        try {
                            JSONObject result = new JSONObject(s);

                            if (result.getBoolean("loading")) {
                                Log.d("status", "Loading: showing blue");
                                LEDCommand.execute(LEDCommand.SHOW_BLUE);
                            } else if (result.getBoolean("occupied")) {
                                Log.d("status", "Occupied: showing red");
                                LEDCommand.execute(LEDCommand.SHOW_RED);
                            } else if (result.getBoolean("available")) {
                                Log.d("status", "Available: showing green");
                                LEDCommand.execute(LEDCommand.SHOW_GREEN);
                            } else {
                                Log.d("status", "Unknown: showing yellow");
                                LEDCommand.execute(LEDCommand.SHOW_YELLOW);
                            }
                        } catch (JSONException e) {
                            Log.e("jsonError", "JSON Exception: ", e);
                        }
                    }
                });
            }
        });
    }
}