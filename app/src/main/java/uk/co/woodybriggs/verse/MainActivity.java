package uk.co.woodybriggs.verse;

import android.app.ListActivity;
import android.database.DataSetObserver;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends ListActivity {

    MediaPlayer mp = new MediaPlayer();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        updateListView();

        playPause = (ImageButton) findViewById(R.id.PlayPause);
        playPause.setImageResource(R.drawable.play);
    }

    /* ================ UI GLOBAL VARIABLES ================ */
    List<String> songs = new ArrayList<String>();
    ImageButton playPause;

    /* ================ UI SPECIFIC CODE ================ */
    private void updateListView() {

        File home = new File(Environment.getExternalStorageDirectory().getPath() + "/Music/");
        if (home.listFiles(new RsfFilter()).length > 0) {
            for (File file : home.listFiles(new RsfFilter())) {
                songs.add(file.getName());
            }
        }

        ArrayAdapter<String> songList = new ArrayAdapter<String>(this, R.layout.song_item, songs);

        ListView lv = (ListView) findViewById(android.R.id.list);

        try {
            lv.setAdapter(songList);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onListItemClick(ListView list, View view, int position, long id) {

        String path = Environment.getExternalStorageDirectory().getPath() + "/Music/" + songs.get(position);

        FormatRSF rsf = new FormatRSF(path, getApplicationInfo().dataDir);

        try {
            mp.reset();
            mp.setDataSource(rsf.getOutputPath());
            mp.prepare();
            mp.start();
            setPlayButton(true);
        } catch (IOException e) {
            Log.v(getString(R.string.app_name), e.getMessage());
        }
    }

    public void playPauseAction(View view) {
        if (mp.isPlaying()) {
            mp.pause();
            setPlayButton(false);
        } else {
            mp.start();
            setPlayButton(true);
        }
    }

    public void setPlayButton(boolean state) {
        if (state == true) {
            playPause.setImageResource(R.drawable.pause);
        } else {
            playPause.setImageResource(R.drawable.play);
        }
    }

    /* ================ MISC CONVERSION FUNCTIONS ================ */

    public static byte[] intToByteArray(int integer) {
        ByteBuffer buf = ByteBuffer.allocate(4);
        buf.order(ByteOrder.LITTLE_ENDIAN);
        buf.putInt(integer);
        byte[] bytes = buf.array();
        return bytes;
    }

    private static int byteArrayToInt(byte[] b) {
        final ByteBuffer bb = ByteBuffer.wrap(b);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        return bb.getInt();
    }

    private static long byteArrayToLong(byte[] b) {
        final ByteBuffer bb = ByteBuffer.wrap(b);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        return bb.getLong();
    }

}


