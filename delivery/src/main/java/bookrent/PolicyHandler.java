package bookrent;

import bookrent.config.kafka.KafkaProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class PolicyHandler{
    @Autowired 
    DeliveryRepository deliveryRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverBookRented_DeliveryStart(@Payload BookRented bookRented){

        if(!bookRented.validate()) return;
        // Get Methods

        // Sample Logic //
        System.out.println("\n\n##### listener DeliveryStart : " + bookRented.toJson() + "\n\n");

        Delivery delivery = new Delivery();
        delivery.setBookId(bookRented.getBookId());
        delivery.setRentId(bookRented.getId());
        delivery.setUserId(bookRented.getUserId());
        delivery.setAddress(bookRented.getAddress());
        delivery.setStatus("Delivery Started");

        deliveryRepository.save(delivery);
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverBookCancelled_DeliveryCancel(@Payload BookCancelled bookCancelled){

        if(!bookCancelled.validate()) return;
        // Get Methods

        // Sample Logic //
        System.out.println("\n\n##### listener DeliveryCancel : " + bookCancelled.toJson() + "\n\n");

        Delivery delivery = deliveryRepository.findByRentId(bookCancelled.getId());

        delivery.setStatus("Delivery Cancelled");
        deliveryRepository.save(delivery);
    }


    @StreamListener(KafkaProcessor.INPUT)
    public void whatever(@Payload String eventString){}


}
