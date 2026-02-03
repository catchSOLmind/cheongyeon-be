package com.catchsolmind.cheongyeonbe.domain.task.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TaskTypeFavoriteResponse {
    private Long taskTypeId;
    private Boolean isFavorite;
}
