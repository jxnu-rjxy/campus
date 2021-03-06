package cn.edu.jxnu.rj.service.Impl;

import cn.edu.jxnu.rj.dao.CommentDao;
import cn.edu.jxnu.rj.dao.DynamicDao;
import cn.edu.jxnu.rj.dao.MessageDao;
import cn.edu.jxnu.rj.dao.UserDao;
import cn.edu.jxnu.rj.dao.impl.CommentDaoImpl;
import cn.edu.jxnu.rj.dao.impl.DynamicDaoImpl;
import cn.edu.jxnu.rj.dao.impl.MessageDaoImpl;
import cn.edu.jxnu.rj.dao.impl.UserDaoImpl;
import cn.edu.jxnu.rj.domain.Comment;
import cn.edu.jxnu.rj.domain.Dynamic;
import cn.edu.jxnu.rj.domain.Message;
import cn.edu.jxnu.rj.service.CommentService;

import java.util.List;

public class CommentServiceImpl implements CommentService {
    CommentDao commentDao = new CommentDaoImpl();
    @Override
    public Comment add(Comment comment) {
        UserDao userDao = new UserDaoImpl();
        comment.setUserName(userDao.findById(comment.getUserId()).getUserName());
        int i = commentDao.insert(comment);
        System.out.println("评论id:"+i);
        MessageDao messageDao = new MessageDaoImpl();

        DynamicDao dynamicDao = new DynamicDaoImpl();
        Dynamic dynamic = dynamicDao.findById(comment.getWorkId(),comment.getUserId());
        System.out.println("被评论的动态是："+dynamic);
        if(comment.getUserId()!=dynamic.getUserId()){

            messageDao.insert(new Message(comment.getUserId(),dynamic.getUserId(),1,comment.getCommentContent(),dynamic.getDynamicId(),1,comment.getUserName(),dynamic.getUserName()));

        }
        return commentDao.findById(i);
    }

    @Override
    public void delete(int commentId) {
        Comment comment = commentDao.findById(commentId);
        commentDao.delete(comment);
    }

    @Override
    public List<Comment> getAllByWorkId(int workType, int WorkId) {
        List<Comment> comments = commentDao.findByWorkId(workType, WorkId);
        return comments;
    }
}
