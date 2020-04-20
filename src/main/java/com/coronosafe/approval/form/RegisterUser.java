package com.coronosafe.approval.form;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class RegisterUser {
    @NotNull(message = "Please enter first name")
    private String firstName;
    @NotNull(message = "Please enter last name")
    private String lastName;
    private String roleId;
    @NotNull(message = "Please enter user name")
    @Size(min=4)
    private String userName;
    @NotNull(message = "Please enter password")
    @Size(min=5)
    private String password;
    @Email(message = "Please enter a valid email")
    private String email;
    @Pattern(regexp="(^$|[0-9]{10})", message = "Please enter only numbers with 10 digits")
    private String mobile;

    public RegisterUser() {
    }

    public RegisterUser(String firstName, String lastName, String roleId, String userName, String password, String email, String mobile) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.roleId = roleId;
        this.userName = userName;
        this.password = password;
        this.email = email;
        this.mobile = mobile;
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

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String toString(){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("First Name :").append(firstName)
            .append("Last Name :").append(lastName)
            .append("Email :").append(email)
            .append("Mobile :").append(mobile)
            .append("UserName :").append(userName)
            .append("Role Id :").append(roleId);

        return stringBuilder.toString();
    }
}
