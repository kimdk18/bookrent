package bookrent;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;

@Entity
@Table(name="Rent_table")
public class Rent {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private Long bookId;
    private Long userId;
    private String address;
    private String status;

    @PostPersist
    public void onPostPersist(){
        //Following code causes dependency to external APIs
        // it is NOT A GOOD PRACTICE. instead, Event-Policy mapping is recommended.

        // mappings goes here
        if ("Rent".equals(getStatus())) {
            BookRented bookRented = new BookRented();
            BeanUtils.copyProperties(this, bookRented);
            bookRented.publishAfterCommit();
        }
    }
    @PostUpdate
    public void onPostUpdate(){
        if ("Cancel".equals(getStatus()))
        {
            BookCancelled bookCancelled = new BookCancelled();
            BeanUtils.copyProperties(this, bookCancelled);
            bookCancelled.publishAfterCommit();
        }
    }
    @PrePersist
    public void onPrePersist(){
        boolean status = RentApplication.applicationContext.getBean(bookrent.external.BookService.class)
            .checkAndChangeStatus(getBookId());

        if (status)
        {
            setStatus("Rent");
        }
        else
        {
            setStatus("Fail");
        }
    }
    @PreUpdate
    public void onPreUpdate(){
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }




}
