package com.resale.homeflyuser.components.userManagement.services;

import com.resale.homeflyuser.components.userManagement.dto.ChangePasswordDTO;
import com.resale.homeflyuser.components.userManagement.UserFetcher;
import com.resale.homeflyuser.components.userManagement.dto.ForgotPasswordDTO;
import com.resale.homeflyuser.components.userManagement.updaters.PasswordUpdater;
import com.resale.homeflyuser.components.userManagement.validators.PasswordChangeValidator;
import com.resale.homeflyuser.model.User;
import com.resale.homeflyuser.utils.MessageUtil;
import com.resale.homeflyuser.utils.ReturnObject;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserPasswordService {

    private final UserFetcher userFetcher;
    private final PasswordChangeValidator passwordChangeValidator;
    private final PasswordUpdater passwordUpdater;
    private final MessageUtil messageUtil;
    private static final int OTP_EXPIRY_MINUTES = 5;
    //private static final int OTP_LENGTH = 4;      //use it with generateOtp
    private static final int MIN_PASSWORD_LENGTH = 8;
    public ReturnObject<String> changePassword(
            Integer userId,
            ChangePasswordDTO dto
    ) {

        Optional<User> userOptional = userFetcher.findUser(userId);

        if (userOptional.isEmpty()) {
            return new ReturnObject<>(
                    messageUtil.getMessage("user.not.found"),
                    false,
                    null
            );
        }

        User user = userOptional.get();

        ReturnObject<Void> validationResult =
                passwordChangeValidator.validate(user, dto);

        if (!validationResult.getStatus()) {
            return new ReturnObject<>(
                    validationResult.getMessage(),
                    false,
                    null
            );
        }

        passwordUpdater.updatePassword(user, dto.getNewPassword());

        return new ReturnObject<>(
                messageUtil.getMessage("password.changed.successfully"),
                true,
                "Password changed successfully"
        );
    }


    // API 3: Reset Password with OTP
    public ReturnObject<String> forgotPasswordReset(ForgotPasswordDTO dto) {
        // Find user by email
        Optional<User> userOpt = userFetcher.findUserByEmail(dto.getEmail());
        if (userOpt.isEmpty()) {
            String message = messageUtil.getMessage("user.not.found");
            return new ReturnObject<>(message, false, null);
        }
        User user = userOpt.get();

        // Check if OTP exists
        if (user.getResetPasswordOtp() == null || user.getResetPasswordOtp().isEmpty()) {
            String message = messageUtil.getMessage("otp.not.found");
            return new ReturnObject<>(message, false, null);
        }

        // Check if OTP is expired (5 minutes)
        if (user.getResetPasswordOtpSentAt() == null) {
            String message = messageUtil.getMessage("otp.not.found");
            return new ReturnObject<>(message, false, null);
        }
        long minutesSinceSent = ChronoUnit.MINUTES.between(
                user.getResetPasswordOtpSentAt(),
                LocalDateTime.now()
        );

        //Check if OTP is within the 5 minutes
        if (minutesSinceSent > OTP_EXPIRY_MINUTES) {
            String message = messageUtil.getMessage("otp.expired");
            return new ReturnObject<>(message, false, null);
        }

        // Validate password strength
        if (dto.getNewPassword() == null || dto.getNewPassword().length() < MIN_PASSWORD_LENGTH) {
            String message = messageUtil.getMessage("password.too.short");
            return new ReturnObject<>(message, false, null);
        }

/*        if(user.getIsResetPasswordVerifyOtp() == null || !user.getIsResetPasswordVerifyOtp()){
            String message = messageUtil.getMessage("otp.not.verified");
            return new ReturnObject<>(message, false, null);
        }*/
        // Update password
        user.setResetPasswordOtpSentAt(null);
        user.setIsResetPasswordVerifyOtp(null);
        user.setOtpResendCount(0);
        user.setOtpResendBlockedUntil(null);

        passwordUpdater.updatePassword(user, dto.getNewPassword());
        String message = messageUtil.getMessage("password.reset.successfully");
        return new ReturnObject<>(message, true, "Password reset successfully");
    }


}


