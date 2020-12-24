package cn.edu.jxnu.rj.dao.impl;

import cn.edu.jxnu.rj.dao.CommentDao;
import cn.edu.jxnu.rj.domain.Comment;
import cn.edu.jxnu.rj.util.Jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CommentDaoImpl implements CommentDao {
    @Override
    public int insert(Comment comment) {
        String sql = "insert into db_campus_comment(work_id,work_type,user_id,comment_content) values(?,?,?,?);";
        Jdbc jdbc = new Jdbc();
        int commentId = jdbc.executeUpdate(sql, comment.getWork_id(), comment.getWork_type(), comment.getUser_id(), comment.getComment_content());
        return commentId;
    }

    @Override
    public List<Comment> findByWorkId(int work_type,int workId) {
        String sql = "select * from db_campus_comment where work_type = ? and work_id = ?";
        Jdbc jdbc = new Jdbc();
        ResultSet resultSet = jdbc.executeQuery(sql, workId);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<Comment> list = new ArrayList<>();
        try {
            while (resultSet.next()){
                int commentId = resultSet.getInt("comment_id");
                int workType = resultSet.getInt("work_type");
                int userId = resultSet.getInt("user_id");
                String commentContent = resultSet.getString("comment_content");
                Date gmt_create = simpleDateFormat.parse(resultSet.getString("gmt_create"));
                Date gmt_modified = simpleDateFormat.parse(resultSet.getString("gmt_modified"));
                Comment comment = new Comment(commentId,workId,workType,userId,commentContent,gmt_create,gmt_modified);
                list.add(comment);
            }
            return list;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Comment findById(int findCommentId) {
        String sql = "select * from db_campus_comment where comment_id=?";
        Jdbc jdbc = new Jdbc();
        ResultSet resultSet = jdbc.executeQuery(sql, findCommentId);
        try {
            while (resultSet.next()){
                int commentId = resultSet.getInt("comment_id");
                int workId = resultSet.getInt("work_id");
                int workType = resultSet.getInt("work_type");
                int userId = resultSet.getInt("user_id");
                String commentContent = resultSet.getString("comment_content");
                Timestamp gmt_create = resultSet.getTimestamp("gmt_create");
                System.out.println(resultSet.getTimestamp("gmt_create"));
                System.out.println(gmt_create);
                Timestamp gmt_modified = resultSet.getTimestamp("gmt_modified");
                Comment comment = new Comment(commentId,workId,workType,userId,commentContent,gmt_create,gmt_modified);
                return comment;
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    @Override
    public void delete(int commentId) {
        String sql = "delete from db_campus_comment where comment_id = ?";
        Jdbc jdbc = new Jdbc();
        jdbc.executeUpdate(sql, commentId);
    }
}
