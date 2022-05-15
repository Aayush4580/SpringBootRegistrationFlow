package com.example.demo.service;

import java.util.Calendar;
import java.util.Optional;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.entity.PasswordResetToken;
import com.example.demo.entity.UserEntity;
import com.example.demo.entity.VerificationToken;
import com.example.demo.model.PasswordModel;
import com.example.demo.model.UserModel;
import com.example.demo.repository.PasswordResetTokenRepositiory;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.VerificationTokenRepositiory;


@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordResetTokenRepositiory passwordResetTokenRepositiory;

	private VerificationTokenRepositiory verificationTokenRepositiory;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	public UserEntity registerUser(@Valid UserModel userModel) {
		// TODO Auto-generated method stub
		UserEntity userEntity = new UserEntity();
		userEntity.setEmail(userModel.getEmail());
		userEntity.setFirstName(userModel.getFirstName());
		userEntity.setLastName(userModel.getLastName());
		userEntity.setRole("User");
		userEntity.setEnable(false);
		userEntity.setPassword(passwordEncoder.encode(userModel.getPassword()));
		userRepository.save(userEntity);
		return userEntity;
	}

	@Override
	public void saveVerificationTokenForUser(UserEntity user, String token) {
		// TODO Auto-generated method stub
		VerificationToken verificationToken = new VerificationToken(user, token);
		verificationTokenRepositiory.save(verificationToken);

	}

	@Override
	public String validateVerificationToken(String token) {
		VerificationToken verificationToken = verificationTokenRepositiory.findByToken(token);

		if (null == verificationToken) {
			return "invalid";
		}
		UserEntity user = verificationToken.getUserEntity();
		Calendar calendar = Calendar.getInstance();
		if (verificationToken.getExpirationTime().getTime() - calendar.getTime().getTime() <= 0) {
			verificationTokenRepositiory.delete(verificationToken);
			return "token expired";
		}
		user.setEnable(true);
		userRepository.save(user);
		return "valid";
	}

	@Override
	public VerificationToken generateNewVerificationToken(String oldToken) {
		VerificationToken verificationToken = verificationTokenRepositiory.findByToken(oldToken);
		verificationToken.setToken(UUID.randomUUID().toString());
		verificationTokenRepositiory.save(verificationToken);
		return verificationToken;
	}

	@Override
	public UserEntity findUserByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	@Override
	public void createPasswordResetTokenForUser(UserEntity existingUser, String token) {
		PasswordResetToken passwordResetToken = new PasswordResetToken(existingUser, token);
		passwordResetTokenRepositiory.save(passwordResetToken);
	}

	@Override
	public String validatePasswordResetToken(String token) {
		PasswordResetToken passwordResetToken = passwordResetTokenRepositiory.findByToken(token);

		if (null == passwordResetToken) {
			return "invalid";
		}
		Calendar calendar = Calendar.getInstance();
		if (passwordResetToken.getExpirationTime().getTime() - calendar.getTime().getTime() <= 0) {
			passwordResetTokenRepositiory.delete(passwordResetToken);
			return "token expired";
		}

		return "valid";
	}

	@Override
	public Optional<UserEntity> getUserByPasswordResetToken(String token) {
		return Optional.ofNullable(passwordResetTokenRepositiory.findByToken(token).getUserEntity());
	}

	@Override
	public void changePassword(UserEntity userEntity, String newPassword) {
		userEntity.setPassword(passwordEncoder.encode(newPassword));
		userRepository.save(userEntity);
	}

	@Override
	public String validateAndChangePassword(PasswordModel passwordModel) {
		UserEntity user = userRepository.findByEmail(passwordModel.getEmail());
		if (passwordEncoder.matches(passwordModel.getOldPassword(), user.getPassword())) {
			changePassword(user, passwordModel.getNewPassword());
			return "password changed successfully";
		} else {
			return "Invalid Old Password";
		}

	}

}
