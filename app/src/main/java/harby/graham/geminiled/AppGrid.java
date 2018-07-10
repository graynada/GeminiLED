package harby.graham.geminiled;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * To do
 */
public class AppGrid extends AppCompatActivity {

    int led;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_grid);

        Intent intent = getIntent();
        led = intent.getIntExtra(MainActivity.LED, 255);

        GridView gridView = (GridView) findViewById(R.id.app_grid);
        gridView.setAdapter(new AppAdapter(this));
    }

    private class AppAdapter extends BaseAdapter{

        Context context;
        List<PackageInfo> apps;

        AppAdapter(Context c){
            context = c;
            apps = MainActivity.installedApps;
        }

        @Override
        public int getCount() {
            return apps.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View view, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater)
                    context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.package_item, null);
            view.setLayoutParams(new GridView.LayoutParams(GridView.AUTO_FIT, 300));
            ImageView iv = (ImageView) view.findViewById(R.id.app_image);
            iv.setImageDrawable(apps.get(position).applicationInfo.loadIcon(getPackageManager()));
            iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GeminiNotificationListener.geminiLED.ledMap.put(led, new NotificationProfile(
                            apps.get(position).applicationInfo.packageName,
                            GeminiNotificationListener.geminiLED.ledMap.get(led).getColour()));
                    finish();
                }
            });
            TextView tv = (TextView) view.findViewById(R.id.app_name);
            tv.setTextSize(10);
            tv.setText(apps.get(position).applicationInfo.loadLabel(getPackageManager()));
            return view;
        }
    }
}
