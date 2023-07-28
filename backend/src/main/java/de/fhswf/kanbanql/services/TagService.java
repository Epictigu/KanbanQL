package de.fhswf.kanbanql.services;

import de.fhswf.kanbanql.model.Tag;
import de.fhswf.kanbanql.model.Ticket;
import de.fhswf.kanbanql.repositories.TagRepository;
import de.fhswf.kanbanql.repositories.TicketRepository;
import de.fhswf.kanbanql.request.create.CreateTagRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@ParametersAreNonnullByDefault
@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;
    private final TicketRepository ticketRepository;

    @Nullable
    public Tag getTagById(String id) {
        return tagRepository.findById(id)
                .orElse(null);
    }

    @Nonnull
    public List<Tag> getAllTags() {
        return tagRepository.findAll();
    }

    @Nonnull
    public Tag createTag(CreateTagRequest tagRequest) {
        Tag tag = new Tag();
        tag.setTagName(tagRequest.getTagName());
        return tagRepository.save(tag);
    }

    @Transactional
    @Nullable
    public Tag deleteTag(String id) {
        Tag tag = tagRepository.findById(id).orElse(null);
        if (tag == null) {
            return null;
        }

        Set<Ticket> tickets = tag.getTickets();
        tickets.forEach(ticket -> deleteTagFromTicket(ticket, id));
        ticketRepository.saveAll(tickets);

        tagRepository.delete(tag);
        return tag;
    }

    private void deleteTagFromTicket(Ticket ticket, String tagId) {
        Set<Tag> tags = ticket.getTags().stream()
                .filter(tag -> !tag.getId().equals(tagId))
                .collect(Collectors.toSet());
        ticket.setTags(tags);
    }
}
