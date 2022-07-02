SRP: 단일 책임 원칙 (Single Responsibility Principle)

OCP: 개방-폐쇄 원칙 (Open/Closed Principle)

LSP: 리스코프 치환 원칙 (Liskov Substitution Principle)

ISP: 인터페이스 분리 원칙 (Interface Segregation Principle)

DIP: 의존관계 역전 원칙 (Dependency Inversion Principle)

### SRP 단일 책임 원칙 (Single Responsibility Principle)

- 한 클래스는 하나의 책임만 가져야 한다
- SRP에서 말하는 책임의 기본 단위: 객체
- 책임이란 객체가 수행하는 활동이라고 해석할 수 있다

```java
public class Professor {
    public void teach() {
    }

    public void load() {
    }

    public void save() {
    }

    public void printReport() {
    }

    public void check() {
    }
}
```

위의 Professor 클래스는 여러개의 함수를 가지고 있습니다. 현재 Professor가 가장 잘할 수 있는 것은 가르치는 일입니다. 따라서 그 외의 다른 활동(책임)은 없애고 teach만 수행하도록 하는 것이
SRP를 만족하는 설계

### OCP: 개방-폐쇄 원칙 (Open/Closed Principle)

- 소프트웨어 요소는 확장에는 열려있으나 변경에는 닫혀 있어야 한다.

  => 기존의 코드를 변경하지 않으면서 기능을 추가할 수 있도록 설계되어야 한다는 뜻입니다.

```java
public class MyCar {
    private Car car = new HyundaiCar();

    //private Car car = new HyundaiCar();
    private Car car = new KiaCar();

}
```

처음에 현대자동차를 가지고 있다가 기아자동차를 가지게 되었을 때 코드의 변경이 발생하는 데 이럴 경우 구현 객체를 변경하려면 클라이언트 코드를 변경해야하고 다형성을 이용한 코드이긴 하지만 OCP설계원칙에 위반됩니다.
이럴 경우 Interface를 통해 표현해주는 것이 좋다

![OCP Structure](img/solid/OCP%20Structure.png)

<font size="4"><OCP 설계원칙을 만족하는 구조></font>

LSP: 리스코프 치환 원칙 (Liskov Substitution Principle)

- LSP는 일반화 관계에 있어서 자식 클래스는 최소한 자신의 부모 클래스에 가능한 행위는 수행할 수 있어야 한다는 뜻

- LSP를 만족하면 부모클래스 대신에 자식 클래스의 인스턴스로 대체해도 프로그램의 의미가 변화하지 않는다

  ex) 자동차 인터페이스의 엑셀은 앞으로 가라는 기능인데 추가로 개발된 엑셀이 뒤로가게 된다면 LSP가 위반됨

- 행위 일관성 pre -> pre', post' -> post

```java
import java.util.*;

class MinMax {
    public int[] mimax(int[] a) {
        int[] b = a.clone();
        Arrays.sort(a);
        int minValue = a[0];
        int maxValue =
                a[a.length - 1];
        b[0] = minValue;
        b[b.length - 1] = maxValue;
        return b;
    }
}

class MinMax1 extends MinMax {
    public int[] mimax(int[] a) {
        int[] b = a.clone();
        Arrays.sort(a);
        int minValue = a[0];
        int maxValue = a[0];
        b[0] = minValue;
        b[b.length - 1] = maxValue;
        return b;
    }
}
/*
 * 이런 식의 코드라면
 * •pre: ∀i∈int:a[i]∈int,post:a[0]=smallest(a)and a[size(a)-1]=largest(a)
 * •pre1: ∀i∈int:a[i]∈int,post1:a[0]=smallest(a)and a[size(a)-1]=smallest(a)가 되기 때문에 행위적으로 일관성이 없습니다.
 */

public class MinMax2 extends MinMax {
    public int[] mimax(int[] a) {
        int[] b = a.clone();
        Arrays.sort(b);
        return b;
    }
}

/* 위의 코드가 적용된다면
 *
 * •pre: ∀i∈int:a[i]∈int,post:a[0]=smallest(a)and a[size(a)-1]=largest(a)
 * •pre2: ∀i∈int:a[i]∈int,post2:for ∀i1,i2:0≤i1≤i2<size(a),a[i1]≤a[i2]가 적용되기에 pre->pre2이 만족되고 post2->post2가 만족됩니다.
 */
```

### ISP: 인터페이스 분리 원칙 (Interface Segregation Principle)

- Client should not be forced to depend upon interfaces that tey do not use

- 인터페이스를 클라이언트에 특화되도록 분리시키라는 설계 원칙

- 클라이언트의 관점에서 클라이언트 자신이 이용하지 않는 기능에는 영향을 받지 않아야 한다

- 분리하게 되면 인터페이스 자체가 변하더라도 클라이언트에 영향을 주지 않게 된다

- 인터페이스가 명확해지고, 대체 가능성이 높아짐

  ex) 비행기 인터페이스 -> 비행운전 인터페이스, 비행기정비 인터페이스

  ex) 사용자 클라이언트 -> 운전자 인터페이스, 정비사 인터페이스,

### DIP: 의존관계 역전 원칙 (Dependency Inversion Principle)

- DIP를 만족하려면 어떤 클래스가 도움을 받을 때 구체적인 클래스보다 인터페이스나 추상 클래스와 의존관계를 맺도록 설계

![DIP Structure](img/solid/DIP%20Structure.png)

<font size="4"><DIP를 만족시키는 설계></font>

```java
class Person {
  private Transportation transportation;

  public void setTransportation(Transportation transportation) {
    this.transportation = transportation;
  }

  public void board() {
    System.out.println(transportation.toString());
  }

  abstract class Transportation {
    abstract public String toString();
  }

  class Bus extends Transportation {
    public String toString() {
      return "Bus 탑승";
    }
  }

  class Subway extends Transportation {
    public String toString() {
      return "Subway 탑승";
    }
  }

  class Airplane extends Transportation {
    public String toString() {
      return "Airplane 탑승";
    }
  }

  class Main {
    public static void main(String[] args) {
      Person p = new Person();
      Transportation t = new Bus();
      p.setTransportation(t);
      p.board();
    }
  }
}
```




