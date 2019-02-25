package com.example.tryandfinish.db.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = Filmovi.TABLE_NAME)
public class Filmovi {

    public static final String TABLE_NAME = "movies";
    public static final String FIELD_ID = "id";
    public static final String FIELD_movieNAME = "movieName";

    @DatabaseField(columnName = FIELD_ID, generatedId = true)
    private int id;

    @DatabaseField(columnName = FIELD_movieNAME)
    private String movieName;

    public Filmovi() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMovieName() {
        return movieName;
    }

    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }

    public String toString() {
        return movieName;
    }
}
