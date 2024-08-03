
package com.ele.thecontacts;

public class Contact {

    private final long id;
    private String name;
    private final String email;
    private final String phone;
    private final String address;
    private final byte[] image;

    public Contact(long id, String name, String email, String phone, String address, byte[] image) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.image = image;
    }

    public Contact(String name, String email, String phone, String address, byte[] image, long id, String email1, String phone1, String address1, byte[] image1) {
        this.id = id;
        this.email = email1;
        this.phone = phone1;
        this.address = address1;
        this.image = image1;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public byte[] getImage() {
        return image;
    }
}
