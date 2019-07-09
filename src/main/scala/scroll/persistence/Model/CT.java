package scroll.persistence.Model;

import javax.persistence.*;
import javax.persistence.Entity;
import java.util.HashSet;
import java.util.Set;

@Entity
@DiscriminatorValue("ct")
public class CT extends scroll.persistence.Model.Entity {

    @OneToMany(mappedBy="containedIn", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    public Set<RT> containing;

}
