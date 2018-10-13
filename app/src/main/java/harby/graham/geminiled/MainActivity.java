package harby.graham.geminiled;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    static final String FILTER = "harby.graham.geminiled.GNL_MA";
    static final String COMMAND = "command";
    static final String UPDATE = "update";
    static final String LED = "led";
    static final String PACKAGE_NAME = "packageName";
    static final String COLOUR = "colour";

    private NotificationReceiver notificationReceiver;
    static List<PackageInfo> installedApps;
    GeminiLED geminiLED;
    static TextView[] colours;
    private TextView[] packages;
    private ImageView[] icons;
    private Switch keyboardLEDSwitch, coverLEDSwitch;
    private ImageView settings;
    static int[] colors = {Color.BLACK, Color.BLUE, Color.GREEN, Color.CYAN, Color.RED,
            Color.MAGENTA, Color.YELLOW, Color.WHITE};
    static int updated = 255;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String notificationListenerString = Settings.Secure.getString(this.getContentResolver(),"enabled_notification_listeners");

        prepUI();
        if (notificationListenerString == null ||
                !notificationListenerString.contains(getPackageName()) ||
                notificationListenerString.equals(""))
        {
            noPermission();
        }
        else{
            startService();
            if(geminiLED != null){
                loadUI();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (notificationReceiver != null) {
            unregisterReceiver(notificationReceiver);
        }
        finish();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(geminiLED != null) {
            loadUI();
        }
        sendCommand(UPDATE);
    }

    private void noPermission(){

        //https://www.mkyong.com/android/android-alert-dialog-example/
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // set title
        alertDialogBuilder.setTitle("GeminiLED permissions");

        // set dialog message
        alertDialogBuilder
                .setMessage("GeminiLED needs notification access to function.  Please enable and then restart the app.")
                .setCancelable(false)
                .setPositiveButton("Enable",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        startActivity(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS));
                        MainActivity.this.finish();
                    }
                })
                .setNegativeButton("Exit",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        finish();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    private void startService(){

        notificationReceiver = new NotificationReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(FILTER);
        registerReceiver(notificationReceiver, filter);

        startService(new Intent(this, GeminiNotificationListener.class));

        geminiLED = GeminiNotificationListener.geminiLED;
        installedApps = getInstalledApps(this);
        Collections.sort(installedApps, myComparator);
    }

    private void prepUI() {

        colours = new TextView[5];
        packages = new TextView[5];
        icons = new ImageView[5];

        colours[0] = (TextView) findViewById(R.id.colour_1);
        colours[1] = (TextView) findViewById(R.id.colour_2);
        colours[2] = (TextView) findViewById(R.id.colour_3);
        colours[3] = (TextView) findViewById(R.id.colour_4);
        colours[4] = (TextView) findViewById(R.id.colour_5);

        packages[0] = (TextView) findViewById(R.id.package_1);
        packages[1] = (TextView) findViewById(R.id.package_2);
        packages[2] = (TextView) findViewById(R.id.package_3);
        packages[3] = (TextView) findViewById(R.id.package_4);
        packages[4] = (TextView) findViewById(R.id.package_5);

        icons[0] = (ImageView) findViewById(R.id.icon_1);
        icons[1] = (ImageView) findViewById(R.id.icon_2);
        icons[2] = (ImageView) findViewById(R.id.icon_3);
        icons[3] = (ImageView) findViewById(R.id.icon_4);
        icons[4] = (ImageView) findViewById(R.id.icon_5);

        coverLEDSwitch = (Switch) findViewById(R.id.coverledsw);
        keyboardLEDSwitch = (Switch) findViewById(R.id.kbledsw);
        settings = (ImageView) findViewById(R.id.settings);
    }

    void loadUI(){

        for(int i: geminiLED.ledMap.keySet()){
            int j = 5 - i;
            colours[j].setBackgroundColor(colors[geminiLED.ledMap.get(i).getColour().getInt()]);
            colours[j].setOnClickListener(new ColourClick(i));
            packages[j].setTextSize(20);
            packages[j].setTextColor(Color.WHITE);
            try {
                icons[j].setImageDrawable(getPackageManager().getApplicationIcon(geminiLED.ledMap.get(i).getPackName()));
                icons[j].setOnClickListener(new IconClick(i));
                icons[j].setOnLongClickListener(new IconLongClick(i));
                packages[j].setText(getPackageManager().getApplicationLabel(getPackageManager().getApplicationInfo(geminiLED.ledMap.get(i).getPackName(), 0)));
            }
            catch (PackageManager.NameNotFoundException e){
                geminiLED.ledMap.put(i, new NotificationProfile(GeminiNotificationListener.defaultPackage, Colour.WHITE));
                icons[j].setImageResource(R.mipmap.ic_launcher);
                icons[j].setOnClickListener(new IconClick(i));
                packages[j].setText(getApplicationContext().getPackageName());
                Log.e(GeminiLED.TAG, "Package name not found");
            }
        }

        coverLEDSwitch.setChecked(geminiLED.coverLEDsOn);
        coverLEDSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                geminiLED.coverLEDsOn = coverLEDSwitch.isChecked();
                sendCommand(UPDATE);
            }
        });

        keyboardLEDSwitch.setChecked(geminiLED.keyboardLEDon);
        keyboardLEDSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                geminiLED.keyboardLEDon = keyboardLEDSwitch.isChecked();
                sendCommand(UPDATE);
            }
        });
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(MainActivity.this, AdvancedSettings.class);
                //startActivity(intent);
            }
        });

    }

    private void sendCommand(String command){
        Intent gnl = new Intent(GeminiNotificationListener.FILTER);
        gnl.putExtra(COMMAND, command);
        sendBroadcast(gnl);
    }

    class NotificationReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            int led = intent.getIntExtra(LED, 256);
            String pack = intent.getStringExtra(PACKAGE_NAME);
            int colour = intent.getIntExtra(COLOUR, 0);
            geminiLED.ledMap.put(led, new NotificationProfile(pack, new Colour(colour)));
        }
    }

    //https://code.i-harness.com/en/q/292242
    List<PackageInfo> getInstalledApps(Context ctx) {
        final PackageManager packageManager = ctx.getPackageManager();

        final List<PackageInfo> allInstalledPackages = packageManager.getInstalledPackages(PackageManager.GET_META_DATA);
//        final List<PackageInfo> notificationPackages = packageManager.getInstalledPackages(PackageManager.GET_PERMISSIONS);
        final List<PackageInfo> filteredPackages = new ArrayList<>();

        Drawable defaultActivityIcon = packageManager.getDefaultActivityIcon();

        for(PackageInfo each : allInstalledPackages) {
            if(ctx.getPackageName().equals(each.packageName)) {
                continue;  // skip own app
            }

            try {
                // add only apps with application icon
                Intent intentOfStartActivity = packageManager.getLaunchIntentForPackage(each.packageName);
                if(intentOfStartActivity == null)
                    continue;

                Drawable applicationIcon = packageManager.getActivityIcon(intentOfStartActivity);
                if(applicationIcon != null && !defaultActivityIcon.equals(applicationIcon)) {
                    filteredPackages.add(each);
                }
            } catch (PackageManager.NameNotFoundException e) {
                Log.e(GeminiLED.TAG, "Unknown package name " + each.packageName);
            }
        }

        return filteredPackages;
    }

    //Method reserved for later version
//    void makeGreyscale(){
//        ColorMatrix matrix = new ColorMatrix();
//        matrix.setSaturation(0);
//        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
//        imageView.setColorFilter(filter);
//    }

    Comparator<PackageInfo> myComparator = new Comparator<PackageInfo>() {
        public int compare(PackageInfo obj1, PackageInfo obj2) {
            return obj1.applicationInfo.loadLabel(getPackageManager()).toString().compareTo(obj2.applicationInfo.loadLabel(getPackageManager()).toString());
        }
    };

    private class IconClick implements View.OnClickListener{

        final int led;

        IconClick(int i){
            led = i;
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(MainActivity.this, AppGrid.class);
            intent.putExtra(LED, led);
            startActivity(intent);
        }
    }

    private class IconLongClick implements View.OnLongClickListener{

        final int led;

        IconLongClick(int i){
            led = i;
        }

        @Override
        public boolean onLongClick(View v) {
            //https://www.mkyong.com/android/android-alert-dialog-example/
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);

            // set title
            alertDialogBuilder.setTitle("Clear LED?");

            // set dialog message
            alertDialogBuilder
                    .setMessage("Do you wish to clear the app assignment to the LED?")
                    .setCancelable(false)
                    .setPositiveButton("Clear",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int id) {
                            geminiLED.ledMap.put(led, new NotificationProfile(GeminiNotificationListener.defaultPackage, Colour.WHITE));
                            //finish();
                        }
                    })
                    .setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int id) {
                            // if this button is clicked, just close
                            // the dialog box and do nothing
                            //finish();
                        }
                    });

            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();

            // show it
            alertDialog.show();
            return false;
        }
    }

    private class ColourClick implements View.OnClickListener {

        final int led;

        ColourClick(int i){
            led = i;
        }

        @Override
        public void onClick(View v) {
            final Dialog colourSelect = new Dialog(MainActivity.this);
            colourSelect.requestWindowFeature(Window.FEATURE_NO_TITLE);
            colourSelect.setContentView(R.layout.dialog_colour_select);
            TextView[] colourBoxes = new TextView[8];
            colourBoxes[1] = (TextView) colourSelect.findViewById(R.id.blue);
            colourBoxes[2] = (TextView) colourSelect.findViewById(R.id.green);
            colourBoxes[3] = (TextView) colourSelect.findViewById(R.id.cyan);
            colourBoxes[4] = (TextView) colourSelect.findViewById(R.id.red);
            colourBoxes[5] = (TextView) colourSelect.findViewById(R.id.magenta);
            colourBoxes[6] = (TextView) colourSelect.findViewById(R.id.yellow);
            colourBoxes[7] = (TextView) colourSelect.findViewById(R.id.white);
            for(int i = 1; i < 8; i++){
                final int I = i;
                colourBoxes[i].setBackgroundColor(MainActivity.colors[i]);
                colourBoxes[i].setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        GeminiNotificationListener.geminiLED.ledMap.get(led).setColour(new Colour(I));
                        colours[5 - led].setBackgroundColor(colors[I]);
                        MainActivity.updated = led;
                        colourSelect.dismiss();
                    }
                });
            }
            colourSelect.show();
        }
    }

}
