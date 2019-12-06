package controller.mateMgt;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import dto.Mate;
import serivce.face.MateService;
import serivce.impl.MateServiceImpl;

@WebServlet("/mate/list")
public class MateListController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private MateService mateService = MateServiceImpl.getInstance();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		//유저 정보 셋팅
		Mate mate = new Mate();
		HttpSession session = req.getSession();
		try {
		mate.setUserno((int)session.getAttribute("userno"));
		} catch (NullPointerException e) {
			System.out.println("로그인 안했음");
		}
//		mate = mateService.getProj_no(mate);
		//내가 가입한 프로젝트 리스트 불러오기
		List<Mate> list = mateService.getProj_noByUserno(mate);
		req.setAttribute("list", list);
		
		
		req.getRequestDispatcher("/WEB-INF/views/mateMgt/mateList.jsp").forward(req, resp);
		
	}
	

}