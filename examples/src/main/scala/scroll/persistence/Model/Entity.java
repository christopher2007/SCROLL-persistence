package scroll.persistence.Model;

//import javax.persistence.Entity;
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
    public String name;

    @OneToMany(mappedBy="entity")
    public Set<Variable> variables;

    @Column(columnDefinition = "BINARY(16)", nullable=false, unique = true)
    public UUID uuid_;

}
