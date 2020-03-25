package com.coronosafe.approval.jdbc.data;

import javax.persistence.*;

@Entity
public class DigiUser {
    @Column
    private String firstName;
    @Column
    private String lastName;
    @OneToOne
    @JoinColumn(name = "role_id", referencedColumnName = "role_id")
    private DigiUserRole role;
    @Column
    private String userName;
    @Column
    private String password;
    @Column
    @Id
    private long id;

    protected DigiUser(){}

    public DigiUser(long id,String firstName,String lastName,String userName,String password){
        this.id =id;
        this.firstName=firstName;
        this.lastName=lastName;
        this.userName=userName;
        this.password=password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public DigiUserRole getRole() {
        return role;
    }

    public void setRole(DigiUserRole role) {
        this.role = role;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public String toString(){
        return this.firstName+" "+this.lastName;
    }
}
