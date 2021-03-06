package serivce.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import dao.face.ScheduleDao;
import dao.impl.ScheduleDaoImpl;
import dto.CheckList;
import dto.Schedule;
import serivce.face.ScheduleService;

public class ScheduleServiceImpl implements ScheduleService {

	private ScheduleDao scheduleDao = ScheduleDaoImpl.getInstance();

	private ScheduleServiceImpl() {
	}

	private static class Singleton {
		private static final ScheduleService instance = new ScheduleServiceImpl();
	}

	public static ScheduleService getInstance() {
		return Singleton.instance;
	}

	@Override
	public Schedule getSchedule(HttpServletRequest req) {
		Schedule schedule = new Schedule();

		String param = req.getParameter("scheduleno");
		if (param != null && !"".equals(param)) {
			try {
				schedule.setScheduleno(Integer.parseInt(param));
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}

		param = req.getParameter("proj_no");
		if (param != null && !"".equals(param)) {
			try {
				schedule.setProj_no(Integer.parseInt(param));
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}

		Object userno = req.getSession().getAttribute("userno");
		if (userno != null && !"".equals(userno)) {
			try {
				schedule.setUserno((Integer) userno);
			} catch (ClassCastException e) {
				e.printStackTrace();
			}
		}

		param = req.getParameter("title");
		if (param != null && !"".equals(param)) {
			schedule.setTitle(param);
		} else {
			schedule.setTitle("내용없음");
		}

		param = req.getParameter("content");
		if (param != null && !"".equals(param)) {
			schedule.setContent(param);
		} else {
			schedule.setContent("내용없음");
		}

		param = req.getParameter("schedule_date");
		if (param != null && !"".equals(param)) {
//			SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd");

			Date date = null;
			try {
				date = transFormat.parse(param);
			} catch (ParseException e) {
				e.printStackTrace();
			}

			schedule.setSchedule_date(date);
		}

		param = req.getParameter("due_date");
		if (param != null && !"".equals(param)) {
//			SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd");

			Date date = null;
			try {
				date = transFormat.parse(param);
			} catch (ParseException e) {
				e.printStackTrace();
			}

			schedule.setDue_date(date);
		}

		SimpleDateFormat format = new SimpleDateFormat();

		param = req.getParameter("curYear");
		if (param != null && !"".equals(param)) {
			schedule.setCurYear(param);
		} else {
			format.applyPattern("yyyy");

			schedule.setCurYear(format.format(new Date()));
		}

		param = req.getParameter("curMonth");
		if (param != null && !"".equals(param)) {
			schedule.setCurMonth(param);
		} else {
			format.applyPattern("MM");

			schedule.setCurMonth(format.format(new Date()));
		}

		return schedule;
	}
	
	@Override
	public CheckList getCheck(HttpServletRequest req) {
		CheckList checkList = new CheckList();

		String param = req.getParameter("checkno");
		if (param != null && !"".equals(param)) {
			try {
				checkList.setCheckno(Integer.parseInt(param));
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}

		param = req.getParameter("scheduleno");
		if (param != null && !"".equals(param)) {
			try {
				checkList.setScheduleno(Integer.parseInt(param));
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}

		param = req.getParameter("check_content");
		if (param != null && !"".equals(param)) {
			checkList.setCheck_content(param);
		} else {
			checkList.setCheck_content("빈 체크리스트");
		}
		
		param = req.getParameter("do_check");
		if (param != null && !"".equals(param)) {
			if (param.equals("true")) {
				checkList.setDo_check('1');;
			} else {
				checkList.setDo_check('0');
			}
		} else {
			checkList.setDo_check('0');
		}
		
		return checkList;
	}

	@Override
	public List<Schedule> getScheduleList(Schedule schedule) {
		return scheduleDao.selectAll(schedule);
	}

	@Override
	public Schedule getSchedule(Schedule schedule) {
		Schedule selectSchedule = scheduleDao.selectByScheduleno(schedule);

		selectSchedule.setCheckList(scheduleDao.selectCheckList(selectSchedule));

		return scheduleDao.selectByScheduleno(schedule);
	}

	@Override
	public void putSchedule(Schedule schedule) {
		schedule.setScheduleno(scheduleDao.selectScheduleno());
		scheduleDao.insertSchedule(schedule);
	}

	@Override
	public void modifySchedule(Schedule schedule) {
		scheduleDao.updateSchedule(schedule);
	}

	@Override
	public void removeSchedule(Schedule schedule) {
		CheckList temp = new CheckList();
		temp.setScheduleno(schedule.getScheduleno());
		scheduleDao.deleteCheckList(temp);
		scheduleDao.deleteSchedule(schedule);
	}

	@Override
	public boolean checkSchedule(Schedule schedule) {
		int result = scheduleDao.selectCntScheduleDate(schedule);

		if (result == 0) {
			return true;
		}

		return false;
	}

	// ============================================== 체크리스트 서비스

	@Override
	public List<CheckList> getCheckList(Schedule selectSchedule) {
		return scheduleDao.selectCheckList(selectSchedule);
	}

	@Override
	public void addCheck(CheckList checkList) {
		scheduleDao.insertCheck(checkList);
	}

	@Override
	public void modifyCheckDo_check(CheckList checkList) {
		scheduleDao.updateCheckDo_check(checkList);
	}

	@Override
	public void modifyCheckContent(CheckList checkList) {
		scheduleDao.updateCheckContent(checkList);
	}

	@Override
	public void removeCheck(CheckList checkList) {
		scheduleDao.deleteCheck(checkList);
	}

	@Override
	public void removeCheckList(CheckList checkList) {
		scheduleDao.deleteCheckList(checkList);
	}

	// =============================================================================================================

}
