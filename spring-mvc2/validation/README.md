# Spring MVC 2편

<details>
<summary>Section 04 메시지, 국제화 </summary>
<div markdown="1">

## 검증 요구사항 도착
- 상품 관리 시스템에 새로운 요구사항이 추가되었다.
  - 타입 검증
    - 가격, 수량에 문자가 들어가면 검증 오류 처리
  - 필드 검증
    - 상품명: 필수, 공백 X
    - 가격 1000원 이상, 1백만원 이하
    - 수량 최대 9999
  - 특정 필드의 범위를 넘어서는 검증
    - 가격 * 수량의 합은 10000원 이상
- 지금까지 만든 웹 애플리케이션은 폼 입력시 숫자를 문자로 작성하거나 해서 검증 오류가 발생하면 오류 화면으로 바로 이동한다.
- 이렇게 되면 사용자는 처음부터 해당 폼으로 이동해서 다시 입력을 해야 한다
- 유저 경험이 좋지 않다. 웹 서비스는 의례 폼 입력시 오류가 발생하면 고객이 입력한 데이터를 유지한 상태로 어떤 오류가 발생했는지 친절하게 알려주어야 한다.
- 컨트롤러의 중요한 역할 중 하나는 HTTP 요청이 정상인지 검증하는 것이다!
- 그리고 정상 로직보다 이런 검증 로직을 잘 개발하는 것이 더 어려울 수 있다.

### 참고: 클라이언트 검증, 서버 검증
- 클라이언트 검증은 조작할 수 있음으로 보안에 취약하다.
- 서버만으로 검증하면, 즉각적인 고객 사용성이 부족해진다.
- 둘을 적절히 섞어서 사용하되, 최종적으로 서버 검증은 필수
- API 방식을 사용하면 API 스펙을 잘 정의해서 검증 오류를 API 응답 결과에 잘 남겨주어야 함

## V1 검증 직접 처리
- 고객이 상품 등록 폼에서 상품명을 입력하지 않거나 가격, 수량 등이 너무 작거나 커서 검증 범위를 넘어서면 서버 검증 로직이 실패해야 한다.
- v1에서는 컨트롤러에서의 분기를 통해 직접적으로 검증을 처리한다. 

#### ValidationControllerV1 - addItem()

```java
    @PostMapping("/add")
    public String addItem(@ModelAttribute Item item, RedirectAttributes redirectAttributes, Model model) {

        //검증 오류 결과를 보관
        Map<String, String> errors = new HashMap<>();

        //검증 로직
        if (!StringUtils.hasText(item.getItemName())) {
            errors.put("itemName", "상품 이름은 필수입니다.");
        }
        if (item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000) {
            errors.put("price", "가격은 1,000 ~ 1,000,000 까지 허용합니다.");
        }
        if (item.getQuantity() == null || item.getQuantity() >= 9999) {
            errors.put("quantity", "수량은 최대 9,999 까지 허용합니다.");
        }
        //특정 필드가 아닌 복합 룰 검증
        if (item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();
            if (resultPrice < 10000) {
                errors.put("globalError", "가격 * 수량의 합은 10,000원 이상이어야 합니다. 현재 값 = " + resultPrice);
            }
        }
        //검증에 실패하면 다시 입력 폼으로
        if (!errors.isEmpty()) {
            log.info("errors = {} ", errors);
            model.addAttribute("errors", errors);
            return "validation/v1/addForm"; //이렇게 넘어가도 유저가 입력한 값이 남아있다. 왜? th:object item에 들어있으니까
        }

        //성공 로직
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v1/items/{itemId}";
    }
```
- 해당 코드를 보면 검증시 오류가 발생할 경우 Map에 오류 정보를 담아두는 것을 확인할 수 있다.
- 이때 어떤 필드에서 오류가 발생했는지 구분하기 위해 오류가 발생한 필드명을 key로 이용한다
- 이후 뷰에서 이 데이터를 사용해서 고객에게 친절한 오류메시지를 출력하는 것이다.

#### 검증에 실패하면 다시 입력 폼으로
```java
if (!errors.isEmpty()) {
 model.addAttribute("errors", errors);
 return "validation/v1/addForm";
}
```
- 만약 검증에서 오류 메시지가 하나라도 있으면 오류 메시지를 출력하기 위해 model에 errors를 담고 입력 폼이 있는 뷰 템플릿으로 보낸다.
- 이렇게 해도 유저가 입력한 데이터는 남아있다. 왜!? th:object Item에 담겨있으니까!

### 타임리프 오류 처리 
```html
<!DOCTYPE HTML>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="utf-8">
    <link th:href="@{/css/bootstrap.min.css}"
          href="../css/bootstrap.min.css" rel="stylesheet">
    <style>
 .container {
 max-width: 560px;
 }
 .field-error {
 border-color: #dc3545;
 color: #dc3545;
 }
 </style>
</head>
<body>
<div class="container">
    <div class="py-5 text-center">
        <h2 th:text="#{page.addItem}">상품 등록</h2>
    </div>
    <form action="item.html" th:action th:object="${item}" method="post">
        <div th:if="${errors?.containsKey('globalError')}">
            <p class="field-error" th:text="${errors['globalError']}">전체 오류
                메시지</p>
        </div>
        <div>
            <label for="itemName" th:text="#{label.item.itemName}">상품명</
            label>
            <input type="text" id="itemName" th:field="*{itemName}"
                   th:class="${errors?.containsKey('itemName')} ? 'form-control 
field-error' : 'form-control'"
                   class="form-control" placeholder="이름을 입력하세요">
            <div class="field-error" th:if="${errors?.containsKey('itemName')}"
                 th:text="${errors['itemName']}">
                상품명 오류
            </div>
        </div>
        <div>
            <label for="price" th:text="#{label.item.price}">가격</label>
            <input type="text" id="price" th:field="*{price}"
                   th:class="${errors?.containsKey('price')} ? 'form-control 
field-error' : 'form-control'"
                   class="form-control" placeholder="가격을 입력하세요">
            <div class="field-error" th:if="${errors?.containsKey('price')}"
                 th:text="${errors['price']}">
                가격 오류
            </div>
        </div>
        <div>
            <label for="quantity" th:text="#{label.item.quantity}">수량</label>
            <input type="text" id="quantity" th:field="*{quantity}"
                   th:class="${errors?.containsKey('quantity')} ? 'form-control 
field-error' : 'form-control'"
                   class="form-control" placeholder="수량을 입력하세요">
            <div class="field-error" th:if="${errors?.containsKey('quantity')}"
                 th:text="${errors['quantity']}">
                수량 오류
            </div>
        </div>
        <hr class="my-4">
        <div class="row">
            <div class="col">
                <button class="w-100 btn btn-primary btn-lg" type="submit"
                        th:text="#{button.save}">저장</button>
            </div>
            <div class="col">
                <button class="w-100 btn btn-secondary btn-lg"
                        onclick="location.href='items.html'"
                        th:onclick="|location.href='@{/validation/v1/items}'|"
                        type="button" th:text="#{button.cancel}">취소</button>
            </div>
        </div>
    </form>
</div> <!-- /container -->
</body>
</html>
```
- th:if를 사용하여 조건에 만족할 경우에만 HTML 태그를 보여주는 것을 확인할 수 있다

### 정리
- 만약 검증 오류가 발생하면 입력 폼을 다시 보여준다.
- 검증 오류들을 고객에게 친절하게 안내해서 다시 입력할 수 있게 한다.
- 검증 오류가 발생해도 고객이 입력한 데이터가 유지된다.

### 문제점
- 뷰 템플릿에서 중복 처리가 많다 (뭔가 비슷한 코드의 향연)
- 타입 오류 처리가 안된다.
  - Item의 price, quantity 같은 숫자 필드는 타입이 Integer 임으로 문자 타입으로 설정하는 것이 불가능하다.
  - 숫자 타입에 문자가 들어오면 오류가 발생한다.
  - 그런데 이러한 오류는 스프링 MVC에서 컨트롤러에 진입하기도 전에 예외가 발생하기 때문에 컨트롤러가 호출되지도 않고 400을 띄우게 된다.
- 결국 고객이 입력한 값도 어딘가에 별도로 관리가 되어야 한다.

## V2 Binding Result 
- 지금부터는 스프링이 제공하는 검증 오류 처리 방법을 알아보자
- 핵심은 BindingResult

#### ValidationControllerV2 - addItemV1
```java

    @PostMapping("/add")
    public String addItemV1(@ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {


        //검증 로직
        if (!StringUtils.hasText(item.getItemName())) {
//            errors.put("itemName", "상품 이름은 필수입니다.");
            bindingResult.addError(new FieldError("item", "itemName", "상품 이름은 필수 입니다"));
        }
        if (item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000) {
//            errors.put("price", "가격은 1,000 ~ 1,000,000 까지 허용합니다.");
            bindingResult.addError(new FieldError("item", "price", "가격은 1,000 ~ 1,000,000 까지 허용합니다."));
        }
        if (item.getQuantity() == null || item.getQuantity() >= 9999) {
//            errors.put("quantity", "수량은 최대 9,999 까지 허용합니다.");
            bindingResult.addError(new FieldError("item", "quantity", "수량은 최대 9,999 까지 허용합니다."));
        }
        //특정 필드가 아닌 복합 룰 검증
        if (item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();
            if (resultPrice < 10000) {
//                errors.put("globalError", "가격 * 수량의 합은 10,000원 이상이어야 합니다. 현재 값 = " + resultPrice);
                bindingResult.addError(new ObjectError("item", "가격 * 수량의 합은 10,000원 이상이어야 합니다. 현재 값 = " + resultPrice));
            }
        }
        //검증에 실패하면 다시 입력 폼으로
        if (bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            return "validation/v2/addForm";
        }

        //성공 로직
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }

```
- 이전 Map에서 하던 역할을 BindingResult가 하는 것이다.
- 필드에 오류가 있으면 FieldError 객체를 생성해서 bindingResult에 담아두면 된다.
- 이렇게 bindingResult를 채워서 뷰에 넘겨주면 타임리프는 또 최적화된 문법으로 이를 조회하고 조건 렌더링 할 수 있다.

```html
<form action="item.html" th:action th:object="${item}" method="post">
  <div th:if="${#fields.hasGlobalErrors()}">
    <p class="field-error" th:each="err : ${#fields.globalErrors()}"
       th:text="${err}">글로벌 오류 메시지</p>
  </div>
  <div>
    <label for="itemName" th:text="#{label.item.itemName}">상품명</label>
    <input type="text" id="itemName" th:field="*{itemName}"
           th:errorclass="field-error" class="form-control"
           placeholder="이름을 입력하세요">
    <div class="field-error" th:errors="*{itemName}">
      상품명 오류
    </div>
  </div>
  <div>
    <label for="price" th:text="#{label.item.price}">가격</label>
    <input type="text" id="price" th:field="*{price}"
           th:errorclass="field-error" class="form-control"
           placeholder="가격을 입력하세요">
    <div class="field-error" th:errors="*{price}">
      가격 오류
    </div>
  </div>
  <div>
    <label for="quantity" th:text="#{label.item.quantity}">수량</label>
    <input type="text" id="quantity" th:field="*{quantity}"
           th:errorclass="field-error" class="form-control"
           placeholder="수량을 입력하세요">
    <div class="field-error" th:errors="*{quantity}">
      수량 오류
    </div>
  </div>
```
- 타임리프는 스프링의 BindingResult를 활용해서 편리하게 검증 오류를 표현하는 기능을 제공한다.

## BindingResult
- 스프링이 제공하는 검증 오류를 보관하는 객체이다.
- 검증 오류가 발생하면 여기에 보관하면 된다.
- BindingResult가 있으면 @ModelAttribute에 데이터 바인딩 시 오류가 발생해도 컨트롤러가 호출된다! 
- 검증오류를 핸들링하겠다는 의사를 표명하는 것!

## FieldError, ObjectError
- 사용자 입력 오류 메시지가 화면에 남지 않는다.
- 화면에 남도록 수정해보자

```java    
    @PostMapping("/add")
    public String addItemV2(@ModelAttribute Item item, BindingResult bindingResult,
                            RedirectAttributes redirectAttributes) {
        if (!StringUtils.hasText(item.getItemName())) {
            bindingResult.addError(new FieldError("item", "itemName",
                    item.getItemName(), false, null, null, "상품 이름은 필수입니다."));
        }
        if (item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() >
                1000000) {
            bindingResult.addError(new FieldError("item", "price", item.getPrice(),
                    false, null, null, "가격은 1,000 ~ 1,000,000 까지 허용합니다."));
        }
        if (item.getQuantity() == null || item.getQuantity() > 10000) {
            bindingResult.addError(new FieldError("item", "quantity",
                    item.getQuantity(), false, null, null, "수량은 최대 9,999 까지 허용합니다."));
        }
        //특정 필드 예외가 아닌 전체 예외
        if (item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();
            if (resultPrice < 10000) {
                bindingResult.addError(new ObjectError("item", null, null, "가격 * 수량의 합은 10,000원 이상이어야 합니다. 현재 값 = " + resultPrice));
            }
        }
        if (bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            return "validation/v2/addForm";
        }
        //성공 로직
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }

```
- FieldError의 생성자를 다르게 바꾸어 rejectedValue의 값을 남기도록 설정했다.
- 사용자의 입력 데이터가 컨트롤러의 @ModelAttribute에 바인딩되는 시점에 오류가 발생하면 모델 객체에 사용자 입력 값을 유지하기 어렵다
- 예를 들어서 가격에 숫자가 아닌 문자가 입력된다면 가격은 Integer 타입임으로 문자를 보관할 수 있는 방법이 없는 것이다.
- 그래서 오류가 발생한 경우 사용자 입력 값을 보관하는 별도의 방법이 필요하다.
- 그리고 이렇게 보관한 사용자 입력 값을 검증 오류 발생시 화면에 다시 출력하는 것이 위의 코드이다.

## 오류 코드와 메시지 처리1
- 오류 메시지를 하드코딩 하지 않고 체계적으로 다루어 보자

#### errors.properties

```properties
required.item.itemName=상품 이름은 필수입니다.
range.item.price=가격은 {0} ~ {1} 까지 허용합니다.
max.item.quantity=수량은 최대 {0} 까지 허용합니다.
totalPriceMin=가격 * 수량의 합은 {0}원 이상이어야 합니다. 현재 값 = {1}
```

#### addItemV3()
```java    
    @PostMapping("/add")
    public String addItemV3(@ModelAttribute Item item, BindingResult bindingResult,
                            RedirectAttributes redirectAttributes) {
        if (!StringUtils.hasText(item.getItemName())) {
            bindingResult.addError(new FieldError("item", "itemName",
                    item.getItemName(), false, new String[]{"required.item.itemName"}, null,
                    null));
        }
        if (item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() >
                1000000) {
            bindingResult.addError(new FieldError("item", "price", item.getPrice(),
                    false, new String[]{"range.item.price"}, new Object[]{1000, 1000000}, null));
        }
        if (item.getQuantity() == null || item.getQuantity() > 10000) {
            bindingResult.addError(new FieldError("item", "quantity",
                    item.getQuantity(), false, new String[]{"max.item.quantity"}, new Object[]
                    {9999}, null));
        }
        //특정 필드 예외가 아닌 전체 예외
        if (item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();
            if (resultPrice < 10000) {
                bindingResult.addError(new ObjectError("item", new String[]
                        {"totalPriceMin"}, new Object[]{10000, resultPrice}, null));
            }
        }
        if (bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            return "validation/v2/addForm";
        }
        //성공 로직
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }

```
- codes 인자를 통해 메시지 코드를 지정하고 argument를 전달해 치환할 값이 있으면 치환하도록 했다
- 중앙에서 메시지를 관리하고 코드를 이용하여 오류 메시지의 일관성을 유지하도록 설계한 것이다.

## 오류 코드와 메시지 처리2
- FieldError, ObjectError는 파라미터도 많아서 직접 다루기 너무 번거롭다.
- 좀 더 자동화 할 수 있는 여지가 없을까?
- 컨트롤러에서 BindingResult는 검증해야 할 객체인 target 바로 다음에 온다.
- 따라서 BindingResult는 이미 본인이 검증해야 할 객체인 target을 알고 있는 것이다.
- 미리 알고 있다는 점을 이용해서 코드를 줄여보자!
- BindingResult가 제공하는 rejectValue(), reject()를 사용하면 FieldError, ObjectError를 직접 생성하지 않고 깔끔하게 검증 오류를 다룰 수 있다.

#### ValidationItemControllerV2 - addItemV4() 추가
```java
    @PostMapping("/add")
    public String addItemV4(@ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        log.info("objectName={}", bindingResult.getObjectName());
        log.info("target={}", bindingResult.getTarget());
        if (!StringUtils.hasText(item.getItemName())) {
            bindingResult.rejectValue("itemName", "required");
        }
        if (item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000) {
            bindingResult.rejectValue("price", "range", new Object[]{1000, 1000000}, null);
        }
        if (item.getQuantity() == null || item.getQuantity() > 10000) {
            bindingResult.rejectValue("quantity", "max", new Object[]{9999}, null);
        }
        //특정 필드 예외가 아닌 전체 예외
        if (item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();
            if (resultPrice < 10000) {
                bindingResult.reject("totalPriceMin", new Object[]{10000,
                        resultPrice}, null);
            }
        }
        if (bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            return "validation/v2/addForm";
        }
        //성공 로직
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }
```
- 실행해보니 오류 메시지가 정상 출력된다. 그런데 errors.properties에 있는 코드를 완벽히 직접 입력하지 않았는데 어떻게 출력한 것일까?
- rejectValue의 파라미터로 받는 error코드는 메시지에 등록된 코드가 아니다. (messageResolber를 위한 코드)
- 이는 뒤에서 자세히 알아보자

## 


</div>
</details>