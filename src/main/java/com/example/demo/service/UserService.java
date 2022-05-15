package com.example.demo.service;

import java.util.Optional;

import javax.validation.Valid;

import com.example.demo.entity.UserEntity;
import com.example.demo.entity.VerificationToken;
import com.example.demo.model.PasswordModel;
import com.example.demo.model.UserModel;

public interface UserService {

	public UserEntity registerUser(@Valid UserModel userModel);

	public void saveVerificationTokenForUser(UserEntity user, String token);

	public String validateVerificationToken(String token);

	public VerificationToken generateNewVerificationToken(String token);

	public UserEntity findUserByEmail(String email);

	public void createPasswordResetTokenForUser(UserEntity existingUser, String token);

	public String validatePasswordResetToken(String token);

	public Optional<UserEntity> getUserByPasswordResetToken(String token);

	public void changePassword(UserEntity userEntity, String newPassword);

	public String validateAndChangePassword(PasswordModel passwordModel);

}
