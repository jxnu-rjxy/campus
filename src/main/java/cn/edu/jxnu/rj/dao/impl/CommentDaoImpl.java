package cn.edu.jxnu.rj.dao.impl;

import cn.edu.jxnu.rj.dao.CommentDao;
import cn.edu.jxnu.rj.dao.GiveLikeDao;
import cn.edu.jxnu.rj.dao.ReplyDao;
import cn.edu.jxnu.rj.domain.Comment;
import cn.edu.jxnu.rj.domain.Reply;
import cn.edu.jxnu.rj.util.Jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class CommentDaoImpl implements CommentDao {
    GiveLikeDao giveLikeDao = new GiveLikeDaoImpl();
    @Override
    public int insert(Comment comment) {
        String sql = "insert into db_campus_comment(work_id,work_type,user_id,user_name,comment_content) values(?,?,?,?,?);";
        Jdbc jdbc = new Jdbc();
        int commentId = jdbc.executeUpdate(sql, comment.getWorkId(), comment.getWorkType(), comment.getUserId(),comment.getUserName(), comment.getCommentContent());
        String commentsNum = null;
        if(comment.getWorkType()==0){
            //动态中的评论数加1
            commentsNum = "update db_campus_dynamic set dynamic_comments = dynamic_comments + 1 where dynamic_id = ?";
        }else if(comment.getWorkType()==1){
            commentsNum = "update db_campus_comment set comment_comments = comment_comments + 1 where comment_id = ?";
        }
        Jdbc jdbc1 = new Jdbc();
        jdbc1.executeUpdate(commentsNum,comment.getWorkId());
        return commentId;
    }

    @Override
    public List<Comment> findByWorkId(int work_type,int workId) {
        String sql = "select * from db_campus_comment where work_type = ? and work_id = ?";
        Jdbc jdbc = new Jdbc();
        ResultSet resultSet = jdbc.executeQuery(sql,work_type,workId);
        List<Comment> list = new ArrayList<>();
        try {
            while (resultSet.next()){
                int commentId = resultSet.getInt("comment_id");
                int workType = resultSet.getInt("work_type");
                int userId = resultSet.getInt("user_id");
                String userName = resultSet.getString("user_name");
                String commentContent = resultSet.getString("comment_content");
                Timestamp gmt_create = resultSet.getTimestamp("gmt_create");
                Timestamp gmt_modified = resultSet.getTimestamp("gmt_modified");
                int commentLikes = resultSet.getInt("comment_likes");
                Comment comment = new Comment(commentId,workId,workType,userId,userName,commentContent,gmt_create,commentLikes,false);

                //判断用户是否点赞
                boolean like = giveLikeDao.isLike(commentId, 3, userId);
                comment.setLike(like);

                //获取当前评论的回复
                ReplyDao replyDao = new ReplyDaoImpl();
                List<Reply> replyList = replyDao.getAllByCommentId(commentId);
                comment.setReplyList(replyList);

                list.add(comment);
            }
            return list;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }finally {
            jdbc.close();
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
                int work_id = resultSet.getInt("work_id");
                int workType = resultSet.getInt("work_type");
                int userId = resultSet.getInt("user_id");
                String userName = resultSet.getString("user_name");
                String commentContent = resultSet.getString("comment_content");
                Timestamp gmt_create = resultSet.getTimestamp("gmt_create");
                Timestamp gmt_modified = resultSet.getTimestamp("gmt_modified");
                int commentLikes = resultSet.getInt("comment_likes");
                Comment comment = new Comment(commentId,work_id,workType,userId,userName,commentContent,gmt_create,commentLikes,false);
                //判断当前用户是否点赞
                boolean like = giveLikeDao.isLike(commentId, 3, userId);
                comment.setLike(like);
                //获取当前评论的回复
                ReplyDao replyDao = new ReplyDaoImpl();
                List<Reply> replyList = replyDao.getAllByCommentId(commentId);
                comment.setReplyList(replyList);
                return comment;
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }finally {
            jdbc.close();
        }
        return null;
    }

    @Override
    public void delete(Comment comment) {
        String sql = "delete from db_campus_comment where comment_id = ?";
        Jdbc jdbc = new Jdbc();
        jdbc.executeUpdate(sql, comment.getCommentId());
        String commentsNum = null;
        if(comment.getWorkType()==0){
            //动态中的评论数减1
            commentsNum = "update db_campus_dynamic set dynamic_comments = dynamic_comments - 1 where dynamic_id = ?";
        }else if(comment.getWorkType()==1){
            commentsNum = "update db_campus_confession set confession_comments = comment_comments - 1 where comment_id = ?";
        }else if(comment.getWorkType()==2){
            commentsNum = "update db_campus_comment set comment_comments = comment_comments - 1 where comment_id = ?";
        }
        Jdbc jdbc1 = new Jdbc();
        jdbc1.executeUpdate(commentsNum,comment.getWorkId());
    }

    @Override
    public void deleteAllByWork(int workType, int workId) {//通过作品id删除其下的所有评论
        String sql = "delete from db_campus_comment where work_type = ? and work_id = ?";
        Jdbc jdbc = new Jdbc();
        jdbc.executeUpdate(sql,workType,workId);
    }
}
