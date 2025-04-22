package com.fluig.model;

import com.google.gson.annotations.SerializedName;

public class CreateFolderModel {
    @SerializedName("pathId")
    private String parentFolderId;

    @SerializedName("foldername")
    private String documentDescription;

    public CreateFolderModel(String parentFolderId, String documentDescription) {
        this.parentFolderId = parentFolderId;
        this.documentDescription = documentDescription;
    }

    public String getParentFolderId() {
        return parentFolderId;
    }

    public void setParentFolderId(String parentFolderId) {
        this.parentFolderId = parentFolderId;
    }

    public String getDocumentDescription() {
        return documentDescription;
    }

    public void setDocumentDescription(String documentDescription) {
        this.documentDescription = documentDescription;
    }

}
