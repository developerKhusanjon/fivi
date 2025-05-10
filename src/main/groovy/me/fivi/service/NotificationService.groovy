package me.fivi.service

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.Notification
import groovy.transform.CompileStatic
import io.micronaut.context.annotation.Value
import io.micronaut.context.event.StartupEvent
import io.micronaut.runtime.event.annotation.EventListener
import jakarta.inject.Singleton
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@CompileStatic
@Singleton
class NotificationService {
    private static final Logger LOG = LoggerFactory.getLogger(NotificationService.class)

    @Value('${firebase.credentials-file}')
    private String firebaseCredentialsFile

    @Value('${firebase.project-id}')
    private String projectId

    private boolean initialized = false

    @EventListener
    void onStartup(StartupEvent event) {
        initializeFirebase()
    }

    void initializeFirebase() {
        try {
            FileInputStream serviceAccount = new FileInputStream(firebaseCredentialsFile)

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setProjectId(projectId)
                    .build()

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options)
                LOG.info("Firebase app initialized successfully")
            }

            initialized = true
        } catch (Exception e) {
            LOG.error("Failed to initialize Firebase", e)
            initialized = false
        }
    }

    boolean sendNotification(String deviceToken, String title, String body, Map<String, String> data = [:]) {
        if (!initialized) {
            LOG.warn("Cannot send notification: Firebase not initialized")
            return false
        }

        try {
            Message.Builder messageBuilder = Message.builder()
                    .setToken(deviceToken)
                    .setNotification(
                            Notification.builder()
                                    .setTitle(title)
                                    .setBody(body)
                                    .build()
                    )

            // Add data payload if provided
            if (data) {
                messageBuilder.putAllData(data)
            }

            String response = FirebaseMessaging.getInstance().send(messageBuilder.build())
            LOG.info("Successfully sent notification: {}", response)
            return true
        } catch (Exception e) {
            LOG.error("Failed to send notification", e)
            return false
        }
    }

    boolean sendNotificationToTopic(String topic, String title, String body, Map<String, String> data = [:]) {
        if (!initialized) {
            LOG.warn("Cannot send notification: Firebase not initialized")
            return false
        }

        try {
            Message.Builder messageBuilder = Message.builder()
                    .setTopic(topic)
                    .setNotification(
                            Notification.builder()
                                    .setTitle(title)
                                    .setBody(body)
                                    .build()
                    )

            // Add data payload if provided
            if (data) {
                messageBuilder.putAllData(data)
            }

            String response = FirebaseMessaging.getInstance().send(messageBuilder.build())
            LOG.info("Successfully sent notification to topic {}: {}", topic, response)
            return true
        } catch (Exception e) {
            LOG.error("Failed to send topic notification", e)
            return false
        }
    }

    boolean subscribeToTopic(String deviceToken, String topic) {
        if (!initialized) {
            LOG.warn("Cannot subscribe to topic: Firebase not initialized")
            return false
        }

        try {
            FirebaseMessaging.getInstance().subscribeToTopic(Collections.singletonList(deviceToken), topic)
            LOG.info("Successfully subscribed device to topic: {}", topic)
            return true
        } catch (Exception e) {
            LOG.error("Failed to subscribe to topic", e)
            return false
        }
    }

    boolean unsubscribeFromTopic(String deviceToken, String topic) {
        if (!initialized) {
            LOG.warn("Cannot unsubscribe from topic: Firebase not initialized")
            return false
        }

        try {
            FirebaseMessaging.getInstance().unsubscribeFromTopic(Collections.singletonList(deviceToken), topic)
            LOG.info("Successfully unsubscribed device from topic: {}", topic)
            return true
        } catch (Exception e) {
            LOG.error("Failed to unsubscribe from topic", e)
            return false
        }
    }
}
