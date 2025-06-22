package com.temptationjavaisland.wemeet.adapter;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.temptationjavaisland.wemeet.R;
import com.temptationjavaisland.wemeet.database.EventRoomDatabase;
import com.temptationjavaisland.wemeet.model.Event;

import java.util.List;

public class EventRecyclerAdapter extends RecyclerView.Adapter<EventRecyclerAdapter.ViewHolder>{
    public interface OnItemClickListener {
        void onEventClick(Event event);
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
            itemView.setOnClickListener(v -> listener.onEventClick(event));
        }

        public TextView getTextViewTitle() { return textViewTitle; }
        public TextView getTextViewDate() { return textViewDate; }
        public TextView getTextViewLocation() { return textViewLocation; }
        public CheckBox getCheckBoxSaved() { return checkBoxSaved; }
        public ImageView getEventImageView() { return eventImageView; }

        @Override
        public void onClick(View v){
            if(v.getId() == R.id.favoriteButton){
                onItemClickListener.onFavoriteButtonPressed(getAdapterPosition());
            }else{
                onItemClickListener.onEventClick(eventList.get(getAdapterPosition()));
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
        Event currentEvent = eventList.get(position);
        viewHolder.getTextViewTitle().setText(currentEvent.getName());

        if (currentEvent.getDates() != null && currentEvent.getDates().getStart() != null) {
            viewHolder.getTextViewDate().setText(currentEvent.getDates().getStart().getLocalDate());
        } else {
            viewHolder.getTextViewDate().setText("Data non disponibile");
        }

        String locationText = "Luogo non disponibile";
        if (currentEvent.getEmbedded() != null &&
                currentEvent.getEmbedded().getVenues() != null &&
                !currentEvent.getEmbedded().getVenues().isEmpty() &&
                currentEvent.getEmbedded().getVenues().get(0) != null &&
                currentEvent.getEmbedded().getVenues().get(0).getName() != null) {
            locationText = currentEvent.getEmbedded().getVenues().get(0).getName();
        }
        viewHolder.getTextViewLocation().setText(locationText);

        // Carica immagine evento con Glide
        if (currentEvent.getImages() != null && !currentEvent.getImages().isEmpty()) {
            String imageUrl = currentEvent.getImages().get(0).getUrl();
            Glide.with(viewHolder.itemView.getContext())
                    .load(imageUrl)
                    //.placeholder(R.drawable.placeholder) // immagine temporanea
                    .into(viewHolder.getEventImageView());
        } else {
            viewHolder.getEventImageView().setImageResource(R.drawable.event_background); // fallback
        }

        viewHolder.getCheckBoxSaved().setOnCheckedChangeListener(null);
        viewHolder.getCheckBoxSaved().setChecked(currentEvent.isSaved());

        viewHolder.bind(currentEvent, onItemClickListener);
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public void updateData(List<Event> newEvents) {
        eventList.clear();
        eventList.addAll(newEvents);
        notifyDataSetChanged();
    }
}
