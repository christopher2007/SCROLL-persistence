package scroll.persistence.Model;

//import javax.persistence.Entity;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@javax.persistence.Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "entity_type")
abstract public class Entity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(updatable = false, nullable = false, unique = true)
    public Long id;

    @Column(nullable=false)
    public String classPackage;

    @OneToMany(mappedBy="entity", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @OnDelete(action = OnDeleteAction.CASCADE)
    public Set<Variable> variables;

    @Column(columnDefinition = "BINARY(16)", nullable=false, unique = true) // für MySQL
//    @Column(columnDefinition = "BYTEA", nullable=false, unique = true) // für PostgreSQL
//    @Column(nullable=false, unique = true) // für PostgreSQL, alte Versionen
    public UUID uuid_;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "entity_rt",
            joinColumns = @JoinColumn(name = "entity_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "rt_id", referencedColumnName = "id"))
    public Set<RT> playing = new HashSet<>();

}
