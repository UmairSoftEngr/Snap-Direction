package com.example.devilihboii.fyp;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.ImageView;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.Target;
import com.github.amlcurran.showcaseview.targets.ViewTarget;

import lt.lemonlabs.android.expandablebuttonmenu.ExpandableButtonMenu;
import lt.lemonlabs.android.expandablebuttonmenu.ExpandableMenuOverlay;


public class MainActivity extends ActionBarActivity {
    private ExpandableMenuOverlay menuOverlay;
ImageView s;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        s= (ImageView) findViewById(R.id.snapi);

        s.setBackgroundResource(R.drawable.logof);

        menuOverlay = (ExpandableMenuOverlay) findViewById(R.id.button_menu);
        Target viewTarget = new ViewTarget(R.id.button_menu, this);

        new ShowcaseView.Builder(this, true)
                .setTarget(viewTarget)
                .setStyle(R.style.CustomShowcaseTheme2)
                .setContentTitle("Menu Button")
                .setContentText("Press It to open submenu")
                .singleShot(42)
                .build();
        menuOverlay.setOnMenuButtonClickListener(new ExpandableButtonMenu.OnMenuButtonClick() {
            @Override
              public void onClick(ExpandableButtonMenu.MenuButton action) {

                switch (action) {
                    case MID:

                        Intent myIntent = new Intent(getBaseContext(),
                                MyScheduledReceiver.class);
                        myIntent.setAction(MyScheduledReceiver.ACTION_ALARM_RECEIVER);
                        boolean isWorking = (PendingIntent.getBroadcast(getBaseContext(),
                                1001, myIntent, PendingIntent.FLAG_NO_CREATE) != null);
                        if (isWorking) {
                          //  Toast.makeText(getApplicationContext(), "alarm is " + (isWorking ? "" : "not") + " working...", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(getApplicationContext(), Stopping.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

                        } else {
                            startActivity(new Intent(getApplicationContext(), MapsActivity.class));
                        }

                        menuOverlay.getButtonMenu().toggle();
                        overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);

                        break;
                    case LEFT:
                        startActivity(new Intent(getApplicationContext(), Directions.class));
                        menuOverlay.getButtonMenu().toggle();
                        overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);

                        break;
                    case RIGHT:
                        startActivity(new Intent(getApplicationContext(), ICE.class));
                        menuOverlay.getButtonMenu().toggle();
                        overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);

                        break;
                }
            }
        });

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.downi, R.anim.down);

    }


}
