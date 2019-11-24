package dao.face;

import java.util.List;

import dto.ProfileBoard;

public interface ProfileBoardDao {
	
	/**
	 * profileboard의 모든 게시글을 불러온다
	 * @return
	 */
	public List<ProfileBoard> selectProfileList();

}
