package scroll.persistence.Model;

//import javax.persistence.Entity;
import javax.persistence.*;
import java.util.Set;

@javax.persistence.Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "entity_type")
abstract public class Entity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    public Long id;

    @Column(nullable=false)
    public String name;

    @OneToMany(mappedBy="entity")
    public Set<Variable> variables;

    @Column(nullable=false)
    public int hash;

}
