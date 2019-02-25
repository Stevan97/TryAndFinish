package com.example.tryandfinish.db.model;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = Glumac.TABLE_GLUMAC)
public class Glumac {

    public static final String TABLE_GLUMAC = "actors";
    public static final String FIELD_ID = "id";
    public static final String FIELD_ACTOR_NAME = "name";
    public static final String FIELD_ACTOR_LASTNAME = "lastName";
    public static final String FIELD_ACTOR_OPIS = "description";
    public static final String FIELD_ACTOR_IMAGE = "image";
  //  public static final String FIELD_LIST_OF_MOVIES = "listMovies";

    @DatabaseField(columnName = FIELD_ID, generatedId = true)
    private int id;

    @DatabaseField(columnName = FIELD_ACTOR_NAME)
    private String name;

    @DatabaseField(columnName = FIELD_ACTOR_LASTNAME)
    private String lastName;

    @DatabaseField(columnName = FIELD_ACTOR_OPIS)
    private String description;

    @DatabaseField(columnName = FIELD_ACTOR_IMAGE)
    private String image;

   /* @ForeignCollectionField(foreignFieldName = FIELD_LIST_OF_MOVIES, eager = true)
    private ForeignCollection<Filmovi> filmovi; */

    public Glumac() {
        // prazan konstr za ormLite
    }

    public Glumac(String name, String lastName, String description, String image) {
        this.name = name;
        this.lastName = lastName;
        this.description = description;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getOpis() {
        return description;
    }

    public void setOpis(String opis) {
        this.description = opis;
    }


    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String toString() {
        return name;
    }

}
