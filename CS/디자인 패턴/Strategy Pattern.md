```java
public abstract class Robot {
    private String name;

    public Robot(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public abstract void attack();

    public abstract void move();
}

public class Robot1 extends Robot {
    public Robot1(String name) {
        super(name;
    }

    public void attack() {
        System.out.println("I have punch");
    }

    public void move() {
        System.out.println("I can only walk");
    }
}

public class Robot2 extends Robot {
    public Robot2(String name) {
        super(name);
    }

    public void attack() {
        System.out.println("I have Missile");
    }

    public void move() {
        System.out.println("I can fly");
    }
}

public class Client {
    public static void main(String[] args) {
        Robot robot1 = new Robot1("Robot1");
        Robot robot2 = new Robot2("Robot2");
    }
}
```

Robot이라는 클래스를 상속받은 Robot1, Robot2가 있습니다. 기존 로봇들의 공격 또는 이동방법을 수정하려면 어떤 작업을 해야할까요? 
단순히 객체 안의 데이터를 수정하는 것은 기존의 코드를 수정해야 하므로 OCP에 위배됩니다. 
또한 반대로 Robot2에서 Robot1의 공격방식을 사용하려고 하면 Robot1의 attack메서드가 중복해서 사용되기 때문에 문제를 발생시킬 수 있습니다.

이런 경우 AttackStrategy와 MovingStrategy와 같이 
외부에서 기능 메서드를 활용하여 새로운 기능이 추가되어도 기존의 코드에 영향을 미치지 못하므로 OCP를 만족하는 설계가 됩니다. 
또한 이러한 방식은 이동방식과 공격 방식을 바꾸기 쉬운 집약 관계를 이용합니다.


이러한 방식을 스트래티지 패턴(전략을 쉽게 바꿀 수 있도록 해주는 디자인 패턴) 이라고 합니다.

ex) 게임 캐릭터가 자신이 처한 상황에 따라 공격이나 행동하는 방식을 바꾸고 싶을 때 스트래티지 패턴이 매우 유용

![Strategy Pattern](img/strategy/Strategy%20Pattern.png)