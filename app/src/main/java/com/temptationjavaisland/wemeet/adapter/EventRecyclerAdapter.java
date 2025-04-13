package com.temptationjavaisland.wemeet.adapter;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.temptationjavaisland.wemeet.R;
import com.temptationjavaisland.wemeet.model.Event;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class EventRecyclerAdapter extends RecyclerView.Adapter<EventRecyclerAdapter.ViewHolder> {
        private int layout;
        private List<Event> eventList;
        /**
         * Provide a reference to the type of views that you are using
         * (custom ViewHolder)
         */
        public static class ViewHolder extends RecyclerView.ViewHolder {
            private final TextView textViewTitle;
            private final TextView textViewDate;
            private final TextView textViewLocation;
            private final TextView textViewPartecipant;

            public ViewHolder(View view) {
                super(view);
                // Define click listener for the ViewHolder's View

                textViewTitle = view.findViewById(R.id.event_title);
                textViewDate = view.findViewById(R.id.event_date_time);
                textViewLocation =  view.findViewById(R.id.event_location);
                textViewPartecipant = view.findViewById(R.id.partecipant_count);
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
            public TextView getTextViewPartecipant() {
                return textViewPartecipant;
            }
        }
        public EventRecyclerAdapter(int layout,List <Event> eventList) {
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
            viewHolder.getTextViewDate().setText(eventList.get(position).getDate());
            viewHolder.getTextViewPartecipant().setText(eventList.get(position).getPartecipant());
            viewHolder.getTextViewLocation().setText(eventList.get(position).getLocation());
        }

        @Override
        public int getItemCount() {
            return eventList.size();
        }
    }

