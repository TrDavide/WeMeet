package com.temptationjavaisland.wemeet.adapter;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;  // aggiunto import
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide; // aggiunto import Glide
import com.temptationjavaisland.wemeet.R;
import com.temptationjavaisland.wemeet.database.EventRoomDatabase;
import com.temptationjavaisland.wemeet.model.Event;

import java.util.List;

public class EventRecyclerAdapter extends RecyclerView.Adapter<EventRecyclerAdapter.ViewHolder>{
    public interface OnItemClickListener {
        void onEventItemClick(Event event);
        void onFavoriteButtonPressed(int position);
    }

    private int layout;
    private List<Event> eventList;
    private final OnItemClickListener onItemClickListener;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private final TextView textViewTitle;
        private final TextView textViewDate;
        private final TextView textViewLocation;
        private final CheckBox checkBoxSaved;
        private final ImageView eventImageView;

        public ViewHolder(View view) {
            super(view);
            textViewTitle = view.findViewById(R.id.event_title);
            textViewDate = view.findViewById(R.id.event_date_time);
            textViewLocation = view.findViewById(R.id.event_location);
            checkBoxSaved = view.findViewById(R.id.favoriteButton);
            eventImageView = view.findViewById(R.id.event_image);
            checkBoxSaved.setOnClickListener(this);
            view.setOnClickListener(this);
        }


        public void bind(Event event, OnItemClickListener listener) {
            itemView.setOnClickListener(v -> listener.onEventItemClick(event));
        }

        public TextView getTextViewTitle() { return textViewTitle; }
        public TextView getTextViewDate() { return textViewDate; }
        public TextView getTextViewLocation() { return textViewLocation; }
        public CheckBox getCheckBoxSaved() { return checkBoxSaved; }
        public ImageView getEventImageView() { return eventImageView; }

        @Override
        public void onClick(View v){
            //se il click proviene dal CheckBox "salva nei preferiti"
            if(v.getId() == R.id.favoriteButton){
                //notifica al listener che il pulsante preferiti è stato premuto per l'elemento corrente
                onItemClickListener.onFavoriteButtonPressed(getAdapterPosition());
            } else {
                //altrimenti, considera il click sull'intera card dell'evento
                //e notifica il listener del click sull'evento specifico
                onItemClickListener.onEventItemClick(eventList.get(getAdapterPosition()));
            }
        }

    }

    public EventRecyclerAdapter(int layout, List<Event> eventList, OnItemClickListener onItemClickListener) {
        this.layout = layout;
        this.eventList = eventList;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        //ottiene l'evento corrente dalla lista in base alla posizione
        Event currentEvent = eventList.get(position);

        //Imposta il titolo dell'evento nella TextView corrispondente
        viewHolder.getTextViewTitle().setText(currentEvent.getName());

        //Imposta la data dell'evento, se disponibile, altrimenti mostra un messaggio di fallback
        if (currentEvent.getDates() != null && currentEvent.getDates().getStart() != null) {
            viewHolder.getTextViewDate().setText(currentEvent.getDates().getStart().getLocalDate());
        } else {
            viewHolder.getTextViewDate().setText("Data non disponibile");
        }

        //Imposta la location dell'evento se disponibile, altrimenti mostra un messaggio di fallback
        String locationText = "Luogo non disponibile";
        if (currentEvent.getEmbedded() != null &&
                currentEvent.getEmbedded().getVenues() != null &&
                !currentEvent.getEmbedded().getVenues().isEmpty() &&
                currentEvent.getEmbedded().getVenues().get(0) != null &&
                currentEvent.getEmbedded().getVenues().get(0).getName() != null) {
            locationText = currentEvent.getEmbedded().getVenues().get(0).getName();
        }
        viewHolder.getTextViewLocation().setText(locationText);

        //carica l'immagine dell'evento tramite Glide; se non disponibile, usa immagine di default
        if (currentEvent.getImages() != null && !currentEvent.getImages().isEmpty()) {
            String imageUrl = currentEvent.getImages().get(0).getUrl();
            Glide.with(viewHolder.itemView.getContext())
                    .load(imageUrl)
                    //.placeholder(R.drawable.placeholder)
                    .into(viewHolder.getEventImageView());
        } else {
            viewHolder.getEventImageView().setImageResource(R.drawable.event_background);
        }

        //imposta lo stato del checkbox senza triggerare listener precedenti
        viewHolder.getCheckBoxSaved().setOnCheckedChangeListener(null);
        viewHolder.getCheckBoxSaved().setChecked(currentEvent.isSaved());

        //Associa l'evento corrente al listener di click
        viewHolder.bind(currentEvent, onItemClickListener);
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public void updateData(List<Event> newEvents) { //questo metodo è usato solo in LocationFrgment
        eventList.clear();
        eventList.addAll(newEvents);
        notifyDataSetChanged();
    }

}