선풍기나 형광등의 경우 전원을 키거나 끌 수 있다. 켜져있거나 꺼져 있는 "상태"에 대한 처리가 스테이트 패턴의 핵심이다.

상태란 객체가 시스템에 존재하는 동안, 즉 객체의 라이프 타임 동안 객체가 가질 수 있는 어떤 조건이나 상황을 표현한다.

ex) 어떤 액티비티 등을 수행하거나 특정 이벤트가 발생하기를 기다리는 것이다.

상태 진입은 객체의 한 상태에서 다른 상태로 이동하는 것을 말한다.

ex) 선풍기가 전원이 켜진 상태로 진입

형광등을 키고 끄는 과정을 하나의 객체로서 코드로 표현하자면 아래와 같습니다

```java
public class Light {
    private static int ON = 0;
    private static int OFF = 1;
    private int state;

    public Light() {
        state = OFF;
    }

    public void on() {
        if (state == ON) {
            System.out.println("반응 없음");
        } else {
            System.out.println("Light On");
            state = ON;
        }
    }

    public void off() {
        if (state == OFF) {
            System.out.println("반응 없음");
        } else {
            System.out.println("Light Off");
            state = OFF;
        }
    }
}
```

이 코드의 문제점은 취침등를 구현해달라는 요구사항이 추가된다면 코드를 확장시켜야한다.

```java
public class Light {
    private static int ON = 0;
    private static int OFF = 1;
    private static int SLEEPING = 2;
    private int state;

    public Light() {
        state = OFF;
    }

    public void on() {
        if (state == ON) { // On 상태에서 한번 더 누르면 취침등 상태로 전환
            System.out.println("반응 없음");
        } else if (state == SLEEPING) {
            System.out.println("Light On");
            state = ON;
        } else {
            System.out.println("Light On");
            state = ON;
        }
    }

    public void off() {
        if (state == OFF) {
            System.out.println("반응 없음");
        } else if (state == SLEEPING) {
            System.out.println("Light Off");
            state = OFF;
        } else {
            System.out.println("Light Off");
            state = OFF;
        }
    }
}
```

이런 경우 OCP를 만족하지 않게 되는데 이를 해결하기 위해서 스테이트 패턴을 적용하는 것이다.

스테이트 패턴은 스트래티지 패턴과 매우 흡사하다.

State 인터페이스 및 각 상태에 대한 클래스를 구현하여 살펴보자.

```java
interface State {
    public void on(Light light);

    public void off(Light light);
}

public class ON implements State {
    public void on(Light light) {
        System.out.println("반응 없음");
    }

    public void off(Light light) {
        System.out.println("Light Off");
        light.setState(new OFF(light));
    }
}

public class OFF implements State {
    public void on(Light light) {
        System.out.println("Light ON");
        light.setState(new ON(light));
    }

    public void off(Light light) {
        System.out.println("반응 없음");
    }
}

public class Light {
    private State state;

    public Light() {
        state = new OFF();
    }

    public void setState(State state) {
        this.state = state;
    }

    public void on() {
        state.on();
    }

    public void off() {
        state.off();
    }
}
```

이렇게 처리할 경우 Light 객체에서는 State를 변경해주기만 하면 쉽게 그 State를 상속받은 상태들에 대해서 작동할 수 있지만 상태를 바꿀 때마다 객체가 생성된다는 단점이 있다.

이 부분을 싱글톤을 적용해 해결해보자

```java
public class ON implements State {
    private static ON on = new ON();

    private ON() {
    }

    public static ON getInstance() {
        return on;
    }

    public void on(Light light) {
        System.out.println("반응 없음");
    }

    public void off(Light light) {
        light.setState(OFF.getInstance());
        System.out.println("Light Off");
    }
}

public class OFF
        implements State {
    private static OFF off = new OFF();

    private OFF() {
    }

    public static OFF getInstance() {
        return off;
    }

    public void on(Light light) {
        light.setState(ON.getInstance());
        System.out.println("Light On");
    }

    public void off(Light light) {
        System.out.println("반응 없음");
    }
}
```
