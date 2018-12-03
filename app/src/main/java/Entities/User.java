package Entities;

public class User {
    private String group;
    private String userName;
    private String id;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getId(){
        return id;
    }

    public void setGroup(String group){
        this.group = group;
    }

    public String getGroup(){
        return group;
    }

    public void setId(String id){
        this.id = id;
    }


    @Override
    public String toString() {
        return "User{" +
                "userName='" + userName + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}
