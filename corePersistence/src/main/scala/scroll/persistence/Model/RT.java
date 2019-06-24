package scroll.persistence.Model;

import javax.persistence.*;
import javax.persistence.Entity;
import java.util.HashSet;
import java.util.Set;

@Entity
@DiscriminatorValue("rt")
public class RT extends scroll.persistence.Model.Entity {

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
//    @JoinColumn(name="ct_id", nullable=false)
    @JoinColumn(name="ct_id", nullable=true) // Muss nullable sein, da Entitäten in einer gemeinsamen Tabelle abgelegt werden
    public CT containedIn;

////    @ManyToMany(mappedBy = "playing", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
//    @ManyToMany(mappedBy = "playing", fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
//    public Set<scroll.persistence.Model.Entity> playedBy = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "entity_rt",
            joinColumns = { @JoinColumn(name = "rt_id", referencedColumnName = "id") },
            inverseJoinColumns = { @JoinColumn(name = "entity_id", referencedColumnName = "id") })
    public Set<scroll.persistence.Model.Entity> playedBy = new HashSet<>();

}
