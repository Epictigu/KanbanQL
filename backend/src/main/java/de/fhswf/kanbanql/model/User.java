package de.fhswf.kanbanql.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "kanban_user")
public class User {

    @Id
    @GeneratedValue
    private String id;

    private String username;

    @OneToMany(mappedBy = "user")
    private List<Ticket>  ticket;

}
