package controller.projectMgt;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dto.Schedule;
import serivce.face.ScheduleService;
import serivce.impl.ScheduleServiceImpl;

/**
 * Servlet implementation class ScheduleAddController
 */
@WebServlet("/schedule/add")
public class ScheduleAddController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private ScheduleService scheduleServie = ScheduleServiceImpl.getInstance();

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		System.out.println(req.getParameter("proj_no"));
		System.out.println(req.getParameter("schedule_date"));
		
		Schedule schedule = scheduleServie.getSchedule(req);

		System.out.println(schedule);
		
		scheduleServie.putSchedule(schedule);
		
		resp.sendRedirect("/schedule/list?proj_no=" + schedule.getProj_no());
	}
}
