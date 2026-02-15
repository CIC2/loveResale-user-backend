package com.resale.homeflyuser.components.userManagement.services;

import com.resale.homeflyuser.FeignClient.CommunicationClient;
import com.resale.homeflyuser.components.userManagement.UserFetcher;
import com.resale.homeflyuser.components.userManagement.UserSaver;
import com.resale.homeflyuser.components.userManagement.dto.OtpMailDTO;
import com.resale.homeflyuser.components.userManagement.dto.SendOtpDTO;
import com.resale.homeflyuser.components.userManagement.dto.VerifyOtpDTO;
import com.resale.homeflyuser.model.User;
import com.resale.homeflyuser.utils.MessageUtil;
import com.resale.homeflyuser.utils.ReturnObject;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserOtpService {
    private final UserFetcher userFetcher;
    private final UserSaver userSaver;
    private final MessageUtil messageUtil;
    private final CommunicationClient communicationClient;
    private static final int OTP_EXPIRY_MINUTES = 3;

    @Value("${otp.max.resend.attempts}")
    private int maxOtpResendAttempts;

    @Value("${otp.resend.cooldown-minutes}")
    private int otpResendCooldownMinutes;


    public ReturnObject<?> sendOtp(SendOtpDTO dto) {
        Optional<User> userOpt = userFetcher.findUserByEmail(dto.getEmail());
        if (userOpt.isEmpty()) {
            String message = messageUtil.getMessage("user.not.found");
            return new ReturnObject<>(message, false, null);
        }
        User user = userOpt.get();

        LocalDateTime now = LocalDateTime.now();

        // Check if user is currently blocked
        if (user.getOtpResendBlockedUntil() != null &&
                now.isBefore(user.getOtpResendBlockedUntil())) {

            long minutesLeft = ChronoUnit.MINUTES.between(now, user.getOtpResendBlockedUntil());

            return new ReturnObject<>(
                    messageUtil.getMessage("otp.resend.limit.exceeded"),
                    false,
                    null
            );
        }

        if (user.getOtpResendBlockedUntil() != null &&
                now.isAfter(user.getOtpResendBlockedUntil())) {
            user.setOtpResendCount(0);
            user.setOtpResendBlockedUntil(null);
        }

        Integer resendCount = Optional.ofNullable(user.getOtpResendCount()).orElse(0);

        if (resendCount >= maxOtpResendAttempts) {
            user.setOtpResendBlockedUntil(
                    now.plusMinutes(otpResendCooldownMinutes)
            );
            userSaver.save(user);

            return new ReturnObject<>(
                    messageUtil.getMessage("otp.resend.limit.exceeded"),
                    false,
                    null
            );
        }

        String otp = String.valueOf(100000 + new SecureRandom().nextInt(900000));

        user.setResetPasswordOtp(otp);
        user.setResetPasswordOtpSentAt(now);
        user.setOtpResendCount(resendCount + 1);

/*        try {
            OtpMailDTO otpMailDTO = new OtpMailDTO();
            otpMailDTO.setEmail(user.getEmail());
            otpMailDTO.setOtp(otp);
            otpMailDTO.setMailSubject("OTP");
            otpMailDTO.setMailContent("OTP");

            communicationClient.sendOtpMail(otpMailDTO);

        } catch (Exception ex) {
            return new ReturnObject<>(
                    messageUtil.getMessage("otp.send.failed"),
                    false,
                    null
            );
        }*/

        userSaver.save(user);

        return new ReturnObject<>(
                messageUtil.getMessage("otp.sent.successfully"),
                true,
                "OTP sent to email"
        );
    }

    public ReturnObject<String> verifyOtp(VerifyOtpDTO dto) {

        Optional<User> userOpt = userFetcher.findUserByEmail(dto.getEmail());
        if (userOpt.isEmpty()) {
            String message = messageUtil.getMessage("user.not.found");
            return new ReturnObject<>(message, false, null);
        }
        User user = userOpt.get();

        if (user.getResetPasswordOtp() == null || user.getResetPasswordOtpSentAt() == null) {
            return new ReturnObject<>(
                    messageUtil.getMessage("otp.not.found"),
                    false,
                    null
            );
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiryTime = user.getResetPasswordOtpSentAt().plusMinutes(OTP_EXPIRY_MINUTES);

        // Check if OTP has expired
        if (now.isAfter(expiryTime)) {
            return new ReturnObject<>(
                    messageUtil.getMessage("otp.expired"),
                    false,
                    null
            );
        }

        // Verify OTP matches
        if (!user.getResetPasswordOtp().equals(dto.getOtp())) {
            return new ReturnObject<>(
                    messageUtil.getMessage("otp.invalid"),
                    false,
                    null
            );
        }
//
//        user.setIsResetPasswordVerifyOtp(true);
//        user.setResetPasswordOtp(null);
//        user.setResetPasswordOtpSentAt(null);

//        userSaver.save(user);

        return new ReturnObject<>(
                messageUtil.getMessage("otp.verified.successfully"),
                true,
                "OTP verified"
        );
    }
}

