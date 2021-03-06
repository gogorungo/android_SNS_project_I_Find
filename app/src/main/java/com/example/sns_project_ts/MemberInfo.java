package com.example.sns_project_ts;

public class MemberInfo {
    private String name;
    private String phoneNumber;
    private String birthDay;
    private String address;
    private String email;
    private String userUid;

    public MemberInfo(String name, String phoneNumber, String birthDay, String address, String email, String userUid){
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.birthDay = birthDay;
        this.address = address;
        this.email = email;
        this.userUid = userUid;
    }

    public String getName(){
        return this.name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getPhoneNumber(){
        return this.phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber){
        this.phoneNumber = phoneNumber;
    }

    public String getBirthDay(){
        return this.birthDay;
    }

    public void setBirthDay(String birthDay){
        this.birthDay = birthDay;
    }

    public String getAddress(){
        return this.address;
    }

    public void setAddress(String address){
        this.address = address;
    }

    public String getEmail(){
        return this.email;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public String getUserUid(){
        return this.userUid;
    }

    public void setUserUid(String userUid){
        this.userUid = userUid;
    }

}
