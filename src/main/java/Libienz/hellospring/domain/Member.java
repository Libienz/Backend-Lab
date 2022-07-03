package Libienz.hellospring.domain;

public class Member {

    private Long id;
    private String name;


    //인텔리제이 getter setter 단축키는 alt + insert
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }



}
