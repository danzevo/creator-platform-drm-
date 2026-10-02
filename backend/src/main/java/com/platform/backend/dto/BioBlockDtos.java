package com.platform.backend.dto;

import com.platform.backend.entity.BlockType;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;
import java.util.UUID;

public class BioBlockDtos {
    @Getter @Setter
    @NoArgsConstructor @AllArgsConstructor
    @Builder
    public static class CreateBioBlockRequest {
        @NotBlank(message = "Title is required")
        private String title;
        private String url;
        private String iconUrl;
        private BlockType blockType;
        private Integer sortOrder;
    }

    @Getter @Setter
    @NoArgsConstructor @AllArgsConstructor
    @Builder
    public static class UpdateBioBlockRequest {
        private String title;
        private String url;
        private String iconUrl;
        private BlockType blockType;
        private Integer sortOrder;
        private Boolean isEnabled;
    }

    @Getter @Setter
    @NoArgsConstructor @AllArgsConstructor
    @Builder
    public static class ReorderItem {
        private UUID id;
        private Integer sortOrder;
    }

    @Getter @Setter
    @NoArgsConstructor @AllArgsConstructor
    @Builder
    public static class ReorderBlocksRequest {
        private List<ReorderItem> items;
    }

    @Getter @Setter
    @NoArgsConstructor @AllArgsConstructor
    @Builder
    public static class BioBlockResponse {
        private UUID id;
        private String title;
        private String url;
        private String iconUrl;
        private BlockType blockType;
        private Integer sortOrder;
        private Boolean isEnabled;
    }
}