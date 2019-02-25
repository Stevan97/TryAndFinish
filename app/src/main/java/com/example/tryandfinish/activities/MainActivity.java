package com.example.tryandfinish.activities;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.tryandfinish.R;
import com.example.tryandfinish.db.DatabaseHelper;
import com.example.tryandfinish.db.model.Filmovi;
import com.example.tryandfinish.db.model.Glumac;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.ForeignCollection;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int SELECT_PICTURE = 1;

    private int position;
    private String imagePath;
    private ImageView preview;

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            position = savedInstanceState.getInt("position");
        }


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);



    }

    private void reset() {
        imagePath = "";
        preview = null;
    }

    private void selectPicture() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();

                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                    if (selectedImageUri != null) {
                        imagePath = selectedImageUri.toString();
                        Toast.makeText(MainActivity.this, selectedImageUri.getPath(), Toast.LENGTH_LONG).show();
                    }
                    if (preview != null) {
                        preview.setImageBitmap(bitmap);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }


        super.onActivityResult(requestCode, resultCode, data);

    }

    private void refresh() {
        ListView listView = (ListView) findViewById(R.id.list_view_glumci);
        if (listView != null) {
            ArrayAdapter<Glumac> glumacAdapter = (ArrayAdapter<Glumac>) listView.getAdapter();

            if (glumacAdapter != null ) {
                try {
                    glumacAdapter.clear();
                    List<Glumac> listaGlumca = getDatabaseHelper().getmGlumacDao().queryForAll();
                    glumacAdapter.addAll(listaGlumca);
                    glumacAdapter.notifyDataSetChanged();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void addActor() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.add_actor_dialog);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        Button choose = (Button) dialog.findViewById(R.id.actor_button_gallery);
        choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preview = (ImageView) dialog.findViewById(R.id.actor_preview_image);
                selectPicture();
            }
        });

        final EditText editName = (EditText) dialog.findViewById(R.id.actor_name);
        final EditText editLastName = (EditText) dialog.findViewById(R.id.actor_lastName);
        final EditText editDescription = (EditText) dialog.findViewById(R.id.actor_description);
        Button confirm = (Button) dialog.findViewById(R.id.actor_button_ok);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String name = editName.getText().toString();
                    String lastName = editLastName.getText().toString();
                    String opis = editDescription.getText().toString();
                 //   ForeignCollection<Filmovi> listFilmovi = (ForeignCollection<Filmovi>) getDatabaseHelper().getmFilmoviDao().queryForId(position);

                    if (preview == null || imagePath == null) {
                        Toast.makeText(MainActivity.this, "MUST NOT BE EMPTY", Toast.LENGTH_LONG).show();
                        return;
                    }

                    Glumac glumac = new Glumac();
                    glumac.setName(name);
                    glumac.setLastName(lastName);
                    glumac.setOpis(opis);
                    glumac.setImage(imagePath);

                    getDatabaseHelper().getmGlumacDao().create(glumac);
                    refresh();
                    Toast.makeText(MainActivity.this, "Actor Added!", Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                    reset();


                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_addGlumac:
                addActor();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    // ListView za MainActivity (pocetni ekran)
    private void activityStart() throws SQLException {


        List<Glumac> listaGlumaca = getDatabaseHelper().getmGlumacDao().queryForAll();
        ArrayAdapter<Glumac> adapterGlumac = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listaGlumaca);
        final ListView listViewGlumci = (ListView) findViewById(R.id.list_view_glumci);
        listViewGlumci.setAdapter(adapterGlumac);
        listViewGlumci.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listViewGlumci.getItemAtPosition(position);

            }
        });
    }


    public DatabaseHelper getDatabaseHelper() {
        if (databaseHelper == null) {
            OpenHelperManager.getHelper(this, DatabaseHelper.class);
        }
        return databaseHelper;
    }

    @Override
    protected void onDestroy() {

        if (databaseHelper != null) {
            OpenHelperManager.releaseHelper();
            databaseHelper = null;
        }

        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt("position", position);
    }
}
