package com.example.audioplayer;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ListView ListView;
    String[] item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

        getSupportActionBar().setCustomView(R.layout.actionbar);

        setContentView(R.layout.activity_main);

        ListView = findViewById(R.id.listview);

        runtimePermission();
    }

    public void runtimePermission(){
        Dexter.withContext(this).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        displaySongs();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
    }
    public ArrayList<File> findSong (File file){
        ArrayList<File> arrayList = new ArrayList<>();

        File[] files = file.listFiles();

        for (File singlefile: files){
            if (singlefile.isDirectory() && !singlefile.isHidden()){
                arrayList.addAll(findSong(singlefile));
            }else{
                if (singlefile.getName().endsWith(".mp3") || singlefile.getName().endsWith(".wav")){
                    arrayList.add(singlefile);
                }
            }
        }
        return arrayList;
    }

    void displaySongs(){
        final ArrayList<File> mysongs = findSong(Environment.getExternalStorageDirectory());

        item = new String[mysongs.size()];
        for (int i = 0; i<mysongs.size(); i++){
            item[i] = mysongs.get(i).getName().toString().replace(".mp3", "").replace(".wav", "");
        }
        /*ArrayAdapter<String> myadapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, item);
        ListView.setAdapter(myadapter);*/

        customAdapter customAdapter = new customAdapter();
        ListView.setAdapter(customAdapter);

        ListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l){
                String songName = (String) ListView.getItemAtPosition(i);
                startActivity(new Intent(getApplicationContext(), PlayMusicActivity.class)
                .putExtra("songs", mysongs)
                .putExtra("songname", songName)
                .putExtra("pos", i));
            }
        });
    }

    class customAdapter extends BaseAdapter{
        @Override
        public int getCount() {
            return item.length;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View myview = getLayoutInflater().inflate(R.layout.list_item, null);
            TextView textsong = myview.findViewById(R.id.songname);
            textsong.setSelected(true);
            textsong.setText(item[i]);

            return myview;
        }
    }
}