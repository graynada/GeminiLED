package harby.graham.geminiled;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class AdvancedSettings extends AppCompatActivity {

    TextView[] ledTabs;
    int selectedTab;
    final int GEMINI_RED = 0x2C;
    final int GEMINI_GREEN = 0x2A;
    final int GEMINI_BLUE = 0x25;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advanced_settings);

        selectedTab = 0;
        prepUI();
        loadUI();
    }

    void prepUI(){
        ledTabs = new TextView[6];

        ledTabs[0] = (TextView) findViewById(R.id.kbledtab);
        ledTabs[1] = (TextView) findViewById(R.id.led1tab);
        ledTabs[2] = (TextView) findViewById(R.id.led2tab);
        ledTabs[3] = (TextView) findViewById(R.id.led3tab);
        ledTabs[4] = (TextView) findViewById(R.id.led4tab);
        ledTabs[5] = (TextView) findViewById(R.id.led5tab);

        for(int i = 0; i < ledTabs.length; i++){
            ledTabs[i].setOnClickListener(new TabClick(i));
            ledTabs[i].setTextColor(Color.WHITE);
        }
    }

    void loadUI(){
        for(int i = 0; i < ledTabs.length; i++){
            System.out.println("absolute = " + Math.abs(selectedTab - i));
            int delta = (Math.abs(selectedTab - i));
                float factor =  (float) delta / 5;
            factor = 1 - factor;
            System.out.println("factor = " + factor);
                int r = (int) (GEMINI_RED * factor);
                int g = (int) (GEMINI_GREEN * factor);
                int b = (int) (GEMINI_BLUE * factor);
                ledTabs[i].setBackgroundColor(Color.rgb(r, g, b));
            if(i == selectedTab){
                //TO DO
            }
        }
    }

    private class TabClick implements View.OnClickListener{

        int tab;

        TabClick(int i){
            tab = i;
        }

        @Override
        public void onClick(View v) {
            selectedTab = tab;
            loadUI();
        }
    }
}
