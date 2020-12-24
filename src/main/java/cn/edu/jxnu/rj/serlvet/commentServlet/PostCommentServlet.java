package cn.edu.jxnu.rj.serlvet.commentServlet;

import cn.edu.jxnu.rj.domain.Comment;
import cn.edu.jxnu.rj.domain.User;
import cn.edu.jxnu.rj.service.CommentService;
import cn.edu.jxnu.rj.service.Impl.CommentServiceImpl;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "PostCommentServlet",urlPatterns = "/postComment")
public class PostCommentServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        //获取数据
        int workId = Integer.parseInt(request.getParameter("workId"));
        int workType = Integer.parseInt(request.getParameter("workType"));
        String commentContent = request.getParameter("commentContent");
        //模拟：登录存入session
        request.getSession().setAttribute("user",new User(2));

        User user = (User) (request.getSession().getAttribute("user"));

        //插入评论
        CommentService commentService = new CommentServiceImpl();
        Comment comment = commentService.add(new Comment(workId, workType, user.getUser_id(), commentContent));

        Gson gson = new GsonBuilder().setDateFormat("yyyy-mm-dd HH:mm:ss").create();
        String json = gson.toJson(comment);
        System.out.println(json);
        response.getWriter().write(json);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}