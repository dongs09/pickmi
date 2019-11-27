package dao.face;

import java.util.List;

import dto.Manager;
import dto.ProfileBoard;
import dto.User;
import util.Paging;

public interface ManagerDao {

	
// Login -----
	/**
	 * mgrid와 mgrpw가 일치하는 관리자 수 조회
	 * @param mgr - mgrid와 mgrpw를 가진 관리자
	 * @return int -1(존재하는 회원), 0(존재하지않는회원), -1(에러)
	 */
	public int selectCntMemberByMgridAndMgrpw(Manager mgr);
	
	/**
	 * mgrid로 관리자 정보 조회
	 * @param mgr - 조회할 관리자, mgrid 필수
	 * @return Manager 조회된 결과
	 */
	public Manager selectMgrByMgr(Manager mgr);
// ----- Login

// ----- 검색	
	/**
	 * 총 게시글 수 조회
	 * @param search 
	 * @return int - 총 게시글 수
	 */
	public int selectCntAll(String search);
	
// 검색 -----
	
// ----- UserList 	
	
	/**
	 * 게시글 전체 목록 조회
	 * @return List<ProfileBoard> - 조회된 게시글 목록
	 */
	public List<User> userselectAll();

	
	/**
	 * 페이징 대상 게시글 목록 조회
	 * @param paging - 페이징 정보
	 * @return List<ProfileBoard> - 조회된 게시글 목록
	 */
	public List<User> userselectAll(Paging paging);	

// UserList -----
}
