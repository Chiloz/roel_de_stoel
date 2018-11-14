package com.noedel.beaconapp;

import android.Manifest;
import android.media.MediaPlayer;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.service.RunningAverageRssiFilter;

import java.util.Collection;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class BeaconActivity extends AppCompatActivity implements BeaconConsumer{

    public static final String TAG = "BeaconsEverywhere";
    public BeaconManager beaconManager;

    MediaPlayer wordt_gepakt_1;
    MediaPlayer wordt_gepakt_2;
    MediaPlayer wordt_gepakt_3;
    MediaPlayer wordt_gepakt_4;
    MediaPlayer wordt_gepakt_5;

    MediaPlayer herinnering_1;
    MediaPlayer herinnering_2;
    MediaPlayer herinnering_3;
    MediaPlayer herinnering_4;
    MediaPlayer herinnering_5;

    MediaPlayer alarm_1;
    MediaPlayer alarm_2;
    MediaPlayer alarm_3;
    MediaPlayer alarm_4;
    MediaPlayer alarm_5;

    MediaPlayer te_laat_1;
    MediaPlayer te_laat_2;
    MediaPlayer te_laat_3;
    MediaPlayer te_laat_4;
    MediaPlayer te_laat_5;

    MediaPlayer op_tijd_1;
    MediaPlayer op_tijd_2;
    MediaPlayer op_tijd_3;
    MediaPlayer op_tijd_4;
    MediaPlayer op_tijd_5;

    TextView beaconDistance;
    TextView beaconTime;
    String distance = "";
    int time = 30;

    int chooseLeaveEffect;
    int chooseAlarmEffect;
    int chooseRememberEffect;
    int chooseReturnEffect;
    int chooseTooLateEffect;

    boolean chairLeft;
    boolean soundPlayed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.INTERNET,}, 1);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        beaconManager = BeaconManager.getInstanceForApplication(this);

        beaconManager.getBeaconParsers().add(new BeaconParser()
            .setBeaconLayout("s:0-1=feaa,m:2-2=10,p:3-3:-41,i:4-20v"));
        beaconManager.setRssiFilterImplClass(RunningAverageRssiFilter.class);
        RunningAverageRssiFilter.setSampleExpirationMilliseconds(1000L);

        beaconManager.bind(this);

        beaconDistance = findViewById(R.id.textViewDistance);
        beaconTime = findViewById(R.id.textViewTime);

        wordt_gepakt_1 = MediaPlayer.create(this, R.raw.raoul_breng_je_me_weer_terug);
        wordt_gepakt_2 = MediaPlayer.create(this, R.raw.raoul_breng_me_optijd_terug);
        wordt_gepakt_3 = MediaPlayer.create(this, R.raw.raoul_ik_wil_niet_te_lang_weg_zijn);
        wordt_gepakt_4 = MediaPlayer.create(this, R.raw.raoul_oh_waar_gaan_we_heen);
        wordt_gepakt_5 = MediaPlayer.create(this, R.raw.raoul_voor_eventjes_kan_wel);

        herinnering_1 = MediaPlayer.create(this, R.raw.raoul_ik_hou_het_nog_een_paar_minuten_vol);
        herinnering_2 = MediaPlayer.create(this, R.raw.raoul_ik_voel_me_niet_zo_goed);
        herinnering_3 = MediaPlayer.create(this, R.raw.raoul_ik_wil_niet_zeuren);
        herinnering_4 = MediaPlayer.create(this, R.raw.raoul_nog_een_paar_minuten);
        herinnering_5 = MediaPlayer.create(this, R.raw.raoul_wordt_het_niet_eens_tijd);

        alarm_1 = MediaPlayer.create(this, R.raw.raoul_huilt);
        alarm_2 = MediaPlayer.create(this, R.raw.raoul_ik_mis_de_tafel);
        alarm_3 = MediaPlayer.create(this, R.raw.raoul_ik_voel_me_hier_niet_fijn);
        alarm_4 = MediaPlayer.create(this, R.raw.raoul_ik_wil_terug);
        alarm_5 = MediaPlayer.create(this, R.raw.raoul_we_zijn_al_veel_te_lang_weg);

        te_laat_1 = MediaPlayer.create(this, R.raw.raoul_beetje_laat);
        te_laat_2 = MediaPlayer.create(this, R.raw.raoul_dankjewel_denk_ik);
        te_laat_3 = MediaPlayer.create(this, R.raw.raoul_let_je_de_volgende_keer_beter_op);
        te_laat_4 = MediaPlayer.create(this, R.raw.raoul_niet_meer_doen_he);
        te_laat_5 = MediaPlayer.create(this, R.raw.raoul_volgende_keer_wat_eerder);

        op_tijd_1 = MediaPlayer.create(this, R.raw.raoul_dankjewel_voor_het_terugbrengen);
        op_tijd_2 = MediaPlayer.create(this, R.raw.raoul_fijn_dat_je_me_netjes_terugbrengt);
        op_tijd_3 = MediaPlayer.create(this, R.raw.raoul_lief_hoor);
        op_tijd_4 = MediaPlayer.create(this, R.raw.raoul_netjes_teruggebracht);
        op_tijd_5 = MediaPlayer.create(this, R.raw.raoul_toch_wel_fijn);




        chairLeft = false;
        soundPlayed = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);
        Log.d(TAG, "onDestroy: beaconManager unbound");
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (beaconManager.isBound(this)) beaconManager.setBackgroundMode(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (beaconManager.isBound(this)) beaconManager.setBackgroundMode(false);
    }

    @Override
    public void onBeaconServiceConnect() {
        final Region region = new Region("myBeacons", Identifier.parse("E584FBCB-829C-48B2-88CC-F7142b926AEA"), null, null);

        beaconManager.setMonitorNotifier(new MonitorNotifier() {
            @Override
            public void didEnterRegion(Region region) {
                try {
                    Log.d(TAG, "didEnterRegion");
                    beaconManager.startRangingBeaconsInRegion(region);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void didExitRegion(Region region) {
                try {
                    Log.d(TAG, "didExitRegion");
                    beaconManager.stopRangingBeaconsInRegion(region);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void didDetermineStateForRegion(int i, Region region) {

            }
        });

        beaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                for (Beacon oneBeacon : beacons) {
                    Log.d(TAG, "Distance: " + oneBeacon.getDistance() + "Id: " + oneBeacon.getId1() + "/" + oneBeacon.getId2() + "/" + oneBeacon.getId3());

                    if (oneBeacon.getDistance() > 1.6) {

                        if (time == 25) {
                            chooseLeaveEffect = (int) (5 * Math.random()) + 1;
                            switch (chooseLeaveEffect) {
                                case 1:
                                    wordt_gepakt_1.start();
                                    chairLeft = true;
                                    break;
                                case 2:
                                    wordt_gepakt_2.start();
                                    chairLeft = true;
                                    break;
                                case 3:
                                    wordt_gepakt_3.start();
                                    chairLeft = true;
                                    break;
                                case 4:
                                    wordt_gepakt_4.start();
                                    chairLeft = true;
                                    break;
                                case 5:
                                    wordt_gepakt_5.start();
                                    chairLeft = true;
                                    break;
                            }
                        }

                        time--;
                        distance = "Te ver!";
                        beaconTime.setText(""+time);
                        if (time < 12 && time > 10) {
                            chooseRememberEffect = (int) (5 * Math.random()) + 1;
                            soundPlayed = true;
                            switch (chooseRememberEffect) {
                                case 1:
                                    herinnering_1.start();
                                    break;
                                case 2:
                                    herinnering_2.start();
                                    break;
                                case 3:
                                    herinnering_3.start();
                                    break;
                                case 4:
                                    herinnering_4.start();
                                    break;
                                case 5:
                                    herinnering_5.start();
                                    break;
                            }
                        }

                        while (time < 0 && soundPlayed == true){
                            chooseAlarmEffect = (int) (5 * Math.random()) + 1;
                            chairLeft = true;
                            switch (chooseAlarmEffect) {
                                case 1:
                                    alarm_1.start();
                                    soundPlayed = false;
                                    alarm_1.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                        @Override
                                        public void onCompletion(MediaPlayer mp) {
                                            try {
                                                TimeUnit.MILLISECONDS.sleep(1000);
                                                soundPlayed = true;
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                    break;
                                case 2:
                                    alarm_2.start();
                                    soundPlayed = false;
                                    alarm_2.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                        @Override
                                        public void onCompletion(MediaPlayer mp) {
                                            try {
                                                TimeUnit.MILLISECONDS.sleep(1000);
                                                soundPlayed = true;
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                    break;
                                case 3:
                                    alarm_3.start();
                                    soundPlayed = false;
                                    alarm_3.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                        @Override
                                        public void onCompletion(MediaPlayer mp) {
                                            try {
                                                TimeUnit.MILLISECONDS.sleep(1000);
                                                soundPlayed = true;
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                    break;
                                case 4:
                                    alarm_4.start();
                                    soundPlayed = false;
                                    alarm_4.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                        @Override
                                        public void onCompletion(MediaPlayer mp) {
                                            try {
                                                TimeUnit.MILLISECONDS.sleep(1000);
                                                soundPlayed = true;
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                    break;
                                case 5:
                                    alarm_5.start();
                                    soundPlayed = false;
                                    alarm_5.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                        @Override
                                        public void onCompletion(MediaPlayer mp) {
                                            try {
                                                TimeUnit.MILLISECONDS.sleep(2000);
                                                soundPlayed = true;
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                    break;
                            }
                        }
                    }
                    else {
                        if (chairLeft == true) {
                            alarm_1.pause();
                            alarm_2.pause();
                            alarm_3.pause();
                            alarm_4.pause();
                            alarm_5.pause();
                            alarm_1.seekTo(0);
                            alarm_2.seekTo(0);
                            alarm_3.seekTo(0);
                            alarm_4.seekTo(0);
                            alarm_5.seekTo(0);
                            chairLeft = false;
                            if (time < 0) {
                                chooseTooLateEffect = (int) (5 * Math.random()) + 1;
                                switch (chooseTooLateEffect) {
                                    case 1:
                                        te_laat_1.start();
                                        break;
                                    case 2:
                                        te_laat_2.start();
                                        break;
                                    case 3:
                                        te_laat_3.start();
                                        break;
                                    case 4:
                                        te_laat_4.start();
                                        break;
                                    case 5:
                                        te_laat_5.start();
                                        break;
                                }
                            }
                            else {
                                chooseReturnEffect = (int) (5 * Math.random()) + 1;
                                switch (chooseReturnEffect) {
                                    case 1:
                                        op_tijd_1.start();
                                        break;
                                    case 2:
                                        op_tijd_2.start();
                                        break;
                                    case 3:
                                        op_tijd_3.start();
                                        break;
                                    case 4:
                                        op_tijd_4.start();
                                        break;
                                    case 5:
                                        op_tijd_5.start();
                                        break;
                                }
                            }
                        }
                        distance = Double.toString(oneBeacon.getDistance());
                        time = 30;
                        beaconTime.setText("Home!");
                    }

                    beaconDistance.setText(distance);

                }
            }
        });

        try {
            beaconManager.startMonitoringBeaconsInRegion(region);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}


// https://stackoverflow.com/questions/23856163/wait-until-current-media-finish-playing-before-it-play-another-in-android