# JAVA_Spring_Lab


<details>
<summary>00 환경설정</summary>
<div markdown="1">

## 프로젝트 생성
### - start.spring.io
여기 사이트에서 프로젝트를 빌드 할 수 있다. 
### - MAVEN vs Gradle ??
전체적인 빌드시스템 혹은 프로젝트 관리 방식을 말한다. 라이브러리를 어떻게 긁어와 관리할 것인지
라이프 사이클을 어떻게 설정하는지의 차이가 정해진다. 현재는 Gradle을 많이 사용하는 추세임 훨 빠르고 최신기술이다.
### - Spring Web, Thymeleaf
각자는 우리가 기본적으로 사용하게 될 라이브러리. 프로젝트를 생성할 때 dependencies를 선택하여 라이브러리를 긁어올 수 있다.
## 라이브러리 살펴보기
우리가 긁어온 라이브러리는 Spring Web, Thymeleaf 하지만 Externel Libraries를 살펴보면 훨씬 많은 양의 라이브러리들이 임포트 되어 있는 것을 확인할 수 있다.
이는 우리가 긁어온 라이브러리들이 구동되기 위해 의존하는 라이브러리들로 Gradle 방식이 의존하는 모든 라이브러리들을 관리하는 모습을 보인다.
## View 환경설정
고객의 요청을 수행하여 화면을 보이는 데에는 3가지 방식이 있다.
### 1. static
적어놓은 html을 별도의 작업 없이 웹서버가 요청한 쪽으로 넘겨준다.
### 2. Template MVC
http 리퀘스트 도착하면 스프링 부트 프로젝트의 내장 서버인 톰캣 서버가 컨트롤러 속 @GetMapping 어노테이션을 뒤지면서 요청한 url이랑 매핑되는 메소드를 찾는다.
이어 해당 메소드가 실행되고 메소드는 View Resolver에게 html 파일 이름과 모델을 보낸다. View Resolver는 템플릿 엔진을 이용, 해당 html 파일에서 모델등의 값과 함께 html 파일을 렌더링하고 요청한 쪽으로 반환하다.
우리의 실습에서 import한 Thymeleaf가 템플릿 엔진임
### 3. API
http 리퀘스트를 받고 메소드를 찾아가지만 @ResponseBody 라는 어노테이션을 사용. 렌더링이나 html 파일을 뿌리는 것이 아닌 Body부를 직접 넘겨주는 방식이다.

## 빌드하고 실행하기
https://dev-gorany.tistory.com/281
</div>
</details>

<details>
<summary>01 스프링 웹 개발 기초</summary>
<div markdown="1">

## 정적 컨텐츠
이전에 00 환경설정에서 설명했던 것처럼 http 요청에 반응하여 html 파일을 그냥 뿌려주는 방식이다. 다만 주의 할 점은 스프링 부트 속 톰 캣 서버에 요청이 오게 되면 바로 resources의 static으로 찾아가지 않고 먼저 컨트롤러를 뒤진다.
컨트롤러를 찾지 못하고 매핑된 메소드를 찾지 못하면 static에서 html파일을 찾는 것이다.
순서가 후위임을 기억하자.

![img.png](img.png)
## MVC와 템플릿 엔진
mvc패턴은 디자인 패턴 중 하나를 말한다. Model, View, Controller의 약자로 프로젝트의 구성 요소를 세가지의 역할로 구분한 패턴이다.
![img_1.png](img_1.png)
사용자가 controller를 조작하면 컨트롤러는 model을 통해서 데이터를 가져오고 그 정보를 바탕으로 시각적인 표현을 담당하는 View를 제어해서 사용자에게 전달함

### Model
애플리케이션의 정보 데이터를 나타낸다. 데이터베이스, 처음의 정의하는 상수, 초기화값, 변수 등을 뜻함
사용자가 편집하길 원하는 모든 데이터를 다룰 수 있어야 하며 뷰나 컨트롤러에 대해서 어떤 정보도 알지 못하게 설계함으로써 객체의 응집성을 지켜야한다.
### View
사용자 인터페이스 요소들을 나타낸다. 즉 데이터 및 객체의 입력, 그리고 보여주는 출력만을 담당
모델이 가지고 있는 정보를 따로 저장하는게 아닌 받아서 화면에 적절한 방식으로 뿌리는 역할만을 수행!
### Controller
데이터와 사용자 인터페이스 요소들을 잇는 다리 역할을 한다. 여러 이벤트들을 처리하는 부분을 뜻한 다는 것
컨트롤러는 둘 사이를 중재하기에 모델이나 뷰등에 대해서 알고 있어야 하는 점이 다른 요소들과 다른 점이다.
### 템플릿, 템플릿 엔진
![img_2.png](img_2.png)
요청이 들어오면 내장 톰캣 서버에서 컨트롤러의 메소드를 뒤지고 같은 이름으로 매핑된 메소드를 찾는다. ViewResolver에게 메소드의 반환값을 전달하고 ViewResolver는 템플릿 엔진 처리를 마치고 HTML파일을 반환하는 방식
## API
@ResponseBody 어노테이션을 컨트롤러 안에 있는 메소드에 붙여주게 되면 해당메소드는 동작할 때 ViewResolver를 사용하지 않는다.
대신에 HTTP의 Body에 문자 내용을 직접 반환한다.
즉 http 요청에 응답하여 반환하는 response의 바디를 직접 쓰는 것
![img_3.png](img_3.png)
그림처럼 ViewResolver대신에 HttpMessageConverter가 작동하게 되고 문자의 처리는 String Converter, 객체의 처리는 JsonConverter가 작동하게 된다. 


</div>
</details>

<details>
<summary>02 회원관리 예제 백엔드 개발</summary>
<div markdown="1">

## 비즈니스 요구사항 
데이터 : 회원ID, 이름
기능 : 회원 등록, 조회
아직 데이터 저장소가 선정되지 않음 -> 인터페이스로 만들고 내부의 저장소 우선 사용

![img_4.png](img_4.png)

## MemberRepository
여기 interface에서 Optional<Member> findById (Long id);
라는 미구현 메소드를 볼 수 있는데 Optional은 널처리에 많이 쓰이는 방식으로 Optional로 감싸면 
널처리가 쉬워진다.
```java
public Optional<Member> findByid(Long id) {
        return Optional.ofNullable(store.get(id));
        }

```
스트림과 람다식을 이용한 findByName
```java
public Optional<Member> findByName(String name) {
    return store.values().stream() //value는 맵의 값들을 콜렉션 형태로 반환 .stream()은 
        .filter(member -> member.getName().equals(name))
        .findAny(); //하나라도 찾으면 
}
```
자바 실무에서 List를 자주쓴다. 인터페이스임을 기억하자!
```java
    @Override
    public List<Member> findAll() {
        return new ArrayList<>(store.values());
    }

```

## Test Case
메인 메소드 계속해서 돌리면서 체크하면 오버헤드 쩐다. 코드를 코드로 검토하자
--> junit이라는 프레임워크 사용 테스트를 간편하게 할 수 있다. 

assert를 이용한 방법
```java
@Test //이게 junit에서 제공하는 거
    public void save() {
        Member member = new Member();
        member.setName("spring");

        repository.save(member);

        Member result = repository.findById(member.getId()).get();
        //System.out.println("result = " + (result == member));
        //Assertions.assertEquals(member, result); 
        assertThat(member).isEqualTo(result); //alt enter -> 스태틱 임포트
    }
```
모든 테스트는 순서가 보장되지 않으니 저장소 같은 곳을 건드릴 때 생각하고 사용하자
여담 : 테스트를 먼저 만들고 구현을 나중에 하는 방식을 테스트 주도 개발, TTD라고 함
//given
//when
//then으로 구분하면 보기 쉬움
```java
    @AfterEach 
    // 메소드 테스트가 끝날때마다 실행되는 콜백 메소드
    //콜백 메소드 : 어떤 이벤트가 발생했거나 특정 시점에 도달했을 때 시스템에서 호출하는 함수!
    public void afterEach() {
        repository.clearStore();
    }
```
## Member Service 
```java
    public Long join(Member member) {
        Optional<Member> result = memberRepository.findByName(member.getName());
        result.ifPresent(m -> { //result는 옵셔널 
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        });
        memberRepository.save(member);
        return member.getId();
    }

```
##Member Service Test
예외가 올바르게 터지는지 확인하는 법 try catch 보다 편한 문법은 assertThrows

```java
        assertThrows(IllegalStateException.class, () -> memberService.join(member2));
        //람다를 실행할 건데 앞의 오류가 터져야함 안터지면 test fail
/*
        try {
            memberService.join(member2);
            fail();
        }
        catch (IllegalStateException e) {
            assertThat(e.getMessage()).isEqualTo("이미 존재하는 회원입니다.");
        }
```

```java
    @BeforeEach
    public void beforeEach() {
        memberRepository = new MemoryMemberRepository();
        memberService = new MemberService(memberRepository);
        //멤버 서비스 입장에서 레포를 직접 만들지 않고 외부에서 받네? -> dependency injection 의존성 주입 di
        
    }
    @AfterEach
    public void afterEach() {
        memberRepository.clearStore();
    }
```
</div>
</details>


<details>
<summary>편리한 단축키</summary>
<div markdown="1">

shift + f6 : 변수이름 같은거 한꺼번에 바꾸기

alt + enter : static도 임포트 가능

crtl + alt + m : 해당 식을 메소드로 추출

crtl + shift + t : 해당클래스의 테스트 클래스 만들기

crtl(command) + alt(option) + v : 함수 표현식만 쓰고 리턴값을 받고 싶을때 사용

</div>
</details>