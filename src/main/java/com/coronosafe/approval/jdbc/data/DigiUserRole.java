package com.coronosafe.approval.jdbc.data;

import javax.persistence.*;

@Entity
public class DigiUserRole {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long role_id;
    @Column
    private String roleName;

    public DigiUserRole(String roleName){
        this.roleName=roleName;
    }

    protected DigiUserRole(){

    }

    public long getRole_id() {
        return role_id;
    }

    public void setRole_id(long role_id) {
        this.role_id = role_id;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
}
