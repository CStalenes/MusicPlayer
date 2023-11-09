package com.example.musicplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    /** ajout des var glo*/

    private RecyclerView rvSong;

    private ImageView btnPrevious, btnPlay, btnivNext;
    private TextView tvSongTitle, tvCurrentPos, tvTotalDuration;

    private SeekBar sbPosition;

    private MediaPlayer mediaPlayer;

    private ArrayList<ModeleSong> songArrayList;

    //pour l instantiation de l adapteur
    private AdapterSong adapterSong;

    private LinearLayoutManager linearLayoutManager;

    //pour cree new case qui met item l'un apres l autre
    //permet de gerer ajout de cet item

    //calcul du temps dans la seekbar
    private double currentPosition, totalDuration;

    /**Check permissin */

    //R.id.

    /**Init method to inialize all widgets*/
    private void init(){

      linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
      rvSong = findViewById(R.id.rv_song);

      btnPrevious = findViewById(R.id.iv_btn_previous);
      btnPlay = findViewById(R.id.iv_btn_play);
      btnivNext = findViewById(R.id.iv_btn_next);

      tvSongTitle = findViewById(R.id.tv_song_title);
      tvCurrentPos = findViewById(R.id.tv_current_pos);
      tvTotalDuration = findViewById(R.id.tv_total_duration);

      sbPosition = findViewById(R.id.sb_position);


      mediaPlayer = new MediaPlayer();
      songArrayList = new ArrayList<>();
    }
    //si ya permission dans notre app et

    public void checkPermission(){
        if ((ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_MEDIA_AUDIO) == PackageManager.PERMISSION_DENIED)) {
            ActivityCompat.requestPermissions
                    (MainActivity.this, new String[]{Manifest.permission.READ_MEDIA_AUDIO},100);
        } else {
            Toast.makeText(this, "Permission already granted", Toast.LENGTH_SHORT).show();
        }
    }
    //on passe dans tb de string tt les demande que'on veut qu il fasse
    //on va checké par pop up


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //on below line we are chacking for the req code
        if(requestCode == 100){
            //Check if perm are granted
            if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Read music folder is granted", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(this, "Read music folder is denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /** Methode to get the audio files from the terminal(portable)**/

    private void getAudioFiles(){
        //Content resolver = intent pour les data audio
        ContentResolver contentResolver = getContentResolver();

        //External content uri = partion pour séparé OS des data par exemple
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        //MediaStore c'est l'endroit/dossier ou on a les medias tel que lees download img, music

        String[] projections = {
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.ALBUM_ID
        };

        String selection = MediaStore.Audio.Media.IS_MUSIC+ "!=0";
        /// ==> if(is_Music !=0){} c'est une chaine

        Cursor cursor = contentResolver.query(uri, projections, selection, null, null);
        //selctionArgs = qd cursor null il fill par qlq chose par default exemple toto

        //a chaque deplacement du cursor on change les infos

        //moveToFirst au cas si ça plante remettre le cursor en debut de lst de music
        if(cursor!= null && cursor.moveToFirst())
        {
            //tant que le cursor peut bouger à celui d'après
            do{
                String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
                String artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                String album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));
                String duration = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
                String data = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));

                long album_id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));
                Uri uriCoverFolder = Uri.parse("content://media/external/audio/albumart");
                //on parse car on ne peut pas entré des uri dans le code dc on le transforme en str

                //on doit faire croisement entre cheminImge et album
                Uri uriAlbumArt = ContentUris.withAppendedId(uriCoverFolder, album_id);

               ModeleSong modeleSong = new ModeleSong();
               //Associ les data ci dessous  au arraylist en usant le modele

                modeleSong.setSongTitle(title);
                modeleSong.setSongAlbum(album);
                modeleSong.setSongArtist(artist);
                modeleSong.setSongDuration(duration);
                modeleSong.setSongUri(Uri.parse(data));
                modeleSong.setSongCover(uriAlbumArt);

                songArrayList.add(modeleSong);

            }while(cursor.moveToNext());
        }
        // IS_Music = séparation divers bruit c-a-d music sonnerie et autre
        //getColumnIndexOrThrow = permet de gerer si ya des exception
        //uri cvFolder = chemin ou sont save image


        adapterSong = new AdapterSong(MainActivity.this,songArrayList);

        //dis de creer des ligne automa avt de rajouter un new layout d item via setadapter logique non
        rvSong.setLayoutManager(linearLayoutManager);

        rvSong.setAdapter(adapterSong);

    }

    //COMMENTER
    private void manageRv(){
        adapterSong = new AdapterSong(MainActivity.this, songArrayList);
        rvSong.setLayoutManager(linearLayoutManager);
        rvSong.setAdapter(adapterSong);


        adapterSong.setMyOnItemClickListener(new AdapterSong.MyOnItemClickListener() {
            @Override
            public void onItemClick(int position, View view) {
                playSong(position);
            }
        });

    }

    //COMMENTER
    private void playSong(int position) {

        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(this, songArrayList.get(position).getSongUri());
            mediaPlayer.prepare();//pour mettre dans le cache afin que ça démarre bien
            mediaPlayer.start();

            btnPlay.setImageResource(R.drawable.ic_pause_48_w);
            tvSongTitle.setText(songArrayList.get(position).getSongTitle());
        } catch (IOException e) {
            e.printStackTrace();
            //pour recup dans le log error
            //on pourra gerer d ou vient ts les erreur
            //alors que l autre gere que input output

            songProgress();//setSong Method
        }
    }


    private void songProgress(){
        currentPosition = mediaPlayer.getCurrentPosition();
        totalDuration = mediaPlayer.getDuration();
        //Assignation aux test view des temps qui leurs sont appartis

        //tvCurrentPos.setText(timerConvertion((long) currentPosition));
        //tvCurrentPos.setText((int) TimeUnit.MICROSECONDS.toHours((long) currentPosition));
        tvCurrentPos.setText(timerConvertion((long) currentPosition));
        tvTotalDuration.setText(timerConvertion((long) totalDuration));

        sbPosition.setMax((int) totalDuration);

        //Gestion du thread qui va s'occuper du temps du "temps reel"
        final Handler handler = new Handler();
        //



        Runnable runnable =  new Runnable() {
            @Override
            public void run() {

                //ecrire tt ca avt try catch
                try {
                    currentPosition = mediaPlayer.getCurrentPosition();
                    tvCurrentPos.setText(timerConvertion((long) currentPosition));
                    sbPosition.setProgress((int) currentPosition);
                    handler.postDelayed(this, 1000);
                }catch (IllegalThreadStateException ie)
                {
                    ie.printStackTrace();
                }
            }
        };
        handler.postDelayed(runnable, 1000);
        //le thread a cote va nous rapporter les autre info
    }


    //methode pour convert milliSec en Str
    public String timerConvertion(long value){
        String songDuration;

        int dur = (int) value; //duration in millis
        int hrs = dur/3600000;
        int mns = (dur/60000);
        int sec = (dur/1000)%60;
        //value = TimeUnit.MICROSECONDS.toHours();

        if(hrs>0)
        {
            songDuration = String.format("%02d:%02d:%02d", hrs, mns, sec);//regex expre reguliere pour horaire
        } else{
            songDuration = String.format("%02d:%02d", mns, sec);
        }
        return songDuration;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        init();
        checkPermission(); //init les composant
        getAudioFiles();
        manageRv();
    }
}