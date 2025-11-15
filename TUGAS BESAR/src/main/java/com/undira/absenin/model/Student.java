package com.undira.absenin.model;

public class Student {
    private int id;
    private String nim;
    private String nama;
    private String email;
    private String no_hp;

    public Student() {}

    public Student(int id, String nim, String nama, String email, String no_hp) {
        this.id = id;
        this.nim = nim;
        this.nama = nama;
        this.email = email;
        this.no_hp = no_hp;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNim() { return nim; }
    public void setNim(String nim) { this.nim = nim; }
    public String getNama() { return nama; }
    public void setNama(String nama) { this.nama = nama; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getNo_hp() { return no_hp; }
    public void setNo_hp(String no_hp) { this.no_hp = no_hp; }
}