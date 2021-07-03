package bookrent;

import bookrent.config.kafka.KafkaProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ViewViewHandler {


    @Autowired
    private ViewRepository viewRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void whenBookRented_then_CREATE_1 (@Payload BookRented bookRented) {
        try {

            if (!bookRented.validate()) return;

            // view 객체 생성
            View view = new View();
            // view 객체에 이벤트의 Value 를 set 함
            view.setRentId(bookRented.getId());
            view.setBookId(bookRented.getBookId());
            view.setUserId(bookRented.getUserId());
            view.setRentStatus(bookRented.getStatus());
            // view 레파지 토리에 save
            viewRepository.save(view);

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @StreamListener(KafkaProcessor.INPUT)
    public void whenBookCancelled_then_UPDATE_1(@Payload BookCancelled bookCancelled) {
        try {
            if (!bookCancelled.validate()) return;
                // view 객체 조회

            List<View> viewList = viewRepository.findByRentId(bookCancelled.getId());
            for(View view : viewList){
                // view 객체에 이벤트의 eventDirectValue 를 set 함
                view.setRentStatus(bookCancelled.getStatus());
                // view 레파지 토리에 save
                viewRepository.save(view);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void whenDeliveryStarted_then_UPDATE_2(@Payload DeliveryStarted deliveryStarted) {
        try {
            if (!deliveryStarted.validate()) return;
                // view 객체 조회

            List<View> viewList = viewRepository.findByRentId(deliveryStarted.getRentId());
            for(View view : viewList){
                // view 객체에 이벤트의 eventDirectValue 를 set 함
                view.setDeliveryId(deliveryStarted.getId());
                view.setDeliveryStatus(deliveryStarted.getStatus());
                // view 레파지 토리에 save
                viewRepository.save(view);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void whenDeliveryCancelled_then_UPDATE_3(@Payload DeliveryCancelled deliveryCancelled) {
        try {
            if (!deliveryCancelled.validate()) return;
                // view 객체 조회

            List<View> viewList = viewRepository.findByRentId(deliveryCancelled.getRentId());
            for(View view : viewList){
                // view 객체에 이벤트의 eventDirectValue 를 set 함
                view.setDeliveryStatus(deliveryCancelled.getStatus());
                // view 레파지 토리에 save
                viewRepository.save(view);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

}

