package bookrent;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ViewRepository extends CrudRepository<View, Long> {

    List<View> findByRentId(Long rentId);

}