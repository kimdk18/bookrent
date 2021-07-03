package bookrent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
public class BookController {
        @Autowired
        BookRepository bookRepository;

        @RequestMapping(value = "/books/checkAndChangeStatus",
        method = RequestMethod.GET,
        produces = "application/json;charset=UTF-8")
        public boolean checkAndChangeStatus(HttpServletRequest request, HttpServletResponse response) {
                boolean rtnVal = false;
                System.out.println("##### /book/checkAndChangeStatus  called #####");

                Long bookId = Long.parseLong(request.getParameter("bookId"));

                System.out.println("###### Book ID : " + bookId + " ######");

                /* Circuit Breaker 
                try {
                        Thread.sleep((long) (800 + Math.random() * 300));
                } catch (InterruptedException e) {
                        e.printStackTrace();
                }
                */

                Optional<Book> optional = bookRepository.findById(bookId);

                if (optional.isPresent())
                {
                        Book book = optional.get();
                        if (book.getStatus())
                        {
                                rtnVal = true;
                                book.setStatus(false);
                                bookRepository.save(book);
                        }
                }

                return rtnVal;
        }

}
