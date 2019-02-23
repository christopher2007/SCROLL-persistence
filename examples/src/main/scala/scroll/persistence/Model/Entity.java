package scroll.persistence.Model;

//import javax.persistence.Entity;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.Set;
import java.util.UUID;

@javax.persistence.Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "entity_type")
abstract public class Entity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false, unique = true)
    public Long id;

    @Column(nullable=false)
    public String classPackage;

    @OneToMany(mappedBy="entity")
    @OnDelete(action = OnDeleteAction.CASCADE)
    public Set<Variable> variables;

    @Column(columnDefinition = "BINARY(16)", nullable=false, unique = true)
    public UUID uuid_;

}
