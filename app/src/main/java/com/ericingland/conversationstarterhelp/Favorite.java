package com.ericingland.conversationstarterhelp;

class Favorite {

    //private variables
    private int _id;
    private String _position;
    private String _string;

    // Empty constructor
    public Favorite() {

    }

    // constructor
    public Favorite(int id, String position, String string) {
        this._id = id;
        this._position = position;
        this._string = string;
    }

    // constructor
    public Favorite(String position, String string) {
        this._position = position;
        this._string = string;
    }

    // getting ID
    public int getID() {
        return this._id;
    }

    // setting id
    public void setID(int id) {
        this._id = id;
    }

    // getting position
    public String getPosition() {
        return this._position;
    }

    // setting position
    public void setPosition(String position) {
        this._position = position;
    }

    // getting string
    public String getString() {
        return this._string;
    }

    // setting string
    public void setString(String string) {
        this._string = string;
    }
}