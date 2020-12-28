package com.example.todoapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

// takes data at a particular position and puts it into a viewholder
// takes an object of our ViewHolder defined class below; it is parameratized by a viewholder
// thats why its important to define a ViewHolder first
public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ViewHolder>{

    // defining a public interface that is used to communicate from this code to our MainActivity.java
    // this interface will be implemented in MainActivity.java
    public interface OnLongClickListener {
        // a method that must be completed in the class it's being implemented in
        // position will tell which item we did the long press on so that the adapter can be notified
        void onItemLongClicked(int position);
    }

    public interface OnClickListener {
        void onItemClicked(int position);
    }

    // our member variables
    // creating an List of strings that we can share throughout the different methods
    List<String> items;
    OnLongClickListener longClickListener;
    OnClickListener clickListener;

    public ItemsAdapter(List<String> items, OnLongClickListener longClickListener, OnClickListener clickListener){

        // make the member variable 'items' equal to the var passed in through the constructor
        this.items = items;
        this.longClickListener = longClickListener;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // use a layout inflator to inflate a view
        View todoView = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        // wrap it inside a ViewHolder and return it
        return new ViewHolder(todoView);
    }

    // resposible for binding data to a particular ViewHolder
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // grab item at the position
        String item = items.get(position);
        // bind the item into the specified ViewHolder
        holder.bind(item);
    }

    // tells the RV how many items are in the list
    @Override
    public int getItemCount() {
        return items.size();
    }

    // container to provide easy access to the views that represent each row on the list
    class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvItem;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItem = itemView.findViewById(android.R.id.text1);
        }

        // update the view inside of the ViewHolder with the data
        // these listeners listen (duh) for the action described; longClick (hold), onClick (tap)
        public void bind(String item) {

            tvItem.setText(item);

            tvItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.onItemClicked(getAdapterPosition());
                }
            });

            tvItem.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    // remove the item from the recycler view
                    // getAdapterPosition returns the position of the viewholder
                    longClickListener.onItemLongClicked(getAdapterPosition());
                    return true;
                }
            });
        }
    }
}
