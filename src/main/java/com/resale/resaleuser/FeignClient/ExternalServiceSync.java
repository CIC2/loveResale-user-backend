package com.resale.resaleuser.FeignClient;

import com.resale.resaleuser.components.userManagement.UserSaver;
import com.resale.resaleuser.components.userManagement.dto.CreateZoomUserDTO;
import com.resale.resaleuser.model.User;
import com.resale.resaleuser.utils.ReturnObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExternalServiceSync {

    private final CommunicationClient communicationClient;
    private final QueueMsFeignClient queueMsFeignClient;
    private final UserSaver userSaver;

    /**
     * Create Zoom user for a salesman or team lead
     */
    public void createZoomUser(User user) {
        if (user.getRole() != null &&
                (user.getRole().name().equals("SALESMAN") || user.getRole().name().equals("TEAM_LEAD"))) {

            try {
                CreateZoomUserDTO zoomDto = new CreateZoomUserDTO(
                        user.getEmail(),
                        user.getFullName(),
                        "" // optional field
                );

                ReturnObject<String> zoomResponse = communicationClient.createZoomUser(zoomDto);

                if (zoomResponse.getStatus()) {
                    user.setZoomId(zoomResponse.getData());
                    userSaver.save(user);
                } else {
                    log.error("Failed to create Zoom user: {}", zoomResponse.getMessage());
                }
            } catch (Exception e) {
                log.error("Communication MS failed while creating Zoom user for {}: {}",
                        user.getEmail(), e.getMessage(), e);
            }
        }
    }

    /**
     * Sync user to Queue MS
     */
    public void syncToQueue(User user) {
        if (user.getRole() != null &&
                (user.getRole().name().equals("SALESMAN") || user.getRole().name().equals("TEAM_LEAD"))) {

            try {
                queueMsFeignClient.addSalesUser(
                        new com.resale.resaleuser.components.userInternal.dto.SalesDTO(
                                user.getId(),
                                user.getFullName()
                        )
                );
            } catch (Exception e) {
                log.error("Failed to sync user {} with Queue MS", user.getId(), e);
            }
        }
    }
}


