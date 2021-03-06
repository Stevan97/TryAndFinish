package com.example.tryandfinish.activities;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
    private static final int MY_PERMISSIONS_REQUEST_READ_MEDIA = 1;

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

        activityStart();

        checkPermission();



    }

    private void checkPermission() {
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_MEDIA);
        } else {

        }
    }


    private void reset() {
        imagePath = "";
        preview = null;
    }

    private void selectPicture() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
    }

    public String getImagePath(Uri uri){
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":")+1);
        cursor.close();

        cursor = getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();

        return path;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();

                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                    if (selectedImageUri != null) {
                        imagePath = getImagePath(selectedImageUri);
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

            if (glumacAdapter != null) {
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
        final EditText editDescription = (EditText) dialog.findViewById(R.id.actor_description);
        Button confirm = (Button) dialog.findViewById(R.id.actor_button_ok);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String name = editName.getText().toString();
                    String opis = editDescription.getText().toString();
                    //   ForeignCollection<Filmovi> listFilmovi = (ForeignCollection<Filmovi>) getDatabaseHelper().getmFilmoviDao().queryForId(position);

                    if (preview == null || imagePath == null) {
                        Toast.makeText(MainActivity.this, "MUST NOT BE EMPTY", Toast.LENGTH_LONG).show();
                        return;
                    }

                    Glumac glumac = new Glumac();
                    glumac.setName(name);
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

        Button cancel = (Button) dialog.findViewById(R.id.actor_button_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
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
    private void activityStart() {

        try {
            List<Glumac> listaGlumaca = null;
            listaGlumaca = getDatabaseHelper().getmGlumacDao().queryForAll();
            ArrayAdapter<Glumac> adapterGlumac = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listaGlumaca);
            final ListView listViewGlumci = (ListView) findViewById(R.id.list_view_glumci);
            listViewGlumci.setAdapter(adapterGlumac);
            listViewGlumci.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Glumac g = (Glumac) listViewGlumci.getItemAtPosition(position);

                    Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                    intent.putExtra("Glumac", g.getId());
                    startActivity(intent);

                }

            });


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public DatabaseHelper getDatabaseHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
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
