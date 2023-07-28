package de.fhswf.kanbanql.request.create;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CreateCommentRequest {

    private String commentText;

    private String ticketId;
}
