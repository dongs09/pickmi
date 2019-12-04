package serivce.impl;


import java.util.List;

import dao.face.MessageDao;
import dao.impl.MessageDaoImpl;
import dto.Chat;
import dto.Chatter;
import serivce.face.MessageService;

public class MessageServiceImpl implements MessageService {
	
	private MessageDao messageDao = new MessageDaoImpl();

	@Override
	public List<Chat> getChatList() {

		return messageDao.selectMsgAll();
	}

	@Override
	public List<Chat> getLastChatList(Chatter chatter) {
		return messageDao.selectLastMsg(chatter);
	}

}