### bookrent
final assessment


### Repositories

- https://github.com/kimdk18/bookrent.git



### Table of contents

- [서비스 시나리오]

  - [기능적 요구사항]

  - [비기능적 요구사항]

  - [Microservice명]

- [분석/설계]

- [구현]

  - [DDD 의 적용]

  - [폴리글랏 퍼시스턴스]

  - [동기식 호출 과 Fallback 처리]

  - [비동기식 호출 / 시간적 디커플링 / 장애격리 / 최종 (Eventual) 일관성 테스트]

- [운영]

  - [Deploy]

  - [Autoscale (HPA)]

  - [Config Map]

  - [Zero-Downtime deploy (Readiness Probe)] 

  - [Self-healing (Liveness Probe)]

  - [Circuit Breaker]

# 서비스 시나리오

### 기능적 요구 사항

```
1. 고객이 책을 대여 요청한다.
2. 대여요청을 하면 대여가능 확인 후 대여를 확정한다.
3. 대여가 확정되면 배송팀에 배송요청한다.
4. 배송요청이 되면 배송이 시작된다.
5. 고객이 대여 요청을 취소 할 수 있다.
6. 대여 요청이 취소되면 배송이 취소된다.
7. 고객은 대여 상태를 조회할 수 있다.
```

### 비기능적 요구 사항

```
1. 트랜잭션
  - 대여요청을 하면 대여 가능여부 확인 후 대여가 자동 확정된다 Sync 호출
2. 장애격리
  - 대여요청은 365일 24시간 받을 수 있어야 한다 Async (event-driven), Eventual Consistency
  - 대여요청 시스템이 과중되면 사용자를 잠시동안 받지 않고 대여요청을 잠시후에 하도록 유도한다 Circuit breaker, fallback
3. 성능
  - 고객이 자주 대여요청 상태를 확인할 수 있어야 한다 CQRS
```

### Microservice명

```
도서관리 – book
대여관리 - rent
배송관리 - delivery
현황조회 - view
```


# 분석/설계

### AS-IS 조직 (Horizontally-Aligned)

![1  AS-IS조직](https://user-images.githubusercontent.com/84000922/122162394-7b1c0f80-ceae-11eb-95c4-8952596bb623.png)




### TO-BE 조직 (Vertically-Aligned)

![2  TO-BE 조직](https://user-images.githubusercontent.com/84000919/124354495-37aff800-dc47-11eb-9c8d-a02d2bdedbaf.png)




### 이벤트 도출

```
- 책 등록됨
- 대여됨
- 대여취소됨
- 배송시작됨
- 배송취소됨
```



### 부적격 이벤트 탈락

```
- 과정중 도출된 잘못된 도메인 이벤트 또는 구현 제외 이벤트들을 걸러내는 작업을 수행
- 결제, 연체관리, 반납관리, 알림 기능 제외
```



### 액터, 커맨드 부착하여 읽기 좋게

![5  액터, 커맨드 부착하여 읽기 좋게](https://user-images.githubusercontent.com/84000919/124354669-113e8c80-dc48-11eb-88d8-d8c49d449a38.JPG)



### 어그리게잇으로 묶기

![6  어그리게잇으로 묶기](https://user-images.githubusercontent.com/84000919/124354684-22879900-dc48-11eb-8c4b-2a01ed5e4636.JPG)

```
- 도서관리, 대여관리, 배송관리는 그와 연결된 command 와 event 들에 의하여 트랜잭션이 유지되어야 하는 단위로 그들 끼리 묶어줌
```



### 바운디드 컨텍스트로 묶기

![7  바운디드 컨텍스트로 묶기](https://user-images.githubusercontent.com/84000919/124354698-3501d280-dc48-11eb-9986-0beebd408ae7.JPG)



### 폴리시 부착, 이동 및 컨텍스트 매핑(점선은 Pub/Sub, 실선은 Req/Resp)

![image](https://user-images.githubusercontent.com/84000919/124354739-71cdc980-dc48-11eb-9690-e624b603f3c1.JPG)



### 완성된 1차 모형

![image](https://user-images.githubusercontent.com/84000919/124354745-785c4100-dc48-11eb-8748-027edbbeb20f.JPG)



### 1차 완성본에 대한 기능적 요구사항을 커버하는지 검증 (1/2)

![10  1차 완성본에 대한 기능적](https://user-images.githubusercontent.com/84000919/124355158-bce8dc00-dc4a-11eb-8111-b4cfe85dac99.JPG)

```
1. 고객이 책을 대여 요청한다. (ok)
2. 대여요청을 하면 대여가능 확인 후 대여를 확정한다. (ok)
3. 대여가 확정되면 배송팀에 배송요청한다. (ok)
4. 배송요청이 되면 배송이 시작된다. (ok)
```




### 1차 완성본에 대한 기능적 요구사항을 커버하는지 검증 (2/2)

![11  1차 완성본에 대한 기능적 요구사항](https://user-images.githubusercontent.com/84000919/124355164-c5411700-dc4a-11eb-9344-278db63af705.JPG)

```
5. 고객이 대여 요청을 취소 할 수 있다. (ok)
6. 대여 요청이 취소되면 배송이 취소된다. (ok)
7. 고객은 대여 상태를 조회할 수 있다. (ok)
```




### 1차 완성본에 대한 비기능적 요구사항을 커버하는지 검증

![12  1차 완성본에 대한 비기능적](https://user-images.githubusercontent.com/84000919/124355168-cc682500-dc4a-11eb-818d-e7aec8de6756.JPG)

```
1. 트랜잭션
 - 대여요청을 하면 대여 가능여부 확인 후 대여가 자동 확정된다 Sync 호출
 
2. 장애격리
 - 대여요청은 365일 24시간 받을 수 있어야 한다 Async (event-driven), Eventual Consistency
 - 대여요청 시스템이 과중되면 사용자를 잠시동안 받지 않고 대여요청을 잠시후에 하도록 유도한다 Circuit breaker, fallback
 
3. 성능
 - 고객이 자주 대여요청 상태를 확인할 수 있어야 한다 CQRS
```



### 헥사고날 아키텍처 다이어그램 도출

![13  헥사고날 아키텍처 다이어그램 도출](https://user-images.githubusercontent.com/84000922/122162425-82dbb400-ceae-11eb-9e47-eef31b055935.png)



# 구현:

(서비스 별 포트) 분석/설계 단계에서 도출된 헥사고날 아키텍처에 따라, 각 BC별로 대변되는 마이크로 서비스들을 스프링부트 등으로 구현하였다. 구현한 각 서비스를 로컬에서 실행하는 방법은 아래와 같다 (각자의 포트넘버는 8081 ~ 8084, 8088 이다)

```
cd book
mvn spring-boot:run

cd rent
mvn spring-boot:run 

cd delivery
mvn spring-boot:run  

cd view
mvn spring-boot:run

cd gateway
mvn spring-boot:run
```

## DDD 의 적용

- (Entity 예시) 각 서비스내에 도출된 핵심 Aggregate Root 객체를 Entity 로 선언하였다: (아래 예시는 대여관리 마이크로 서비스). 이때 가능한 현업에서 사용하는 언어 (유비쿼터스 랭귀지)를 그대로 사용하려고 노력했다.

```
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
```
- (Repository 예시) Entity Pattern 과 Repository Pattern 을 적용하여 JPA 를 통하여 다양한 데이터소스 유형 (RDB or NoSQL) 에 대한 별도의 처리가 없도록 데이터 접근 어댑터를 자동 생성하기 위하여 Spring Data REST 의 RestRepository 를 적용하였다. (아래 예시는 배송관리 마이크로서비스)
```
package bookrent;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel="deliveries", path="deliveries")
public interface DeliveryRepository extends PagingAndSortingRepository<Delivery, Long>{

    Delivery findByRentId(Long id);
    
}
```

- 적용 후 REST API 의 테스트
### 1. 도서관리 등록
 - http POST localhost:8081/books title=title01 status=true

![book_post](https://user-images.githubusercontent.com/84000919/124358807-ec084900-dc5c-11eb-89b1-bf8e0be02f8a.JPG)

### 2. 대여관리 등록
 - http POST localhost:8082/rents bookId=1 userId=1 address=address1

![rent_post](https://user-images.githubusercontent.com/84000919/124358857-2bcf3080-dc5d-11eb-9e81-97826eb1b9c5.JPG)

### 2-1. 대여관리 등록 -> 대여상태 확인 및 변경(Sync)
 - status = false로 변경 확인

![rent_post_book](https://user-images.githubusercontent.com/84000919/124358909-6df87200-dc5d-11eb-9b36-d1a9d25b1fb4.JPG)

### 2-2. 대여관리 등록 -> 대여상태 확인 및 변경(Sync) 등록 실패 확인
 - book MSA kill 후 대여관리 오류 확인

![rent_book_kill_error](https://user-images.githubusercontent.com/84000919/124359037-27574780-dc5e-11eb-8b67-ad3176db9808.JPG)

### 2-3. 대여관리 등록 -> 배송관리 등록(Async)
 - 배송관리 자동 등록 확인

![rent_post_delivery](https://user-images.githubusercontent.com/84000919/124358920-810b4200-dc5d-11eb-8d5e-9812d7bafe9a.JPG)

### 2-4. 조회 확인 (CQRS)
![view_1](https://user-images.githubusercontent.com/84000919/124358964-b31ca400-dc5d-11eb-9c8c-8fea95f60a37.JPG)

### 3. 대여관리 취소
 - http PATCH localhost:8082/rents/1 status=Cancel

![cancel_patch](https://user-images.githubusercontent.com/84000919/124358975-c92a6480-dc5d-11eb-99a5-adb441f448af.JPG)

### 3-1. 대여관리 취소 -> 배송관리 취소 -> 도서 상태 변경 (Async)
![cancel_patch_delivery](https://user-images.githubusercontent.com/84000919/124358986-dc3d3480-dc5d-11eb-91aa-e75bebdb6ff5.JPG)

![cancel_patch_book](https://user-images.githubusercontent.com/84000919/124358988-de9f8e80-dc5d-11eb-979e-7bd3ac41207b.JPG)

### 3-2. 조회 확인 (CQRS)
![view_2](https://user-images.githubusercontent.com/84000919/124358999-ef500480-dc5d-11eb-8fde-1517de13f581.JPG)

### 4. Gateway 확인
 - http GET localhost:8088/views/1

![gateway_1](https://user-images.githubusercontent.com/84000919/124359086-5372c880-dc5e-11eb-99d5-162280206a49.JPG)



## 폴리글랏 퍼시스턴스

(H2DB, HSQLDB 사용) view 마이크로서비스는 이력이 많이 쌓일 수 있으므로 자바로 작성된 관계형 데이터베이스인 HSQLDB를 사용하기로 하였다. 이를 위해 pom.xml 파일에 아래 설정을 추가하였다.

```
		<dependency>
			<groupId>org.hsqldb</groupId>
			<artifactId>hsqldb</artifactId>
			<scope>runtime</scope>
		</dependency>
```

- 도서관리, 대여관리, 배송관리 등 나머지 서비스는 H2 DB를 사용한다.
```
		<dependency>
			<groupId>com.h2database</groupId>
			<artifactId>h2</artifactId>
			<scope>runtime</scope>
		</dependency>
```

## 동기식 호출 과 Fallback 처리

분석단계에서의 조건 중 하나로 책대여(대여관리)->도서상태확인및변경(도서관리) 간의 호출은 동기식 일관성을 유지하는 트랜잭션으로 처리하기로 하였다. 호출 프로토콜은 이미 앞서 Rest Repository 에 의해 노출되어있는 REST 서비스를 FeignClient 를 이용하여 호출하도록 한다. 

- (동기호출-Req)낙찰자정보 등록 서비스를 호출하기 위하여 Stub과 (FeignClient) 를 이용하여 Service 대행 인터페이스 (Proxy) 를 구현 
```
# (rent) BookService.java

package bookrent.external;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name="book", url="http://localhost:8081", fallback=BookServiceFallback.class)
public interface BookService {
    @RequestMapping(method= RequestMethod.GET, path="/books/checkAndChangeStatus")
    public boolean checkAndChangeStatus(@RequestParam("bookId") Long bookId);

}
```

- (Fallback) 도서관리 서비스가 정상적으로 호출되지 않을 경우 Fallback 처리
```
# (rent) BookServiceFallback.java

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
```

```
feign:
  hystrix:
    enabled: true
```

- (동기호출-Res) 도서관리 서비스 (정상 호출)
```
# (book) BookController.java

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

```

- (동기호출-PrePersist) 대여정보 입력 전 도서상태 확인 및 변경 호출
```
# Rent.java (Entity)

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
```

- (Fallback-테스트) 도서관리 서비스 종료 후 Fallback 처리 확인

```
# 도서관리(book) 서비스를 잠시 내려놓음 (ctrl+c)

# 대여관리 등록
http POST localhost:8082/rents bookId=1 userId=1 address=address1http POST localhost:8082/rents bookId=1 userId=1 address=address1
```
- BookServiceFallback 호출되어 대여상태(status)가 Fail로 등록
![rent_book_kill_fallback_1](https://user-images.githubusercontent.com/84000919/124359579-959d0980-dc60-11eb-956e-42ad3206dcc2.JPG)

- Log 확인
![rent_book_kill_fallback_2](https://user-images.githubusercontent.com/84000919/124359595-a0579e80-dc60-11eb-8709-7154327cba57.JPG)



## 비동기식 호출 / 시간적 디커플링 / 장애격리 / 최종 (Eventual) 일관성 테스트

대여정보가 등록된 후에 배송관리 시스템에 알려주는 행위는 동기식이 아니라 비 동기식으로 처리하여 배송관리 시스템의 처리를 위하여 대여등록 트랜잭션이 블로킹 되지 않도록 처리한다.
 
- (Publish) 이를 위하여 대여정보를 남긴 후에 곧바로 등록 되었다는 도메인 이벤트를 카프카로 송출한다(Publish)
 
```
    @PostPersist
    public void onPostPersist(){
        if ("Rent".equals(getStatus())) {
            BookRented bookRented = new BookRented();
            BeanUtils.copyProperties(this, bookRented);
            bookRented.publishAfterCommit();
        }
    }
```
- (Subscribe-등록) 배송관리 서비스에서는 대여됨 이벤트를 수신하면 배송정보를 등록하는 정책을 처리하도록 PolicyHandler를 구현한다:

```
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverBookRented_DeliveryStart(@Payload BookRented bookRented){

        if(!bookRented.validate()) return;

        Delivery delivery = new Delivery();
        delivery.setBookId(bookRented.getBookId());
        delivery.setRentId(bookRented.getId());
        delivery.setUserId(bookRented.getUserId());
        delivery.setAddress(bookRented.getAddress());
        delivery.setStatus("Delivery Started");

        deliveryRepository.save(delivery);
    }

```
- (Subscribe-취소) 배송관리 서비스에서는 대여취소됨 이벤트를 수신하면 배송정보를 변경하는 정책을 처리하도록 PolicyHandler를 구현한다:
  
```
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
```

- (장애격리) 배송관리 시스템은 대여관리 시스템과 완전히 분리되어 있으며, 이벤트 수신에 따라 처리되기 때문에, 배송관리 시스템이 유지보수로 인해 잠시 내려간 상태라도 대여관리 서비스에 영향이 없다:
```
# 배송관리 서비스 (delivery) 를 잠시 내려놓음 (ctrl+c)

# 대여관리 등록 : Success
http POST localhost:8082/rents bookId=1 userId=1 address=address1

# 조회확인
http GET localhost:8084/views/1

# 배송관리 서비스 기동
cd delivery
mvn spring-boot:run

# 조회기능에서 배송정보 갱신 확인
http GET localhost:8084/views/1
```
- delivery 종료 후 대여등록 시 대여등록 정상 및 view 확인 (배송상태 미적용 확인)
![pubsub_rent_post](https://user-images.githubusercontent.com/84000919/124359950-0f81c280-dc62-11eb-9a4b-89b1fc37233d.JPG)

![pubsub_rent_post_view](https://user-images.githubusercontent.com/84000919/124359951-11e41c80-dc62-11eb-8459-1dc641f4f3f7.JPG)


- delivery 재시작 후 view 확인 (배송상태 정상 변경 확인)
![pubsub_rent_post_view_2](https://user-images.githubusercontent.com/84000919/124359955-16103a00-dc62-11eb-82d0-58046bc5dbb9.JPG)




# 운영:

컨테이너화된 마이크로서비스의 자동 배포/조정/관리를 위한 쿠버네티스 환경 운영

## Deploy

- GitHub 와 연결 후 로컬빌드를 진행 진행
```
	cd team
	mkdir sourcecode
	cd sourcecode
	git clone --recurse-submodules https://github.com/21-2-1team/bidding03.git
	
	cd bidding
	cd BiddingExamination
	mvn package
	
	cd ../BiddingManagement
	mvn package
	
	cd ../BiddingParticipation
	mvn package
	
	cd ../MyPage
	mvn package
	
	
	cd ../Notification
	mvn package
	
	
	cd ../gateway
        mvn package
```
- namespace 등록 및 변경
```
kubectl config set-context --current --namespace=bidding  --> bidding namespace 로 변경

kubectl create ns bidding
```

- ACR 컨테이너이미지 빌드
```
az acr build --registry user01skccacr --image user01skccacr.azurecr.io/biddingexamination:latest .
```
![image](https://user-images.githubusercontent.com/70736001/122502677-096cce80-d032-11eb-96e7-84a8024ab45d.png)

나머지 서비스에 대해서도 동일하게 등록을 진행함
```
az acr build --registry user01skccacr --image user01skccacr.azurecr.io/biddingmanagement:latest .
az acr build --registry user01skccacr --image user01skccacr.azurecr.io/biddingparticipation:latest .
az acr build --registry user01skccacr --image user01skccacr.azurecr.io/biddingparticipation:latest .
az acr build --registry user01skccacr --image user01skccacr.azurecr.io/mypage:latest  .
az acr build --registry user01skccacr --image user01skccacr.azurecr.io/notification:latest  .
az acr build --registry user01skccacr --image user01skccacr.azurecr.io/gateway:latest .
```

- 배포진행

1.bidding/BiddingExamination/kubernetes/deployment.yml 파일 수정 (BiddingManagement/BiddingParticipation/MyPage/Notification/gateway 동일)

![image](https://user-images.githubusercontent.com/70736001/122512566-011d8f00-d044-11eb-8bd5-91d939f7ab1b.png)

2.bidding/BiddingExamination/kubernetes/service.yaml 파일 수정 (BiddingManagement/BiddingParticipation/MyPage/Notification 동일)

![image](https://user-images.githubusercontent.com/70736001/122512673-26aa9880-d044-11eb-8587-38f8cd261326.png)

3.bidding/gateway/kubernetes/service.yaml 파일 수정

![image](https://user-images.githubusercontent.com/70736001/122503123-da0a9180-d032-11eb-9283-224d7860c9c3.png)

4. 배포작업 수행
``` 
	cd gateway/kubernetes
	kubectl apply -f deployment.yml
	kubectl apply -f service.yaml
	
	cd ../../BiddingExamination/kubernetes
	kubectl apply -f deployment.yml
	kubectl apply -f service.yaml
	
	cd ../../BiddingManagement/kubernetes
	kubectl apply -f deployment.yml
	kubectl apply -f service.yaml
	
	
	cd ../../BiddingParticipation/kubernetes
	kubectl apply -f deployment.yml
	kubectl apply -f service.yaml
	
	
	cd ../../MyPage/kubernetes
	kubectl apply -f deployment.yml
	kubectl apply -f service.yaml
	
	
	cd ../../Notification/kubernetes
	kubectl apply -f deployment.yml
	kubectl apply -f service.yaml
``` 

5. 배포결과 확인
``` 
kubectl get all
``` 
![image](https://user-images.githubusercontent.com/70736001/122503307-2b1a8580-d033-11eb-83fc-63b0f2154e3b.png)

- Kafka 설치
``` 
curl https://raw.githubusercontent.com/helm/helm/master/scripts/get > get_helm.sh
chmod 700 get_helm.sh
./get_helm.sh

kubectl --namespace kube-system create sa tiller 
kubectl create clusterrolebinding tiller --clusterrole cluster-admin --serviceaccount=kube-system:tiller
helm init --service-account tiller

helm repo add incubator https://charts.helm.sh/incubator
helm repo update

kubectl create ns kafka
helm install --name my-kafka --namespace kafka incubator/kafka

kubectl get all -n kafka
``` 
설치 후 서비스 재기동

## Autoscale (HPA)
앞서 CB(Circuit breaker)는 시스템을 안정되게 운영할 수 있게 해줬지만 사용자의 요청을 100% 받아들여주지 못했기 때문에 이에 대한 보완책으로 자동화된 확장 기능을 적용하고자 한다.

- 리소스에 대한 사용량 정의(bidding/BiddingManagement/kubernetes/deployment.yml)
![image](https://user-images.githubusercontent.com/70736001/122503960-49cd4c00-d034-11eb-8ab4-b322e7383cc0.png)

- Autoscale 설정 (request값의 20%를 넘어서면 Replica를 10개까지 동적으로 확장)
```
kubectl autoscale deployment biddingmanagement --cpu-percent=20 --min=1 --max=10
```

- siege 생성 (로드제너레이터 설치)
```
kubectl apply -f - <<EOF
apiVersion: v1
kind: Pod
metadata:
  name: siege
  namespace: bidding
spec:
  containers:
  - name: siege
    image: apexacme/siege-nginx
EOF
```
- 부하발생 (50명 동시사용자, 30초간 부하)
```
kubectl exec -it pod/siege  -c siege -n bidding -- /bin/bash
siege -c50 -t30S -v --content-type "application/json" 'http://52.231.8.61:8080/biddingManagements POST {"noticeNo":1,"title":"AAA"}'
```
- 모니터링 (부하증가로 스케일아웃되어지는 과정을 별도 창에서 모니터링)
```
watch kubectl get al
```
- 자동스케일아웃으로 Availablity 100% 결과 확인 (시간이 좀 흐른 후 스케일 아웃이 벌어지는 것을 확인, siege의 로그를 보아도 전체적인 성공률이 높아진 것을 확인함)

1.테스트전

![image](https://user-images.githubusercontent.com/70736001/122504322-0aebc600-d035-11eb-883f-35110d9d0457.png)

2.테스트후

![image](https://user-images.githubusercontent.com/70736001/122504349-1e972c80-d035-11eb-814e-a5ab909215c4.png)

3.부하발생 결과

![image](https://user-images.githubusercontent.com/70736001/122504389-31a9fc80-d035-11eb-976e-f43261d1a8c2.png)

## Config Map
ConfigMap을 사용하여 변경가능성이 있는 설정을 관리

- 입찰심사(BiddingExamination) 서비스에서 동기호출(Req/Res방식)로 연결되는 입찰관리(BiddingManagement) 서비스 url 정보 일부를 ConfigMap을 사용하여 구현

- 파일 수정
  - 입찰심사 소스 (BiddingExamination/src/main/java/bidding/external/BiddingManagementService.java)

![image](https://user-images.githubusercontent.com/70736001/122505096-9dd93000-d036-11eb-91b7-0ec57b6e1b10.png)

- Yaml 파일 수정
  - application.yml (BiddingExamination/src/main/resources/application.yml)
  - deploy yml (BiddingExamination/kubernetes/deployment.yml)

![image](https://user-images.githubusercontent.com/70736001/122505177-c5c89380-d036-11eb-91b3-f399547b50ff.png)

- Config Map 생성 및 생성 확인
```
kubectl create configmap bidding-cm --from-literal=url=BiddingManagement
kubectl get cm
```

![image](https://user-images.githubusercontent.com/70736001/122505221-dc6eea80-d036-11eb-8757-b97f8d75baff.png)

```
kubectl get cm bidding-cm -o yaml
```

![image](https://user-images.githubusercontent.com/70736001/122505270-f6103200-d036-11eb-8c96-513f95448989.png)

```
kubectl get pod
```

![image](https://user-images.githubusercontent.com/70736001/122505313-0fb17980-d037-11eb-9b57-c0d14f468a1c.png)


## Zero-Downtime deploy (Readiness Probe)
쿠버네티스는 각 컨테이너의 상태를 주기적으로 체크(Health Check)해서 문제가 있는 컨테이너는 서비스에서 제외한다.

- deployment.yml에 readinessProbe 설정 후 미설정 상태 테스트를 위해 주석처리함 
```
readinessProbe:
httpGet:
  path: '/biddingManagements'
  port: 8080
initialDelaySeconds: 10
timeoutSeconds: 2
periodSeconds: 5
failureThreshold: 10
```

- deployment.yml에서 readinessProbe 미설정 상태로 siege 부하발생

![image](https://user-images.githubusercontent.com/70736001/122505873-2906f580-d038-11eb-86b8-2f8388f82dd1.png)

```
kubectl exec -it pod/siege  -c siege -n bidding -- /bin/bash
siege -c100 -t5S -v --content-type "application/json" 'http://20.194.120.4:8080/biddingManagements POST {"noticeNo":1,"title":"AAA"}
```
1.부하테스트 전

![image](https://user-images.githubusercontent.com/70736001/122506020-75eacc00-d038-11eb-99df-4a4b90478bc3.png)

2.부하테스트 후

![image](https://user-images.githubusercontent.com/70736001/122506060-84d17e80-d038-11eb-8449-b94b28a0f385.png)

3.생성중인 Pod 에 대한 요청이 들어가 오류발생

![image](https://user-images.githubusercontent.com/70736001/122506129-a03c8980-d038-11eb-8822-5ec57926b900.png)

- 정상 실행중인 biddingmanagement으로의 요청은 성공(201),비정상 적인 요청은 실패(503 - Service Unavailable) 확인

- hpa 설정에 의해 target 지수 초과하여 biddingmanagement scale-out 진행됨

- deployment.yml에 readinessProbe 설정 후 부하발생 및 Availability 100% 확인

![image](https://user-images.githubusercontent.com/70736001/122506358-2527a300-d039-11eb-84cb-62eb09687bda.png)

1.부하테스트 전

![image](https://user-images.githubusercontent.com/70736001/122506400-3c669080-d039-11eb-8e5e-a4f76b0e2956.png)

2.부하테스트 후

![image](https://user-images.githubusercontent.com/70736001/122506421-4be5d980-d039-11eb-92a2-44e7827299bf.png)

3.readiness 정상 적용 후, Availability 100% 확인

![image](https://user-images.githubusercontent.com/70736001/122506471-61f39a00-d039-11eb-9077-608f375e27f3.png)


## Self-healing (Liveness Probe)
쿠버네티스는 각 컨테이너의 상태를 주기적으로 체크(Health Check)해서 문제가 있는 컨테이너는 자동으로재시작한다.

- depolyment.yml 파일의 path 및 port를 잘못된 값으로 변경
  depolyment.yml(BiddingManagement/kubernetes/deployment.yml)
```
 livenessProbe:
    httpGet:
        path: '/biddingmanagement/failed'
        port: 8090
      initialDelaySeconds: 30
      timeoutSeconds: 2
      periodSeconds: 5
      failureThreshold: 5
```




![image](https://user-images.githubusercontent.com/70736001/122506714-d75f6a80-d039-11eb-8bd0-223490797b58.png)

- liveness 설정 적용되어 컨테이너 재시작 되는 것을 확인
  Retry 시도 확인 (pod 생성 "RESTARTS" 숫자가 늘어나는 것을 확인) 

1.배포 전

![image](https://user-images.githubusercontent.com/70736001/122506797-fb22b080-d039-11eb-9a0b-754e0fea45b2.png)

2.배포 후

![image](https://user-images.githubusercontent.com/70736001/122506831-0c6bbd00-d03a-11eb-880c-dc8d3e00798f.png)

## Circuit Breaker
서킷 브레이킹 프레임워크의 선택: Spring FeignClient + Hystrix 옵션을 사용하여 구현함
시나리오는 심사결과등록(입찰심사:BiddingExamination)-->낙찰자정보등록(입찰관리:BiddingManagement) 시의 연결을 RESTful Request/Response 로 연동하여 구현이 되어있고, 낙찰자정보등록이 과도할 경우 CB 를 통하여 장애격리.


- Hystrix 를 설정: 요청처리 쓰레드에서 처리시간이 1000ms가 넘어서기 시작하면 CB 작동하도록 설정

**application.yml (BiddingExamination)**
```
feign:
  hystrix:
    enabled: true

hystrix:
  command:
    default:
      execution.isolation.thread.timeoutInMilliseconds: 1000
```
![image](https://user-images.githubusercontent.com/70736001/122508631-3a9ecc00-d03d-11eb-9bce-a786225df40f.png)

- 피호출 서비스(입찰관리:biddingmanagement) 의 임의 부하 처리 - 800ms에서 증감 300ms 정도하여 800~1100 ms 사이에서 발생하도록 처리
BiddingManagementController.java
```
req/res를 처리하는 피호출 function에 sleep 추가

	try {
	   Thread.sleep((long) (800 + Math.random() * 300));
	} catch (InterruptedException e) {
	   e.printStackTrace();
	}
```
![image](https://user-images.githubusercontent.com/70736001/122508689-5609d700-d03d-11eb-9e08-8eadc904d391.png)

- req/res 호출하는 위치가 onPostUpdate에 있어 실제로 Data Update가 발생하지 않으면 호출이 되지 않는 문제가 있어 siege를 2개 실행하여 Update가 지속적으로 발생하게 처리 함
```
siege -c2 –t20S  -v --content-type "application/json" 'http://20.194.120.4:8080/biddingExaminations/1 PATCH {"noticeNo":"n01","participateNo":"p01","successBidderFlag":"true"}'
siege -c2 –t20S  -v --content-type "application/json" 'http://20.194.120.4:8080/biddingExaminations/1 PATCH {"noticeNo":"n01","participateNo":"p01","successBidderFlag":"false"}'
```
![image](https://user-images.githubusercontent.com/70736001/122508763-7b96e080-d03d-11eb-90f8-8380277cdc17.png)

