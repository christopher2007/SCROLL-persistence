package scroll.persistence.Model;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.Repository;
import java.util.List;

public interface NTRepository extends Repository<NT, Long> {

//    List<Entity> findByEmailAddressAndLastname(String emailAddress, String lastname);

    List<NT> findAllByHash(int hash);

}
