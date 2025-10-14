package com.ll.rideon.domain.images.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageUploadResponseDto {
    private Long id;
    private String url;
    private String mimeType;
    private Long size;
}

