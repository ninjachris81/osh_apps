package com.osh.data.entity;

import jakarta.persistence.Entity;

@Entity(name = "dm_processor_tasks")
public class ProcessorTask extends AbstractEntity {

    private Integer taskType;
    private Integer taskTriggerType;
    private String scriptCode;
    private String runCondition;
    private Integer scheduleInterval;
    private Boolean publishResult;
    private boolean enabled;
    private String groupId;

    public Integer getTaskType() {
        return taskType;
    }
    public void setTaskType(Integer taskType) {
        this.taskType = taskType;
    }
    public Integer getTaskTriggerType() {
        return taskTriggerType;
    }
    public void setTaskTriggerType(Integer taskTriggerType) {
        this.taskTriggerType = taskTriggerType;
    }
    public String getScriptCode() {
        return scriptCode;
    }
    public void setScriptCode(String scriptCode) {
        this.scriptCode = scriptCode;
    }
    public String getRunCondition() {
        return runCondition;
    }
    public void setRunCondition(String runCondition) {
        this.runCondition = runCondition;
    }
    public Integer getScheduleInterval() {
        return scheduleInterval;
    }
    public void setScheduleInterval(Integer scheduleInterval) {
        this.scheduleInterval = scheduleInterval;
    }
    public Boolean isPublishResult() {
        return publishResult;
    }
    public void setPublishResult(Boolean publishResult) {
        this.publishResult = publishResult;
    }
    public boolean isEnabled() {
        return enabled;
    }
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    public String getGroupId() {
        return groupId;
    }
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

}
