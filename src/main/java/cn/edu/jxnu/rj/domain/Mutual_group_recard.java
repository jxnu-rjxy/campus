package cn.edu.jxnu.rj.domain;

import java.util.Date;

public class Mutual_group_recard {
    private int user_id;
    private int mutual_id;
    private Date gmt_create;
    private Date gmt_modified;

    public Mutual_group_recard(int mutual_id, int user_id, Date gmt_create, Date gmt_modified) {
        this.mutual_id = mutual_id;
        this.user_id = user_id;
        this.gmt_create = gmt_create;
        this.gmt_modified = gmt_modified;
    }

    @Override
    public String toString() {
        return "Mutual_group_recard{" +
                "user_id=" + user_id +
                ", mutual_id=" + mutual_id +
                ", gmt_create=" + gmt_create +
                ", gmt_modified=" + gmt_modified +
                '}';
    }

    public Mutual_group_recard(int mutual_id) {
        this.mutual_id = mutual_id;
    }


    public void setMutualGroupRecard(int user_id){
        this.user_id = user_id;
    }
    public int getMutualGroupRecard(){
        return mutual_id;
    }
    public void setUser_id(int user_id){
        this.user_id = user_id;
    }
    public int getUser_id(){
        return user_id;
    }
    public void setMutual_id(int mutual_id){
        this.mutual_id = mutual_id;
    }
    public int getMutual_id(){
        return mutual_id;
    }
    public void setGmt_create(Date gmt_create){
        this.gmt_create = gmt_create;
    }
    public Date getGmt_create(){
        return gmt_create;
    }
    public void setGmt_modified(Date gmt_modified){
        this.gmt_modified = gmt_modified;
    }
    public Date getGmt_modified(){
        return gmt_modified;
    }
}
