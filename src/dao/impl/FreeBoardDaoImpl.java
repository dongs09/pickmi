package dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import dao.face.FreeBoardDao;
import dbutil.DBConn;
import dto.FreeBoard;
import dto.ProjectBoard;
import util.Paging;

public class FreeBoardDaoImpl implements FreeBoardDao {
	private Connection conn = null;
	private PreparedStatement ps = null;
	private ResultSet rs = null;

	private FreeBoardDaoImpl() {
		conn = DBConn.getConnection();
	}

	private static class Singleton {
		private static final FreeBoardDao instance = new FreeBoardDaoImpl();
	}

	public static FreeBoardDao getInstance() {
		return Singleton.instance;
	}

	@Override
	public int getNextBoardno() {
		String sql = "SELECT freeboard_seq.nextval FROM dual";

		int result = 0;

		try {
			ps = conn.prepareStatement(sql);

			rs = ps.executeQuery();

			while (rs.next()) {
				result = rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (ps != null)
					ps.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return result;
	}

	@Override
	public int selectCntAll(String search, int searchno, int categoryno) {
		String sql = "SELECT count(*) FROM freeboard";

		// 검색어가 존재하거나 카테고리가 존재한다면
		if (search != null || categoryno != 0) {
			// where 추가
			sql += " WHERE 1 = 1";

			// 검색어가 존재한다면 검색조건 추가
			if (search != null) {
				if (searchno == 2) {
					// 내용으로 검색할 경우
					sql += " AND free_content LIKE '%' || ? || '%'";
				} else if (searchno == 3) {
					// 제목 & 내용으로 검색할 경우
					sql += " AND (free_title LIKE '%' || ? || '%'";
					sql += " OR free_content LIKE '%' || ? || '%')";
				} else {
					// 제목으로 검색하거나 searchno가 2,3이 아닐 때
					sql += " AND free_title LIKE '%' || ? || '%'";
				}

			}

			// 카테고리가 존재한다면 카테고리 추가
			if (categoryno != 0) {
				sql += " AND categoryno = ?";
			}
		}

		int total = 0;

		try {
			ps = conn.prepareStatement(sql);

			// 검색어만 존재할 경우
			if (search != null && categoryno == 0) {
				ps.setString(1, search);
				// 제목&내용으로 검색할 경우
				if (searchno == 3) {
					ps.setString(2, search);
				}
			} else if (categoryno != 0 && search == null) {
				// 검색어가 존재하지 않을 경우
				ps.setInt(1, categoryno);
			} else if (search != null && categoryno != 0) {
				// 검색어와 카테고리가 존재할 경우
				ps.setString(1, search);

				if (searchno == 3) {
					// 제목&내용으로 검색할 경우
					ps.setString(2, search);
					ps.setInt(3, categoryno);
				} else {
					ps.setInt(2, categoryno);
				}
			}

			rs = ps.executeQuery();

			while (rs.next()) {
				total = rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (ps != null)
					ps.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return total;
	}

	@Override
	public List<FreeBoard> selectAll(Paging paging) {
		String sql = "";
		sql += "select * from (";
		sql += "  select rownum rnum, B.* FROM(";
		sql += "   select free_no, categoryno, userno, free_title, free_content, free_time, views, "
				+ " (SELECT name FROM user_table WHERE freeboard.userno = userno) username"
				+ " from freeboard";

		// 검색어나 카테고리가 존재한다면 WHERE절 추가
		if (paging.getSearch() != null || paging.getCategoryno() != 0) {
			sql += " WHERE 1 = 1";

			// 검색어가 존재할 경우
			if (paging.getSearch() != null) {
				if (paging.getSearchno() == 2) {
					// 내용으로 검색할 경우
					sql += " AND free_content LIKE '%' || ? || '%'";
				} else if (paging.getSearchno() == 3) {
					// 제목 & 내용으로 검색할 경우
					sql += " AND (free_title LIKE '%' || ? || '%'";
					sql += " OR free_content LIKE '%' || ? || '%')";
				} else {
					// 제목으로 검색하거나 searchno가 2,3이 아닐 때
					sql += " AND free_title LIKE '%' || ? || '%'";
				}
			}

			if (paging.getCategoryno() != 0) {
				sql += " AND categoryno = ?";
			}
		}

		sql += "   order by free_no desc";
		sql += "  ) B";
		sql += "  ORDER BY rnum";
		sql += " ) BOARD";
		sql += " WHERE rnum BETWEEN ? AND ?";

		List<FreeBoard> list = new ArrayList<FreeBoard>();

		try {
			ps = conn.prepareStatement(sql);

			if (paging.getSearch() != null && paging.getCategoryno() == 0) {
				ps.setString(1, paging.getSearch());
				if (paging.getSearchno() == 3) {
					// 제목 & 내용으로 검색할 때
					ps.setString(2, paging.getSearch());
					ps.setInt(3, paging.getStartNo());
					ps.setInt(4, paging.getEndNo());
				} else {
					// 제목이나 내용으로 검색할 때
					ps.setInt(2, paging.getStartNo());
					ps.setInt(3, paging.getEndNo());
				}
			} else if (paging.getCategoryno() != 0 && paging.getSearch() == null) {
				// 검색어가 없고 카테고리만 존재한다면
				ps.setInt(1, paging.getCategoryno());
				ps.setInt(2, paging.getStartNo());
				ps.setInt(3, paging.getEndNo());
			} else if (paging.getSearch() != null && paging.getCategoryno() != 0) {
				// 검색어가 존재하고 카테고리가 존재할 때
				ps.setString(1, paging.getSearch());
				if (paging.getSearchno() == 3) {
					// 제목 & 내용으로 검색할 때
					ps.setString(2, paging.getSearch());
					ps.setInt(3, paging.getCategoryno());
					ps.setInt(4, paging.getStartNo());
					ps.setInt(5, paging.getEndNo());
				} else {
					// 제목으로 검색하거나 내용으로 검색할 때
					ps.setInt(2, paging.getCategoryno());
					ps.setInt(3, paging.getStartNo());
					ps.setInt(4, paging.getEndNo());
				}
			} else {
				// 검색어, 카테고리가 존재하지 않는 경우 (전체 리스트)
				ps.setInt(1, paging.getStartNo());
				ps.setInt(2, paging.getEndNo());
			}

			rs = ps.executeQuery();

			while (rs.next()) {
				FreeBoard freeBoard = new FreeBoard();

				freeBoard.setFree_no(rs.getInt("free_no"));
				freeBoard.setCategoryno(rs.getInt("categoryno"));
				freeBoard.setUserno(rs.getInt("userno"));
				freeBoard.setUsername(rs.getString("username"));
				freeBoard.setFree_title(rs.getString("free_title"));
				freeBoard.setFree_content(rs.getString("free_content"));
				freeBoard.setFree_time(rs.getDate("free_time"));
				freeBoard.setViews(rs.getInt("views"));

				list.add(freeBoard);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			if (rs != null)
				rs.close();
			if (ps != null)
				ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return list;
	}

	@Override
	public int insertBoard(FreeBoard freeBoard) {
		String sql = "INSERT INTO freeboard VALUES (?, ?, ?, ?, ?, sysdate, 0)";

		int result = 0;

		try {
			ps = conn.prepareStatement(sql);

			ps.setInt(1, freeBoard.getFree_no());
			ps.setInt(2, freeBoard.getCategoryno());
			ps.setInt(3, freeBoard.getUserno());
			ps.setString(4, freeBoard.getFree_title());
			ps.setString(5, freeBoard.getFree_content());

			result = ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (ps != null)
					ps.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	@Override
	public int updateBoard(FreeBoard freeBoard) {
		String sql = "UPDATE freeboard SET free_title = ?, free_content = ?, categoryno = ? WHERE free_no = ?";

		int result = 0;

		try {
			ps = conn.prepareStatement(sql);

			ps.setString(1, freeBoard.getFree_title());
			ps.setString(2, freeBoard.getFree_content());
			ps.setInt(3, freeBoard.getCategoryno());
			ps.setInt(4, freeBoard.getFree_no());

			result = ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (ps != null)
					ps.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return result;
	}

	@Override
	public int deleteBoard(FreeBoard freeBoard) {
		String sql = "DELETE FROM freeboard WHERE free_no = ?";

		int result = 0;

		try {
			ps = conn.prepareStatement(sql);

			ps.setInt(1, freeBoard.getFree_no());

			result = ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (ps != null)
					ps.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return result;
	}

	@Override
	public FreeBoard boardView(FreeBoard freeBoard) {
		String sql = "SELECT * FROM freeboard WHERE free_no = ?";

		FreeBoard result = null;

		try {
			ps = conn.prepareStatement(sql);

			ps.setInt(1, freeBoard.getFree_no());

			rs = ps.executeQuery();

			while (rs.next()) {
				result = new FreeBoard();

				result.setFree_no(rs.getInt("free_no"));
				result.setCategoryno(rs.getInt("categoryno"));
				result.setUserno(rs.getInt("userno"));
				result.setFree_title(rs.getString("free_title"));
				result.setFree_content(rs.getString("free_content"));
				result.setFree_time(rs.getDate("free_time"));
				result.setViews(rs.getInt("views"));

			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (ps != null)
					ps.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return result;
	}

	@Override
	public void countViews(FreeBoard freeBoard) {
		String sql = "UPDATE freeboard SET views = views + 1 WHERE free_no = ?";

		try {
			ps = conn.prepareStatement(sql);

			ps.setInt(1, freeBoard.getFree_no());

			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (ps != null)
					ps.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public List<FreeBoard> selectListToMain() {
		conn = DBConn.getConnection();
		
		String sql = "";
		sql += "SELECT free_no, categoryno, free_title, free_content, free_time, views"; 
		sql += " , (SELECT name FROM user_table WHERE freeboard.userno = userno) username";
		sql += " FROM (SELECT * FROM freeboard ORDER BY free_time DESC) freeboard";
		sql += " WHERE ROWNUM <= 3";
		
		List<FreeBoard> list = new ArrayList<FreeBoard>();
		
		try {
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			
			while(rs.next()) {
				FreeBoard freeBoard = new FreeBoard();
				
				freeBoard.setFree_no(rs.getInt("free_no"));
				freeBoard.setCategoryno(rs.getInt("categoryno"));
				freeBoard.setUsername(rs.getString("username"));
				freeBoard.setFree_title(rs.getString("free_title"));
				freeBoard.setFree_content(rs.getString("free_content"));
				freeBoard.setFree_time(rs.getDate("free_time"));
				freeBoard.setViews(rs.getInt("views"));
				
				list.add(freeBoard);
			}
		} catch (SQLException e) {

			e.printStackTrace();
		} finally {
			try {
				if(ps!=null)	ps.close();
				if(rs!=null)	rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return list;
	}

}
