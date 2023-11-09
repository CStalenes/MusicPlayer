package com.example.musicplayer;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

//ALT + ENTRER pour implementer les methode de AdapterSong


//Comprendre les adaptateurs
public class AdapterSong extends RecyclerView.Adapter<AdapterSong.MyViewHolder> {


    private Context context;
    private ArrayList<ModeleSong> songArrayList;

    public AdapterSong(Context context, ArrayList<ModeleSong> songArrayList) {
        this.context = context;
        this.songArrayList = songArrayList;
    }

    @NonNull
    @Override
    public AdapterSong.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;
        view = LayoutInflater.from(context).inflate(R.layout.item_song, parent, false);
        //a chaque fois qu on a une new ligne on a un new composant
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterSong.MyViewHolder holder, int position) {
        holder.title.setText(songArrayList.get(position).getSongTitle());
        holder.artist.setText(songArrayList.get(position).getSongArtist().trim());

        String album = songArrayList.get(position).getSongAlbum();
        holder.album.setText(album);

        Uri imgUri = songArrayList.get(position).getSongCover();

        //Context c'est juste situer l'endroit on on est pour chaqun de nos item exemple le cover(pour l'image)
        Context context = holder.cover.getContext();


        //Methode normale
        RequestOptions options =  new RequestOptions()
                .centerCrop()
                .error(R.drawable.ic_note_24_w)
                .placeholder(R.drawable.ic_note_24_w);


        Glide.with(context)
                .load(imgUri) // On applique les options de chargement
                .apply(options) // Resize et alignement au centre
                .fitCenter()// Resize pour que les images soient toutes à la même taille
                .override(150, 150) // Gestion des images dans le cache pour améliorer l'affichage
                .diskCacheStrategy(DiskCacheStrategy.ALL)// Emplacement où afficher l'image
                .into(holder.cover);
    }

    //renvoie le nb d elem
    @Override
    public int getItemCount() {
        return songArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title, artist, album;

        ImageView cover;

        //la gestion du click ici car ca doit etre effectif sur chaque ligne
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            //on va lier obj java a nos obj graphique

            title = itemView.findViewById(R.id.tv_title);
            album = itemView.findViewById(R.id.tv_album);
            artist = itemView.findViewById(R.id.tv_artist);
            cover = itemView.findViewById(R.id.iv_cover);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myOnItemClickListener.onItemClick(getAdapterPosition(), v);
                }
            });

        }

        //on clique dans le recycler pas dans l item directement
        //on va cree un contrat entre item et recycler



    }

    //Pour importer les images faudrait plutot utiliser les glide en important cette libra via Gradle
    //File -> ProjectStruc -> app -> Dependancie -> on add "+" dans declared dependancie -> on recherche notre lib et on valide

    //COMMENTER
    public interface MyOnItemClickListener{
        void onItemClick(int position, View view);
    }

    //COMMENTER
    private MyOnItemClickListener myOnItemClickListener;

    //COMMENTER
    public void setMyOnItemClickListener(MyOnItemClickListener pMyOnItemClickListener){
        this.myOnItemClickListener = pMyOnItemClickListener;
    }




}
