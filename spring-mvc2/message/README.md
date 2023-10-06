# Spring MVC 2편 

<details>
<summary>Section 03 메시지, 국제화 </summary>
<div markdown="1">

## 메시지, 국제화 소개
#### 메시지
- 기획자가 화면에 보이는 문구가 마음에 들지 않는다고 상품명이라는 단어를 모두 상품이름으로 고쳐달라고 했다.
- 여러 화면에 보이는 상품명, 가격, 수량 등 label에 있는 단어를 변경하려면 다음 화면들을 다 찾아가면서 변경해야 할 것
- 지금 처럼 화면 수가 적으면 작업이 많지 않겠지만 화면이 수십개 이상이라면 작업량이 상당할 것이다..
- 왜냐하면 해당 HTML 파일에 현재 메시지가 하드코딩 되어 있기 때문이다.
- 이런 다양한 메시지를 한 곳에서 관리하도록 하는 기능을 메시지 기능이라 한다.
- 예를 들어서 messages.properties라는 메시지 관리용 파일을 만들고

```properties
item=상품
item.id=상품 ID
item.itemName=상품명
item.price=가격
item.quantity=수량
```
- 각 HTML들은 다음과 같이 해당 데이터를 key 값으로 불러서 사용하는 것이다.
- ```<label for="itemName" th:text="#{item.itemName}"></label>```

## 국제화
- 메시지에서 한 발 더 나가보자
- 메시지에서 설명한 메시지 파일 messages.properties를 나라별로 별도로 관리하면 서비스를 국제화 할 수 있다.
- 예를 들어서 다음과 같이 2개의 파일을 만들어서 분류한다.

#### messages_en.properties
```properties
item=Item
item.id=Item ID
item.itemName=Item Name
item.price=price
item.quantity=quantity
```

#### messages_ko.properties
```properties
item=상품
item.id=상품 ID
item.itemName=상품명
item.price=가격
item.quantity=수량
```
- 영어를 사용하는 사람이면 messages_en.properties를 사용하고
- 한국어를 사용하는 사람이면 messages_ko.properties를 사용하게 개발하면 된다.
- 이렇게 하면 사이트를 국제화 할 수 있다.
- 한국에서 접근한 것인지 영어에서 접근한 것인지 인식하는 방법은 HTTP의 헤더 값을 사용하거나 사용자가 직접 언어를 선택하도록 하거나 쿠키 등을 사용해서 처리하면 된다.
- 메시지 국제화 기능을 직접 구현할 수도 있겠지만, 스프링은 기본적인 메시지와 국제화 기능을 모두 제공한다.
- 그리고 타임리프도 스프링이 제공하는 메시지와 국제화 기능을 편리하게 통합해서 제공한다.
- 지금부터 스프링이 제공하는 메시지와 국제화 기능을 알아보자

## 스프링 메시지 소스 설정
- 스프링은 기본적인 메시지 관리 기능을 제공한다.
- 메시지 관리 기능을 사용하려면 스프링이 제공하는 MessageSource를 스프링 빈으로 등록하면 된다.
- 그런데 스프링 부트를 사용하면 스프링 부트가 우리가 properties에 정의한 MessageSource들을 자동으로 스프링 빈으로 등록한다.
#### application.properties
```properties
spring.messages.basename=messages,config.i18n.messages
```
- 단 별도의 설정을 하지 않으면 messages라는 이름으로 기본 등록도 처리한다.
- 따라서 messages_en.properties, messages_ko.properties, messages.properties 파일만 등록하면 자동으로 인식된다.

## 메시지 파일 만들기
- 메시지 파일을 만들어보자, 국제화 테스트를 위해서 messages_en 파일도 실험해보자

#### messages.properties
```properties
hello=안녕
hello.name=안녕 {0}
```

#### messages_en.properties
```properties
hello=hello
hello.name=hello {0}
```


## 웹 애플리케이션에 메시지 적용하기
- 실제 웹 애플리케이션에 메시지를 적용해보자
- 타임리프의 메시지 표현식 기능을 이용 메시지를 중앙 관리 해보자

```properties
label.item=상품
label.item.id=상품 ID
label.item.itemName=상품명
label.item.price=가격
label.item.quantity=수량
page.items=상품 목록
page.item=상품 상세
page.addItem=상품 등록
page.updateItem=상품 수정
button.save=저장
button.cancel=취소
```

#### 타임리프 메시지 적용
- 타임리프의 메시지 표현식 ```#{...}```를 사용하면 스프링의 메시지를 편리하게 조회할 수 있다.
- 예를 들어서 방금 등록한 상품이라는 이름을 조회하려면 ```#{label.item}```이라고 하면 된다.
- 모든 html에 적용 실습을 진행해보자 -> 진행완료




</div>
</details>