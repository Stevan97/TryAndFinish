package com.example.tryandfinish.activities;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tryandfinish.R;
import com.example.tryandfinish.db.DatabaseHelper;
import com.example.tryandfinish.db.model.Filmovi;
import com.example.tryandfinish.db.model.Glumac;
import com.j256.ormlite.android.apptools.OpenHelperManager;

import java.sql.SQLException;

public class DetailActivity extends AppCompatActivity {

    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        try {
            showDetailAboutActor();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolBar_detail);
        setSupportActionBar(toolbar);



    }

    private void addMovie() {
        Dialog dialog = new Dialog(this);
        dialog.findViewById(R.layout.add_movie_dialog);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        final EditText editMovieName = (EditText) dialog.findViewById(R.id.edit_movie);

        Button buttonOk = (Button) dialog.findViewById(R.id.movie_button_ok);
        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String movieName = editMovieName.getText().toString();
                if (movieName.isEmpty()) {
                    Toast.makeText(DetailActivity.this, "Please fill movie name", Toast.LENGTH_LONG).show();
                    return;
                }
                Filmovi filmovi = new Filmovi();
                filmovi.setMovieName(movieName);
                try {
                    getDatabaseHelper().getmFilmoviDao().create(filmovi);
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }
        });

    }


    private void showDetailAboutActor() throws SQLException {


        Intent intent = getIntent();
        int id = intent.getExtras().getInt("Glumac");

        Glumac glumac = getDatabaseHelper().getmGlumacDao().queryForId(id);

        TextView textName = (TextView) findViewById(R.id.detail_name);
        textName.setText(glumac.getName());

        TextView textDescr = (TextView) findViewById(R.id.descr_detail);
        textDescr.setText(glumac.getOpis());

        ImageView imageView = (ImageView) findViewById(R.id.detail_imageView);
        Uri mUri = Uri.parse(glumac.getImage());
        imageView.setImageURI(mUri);


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.add_movie:
                 addMovie();
                break;
        }

        return super.onOptionsItemSelected(item);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail_add_movies, menu);
        return super.onCreateOptionsMenu(menu);


    }

    private DatabaseHelper getDatabaseHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
        }
        return databaseHelper;
    }

}
