package com.temptationjavaisland.wemeet.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import com.temptationjavaisland.wemeet.R;
import com.temptationjavaisland.wemeet.database.EventRoomDatabase;
import com.temptationjavaisland.wemeet.model.Event;
import com.temptationjavaisland.wemeet.model.EventAPIResponse;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class EventRecyclerAdapter extends RecyclerView.Adapter<EventRecyclerAdapter.ViewHolder> {
        private int layout;
        private List<Event> eventList;

        public interface OnHeartClickListener {
            void onHeartClick(Event event);
        }
        private OnHeartClickListener onHeartClickListener;

        public void setOnHeartClickListener(OnHeartClickListener listener) {
            this.onHeartClickListener = listener;
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            private final TextView textViewTitle;
            private final TextView textViewDate;
            private final TextView textViewLocation;
            private final CheckBox checkBoxSaved;
            //private final TextView textViewPartecipant;


            public ViewHolder(View view) {
                super(view);
                // Define click listener for the ViewHolder's View

                textViewTitle = view.findViewById(R.id.event_title);
                textViewDate = view.findViewById(R.id.event_date_time);
                textViewLocation =  view.findViewById(R.id.event_location);
                checkBoxSaved =  view.findViewById(R.id.favoriteButton);
                //textViewPartecipant = view.findViewById(R.id.partecipant_count);
            }

            public TextView getTextViewTitle() {
                return textViewTitle;
            }
            public TextView getTextViewDate() {
                return textViewDate;
            }
            public TextView getTextViewLocation(){
                return textViewLocation;
            }
            public CheckBox getCheckBoxSaved() {return checkBoxSaved;}
            //public TextView getTextViewPartecipant() {return textViewPartecipant;}
        }
        public EventRecyclerAdapter(int layout, List<Event> eventList) {
            this.layout = layout;
            this.eventList = eventList;
        }

        // Create new views (invoked by the layout manager

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            // Create a new view, which defines the UI of the list item
            View view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(layout, viewGroup, false);

            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, final int position) {
            viewHolder.getTextViewTitle().setText(eventList.get(position).getName());
            //viewHolder.getTextViewDate().setText(eventList.get(position).getDates().getStart().getLocalDate());
            //viewHolder.getTextViewLocation().setText(eventList.get(position).getEmbedded().getVenues().get(0).getName());

            if (eventList.get(position).getDates() != null && eventList.get(position).getDates().getStart() != null) {
                viewHolder.getTextViewDate().setText(eventList.get(position).getDates().getStart().getLocalDate());
            } else {
                viewHolder.getTextViewDate().setText("Data non disponibile");
            }

            String locationText = "Luogo non disponibile";
            if (eventList.get(position).getEmbedded() != null &&
                    eventList.get(position).getEmbedded().getVenues() != null &&
                    !eventList.get(position).getEmbedded().getVenues().isEmpty() &&
                    eventList.get(position).getEmbedded().getVenues().get(0) != null &&
                    eventList.get(position).getEmbedded().getVenues().get(0).getName() != null) {
                locationText = eventList.get(position).getEmbedded().getVenues().get(0).getName();
            }
            viewHolder.getTextViewLocation().setText(locationText);
            viewHolder.getCheckBoxSaved().setChecked(eventList.get(position).isSaved());


            viewHolder.getCheckBoxSaved().setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    Event currentEvent = eventList.get(viewHolder.getAdapterPosition());
                    currentEvent.setSaved(b);

                    viewHolder.getCheckBoxSaved().setOnCheckedChangeListener(null); // evita il trigger errato
                    viewHolder.getCheckBoxSaved().setChecked(eventList.get(position).isSaved());

                    viewHolder.getCheckBoxSaved().setOnCheckedChangeListener((buttonView, isChecked) -> {
                        currentEvent.setSaved(isChecked);

                        if (isChecked) {
                            EventRoomDatabase.getDatabase(viewHolder.itemView.getContext())
                                    .eventsDao().insertAll(currentEvent);
                        } else {
                            if (onHeartClickListener != null) {
                                onHeartClickListener.onHeartClick(currentEvent); //chiama il Fragment
                            }
                        }
                    });

                    /*EventRoomDatabase.getDatabase(viewHolder.getTextViewTitle().getContext()).
                            eventsDao().updateArticle(currentEvent);*/
                    if (b){
                        EventRoomDatabase.getDatabase(viewHolder.getTextViewTitle().getContext())
                                .eventsDao().insertAll(eventList.get(position));
                    }else {
                        EventRoomDatabase.getDatabase(viewHolder.getTextViewTitle().getContext())
                                .eventsDao().delete(eventList.get(position));
                    }

                    /*new Thread(() -> {
                        if (b) {
                            EventRoomDatabase
                                    .getDatabase(viewHolder.itemView.getContext())
                                    .eventsDao()
                                    .insertAll(eventList.get(viewHolder.getAdapterPosition()));
                        } else {
                            EventRoomDatabase
                                    .getDatabase(viewHolder.itemView.getContext())
                                    .eventsDao()
                                    .delete(eventList.get(viewHolder.getAdapterPosition()));
                        }
                    }).start();*/

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

