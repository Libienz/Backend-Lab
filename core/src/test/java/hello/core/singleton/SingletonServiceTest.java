package hello.core.singleton;

public class SingletonServiceTest {

    private static final SingletonServiceTest instance = new SingletonServiceTest(); //하나만 만들어서 가지고 있기

    private SingletonServiceTest() { //내부에서는 만들 수 있지만 그럴 일 없음 외부에서 생성할 수 없도록 하는 역할
    }

    public static SingletonServiceTest getInstance() {
        return instance;
    }

    public void logic() {
        System.out.println("싱글톤 객체 로직 호출");
    }
}
