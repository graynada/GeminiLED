package harby.graham.geminiled;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    TextView panel;
    String text = "Beta version of GeminiLED\n\n" +
            "This version will flash centre LED for notifications colours as follows:\n" +
            "Viber - magenta\n" + "WhatsApp - green\n" + "K9 Mail - red\n" +
            "Handcent Next SMS - blue\n" + "Dialer (call and missed call) - cyan\n\n" +
            "NOTE: This app needs access to notifications to work.\n" +
            "Select Settings, Apps, Settings (gear wheel), Special access, Notification access " +
            "slider on for GeminiLED\n\n" + "User configuration to be developed";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        panel = (TextView) findViewById(R.id.panel);
        panel.setText(text);
        startService();
    }

    private void startService(){
        startService(new Intent(this, GeminiNotificationListener.class));
    }

}
