package bookrent;

import javax.persistence.*;

@Entity
@Table(name="View_table")
public class View {

        @Id
        @GeneratedValue(strategy=GenerationType.AUTO)
        private Long id;
        private Long rentId;
        private Long bookId;
        private Long deliveryId;
        private String rentStatus;
        private String deliveryStatus;
        private Long userId;


        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }
        public Long getRentId() {
            return rentId;
        }

        public void setRentId(Long rentId) {
            this.rentId = rentId;
        }
        public Long getBookId() {
            return bookId;
        }

        public void setBookId(Long bookId) {
            this.bookId = bookId;
        }
        public Long getDeliveryId() {
            return deliveryId;
        }

        public void setDeliveryId(Long deliveryId) {
            this.deliveryId = deliveryId;
        }
        public String getRentStatus() {
            return rentStatus;
        }

        public void setRentStatus(String rentStatus) {
            this.rentStatus = rentStatus;
        }
        public String getDeliveryStatus() {
            return deliveryStatus;
        }

        public void setDeliveryStatus(String deliveryStatus) {
            this.deliveryStatus = deliveryStatus;
        }
        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

}
