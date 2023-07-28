package de.fhswf.kanbanql.services;

import de.fhswf.kanbanql.model.Comment;
import de.fhswf.kanbanql.repositories.CommentRepository;
import de.fhswf.kanbanql.repositories.TicketRepository;
import de.fhswf.kanbanql.request.create.CreateCommentRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Date;
import java.util.List;

@ParametersAreNonnullByDefault
@Service
@RequiredArgsConstructor
public class CommentService {

    private final TicketRepository ticketRepository;
    private final CommentRepository commentRepository;

    @Nonnull
    public Comment createComment(CreateCommentRequest commentRequest) {
        Comment comment = new Comment();

        comment.setCommentText(commentRequest.getCommentText());
        comment.setCreationDate(new Date());
        if (commentRequest.getTicketId() != null) {
            comment.setTicket(ticketRepository.getReferenceById(commentRequest.getTicketId()));
        }

        return commentRepository.save(comment);
    }

    @Nonnull
    public List<Comment> getAllComments() {
        return commentRepository.findAll();
    }

    @Nullable
    public Comment getCommentById(String id) {
        return commentRepository.findById(id)
                .orElse(null);
    }

    @Nonnull
    public List<Comment> getAllCommentsForTicketId(String ticketId) {
        return commentRepository.findByTicket_Id(ticketId);
    }
}
