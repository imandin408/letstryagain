package com.example.a10013461.attendanceapp;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Parcelable;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.prefs.Preferences;

import yuku.ambilwarna.AmbilWarnaDialog;

public class MainActivity extends ListActivity {

    Button addClass;
    Button saveClasses;

    int defaultColor;

    ClassElement theClassElement;

    int classes;

    String currentBlock;
    String currentClassName;

    static final int CODE = 123;
    static final int CODEE = 122;
    static final String KEY = "adw";
    static final String KEYY = "aaaa";
    static final String KEYYY = "awd";
    static final String KEYYYY = "awdadadwadwadw";
    static final String ANOTHERKEY = "yeyy";

    private static final String SHARED_PREFZ_NAME = "MY_SHARED_PREF";

    TextView amountOfClassesText;

    ArrayList<ClassElement> list;
    CharSequence[] charSequences = {"Add People","Take Attendance","Edit","Remove","Cancel"};

    CustomAdapter adapter;

    /*
    Awesome Notes Section!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

    YALL BETTER GIVE ME CREDZ - IMAN DIN MADE THIS THING K

     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadData();

        currentBlock = "";
        currentClassName = "";

        addClass = (Button) findViewById(R.id.addButton);
        saveClasses = (Button) findViewById(R.id.saveClassesButton);
        amountOfClassesText = (TextView) findViewById(R.id.textAmountOfClasses);
        adapter = new CustomAdapter(this,R.layout.listview,list);
        setListAdapter(adapter);
        adapter.notifyDataSetChanged();

        classes=0;

        amountOfClassesText.setText("Amount of Classes: "+adapter.getCount());

        addClass.setOnClickListener(new View.OnClickListener() {
            String blok;
            String clas;
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                LayoutInflater inflater = getLayoutInflater();
                View v = inflater.inflate(R.layout.addclass_dialog,null,false);
                final EditText editTextBlock = (EditText) v.findViewById(R.id.editTextBlock);
                final EditText editTextClass = (EditText) v.findViewById(R.id.editTextClass);

                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        blok = editTextBlock.getText().toString();
                        clas = editTextClass.getText().toString();
                        adapter.add(new ClassElement(blok,clas));
                        amountOfClassesText.setText("Amount of Classes: "+adapter.getCount());
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.setView(v);
                dialog.show();
            }
        });

        saveClasses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveData();
                Toast.makeText(MainActivity.this,"Classes Saved!",Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    protected void onListItemClick(final ListView l, View v, final int position, long id) {
        super.onListItemClick(l, v, position, id);
        if(theClassElement!=null) {
            theClassElement=null;
        }
        final int positionToRemove = position;
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(list.get(position).getBlock()+" - "+list.get(position).getClassName());
        builder.setItems(charSequences, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch(i){
                    case 0://add people
                        Intent intent = new Intent(MainActivity.this,AddPeopleActivity.class);
                        intent.putExtra("className", adapter.getItem(position));
                        intent.putExtra("pos",position);
                        startActivityForResult(intent,CODE);
                        break;
                    case 1://take attendance
                        Intent intent2 = new Intent(MainActivity.this,TakeAttendanceActivity.class);
                        intent2.putExtra("pos",position);
                        intent2.putStringArrayListExtra("classList", adapter.getItem(position).getPeople());
                        startActivityForResult(intent2,CODEE);
                        break;
                    case 2://edit
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        LayoutInflater inflater = getLayoutInflater();
                        View v = inflater.inflate(R.layout.editclass_dialog,null,false);
                        final EditText editTextBlock = (EditText) v.findViewById(R.id.editTextBlock2);
                        final EditText editTextClass = (EditText) v.findViewById(R.id.editTextClass2);

                        editTextBlock.setText(list.get(position).getBlock());
                        editTextClass.setText(list.get(position).getClassName());

                        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                list.get(position).setBlock(editTextBlock.getText().toString());
                                list.get(position).setClassName(editTextClass.getText().toString());
                                l.invalidateViews();
                            }
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.setView(v);
                        dialog.show();
                        break;
                    case 3://delete
                        list.remove(position);
                        l.invalidateViews();
                        amountOfClassesText.setText("Amount of Classes: "+adapter.getCount());
                        break;
                    case 4://cancel
                        break;
                }
            }
        });
        builder.create();
        builder.show();
    }

    public class CustomAdapter extends ArrayAdapter<ClassElement>{

        Context context;
        List<ClassElement> list;

        public CustomAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<ClassElement> objects) {
            super(context, resource, objects);
            this.context = context;
            list=objects;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            View adapterView = layoutInflater.inflate(R.layout.listview, null);

            ImageView imageView = adapterView.findViewById(R.id.statusImage);
            TextView block = adapterView.findViewById(R.id.textBlock);
            TextView className = adapterView.findViewById(R.id.textClassName);
            TextView amountOfPeople = adapterView.findViewById(R.id.textPresent);
            TextView amountHere = adapterView.findViewById(R.id.textAbsent);

            if (list.get(position).getImageColor() == 1)
                imageView.setImageResource(android.R.color.holo_red_dark);
            if(list.get(position).getImageColor()==2){
                imageView.setImageResource(android.R.color.holo_orange_light);
            }
            if(list.get(position).getImageColor()==3){
                imageView.setImageResource(android.R.color.holo_green_light);
            }

            currentBlock = block.toString();
            currentClassName = className.toString();
            amountOfPeople.setText(list.get(position).getPeople().size()+" People");
            amountHere.setText(list.get(position).getAmountPresent()+"/"+list.get(position).getPeople().size()+" Present");


            block.setText("Block: "+list.get(position).getBlock());
            className.setText(list.get(position).getClassName());

            return adapterView;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode==RESULT_OK&&requestCode==CODE){
            theClassElement=(data.getParcelableExtra(KEY));
            list.set(data.getIntExtra(KEYY,0),theClassElement);
            list.get(data.getIntExtra(KEYY,0)).setImageColor(1);
            adapter.notifyDataSetChanged();
        }
        if(resultCode==RESULT_OK&&requestCode==CODEE){
            list.get(data.getIntExtra(KEYYYY,0)).setAmountPresent(data.getIntExtra(KEYYY,0));
            list.get(data.getIntExtra(KEYYYY,0)).setImageColor(data.getIntExtra(ANOTHERKEY,0));
            adapter.notifyDataSetChanged();
        }
    }

    public void saveData(){
        SharedPreferences sp = this.getSharedPreferences(SHARED_PREFZ_NAME,Activity.MODE_PRIVATE);
        SharedPreferences.Editor edit1 = sp.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        edit1.putString("list",json);
        edit1.apply();
    }

    public void loadData(){
        SharedPreferences sp = this.getSharedPreferences(SHARED_PREFZ_NAME,Activity.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sp.getString("list",null);
        Type type = new TypeToken<ArrayList<ClassElement>>() {}.getType();
        list = gson.fromJson(json,type);

        if(list==null){
            list = new ArrayList<>();
        }
    }


}
