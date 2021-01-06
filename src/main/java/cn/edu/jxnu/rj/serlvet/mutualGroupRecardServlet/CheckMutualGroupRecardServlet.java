package cn.edu.jxnu.rj.serlvet.mutualGroupRecardServlet;

import cn.edu.jxnu.rj.domain.Mutual_group_recard;
import cn.edu.jxnu.rj.domain.User;
import cn.edu.jxnu.rj.service.Impl.MutualServiceImpl;
import cn.edu.jxnu.rj.service.MutualService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

//查看组队申请记录
@WebServlet(name = "CheckMutualGroupRecardServlet",urlPatterns = "/checkMutualGroupRecard")
public class CheckMutualGroupRecardServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //设置编码
        resp.setContentType("text/html;charset=UTF-8");
        //从session中获取用户信息
//        HttpSession session = req.getSession();
        //      测试：登录存入用户数据
        User user = new User(2);
//        session.setAttribute("user", user1);
//        User user = (User) session.getAttribute("user");

        MutualService mutualService = new MutualServiceImpl();
        //调用DAO
        List<Mutual_group_recard> mutualgroupList = mutualService.checkallrecard(user.getUser_id());
        //将动态集合以json形式传给前端
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
        String json = gson.toJson(mutualgroupList);
        resp.getWriter().write(json);
    }
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    }

}
