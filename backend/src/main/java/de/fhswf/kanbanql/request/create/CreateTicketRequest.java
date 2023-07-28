package de.fhswf.kanbanql.request.create;

import de.fhswf.kanbanql.model.Priority;
import de.fhswf.kanbanql.model.Status;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Builder
public class CreateTicketRequest {

    private String title;

    private Status status;

    private Priority priority;



}
