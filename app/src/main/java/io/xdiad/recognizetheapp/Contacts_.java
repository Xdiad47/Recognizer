package io.xdiad.recognizetheapp;

public class Contacts_ {

    String Name;
    String phone_number;

    public Contacts_(){

    }

    public Contacts_(String name, String phone_number) {
        Name = name;
        this.phone_number = phone_number;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }
}
