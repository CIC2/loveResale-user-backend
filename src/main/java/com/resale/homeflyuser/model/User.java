package com.resale.homeflyuser.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "full_name", length = 200, nullable = false)
    private String fullName;

    @Column(length = 150, nullable = false, unique = true)
    private String email;

    @Column(length = 20, nullable = false, unique = true)
    private String mobile;

    @Column(length = 255, nullable = false)
    private String password;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "is_verified", nullable = false)
    private Boolean isVerified;

    @Column(nullable = false)
    private Integer status;


    @Column(name = "zoom_id")
    private String zoomId;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private Role role;

    // OTP Fields
    @Column(name = "otp", length = 10)
    private String otp;

    @Column(name = "otp_sent_at")
    private LocalDateTime otpSentAt;

    // Reset Password OTP Fields
    @Column(name = "reset_password_otp", length = 10)
    private String resetPasswordOtp;

    @Column(name = "reset_password_otp_sent_at")
    private LocalDateTime resetPasswordOtpSentAt;

    @Column(name = "is_reset_password_verify_otp")
    private Boolean isResetPasswordVerifyOtp;

    @Column(name = "fcm_token")
    private String fcmToken;

    @Column(name = "user_id")
    private Integer userId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "last_assigned")
    private Timestamp lastAssigned;

    @Column(name = "otp_resend_count")
    private Integer otpResendCount;

    @Column(name = "otp_resend_blocked_until")
    private LocalDateTime otpResendBlockedUntil;
}


