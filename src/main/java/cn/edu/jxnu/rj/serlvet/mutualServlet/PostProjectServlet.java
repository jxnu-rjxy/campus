package cn.edu.jxnu.rj.serlvet.mutualServlet;

import cn.edu.jxnu.rj.domain.Mutual;
import cn.edu.jxnu.rj.domain.User;
import cn.edu.jxnu.rj.service.Impl.MutualServiceImpl;
import cn.edu.jxnu.rj.service.MutualService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpSession;
import java.io.IOException;
//发布互助项目
@WebServlet(name = "PostProjectServlet",urlPatterns = "/postProject")
public class PostProjectServlet extends javax.servlet.http.HttpServlet {
    protected void doPost(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {
        //设置字符编码
        request.setCharacterEncoding("utf-8");
        response.setCharacterEncoding("utf-8");
        //从表单获取用户输入数据
        String mutual_title = request.getParameter("mutual_title");
        String mutual_content = request.getParameter("mutual_content");

        //从session中获取用户信息
        HttpSession session = request.getSession();
        User user1 = new User(2);
        session.setAttribute("user", user1);

        //发表互助项目
        User user = (User) session.getAttribute("user");
        MutualService mutualService = new MutualServiceImpl();
        int mutual= mutualService.post(new Mutual(user.getUser_id(),mutual_title,mutual_content));

        /*将发表的动态传给前端显示*/
        Gson gson = new GsonBuilder().setDateFormat("yyyy-mm-dd HH:mm:ss").create();
        String json = gson.toJson(mutual);
        response.getWriter().write(json);
    }


    protected void doGet(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {

    }
}
