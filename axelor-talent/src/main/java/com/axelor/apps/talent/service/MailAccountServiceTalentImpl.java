package com.axelor.apps.talent.service;

import java.io.IOException;
import java.util.Date;

import javax.mail.MessagingException;

import com.axelor.apps.base.db.AppRecruitment;
import com.axelor.apps.base.db.repo.AppRecruitmentRepository;
import com.axelor.apps.base.service.message.MailAccountServiceBaseImpl;
import com.axelor.apps.base.service.user.UserService;
import com.axelor.apps.message.db.MailAccount;
import com.axelor.apps.message.db.Message;
import com.axelor.mail.MailParser;
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;

public class MailAccountServiceTalentImpl extends MailAccountServiceBaseImpl{
	
	@Inject
	public MailAccountServiceTalentImpl(UserService userService) {
		super(userService);
	}
	
	@Inject
	private AppRecruitmentRepository appRecruitmentRepo; 
	
	@Inject
	private JobPositionService jobPositionService;
	
	@Transactional
	@Override
	public Message createMessage(MailAccount mailAccount, MailParser parser, Date date) throws MessagingException, IOException {
		
		Message message = super.createMessage(mailAccount, parser, date);
		
		AppRecruitment appRecruitment = appRecruitmentRepo.all().fetchOne();
		
		if (appRecruitment != null && appRecruitment.getActive() 
				&& message.getMailAccount() != null
				&& message.getMailAccount().getServerTypeSelect() > 1) {
			
			String lastEmailId = appRecruitment.getLastEmailId();
			if (lastEmailId == null || message.getId() > Long.parseLong(lastEmailId)) {
				jobPositionService.createJobApplication(message);
			}
		}
		
		return message;
	}
}