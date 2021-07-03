package bookrent;

import bookrent.config.kafka.KafkaProcessor;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class PolicyHandler{
    @Autowired BookRepository bookRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverDeliveryCancelled_ChangeStatus(@Payload DeliveryCancelled deliveryCancelled){

        if(!deliveryCancelled.validate()) return;
        // Get Methods

        // Sample Logic //
        System.out.println("\n\n##### listener ChangeStatus : " + deliveryCancelled.toJson() + "\n\n");

        Optional<Book> optional = bookRepository.findById(deliveryCancelled.getBookId());
        
        if (optional.isPresent()) {
            Book book = optional.get();
            book.setStatus(true);
            bookRepository.save(book);
        }
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverDeliveryStarted_ChangeStatus(@Payload DeliveryStarted deliveryStarted){

        if(!deliveryStarted.validate()) return;
        // Get Methods

        // Sample Logic //
        System.out.println("\n\n##### listener ChangeStatus : " + deliveryStarted.toJson() + "\n\n");

        //book 상태변경을 배송 출발 후 할려고 하였으나, Rent 시점에 변경하기로 함
        //따라서 여기서는 do nothing
    }


    @StreamListener(KafkaProcessor.INPUT)
    public void whatever(@Payload String eventString){}


}
