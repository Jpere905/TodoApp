package com.example.todoapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String KEY_ITEM_TEXT = "item_text";
    public static final String KEY_ITEM_POSITION = "item_position";
    public static final int EDIT_TEXT_CODE = 20;
    List<String> items;

    // create a reference of each view to add the appropriate logic
    Button btnAdd;
    EditText etItem;
    RecyclerView rvItems;
    ItemsAdapter itemsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnAdd = findViewById(R.id.btnAddItem);
        etItem = findViewById(R.id.etTodo);
        rvItems = findViewById(R.id.rvItems);

        //etItem.setText("From inside java code");

        loadItems() ;
//        items.add("Buy gabbagool");
//        items.add("Bring gabbagool \"ova heea\"");
//        items.add("Chicken soup, with rice, lotsa pickles");
//        items.add("Lo-fat turkey, no bread, no mayo, no potato salad. Just meat and a pickle.");

        ItemsAdapter.OnLongClickListener onLongClickListener = new ItemsAdapter.OnLongClickListener() {
            @Override
            public void onItemLongClicked(int position) {
                String itemToRemove = items.get(position);
                // Delete item from the model
                items.remove(position);
                // notify the adapter
                itemsAdapter.notifyItemRemoved(position);
                Toast.makeText(getApplicationContext(), itemToRemove + ", was removed", Toast.LENGTH_SHORT).show();
                saveItems();

            }
        };
        ItemsAdapter.OnClickListener onClickListener = new ItemsAdapter.OnClickListener() {
            @Override
            public void onItemClicked(int position) {
                Log.d("MainActivity", "Single click at " + position);
                /*
                    create the new activity
                    use "intents" which is a core part of the android system
                    You can use them to request that android open a new activity
                    or to open a URL or open the camera, intents are very useful
                */
                Intent i = new Intent(MainActivity.this, EditActivity.class);
                // pass the data being edited
                // putExtra takes two arguments: key, value
                // We'll define a key at the top of this code to reference to in both
                // MainActivity and EditActivity to work with the data
                i.putExtra(KEY_ITEM_TEXT, items.get(position));
                i.putExtra(KEY_ITEM_POSITION, position);
                // display the activity
                // we expect a result back from the activity; the updated todo item
                startActivityForResult(i, EDIT_TEXT_CODE);
            }
        };

        itemsAdapter = new ItemsAdapter(items, onLongClickListener, onClickListener);
        rvItems.setAdapter(itemsAdapter);
        rvItems.setLayoutManager(new LinearLayoutManager(this));

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String todoItem = etItem.getText().toString();
                // add item to model
                items.add(todoItem);
                // notify adapter that new item is being added
                itemsAdapter.notifyItemInserted(items.size() - 1);
                etItem.setText("");
                Toast.makeText(getApplicationContext(), todoItem + ", was added", Toast.LENGTH_SHORT).show();
                saveItems();

            }
        });
    }

    // handle the result of the edit activity
    @SuppressLint("MissingSuperCall")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK && requestCode == EDIT_TEXT_CODE) {
            // retrieve the updated text value
            String itemText = data.getStringExtra(KEY_ITEM_TEXT);

            // extract original position of the text item from the key position
            int position = data.getExtras().getInt(KEY_ITEM_POSITION);

            // Update the model at the correct position and with new item text
            items.set(position, itemText);

            // notify adapter so recycler view knows something has changed
            itemsAdapter.notifyItemChanged(position);

            // persist the changes
            saveItems();
            Toast.makeText(getApplicationContext(), "Item updated successfully", Toast.LENGTH_SHORT).show();
        } else {
            Log.w("MainActivity", "Unknown call to onActivityResult");
        }
    }

    private File getDataFile() {
        // getFilesDir() returns the directory of our app
        return new File(getFilesDir(), "todo_data.txt");
    }

    // this methods will load every todo item by reading each line of the data file
    // only called once - at the start of the application boot
    private void loadItems(){
        try {
            // contents of this array list comes from our file which is read by FileUtils
            items = new ArrayList<>(FileUtils.readLines(getDataFile(), Charset.defaultCharset()));
        // if that doesn't succeed then log the error and initialize items with an empty arraylist
        // that way, we'll have something to build our RecyclerView off of
        } catch (IOException e) {
            Log.e("MainActivity", "Error reading items", e);
            items = new ArrayList<>();
        }

    }
    // this method will save items by writing them to a file
    // should be called everytime we make a change to the list of todo items
    private void saveItems() {
        try {
            FileUtils.writeLines(getDataFile(), items);
        } catch (IOException e) {
            Log.e("MainActivity", "Error writing items", e);
        }
    }
}