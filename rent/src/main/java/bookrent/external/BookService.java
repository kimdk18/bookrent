
package bookrent.external;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

//@FeignClient(name="book", url="http://${api.url.book}:8080", fallback=BookServiceFallback.class)
@FeignClient(name="book", url="http://localhost:8081", fallback=BookServiceFallback.class)
//@FeignClient(name="book", url="http://localhost:8081")
public interface BookService {
    @RequestMapping(method= RequestMethod.GET, path="/books/checkAndChangeStatus")
    public boolean checkAndChangeStatus(@RequestParam("bookId") Long bookId);

}

