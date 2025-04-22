package com.fluig.model;

import java.util.List;

public class AttachFileModel {
    private String processId;
    private int version;
    private boolean managerMode;
    private String taskUserId;
    private int processInstanceId;
    private boolean isDigitalSigned;
    private int selectedState;
    private List<Attachment> attachments;
    private int currentMovto;

    // Getters and Setters
    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public boolean isManagerMode() {
        return managerMode;
    }

    public void setManagerMode(boolean managerMode) {
        this.managerMode = managerMode;
    }

    public String getTaskUserId() {
        return taskUserId;
    }

    public void setTaskUserId(String taskUserId) {
        this.taskUserId = taskUserId;
    }

    public int getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(int processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public boolean isDigitalSigned() {
        return isDigitalSigned;
    }

    public void setDigitalSigned(boolean digitalSigned) {
        isDigitalSigned = digitalSigned;
    }

    public int getSelectedState() {
        return selectedState;
    }

    public void setSelectedState(int selectedState) {
        this.selectedState = selectedState;
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }

    public int getCurrentMovto() {
        return currentMovto;
    }

    public void setCurrentMovto(int currentMovto) {
        this.currentMovto = currentMovto;
    }

    public static class Attachment {
        private final boolean deleted = true;
        private String name;
        private boolean newAttach;
        private String description;
        private int documentId;
        private String attachedUser;
        private List<attachments> attachments;

        // Getters and Setters
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public boolean isNewAttach() {
            return newAttach;
        }

        public void setNewAttach(boolean newAttach) {
            this.newAttach = newAttach;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public int getDocumentId() {
            return documentId;
        }

        public void setDocumentId(int documentId) {
            this.documentId = documentId;
        }

        public String getAttachedUser() {
            return attachedUser;
        }

        public void setAttachedUser(String attachedUser) {
            this.attachedUser = attachedUser;
        }


        public List<AttachFileModel.attachments> getAttachments() {
            return attachments;
        }

        public void setAttachments(List<AttachFileModel.attachments> attachments) {
            this.attachments = attachments;
        }
    }

    public static class attachments {
        private boolean principal;
        private String fileName;

        public boolean isPrincipal() {
            return principal;
        }

        public void setPrincipal(boolean principal) {
            this.principal = principal;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }
    }
}

