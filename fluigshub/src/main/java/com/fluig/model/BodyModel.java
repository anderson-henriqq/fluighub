package com.fluig.model;

import java.util.List;

public class BodyModel {
    private String description;
    private int parentId;
    private List<AttachmentModel> attachments;

    public List<AttachmentModel> getAttachments() {
        return attachments;
    }


}
