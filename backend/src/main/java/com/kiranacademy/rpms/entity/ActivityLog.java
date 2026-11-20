package com.kiranacademy.rpms.entity;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "activity_logs")
public class ActivityLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Long logId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "module_name", nullable = false, length = 60)
    private String moduleName;

    @Column(name = "action_name", nullable = false, length = 80)
    private String actionName;

    @Column(name = "record_id", length = 60)
    private String recordId;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "logged_at", nullable = false)
    private OffsetDateTime loggedAt = OffsetDateTime.now();

    public ActivityLog() {}

    public ActivityLog(User user, String moduleName, String actionName, String recordId, String description) {
        this.user = user;
        this.moduleName = moduleName;
        this.actionName = actionName;
        this.recordId = recordId;
        this.description = description;
        this.loggedAt = OffsetDateTime.now();
    }

    public Long getLogId() { return logId; }
    public void setLogId(Long logId) { this.logId = logId; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getModuleName() { return moduleName; }
    public void setModuleName(String moduleName) { this.moduleName = moduleName; }
    public String getActionName() { return actionName; }
    public void setActionName(String actionName) { this.actionName = actionName; }
    public String getRecordId() { return recordId; }
    public void setRecordId(String recordId) { this.recordId = recordId; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public OffsetDateTime getLoggedAt() { return loggedAt; }
    public void setLoggedAt(OffsetDateTime loggedAt) { this.loggedAt = loggedAt; }
}
