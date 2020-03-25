package com.coronosafe.approval.jdbc.data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class DigiUserRole {
    @Id
    @Column
    private long role_id;
    @Column
    private String roleName;

    public DigiUserRole(long roleId, String roleName){
        this.role_id = roleId;
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
