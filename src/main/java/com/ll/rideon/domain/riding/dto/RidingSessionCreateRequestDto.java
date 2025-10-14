package com.ll.rideon.domain.riding.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "라이딩 세션 생성 요청 DTO")
public class RidingSessionCreateRequestDto {
    // 현재는 사용자 ID만 필요하지만, 향후 확장 가능
    // 향후 추가될 수 있는 필드: 시작 위치, 목적지, 예상 거리 등
} 