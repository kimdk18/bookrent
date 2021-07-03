
package bookrent.external;

import org.springframework.stereotype.Component;

@Component
public class BookServiceFallback implements BookService {

    @Override
    public boolean checkAndChangeStatus(Long bookId) {
        System.out.println("###### Fallback ######");
        return false;
    }
}

