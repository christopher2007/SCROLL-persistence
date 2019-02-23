package scroll.persistence.Model;

import javax.persistence.Entity;
import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@DiscriminatorValue("nt")
public class NT extends scroll.persistence.Model.Entity {

}
