package de.fhswf.kanbanql.services;

import de.fhswf.kanbanql.model.Priority;
import de.fhswf.kanbanql.model.Status;
import de.fhswf.kanbanql.model.Tag;
import de.fhswf.kanbanql.model.Ticket;
import de.fhswf.kanbanql.repositories.TagRepository;
import de.fhswf.kanbanql.repositories.TicketRepository;
import de.fhswf.kanbanql.request.create.CreateTicketRequest;
import de.fhswf.kanbanql.request.update.UpdateTagRequest;
import de.fhswf.kanbanql.request.update.UpdateTicketRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.stream.Collectors;

@ParametersAreNonnullByDefault
@Service
@RequiredArgsConstructor
public class TicketService {

    static final Status DEFAULT_STATUS = Status.BACKLOG;
    static final Priority DEFAULT_PRIORITY = Priority.LOW;

    private final TicketRepository ticketRepository;
    private final TagRepository tagRepository;

    @Nullable
    public Ticket getTicketById(String id) {
        return ticketRepository.findById(id).orElse(null);
    }

    @Nonnull
    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    @Nonnull
    public Ticket createTicket(@Nonnull CreateTicketRequest ticketRequest) {
        Ticket ticket = new Ticket();

        ticket.setTitle(ticketRequest.getTitle());
        ticket.setStatus(determineStatus(ticketRequest));
        ticket.setPriority(determinePriority(ticketRequest));
        ticket.setTags(new HashSet<>());
        ticket.setComments(new ArrayList<>());
        ticket.setCreationDate(new Date());

        return ticketRepository.save(ticket);
    }

    private Status determineStatus(CreateTicketRequest ticketRequest) {
        return Optional.ofNullable(ticketRequest.getStatus())
                .orElse(DEFAULT_STATUS);
    }

    private Priority determinePriority(CreateTicketRequest ticketRequest) {
        return Optional.ofNullable(ticketRequest.getPriority())
                .orElse(DEFAULT_PRIORITY);
    }

    @Nullable
    public Ticket updateTicket(UpdateTicketRequest ticketRequest) {
        Ticket ticket = ticketRepository.findById(ticketRequest.getId())
                .orElse(null);
        if (ticket == null) {
            return null;
        }

        Optional.ofNullable(ticketRequest.getTitle())
                .ifPresent(ticket::setTitle);
        Optional.ofNullable(ticketRequest.getDescription())
                .ifPresent(ticket::setDescription);
        Optional.ofNullable(ticketRequest.getStatus())
                .ifPresent(ticket::setStatus);
        Optional.ofNullable(ticketRequest.getPriority())
                .ifPresent(ticket::setPriority);
        Optional.ofNullable(ticketRequest.getTags())
                .map(this::convertUpdateTagRequestsToTags)
                .ifPresent(ticket::setTags);

        return ticketRepository.save(ticket);
    }

    private Set<Tag> convertUpdateTagRequestsToTags(List<UpdateTagRequest> tagRequests) {
        return tagRequests.stream()
                .map(tag -> tagRepository.findById(tag.getId()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());
    }

    @Nullable
    public Ticket deleteTicket(String id) {
        Ticket ticket = ticketRepository.findById(id).orElse(null);
        if (ticket == null) {
            return null;
        }

        ticketRepository.delete(ticket);
        return ticket;
    }
}
