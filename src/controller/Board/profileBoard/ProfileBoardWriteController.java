package controller.Board.profileBoard;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import serivce.face.FileService;
import serivce.impl.FileServiceImpl;

/**
 * Servlet implementation class ProfileBoardWriteController
 */
@WebServlet("/profileBoard/write")
public class ProfileBoardWriteController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private FileService fileService = FileServiceImpl.getInstance();
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		HttpSession session = req.getSession();
		
		
		int userno = (int)session.getAttribute("userno");
//		System.out.println("profile write controller :" + userno);
		
		req.setAttribute("userno", userno );
		req.setAttribute("username", session.getAttribute("name") );
//		System.out.println("profileBoard write controller : " + session.getAttribute("name"));
		
		//view 전달
		req.getRequestDispatcher("/WEB-INF/views/board/profileBoard/write.jsp").forward(req, resp);
		
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		//write에 저장
		fileService.writeBoard(req, 1);
		
		//list로 리다이렉트
		resp.sendRedirect("/profileBoard/list");
	}

}
