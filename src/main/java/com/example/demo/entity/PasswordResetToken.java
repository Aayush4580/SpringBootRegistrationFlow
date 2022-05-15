package com.example.demo.entity;

import java.util.Calendar;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class PasswordResetToken {

	// Expiration time 10 minute
	private static final int EXPIRATION_TIME = 10;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String token;

	private Date expirationTime;

	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "FK_USER_PASSWORD_TOKEN"))
	private UserEntity userEntity;

	public PasswordResetToken(UserEntity userEntity, String token) {
		super();
		this.token = token;
		this.userEntity = userEntity;
		this.expirationTime = calculateExpirationTime(EXPIRATION_TIME);
	}
	
	public PasswordResetToken(String token) {
		super();
		this.token = token;
		this.expirationTime = calculateExpirationTime(EXPIRATION_TIME);
	}

	private Date calculateExpirationTime(int expirationTime) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(new Date().getTime());
		calendar.add(Calendar.MINUTE, EXPIRATION_TIME);
		return new Date(calendar.getTime().getTime());
	}

}
