package cn.edu.jxnu.rj.dao.impl;

import cn.edu.jxnu.rj.dao.DynamicDao;
import cn.edu.jxnu.rj.dao.GiveLikeDao;
import cn.edu.jxnu.rj.dao.UserDao;
import cn.edu.jxnu.rj.domain.Dynamic;
import cn.edu.jxnu.rj.domain.User;
import cn.edu.jxnu.rj.util.Jdbc;
import redis.clients.jedis.Jedis;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class DynamicDaoImpl implements DynamicDao {
    GiveLikeDao giveLikeDao = new GiveLikeDaoImpl();
    UserDao userDao = new UserDaoImpl();
    Jedis jedis = new Jedis("118.31.173.242",6379);

    @Override
    public List<String> getLatest(int start,int nums) {
        List<String> list = jedis.lrange("campus:dynamic:latest", start, (start + nums - 1));
        jedis.close();
        if(list.size()<nums){//如果已经超过了redis存储的动态数量，则向mysql查询
            list = new ArrayList<>();
            String sql = "select dynamic_id from db_campus_dynamic order by dynamic_id desc limit ?,?";
            Jdbc jdbc = new Jdbc();
            ResultSet resultSet = jdbc.executeQuery(sql,start,nums);
            try{
                while (resultSet.next()){
                    String dynamicId = resultSet.getInt("dynamic_id")+"";
                    list.add(dynamicId);
                }
                return list;
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        return list;
    }

    @Override
    public List<Dynamic> getByIdSet(List<String> ids) {
        List<Dynamic> list = new ArrayList<>();
        for (int i = 0; i < ids.size() ; i++) {
            String sql = "select * from db_campus_dynamic where dynamic_id = ?";
            Jdbc jdbc = new Jdbc();
            ResultSet resultSet = jdbc.executeQuery(sql, ids.get(i));
            try{
                while (resultSet.next()){
                    Dynamic dynamic = null;
                    //封装对象
                    int dynamic_id = Integer.parseInt(resultSet.getString("dynamic_id"));
                    int user_id = Integer.parseInt(resultSet.getString("user_id"));
                    String dynamic_content = resultSet.getString("dynamic_content");
                    int dynamic_status = Integer.parseInt(resultSet.getString("dynamic_status"));
                    Timestamp gmt_create = resultSet.getTimestamp("gmt_create");
                    List<String> image_path = getImages(dynamic_id);
                    int dynamicComments = resultSet.getInt("dynamic_comments");
                    int dynamicLikes = resultSet.getInt("dynamic_likes");
                    int dynamicForwards = resultSet.getInt("dynamic_forwards");

                    User user = userDao.findById(user_id);

                    dynamic = new Dynamic(dynamic_id,
                            user_id,
                            user.getUserName(),
                            user.getUserSchool(),
                            user.getUserImage(),
                            dynamic_content,
                            dynamic_status,
                            gmt_create,
                            image_path,
                            dynamicLikes,
                            dynamicForwards,
                            dynamicComments);
                    list.add(dynamic);
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                return null;
            }finally {
                jdbc.close();
            }
        }
        return list;
    }

    @Override
    public List<Dynamic> findByUserId(int userId) {
        //jdbc查询动态集合
        Jdbc jdbc = new Jdbc();
        String sql = "select * from db_campus_dynamic where user_id=? order by gmt_create desc";
        ResultSet resultSet = jdbc.executeQuery(sql, userId);

        List<Dynamic> list = new ArrayList<>();
        //处理
        try {
            while(resultSet.next()){

                Dynamic dynamic = null;
                //封装对象
                int dynamic_id = Integer.parseInt(resultSet.getString("dynamic_id"));
                int user_id = Integer.parseInt(resultSet.getString("user_id"));
                String dynamic_content = resultSet.getString("dynamic_content");
                int dynamic_status = Integer.parseInt(resultSet.getString("dynamic_status"));
                Timestamp gmt_create = resultSet.getTimestamp("gmt_create");
                List<String> image_path = getImages(dynamic_id);
                int dynamicComments = resultSet.getInt("dynamic_comments");
                int dynamicLikes = resultSet.getInt("dynamic_likes");
                int dynamicForwards = resultSet.getInt("dynamic_forwards");

                User user = userDao.findById(user_id);

                dynamic = new Dynamic(dynamic_id,
                        user_id,
                        user.getUserName(),
                        user.getUserSchool(),
                        user.getUserImage(),
                        dynamic_content,
                        dynamic_status,
                        gmt_create,
                        image_path,
                        dynamicLikes,
                        dynamicForwards,
                        dynamicComments);

                boolean like = giveLikeDao.isLike(dynamic_id, 0, userId);
                dynamic.setLike(like);
                //将对象加入集合
                list.add(dynamic);
            }
            return list;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    @Override
    public List<String> getImages(int dynamicId) {
        String sql = "select image_path from db_campus_image where dynamic_id = ?";
        Jdbc jdbc = new Jdbc();
        ResultSet resultSet = jdbc.executeQuery(sql, dynamicId);
        List<String> list = new ArrayList<>();
        try{
            while(resultSet.next()){
                String imagePath = resultSet.getString("image_path");
                list.add(imagePath);
            }
            return list;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            jdbc.close();
        }
        return null;
    }

    @Override
    public List<Boolean> isLike(int userId, List<String> ids) {
        List<Boolean> list = new ArrayList<>();
        for (int i = 0; i < ids.size() ; i++) {
            String sql = "select * from db_campus_giveLike where user_id=? and work_type = ? and work_id = ? limit 1";
            Jdbc jdbc = new Jdbc();
            ResultSet resultSet = jdbc.executeQuery(sql, userId, 0, ids.get(i));
            try {
                if(resultSet.next()){
                    list.add(true);
                }else {
                    list.add(false);
                }

            } catch (SQLException throwables) {
                list.add(false);
                throwables.printStackTrace();
            }finally {
                jdbc.close();
            }
        }
        return list;
    }

    @Override
    public Dynamic findById(int DynamicId,int userId) {
        Jdbc jdbc = new Jdbc();
        String sql = "select * from db_campus_dynamic where dynamic_id=?";
        ResultSet resultSet = jdbc.executeQuery(sql,DynamicId);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //处理
        try {  
            Dynamic dynamic = null;
            while(resultSet.next()){
                //封装对象
                int dynamic_id = Integer.parseInt(resultSet.getString("dynamic_id"));
                int user_id = Integer.parseInt(resultSet.getString("user_id"));
                String dynamic_content = resultSet.getString("dynamic_content");
                int dynamic_status = Integer.parseInt(resultSet.getString("dynamic_status"));
                Timestamp gmt_create = resultSet.getTimestamp("gmt_create");
                List<String> image_path = getImages(dynamic_id);
                int dynamicComments = resultSet.getInt("dynamic_comments");
                int dynamicLikes = resultSet.getInt("dynamic_likes");
                int dynamicForwards = resultSet.getInt("dynamic_forwards");

                User user = userDao.findById(user_id);

                dynamic = new Dynamic(dynamic_id,
                        user_id,
                        user.getUserName(),
                        user.getUserSchool(),
                        user.getUserImage(),
                        dynamic_content,
                        dynamic_status,
                        gmt_create,
                        image_path,
                        dynamicLikes,
                        dynamicForwards,
                        dynamicComments);
                boolean like = giveLikeDao.isLike(dynamic_id, 0, userId);
                dynamic.setLike(like);
            }
            return dynamic;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }finally {
            jdbc.close();
        }
        return null;
    }

    @Override
    public int InsertDynamic(Dynamic dynamic) {
        //插入动态帖子
        String sql  = "insert into db_campus_dynamic(user_id,dynamic_content,dynamic_status) values(?,?,?);";
        Jdbc jdbc = new Jdbc();
        System.out.println("准备插入的数据是："+dynamic);

        int id = jdbc.executeUpdate(sql, dynamic.getUserId(), dynamic.getDynamicContent(), dynamic.getDynamicStatus());

        //插入图片
        List<String> imagePath = dynamic.getImagePath();
        for (int i = 0; i < imagePath.size(); i++) {
            String imgSql = "insert into db_campus_image(dynamic_id,image_path) values(?,?)";
            Jdbc jdbc1 = new Jdbc();
            jdbc1.executeUpdate(imgSql,id,imagePath.get(i));
        }

        return id;
    }

    @Override
    public void deleteDynamic(int dynamic_id) {
        String sql  = "delete from db_campus_dynamic where dynamic_id=?;";
        Jdbc jdbc = new Jdbc();
        jdbc.executeUpdate(sql,dynamic_id);

        String deleteComment = "delete from db_campus_comment where work_id=? and work_type = 0;";
        Jdbc jdbc2 = new Jdbc();
        jdbc2.executeUpdate(deleteComment,dynamic_id);

        String deleteLike = "delete from db_campus_giveLike where work_id=? and work_type = 0;";
        Jdbc jdbc3 = new Jdbc();
        jdbc3.executeUpdate(deleteLike,dynamic_id);
    }
}
