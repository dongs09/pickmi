package controller.mypage;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import dao.face.UserDao;
import dao.impl.UserDaoImpl;
import dto.Mate;
import dto.User;
import serivce.face.FileService;
import serivce.face.MateService;
import serivce.face.MyPageService;
import serivce.face.UserService;
import serivce.impl.FileServiceImpl;
import serivce.impl.MateServiceImpl;
import serivce.impl.MyPageServiceImpl;
import serivce.impl.UserServiceImpl;

/**
 * Servlet implementation class MyPageController
 */
@WebServlet("/mypage")
public class MyPageController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private FileService fileService = FileServiceImpl.getInstance();
	private MyPageService myPageService = MyPageServiceImpl.getInstance();
	private UserDao userDao = new UserDaoImpl();
	private UserService userService = new UserServiceImpl();
	private MateService mateService = MateServiceImpl.getInstance();
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		// 세션에서 현재 로그인 돼 있는 email 가져오기
		Object obj = req.getSession().getAttribute("email");
		String email = null;
		if (obj != null && !"".equals(obj)) {
			email = (String) obj;
		} else {
			resp.sendRedirect("/login");
			return;
		}
		
//		System.out.println(email);
		
		// email에 해당하는 회원정보 가져오기
		User userinfo = myPageService.getUser(email);
		

		
		HttpSession session = req.getSession();
		User user = new User();
		
		user.setEmail(email);
		user.setName(session.getAttribute("name").toString());
		if (session.getAttribute("userno") != null && !session.getAttribute("userno").equals(""))
			user.setUserno(Integer.parseInt(session.getAttribute("userno").toString()));
		
		userService.findPw(user);
		
		req.setAttribute("user", user);
		
		req.setAttribute("userinfo", userinfo);
		
		Mate mate = new Mate();
		
		mate.setUserno(user.getUserno());
		
		req.setAttribute("projList", mateService.getMyProjList(mate));

		resp.setCharacterEncoding("utf-8");
		req.getRequestDispatcher("/WEB-INF/views/mypage/mpmain.jsp")
		.forward(req, resp);
	
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//		System.out.println("두포스트");
		req.setCharacterEncoding("UTF-8");
		HttpSession session = req.getSession();
		
		User user = new User();
			
		user.setUserno((Integer)req.getSession().getAttribute("userno"));
//		user.setPhoto_originname((String)req.getAttribute("originname"));
//		user.setPhoto_storedname((String)req.getAttribute("storedname"));
		
//		System.out.println("user 객체 정보 : " + user); -- null
		
		fileService.myPhotoFile(req, user);
//		System.out.println("파일서비스갔다온 후 user :" + user);
				
		userDao.insertphoto(user);
//		System.out.println(user);
		// 헤더 사용자 사진 안바뀌게
		session.setAttribute("photo_storedname", user.getPhoto_storedname());

		
		//Ajax에서 다시 들어올 때 
		resp.setCharacterEncoding("utf-8");
		resp.getWriter().println(user.getPhoto_storedname());
	}
	
}
