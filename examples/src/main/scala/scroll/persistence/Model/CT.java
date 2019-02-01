package scroll.persistence.Model;

import javax.persistence.*;
import javax.persistence.Entity;
import java.util.HashSet;
import java.util.Set;

@Entity
@DiscriminatorValue("ct")
public class CT extends scroll.persistence.Model.Entity {

    @OneToMany(mappedBy="containedIn")
    public Set<RT> containing;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "ct_rt",
            joinColumns = { @JoinColumn(name = "ct_id") },
            inverseJoinColumns = { @JoinColumn(name = "rt_id") })
    public Set<RT> playingCt = new HashSet<>();

}
