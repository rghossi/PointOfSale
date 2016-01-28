package com.rosehulman.rodrigr1.pointofsale;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Item mCurrentItem = null;
    private Item mClearedItem;
    private List<Item> mItems = new ArrayList<>();
    private TextView mNameText;
    private TextView mQuantityText;
    private TextView mDateText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addEditItem(false);
                //mCurrentItem = Item.getDefaultItem();
                //showCurrentItem();
                //Toast.makeText(MainActivity.this, "FAB clicked", Toast.LENGTH_SHORT).show();
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //        .setAction("Action", null).show();
            }
        });

        mNameText = (TextView) findViewById(R.id.name_text);
        mQuantityText = (TextView) findViewById(R.id.quantity_text);
        mDateText = (TextView) findViewById(R.id.date_text);
        registerForContextMenu(mNameText);

    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, v.getId(), 0, "Edit");
        menu.add(0, v.getId(), 0, "Remove");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle() == "Edit") {
            mItems.remove(mCurrentItem);
            addEditItem(true);
        } else if (item.getTitle() == "Remove") {
            mClearedItem = mCurrentItem;
            mItems.remove(mClearedItem);
            mCurrentItem = new Item();
            showCurrentItem();
            Snackbar snackbar = Snackbar.make(findViewById(R.id.coordinator_layout),"Item cleared",Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCurrentItem = mClearedItem;
                    mItems.add(mClearedItem);
                    showCurrentItem();
                    mClearedItem = null;
                }
            });
            snackbar.show();
        } else {
            return false;
        }
        return true;
    }

    private String[] getNames() {
        String[] names = new String[mItems.size()];
        for (int i = 0; i < mItems.size(); i++) {
            names[i] = mItems.get(i).getName();
        }
        return names;
    }

    private void showClearDialog(){
        DialogFragment df = new DialogFragment() {
            public Dialog onCreateDialog(Bundle savedInstanceState) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(getString(R.string.action_clearAll));
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mItems.clear();
                        mCurrentItem = new Item();
                        showCurrentItem();
                    }
                });
                builder.setNegativeButton(android.R.string.cancel, null);
                return builder.create();
            }
        };
        df.show(getSupportFragmentManager(), "clearAll");
    }

    private void showSearchDialog(){
        DialogFragment df = new DialogFragment() {
            public Dialog onCreateDialog(Bundle savedInstanceState) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(getString(R.string.dialog_search_title));
                builder.setItems(getNames(), new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mCurrentItem = mItems.get(which);
                        showCurrentItem();
                    }
                });
                builder.setNegativeButton(android.R.string.cancel, null);
                return builder.create();
            }
        };
        df.show(getSupportFragmentManager(), "search");
    }

    private void addEditItem(final boolean isEditing) {
        DialogFragment df = new DialogFragment() {
            @Override
            public Dialog onCreateDialog(Bundle savedInstanceState) {
                // Inside onCreateDialog

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_add, null, false);
                builder.setView(view);
                final EditText nameEditText = (EditText) view.findViewById(R.id.edit_name);
                final EditText quantityEditText = (EditText) view.findViewById(R.id.edit_quantity);
                final CalendarView dateView = (CalendarView) view.findViewById(R.id.calendar_view);

                if (isEditing) {
                    nameEditText.setText(mCurrentItem.getName());
                    quantityEditText.setText(mCurrentItem.getQuantity() + "");
                    dateView.setDate(mCurrentItem.getDeliveryDateTime());
                }


                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = nameEditText.getText().toString();
                        int quantity = Integer.parseInt(quantityEditText.getText().toString());
                        long date = dateView.getDate();
                        mCurrentItem = new Item(name, quantity, new Date(date));
                        mItems.add(mCurrentItem);
                        showCurrentItem();
                    }
                });
                builder.setNegativeButton(android.R.string.cancel, null);

                return builder.create();
            }
        };
        df.show(getSupportFragmentManager(), "add");
    }

    private void showCurrentItem() {
        mNameText.setText(getString(R.string.name_format, mCurrentItem.getName()));
        mQuantityText.setText(getString(R.string.quantity_format, mCurrentItem.getQuantity()));
        mDateText.setText(getString(R.string.date_format, mCurrentItem.getDeliveryDateTime()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch(item.getItemId()) {
            case R.id.action_reset:
                mClearedItem = mCurrentItem;
                mItems.remove(mClearedItem);
                mCurrentItem = new Item();
                showCurrentItem();
                Snackbar snackbar = Snackbar.make(findViewById(R.id.coordinator_layout),"Item cleared",Snackbar.LENGTH_LONG);
                snackbar.setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mCurrentItem = mClearedItem;
                        mItems.add(mClearedItem);
                        showCurrentItem();
                        mClearedItem = null;
                    }
                });
                snackbar.show();
                return true;
            case R.id.action_settings:
                return true;
            case R.id.action_search:
                showSearchDialog();
                return true;
            case R.id.action_clearAll:
                showClearDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
