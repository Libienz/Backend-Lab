# Spring MVC 2편

<details>
<summary>Section 06 login - cookies, session</summary>
<div markdown="1">

## 로그인 요구 사항
- ![img.png](img.png)
- ![img_1.png](img_1.png)
- ![img_2.png](img_2.png)

## 프로젝트 생성
- 도메인이 가장 중요하다!
- 도메인 = 화면, UI, 기술 인프라 등등의 영역을 제외한 시스템이 구현해야 하는 핵심 비즈니스 업무 영역을 말한다.
- 향후 web을 다른 기술로 바꾸어도 (api로 바꾸더라도, ssr로 바꾸더라도) 도메인은 그대로 유지될 수 있어야 한다.
- 그러기 위해서 중요한 것은 단방향 의존관계 설정이다. (단방향으로 흘러가도록 설계해야 잘 설계한 것)
- web은 domain을 알고있지만 domain은 web을 모르도록 설계해야 한다. 
- 이것을 web은 domain을 의존하지만 domain은 web을 의존하지 않는다고 표현한다.


</div>
</details>