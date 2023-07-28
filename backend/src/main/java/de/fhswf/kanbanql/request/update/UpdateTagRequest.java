package de.fhswf.kanbanql.request.update;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UpdateTagRequest {
    private String id;
}
