package controller.main;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dto.CompBoard;
import dto.FreeBoard;
import dto.ProfileBoard;
import dto.ProjectBoard;
import serivce.face.CompBoardService;
import serivce.face.FreeBoardService;
import serivce.face.ProfileBoardService;
import serivce.face.ProjectBoardService;
import serivce.face.UserService;
import serivce.impl.CompBoardServiceImpl;
import serivce.impl.FreeBoardServiceImpl;
import serivce.impl.ProfileBoardServiceImpl;
import serivce.impl.ProjectBoardServiceImpl;
import serivce.impl.UserServiceImpl;

@WebServlet("/main")
public class MainController extends HttpServlet {
	
	private UserService userService = new UserServiceImpl();
	private ProjectBoardService projectBoardService = new ProjectBoardServiceImpl();
	private CompBoardService compBoardService = new CompBoardServiceImpl();
	private ProfileBoardService profileBoardService = new ProfileBoardServiceImpl();
	private FreeBoardService freeBoardService = new FreeBoardServiceImpl();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 프로필 리스트
		List<ProfileBoard> profileBoard = profileBoardService.getMainProfileList();
		req.setAttribute("profileBoard", profileBoard);
		
		// 프로젝트 리스트
		List<ProjectBoard> projectBoard = projectBoardService.getMainProjectList();
		req.setAttribute("projectBoard", projectBoard);
		
		// 완성된 프로젝트 리스트
		List<CompBoard> compBoard = compBoardService.getMainCompList();
		req.setAttribute("compBoard", compBoard);
		
		// 자유게시판 리스트
		List<FreeBoard> freeBoard = freeBoardService.getMainFreeList();
		req.setAttribute("freeBoard", freeBoard);
		
		
		req.getRequestDispatcher("/WEB-INF/views/main/main.jsp").forward(req, resp);
	}
}
