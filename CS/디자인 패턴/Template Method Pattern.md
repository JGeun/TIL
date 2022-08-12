템플릿 메서드 패턴은 전체적으로 동일하면서 부분적으로는 다른 구문으로 구성된 메서드의 코드 중복을 최소화할 때 유용합니다. 다른 관점에서 보면 동일한 기능을 상위 클래스에서 정의하면서 확장/변화가 필요한 부분만
서브클래스에서 구현할 수 있도록 합니다.

엘리베이터 제어 시스템을 예시로 생각해볼 때 엘리베이터는 움직이는 동안 문이 닫혀있는지 체크해야합니다.

따라서 엘리베이터를 움직이는 Motor와 Door 클래스의 연관 관계를 정의해야 합니다.

```java
enum DoorStatus {CLOSED, OPENED}

enum MotorStatus {MOVING, STOPPED}

enum Direction {UP, DOWN, LEFT, RIGHT}

class Door {
    private DoorStatus doorStatus;

    public Door() {
        this.doorStatus = DoorStatus.CLOSED;
    }

    public DoorStatus getDoorStatus() {
        return doorStatus;
    }

    public void close() {
        doorStatus = DoorStatus.CLOSED;
    }

    public void open() {
        doorStatus = DoorStatus.OPENED;
    }
}

class AMotor {
    private Door door;
    private MotorStatus motorStatus;

    public Motor(Door door) {
        this.door = door;
        this.motorStatus = MotorStatus.STOPPED;
    }

    private void moveAMotor(Direction direction) {
        // AMotor를 구동시킴
    }

    public MotorStatus getMotorStatus() {
        return motorStatus;
    }

    private void setMotorStatus(MotorStatus motorStatus) {
        this.motorStatus = motorStatus;
    }

    public void move(Direction direction) {
        MotorStatus motorStatus = getMotorStatus();
        if (motorStatus == MotorStatus.MOVING)
            return;

        DoorStatus doorStatus = door.getDoorStatus();
        if (doorStatus == DoorStatus.OPENED)
            door.close();

        moveMotor(direction);
        setMotorStatus(MotorStatus.MOVING);
    }
}

public class Client {
    public static void main(String[] args) {
        Door door = new Door();
        AMotor aMotor = new AMotor(door);
        aMotor.move(Direction.UP);
    }
}
```

하지만 이 코드에서 Motor의 종류가 여러개라면 각 Moter는 위의 기능에 해당하는 중복된 기능들을 가지게 됩니다. 
따라서 일반적으로 코드의 중복은 유지보수성을 악화시키므로 바람직 하지 않습니다. 이 때 중복 기능들을 상속을 통해 해결할 수 있습니다.

```java
abstract class Motor {
    protected Door door;
    private MotorStatus motorStatus;

    public Motor(Door door) {
        this.door = door;
        motorStatus = MotorStatus.STOPPED;
    }

    public MotorStatus getMotorStatus() {
        return motorStatus;
    }

    protected void setMotorStatus(MotorStatus motorStatus) {
        this.motorStatus = motorStatus;
    }
}

class AMotor extends Motor {
    public AMotor(Door door) {
        super(door);
    }

    private void moveAMotor(Direction direction) {
        // AMotor를 구동시킴
    }

    public void move(Direction direction) {
        MotorStatus motorStatus = getMotorStatus();
        if (motorStatus == MotorStatus.MOVING) return;

        DoorStatus doorStatus = door.getDoorStatus();
        if (doorStatus == DoorStatus.OPENED)
            door.close();

        moveAMotor(direction);
        setMotorStatus(MotorStatus.MOVING);
    }
}

class BMotor extends Motor {
    public BMotor(Door door) {
        super(door);
    }

    private void moveBMotor(Direction direction) {
        // BMotor를 구동시킴
    }

    public void move(Direction direction) {
        MotorStatus motorStatus = getMotorStatus();
        if (motorStatus == MotorStatus.MOVING) return;

        DoorStatus doorStatus = door.getDoorStatus();
        if (doorStatus == DoorStatus.OPENED)
            door.close();

        moveBMotor(direction);
        setMotorStatus(MotorStatus.MOVING);
    }
}
```

하지만 여전히 move 함수 안에서 중복된 코드들을 확인할 수 있습니다. 이러한 경우에 move메서드를 상위 Motor 클래스로 이동시키고 다른 구문, 
즉 moveAMotor와 moveBMotor 메서드의 호출 부분을 하위 클래스에서 오버라이드 하는 방식으로 move 메서드의 중복을 최소화할 수 있습니다.

```java
abstract class Motor {
    protected Door door;
    private MotorStatus motorStatus;

    public Motor(Door door) {
        this.door = door;
        motorStatus = MotorStatus.STOPPED;
    }

    public MotorStatus getMotorStatus() {
        return motorStatus;
    }

    protected void setMotorStatus(MotorStatus motorStatus) {
        this.motorStatus = motorStatus;
    }

    public void move(Direction direction) {
        MotorStatus motorStatus = getMotorStatus();
        if (motorStatus == MotorStatus.MOVING) return;

        DoorStatus doorStatus = door.getDoorStatus();
        if (doorStatus == DoorStatus.OPENED)
            door.close();

        moveMotor(direction);
        setMotorStatus(MotorStatus.MOVING);
    }

    protected abstract void moveMotor(Direction direction);
}

class AMotor extends Motor {
    public AMotor(Door door) {
        super(door);
    }

    @Override
    protected void moveMotor(Direction direction) {
        // AMotor를 구동시킴
    }
}

class BMotor extends Motor {
    public BMotor(Door door) {
        super(door);
    }

    @Override
    protected void moveMotor(Direction direction) {
        // BMotor를 구동시킴
    }
}
```

이렇듯 중복되는 코드들은 상위 클래스에서 정의하면서 확장/변화가 필요한 부분만 서브클래스에서 구현합니다. 
이런 Motor 클래스의 move 메서드를 템플릿 메서드라고 부르고, move 메서드에서 호출되면서 하위 클래스에서 
오버라이드될 필요가 있는 moveMotor 메서드를 primitive 또는 hook 메서드라고 부릅니다.