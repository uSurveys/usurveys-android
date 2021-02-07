package com.usersneak.demo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public final class EventNameAdapter
    extends RecyclerView.Adapter<EventNameAdapter.EventNameViewHolder> {

  private final EventClickListener listener;
  private List<String> events = new ArrayList<>();

  public EventNameAdapter(EventClickListener listener) {
    this.listener = listener;
  }

  public void setEvents(List<String> events) {
    this.events = events;
    notifyDataSetChanged();
  }

  @NonNull
  @Override
  public EventNameViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    return new EventNameViewHolder(
        LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event, parent, false),
        listener);
  }

  @Override
  public void onBindViewHolder(@NonNull EventNameViewHolder holder, int position) {
    holder.bind(events.get(position));
  }

  @Override
  public int getItemCount() {
    return events.size();
  }

  static final class EventNameViewHolder extends RecyclerView.ViewHolder {

    private final TextView name;
    private final EventClickListener listener;

    public EventNameViewHolder(@NonNull View itemView, EventClickListener listener) {
      super(itemView);
      this.name = itemView.findViewById(R.id.event_name);
      this.listener = listener;
    }

    public void bind(String event) {
      name.setText(event);
      name.setOnClickListener(view -> listener.onClick(event));
    }
  }

  public interface EventClickListener {

    void onClick(String event);
  }
}
