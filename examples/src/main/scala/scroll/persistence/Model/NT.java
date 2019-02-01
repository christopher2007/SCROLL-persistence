package scroll.persistence.Model;

import javax.persistence.Entity;
import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@DiscriminatorValue("nt")
public class NT extends scroll.persistence.Model.Entity {

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "nt_rt",
            joinColumns = { @JoinColumn(name = "nt_id") },
            inverseJoinColumns = { @JoinColumn(name = "rt_id") })
    public Set<RT> playingNt = new HashSet<>();

}
