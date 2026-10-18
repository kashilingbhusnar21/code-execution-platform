package com.codeplatform.code_executor.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "topics")
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
public class Topic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ToString.Include
    private Long id;

    @Column(name = "name", nullable = false, unique = true)
    @ToString.Include
    private String name;

    @Column(name = "description")
    private String description;

    @ManyToMany(mappedBy = "topics", fetch = FetchType.EAGER)
    private Set<Problem> problems = new HashSet<>();

    public Topic() {
    }

    public Topic(String name) {
        this.name = name;
    }

    public Topic(String name, String description) {
        this.name = name;
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Topic)) return false;
        Topic topic = (Topic) o;
        return name != null && name.equals(topic.name);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
