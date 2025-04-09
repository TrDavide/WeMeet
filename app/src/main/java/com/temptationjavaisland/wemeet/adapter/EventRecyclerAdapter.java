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
            private final TextView textViewOrganizator;
            private final TextView textViewDescription;

            public ViewHolder(View view) {
                super(view);
                // Define click listener for the ViewHolder's View

                textViewTitle = view.findViewById(R.id.textViewTitle);
                textViewOrganizator =  view.findViewById(R.id.textViewOrganizator);
                textViewDescription = view.findViewById(R.id.textViewDescription);
            }

            public TextView getTextViewTitle() {
                return textViewTitle;
            }
            public TextView getTextViewOrganizator(){
                return textViewOrganizator;
            }
            public TextView getTextViewDescription() {
                return textViewDescription;
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
            viewHolder.getTextViewDescription().setText(eventList.get(position).getDescription());
            viewHolder.getTextViewOrganizator().setText(eventList.get(position).getVenue());
        }

        @Override
        public int getItemCount() {
            return eventList.size();
        }
    }

