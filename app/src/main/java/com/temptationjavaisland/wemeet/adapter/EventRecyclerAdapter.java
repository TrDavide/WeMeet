package com.temptationjavaisland.wemeet.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.temptationjavaisland.wemeet.R;
import com.temptationjavaisland.wemeet.database.EventRoomDatabase;
import com.temptationjavaisland.wemeet.model.Event;

import java.util.List;

public class EventRecyclerAdapter extends RecyclerView.Adapter<EventRecyclerAdapter.ViewHolder> {
    private int layout;
    private List<Event> eventList;
    private OnHeartClickListener onHeartClickListener;

    public interface OnHeartClickListener {
        void onHeartClick(Event event);
    }

    public void setOnHeartClickListener(OnHeartClickListener listener) {
        this.onHeartClickListener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewTitle;
        private final TextView textViewDate;
        private final TextView textViewLocation;
        private final CheckBox checkBoxSaved;

        public ViewHolder(View view) {
            super(view);
            textViewTitle = view.findViewById(R.id.event_title);
            textViewDate = view.findViewById(R.id.event_date_time);
            textViewLocation = view.findViewById(R.id.event_location);
            checkBoxSaved = view.findViewById(R.id.favoriteButton);
        }

        public TextView getTextViewTitle() { return textViewTitle; }
        public TextView getTextViewDate() { return textViewDate; }
        public TextView getTextViewLocation() { return textViewLocation; }
        public CheckBox getCheckBoxSaved() { return checkBoxSaved; }
    }

    public EventRecyclerAdapter(int layout, List<Event> eventList) {
        this.layout = layout;
        this.eventList = eventList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(layout, viewGroup, false);
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
        viewHolder.getCheckBoxSaved().setChecked(currentEvent.isSaved());

        viewHolder.getCheckBoxSaved().setOnCheckedChangeListener(null); // previene loop
        viewHolder.getCheckBoxSaved().setChecked(currentEvent.isSaved());

        viewHolder.getCheckBoxSaved().setOnCheckedChangeListener((buttonView, isChecked) -> {
            currentEvent.setSaved(isChecked);

            new Thread(() -> {
                if (isChecked) {
                    EventRoomDatabase.getDatabase(viewHolder.itemView.getContext())
                            .eventsDao()
                            .insertAll(currentEvent);
                } else {
                    EventRoomDatabase.getDatabase(viewHolder.itemView.getContext())
                            .eventsDao()
                            .deleteById(currentEvent.getUid());
                }
            }).start();

            if (onHeartClickListener != null) {
                onHeartClickListener.onHeartClick(currentEvent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public void updateData(List<Event> newEvents) {
        this.eventList = newEvents;
        notifyDataSetChanged();
    }
}
