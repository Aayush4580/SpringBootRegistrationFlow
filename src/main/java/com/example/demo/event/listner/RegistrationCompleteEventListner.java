package com.example.demo.event.listner;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.example.demo.entity.UserEntity;
import com.example.demo.event.RegistrationCompleteEvent;
import com.example.demo.service.UserService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class RegistrationCompleteEventListner implements ApplicationListener<RegistrationCompleteEvent> {
		
	@Autowired
	private UserService userService;
	
	@Override
	public void onApplicationEvent(RegistrationCompleteEvent event) {
		// create the verification token for the user with link

		UserEntity user = event.getUser();
		String token = UUID.randomUUID().toString();
		userService.saveVerificationTokenForUser(user,token);
		// send mail to user
		String url=event.getApplicationUrl()+"/verifyRegistration?token="+token;
		
		//send verification email
		log.info("Click on the link to verify your account: {}",url);
	}

}
