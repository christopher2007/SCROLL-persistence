package scroll.persistence.Model;

import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.persistence.Entity;
import java.util.Set;

@Entity
public class Variable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    public Long id;

    @Column(nullable=false)
    public String name;

    @Type(type = "serializable")
    public Object value;

    @ManyToOne
    @JoinColumn(name="entity_id", nullable=false)
    public scroll.persistence.Model.Entity entity;

}
