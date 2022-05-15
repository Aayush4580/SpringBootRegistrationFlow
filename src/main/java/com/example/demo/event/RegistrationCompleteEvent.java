package com.example.demo.event;

import org.springframework.context.ApplicationEvent;

import com.example.demo.entity.UserEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegistrationCompleteEvent extends ApplicationEvent{

	private static final long serialVersionUID = 1L;
	
	private UserEntity user;
	private String applicationUrl;
	
	public RegistrationCompleteEvent(UserEntity user,String applicationUrl) {
		super(user);
		// TODO Auto-generated constructor stub
		this.user=user;
		this.applicationUrl=applicationUrl;
	}

}
