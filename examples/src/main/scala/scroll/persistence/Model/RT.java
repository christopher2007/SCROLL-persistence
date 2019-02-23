package scroll.persistence.Model;

import javax.persistence.*;
import javax.persistence.Entity;
import java.util.HashSet;
import java.util.Set;

@Entity
@DiscriminatorValue("rt")
public class RT extends scroll.persistence.Model.Entity {

    @ManyToOne
    @JoinColumn(name="ct_id", nullable=true)
    public CT containedIn;

    @ManyToMany(mappedBy = "playing")
    public Set<scroll.persistence.Model.Entity> playedBy = new HashSet<>();

}
