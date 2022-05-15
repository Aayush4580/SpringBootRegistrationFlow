package com.example.demo.controller;

import java.util.Optional;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.entity.UserEntity;
import com.example.demo.entity.VerificationToken;
import com.example.demo.event.RegistrationCompleteEvent;
import com.example.demo.model.PasswordModel;
import com.example.demo.model.UserModel;
import com.example.demo.service.UserService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class RegistrationController {

	@Autowired
	private UserService userService;

	@Autowired
	private ApplicationEventPublisher eventPublisher;

	@PostMapping("/register")
	public UserEntity registerUser(@Valid @RequestBody UserModel userModel, final HttpServletRequest request) {
		UserEntity existingUser = userService.findUserByEmail(userModel.getEmail());
		if (null == existingUser) {
			UserEntity user = userService.registerUser(userModel);
			eventPublisher.publishEvent(new RegistrationCompleteEvent(user, applicationUrl(request)));
			return user;
		} else {
			return new UserEntity();
		}
	}

	@GetMapping("/verifyRegistration")
	public String verifyRegistration(@RequestParam("token") String token) {
		String result = userService.validateVerificationToken(token);

		if (result.equalsIgnoreCase("valid")) {
			return "User verified successfully";
		} else {
			return "Bad User";
		}
	}

	@GetMapping("/resendVerificationToken")
	public String resendVerificationToken(@RequestParam("token") String token, HttpServletRequest request) {
		VerificationToken verificationToken = userService.generateNewVerificationToken(token);
		UserEntity user = verificationToken.getUserEntity();
		resendVerificationMail(user, applicationUrl(request), verificationToken);
		return "verification mail sent";
	}

	private void resendVerificationMail(UserEntity user, String applicationUrl, VerificationToken verificationToken) {
		// eventPublisher.publishEvent(new
		// RegistrationCompleteEvent(user,applicationUrl(request)));
		String url = applicationUrl + "/verifyRegistration?token=" + verificationToken.getToken();
		// send verification email
		log.info("Click on the link to verify your account: {}", url);
	}

	private String applicationUrl(HttpServletRequest request) {
		return "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
	}

	@PostMapping("/resetPassword")
	public String resetPassword(@RequestBody PasswordModel passwordModel, HttpServletRequest request) {
		UserEntity existingUser = userService.findUserByEmail(passwordModel.getEmail());
		String url = "";
		if (null != existingUser) {
			String token = UUID.randomUUID().toString();
			userService.createPasswordResetTokenForUser(existingUser, token);
			url = passwordResetTokenMail(existingUser, applicationUrl(request), token);
		}
		return url;
	}

	@PostMapping("/savePassword")
	public String savePassword(@RequestParam("token") String token,
			@RequestBody PasswordModel passwordModel) {
		String result = userService.validatePasswordResetToken(token);
		if (!result.equalsIgnoreCase("valid")) {
			return "invalid token";
		}

		Optional<UserEntity> user = userService.getUserByPasswordResetToken(token);
		if (user.isPresent()) {
			userService.changePassword(user.get(), passwordModel.getNewPassword());
			return "password changed successfully";
		} else {
			return "password changed successfully";
		}
	}
	
	@PostMapping("/changePassword")
	public String changePassword(@RequestBody PasswordModel passwordModel) {
		return userService.validateAndChangePassword(passwordModel);
	}

	private String passwordResetTokenMail(UserEntity existingUser, String applicationUrl, String token) {
		String url = applicationUrl + "/savePassword?token=" + token;
		// send verification email
		log.info("Click on the link to reset your password: {}", url);
		return url;
	}
}
