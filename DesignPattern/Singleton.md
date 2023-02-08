### 싱글톤(Singleton)이란?

> 소프트웨어 디자인 패턴에서 싱글턴 패턴(Singleton pattern)을 따르는 클래스는, 생성자가 여러 차례 호출되더라도 실제로 생성되는 객체는 하나이고 최초 생성 이후에 호출된 생성자는 최초의 생성자가 생성한 객체를 리턴한다. 이와 같은 디자인 유형을 싱글턴 패턴이라고 한다. 주로 공통된 객체를 여러개 생성해서 사용하는 DBCP(DataBase Connection Pool)와 같은 상황에서 많이 사용된다. by 위키피디아 핵심은 객체의 인스턴스를 한 개만 생성되도록 만드는 패턴입니다

싱글톤 패턴을 사용하면 총 3가지 측점에서 이점을 얻을 수 있습니다.

#### 1. 메모리 측면

싱글톤 패턴을 사용하게 된다면 한 개의 인스턴스 만을 고정 메모리 영역에 생성하고 추후 해당 객체를 접근할 때 메모리 낭비를 방지할 수 있다.

#### 2. 속도 측면

이미 생성된 인스턴스를 활용하므로 빠르다.

#### 3. 데이터 공유 측면

전역으로 사용하는 인스턴스이기 때문에 다른 여러 클래스에서 데이터를 공유하며 사용할 수 있다. 하지만 동시성 문제가 발생할 수 있어 이 점을 유의해서 설계해야 한다.

### 싱글톤 구현 방법

싱글톤 패턴을 구현하는 방법은 굉장히 다양합니다.

그러나 각자의 패턴이 공통적으로 갖는 특징이 있는데, 이는 다음과 같습니다.

private 생성자만을 정의해 외부 클래스로부터 인스턴스 생성을 차단합니다. 싱글톤을 구현하고자 하는 클래스 내부에 멤버 변수로서 private static 객체 변수를 만듭니다. public static 메소드를
통해 외부에서 싱글톤 인스턴스에 접근할 수 있도록 접점을 제공합니다. 지금부터 싱글톤을 구현하는 6가지 방법에 대해 살펴보겠습니다.

#### 1. Eager Initialization

Eager Initialization은 가장 간단한 형태의 구현방법입니다. 이는 싱글톤 클래스의 인스턴르를 클래스 로딩 단계에서 생성하는 방법입니다. 그러나 어플리케이션에서 해당 인스턴스를 사용하지 않더라도
인스턴스를 생성하기 때문에 자칫 낭비가 발생할 수 있습니다.

```java
public class Singleton {

    private static final Singleton instance = new Singleton();

    // 다른 개발자들이 싱글톤 객체를 생성하지 않도록 생성자를 private으로 선언
    private Singleton() {
    }

    public static Singleton getInstance() {
        return instance;
    }
}
```

이 방법을 사용할 때는 싱글톤 클래스가 다소 적은 리소스를 다룰 때여야 합니다.

File System, Database Connection 등 큰 리소스들을 다루는 싱글톤을 구현할 때는 위와 같은 방식보다는 getInstance() 메소드가 호출될 때까지 싱글톤 인스턴스를 생성하지 않는 것이 더
좋습니다. 큰 리소스들은 많은 기능을 가지고 있는 만큼 메모리를 많이 필요로 하기 때문에 호출할 때까지 미루는 것이 좋습니다.

또한 이 방법은 Exception에 대한 Handling을 제공하지 않습니다.

#### 2. Static Block Initialization

Static Block Initialization은 1번에서 살펴본 Eager Initialization과 유사하지만 static block을 통해서 Exception Handling에 대한 옵션을 제공합니다.

```java
public class Singleton {

    private static Singleton instance;

    private Singleton() {
    }

    //exception을 다루기 위한 static block initialization
    static {
        try {
            instance = new Singleton();
        } catch (Exception e) {
            throw new RuntimeException("Exception occured in creating singleton instance");
        }
    }

    public static Singleton getInstance() {
        return instance;
    }
}
```

위와 같이 구현하면 예외처리가 가능하지만 여전히 클래스 로딩 단계에서 인스턴스를 생성하기 때문에 큰 리소스를 다루는 경우 적합하지 않습니다.

#### 3. Lazy Initialization

앞선 두 방식과는 달리 나중에 초기화하는 방법입니다.

```java
public class Singleton {

    private static Singleton instance;

    private Singleton() {
    }

    public static Singleton getInstance() {
        if (instance == null) {
            instance = new Singleton();
        }
        return instance;
    }
}
```

이 방식으로 구현할 경우 1, 2번에서 발생했던 문제 (사용하지 않을 경우 인스턴스가 낭비)에 대해 어느정도 해결책이 됩니다. 하지만 이 경우 Multi-Thread 환경에서 동기화 문제가 발생합니다. 만약
인스턴스가 생성되지 않은 시점에서 여러 쓰레드가 동시에 getInstance()를 호출한다면 예상치 못한 결과를 얻을 수 있을 뿐더러, 단 하나의 인스턴스를 생성한다는 싱글톤 패턴에 위반하는 문제점이 야기될 수
있습니다.

#### 4. Thread Safe Singleton

Thread Safe Singleton은 3번의 문제를 해결하기 위한 방법으로 getInstance() 메서드에 synchronized를 걸어두는 방식입니다. synchronized 키워드는 임계 영역 (
Critical Section)을 형성해 해당 영역에 오직 하나의 쓰레드만 접근 가능하게 해줍니다.

```java
public class Singleton {

    private static Singleton instance;

    private Singleton() {
    }

    public static synchronized Singleton getInstance() {
        if (instance == null) {
            instance = new Singleton();
        }
        return instance;
    }

}
```

하지만 synchronized 키워드 자체에 대한 비용이 크기 때문에 싱글톤 인스턴스 호출이 잦은 어플리케이션에서는 성능이 떨어지게 됩니다.

그래서 고안한 방식이 double checked locking입니다.

이는 getInstance() 메소드 수준에 lock을 걸지 않고 instance가 null일 경우에만 synchronized가 동작하도록 합니다.

```java
public static Singleton getInstance(){
    if(instance==null){
        synchronized (Singleton.class){
            if(instance==null){
        instance=new Singleton();
            }
        }
    }
    return instance;
}
```

#### 5. Bill Pugh Singleton Implementation

inner static helper class를 사용하는 방식입니다.

앞선 방식이 안고 있는 문제점들을 대부분 해결한 방식으로, 현재 가장 널리 쓰이는 싱글톤 구현 방식입니다.

```java
public class Singleton {

    private Singleton(){}
    
    private static class SingletonHelper{
        private static final Singleton INSTANCE = new Singleton();
    }
    
    public static Singleton getInstance(){
        return SingletonHelper.INSTANCE;
    }
}
```

inner class로 인해 복잡해 보일 수 있지만 생각보다 간단합니다. private inner static class를 두어 싱글톤 인스턴스를 갖게 됩니다. 이때 1번이나 2번 방식과의 차이점이라면
SingletonHelper 클래스는 Singleton 클래스가 Load 될 때에도 Load 되지 않다가 getInstance()가 호출됐을 때 비로소 JVM 메모리에 로드되고, 인스턴스를 생성하게 됩니다.
synchronized를 사용하지 않기 때문에 4번에서 문제가 되었던 성능 저하 또한 해결됩니다.

<참고>

https://readystory.tistory.com/116

https://velog.io/@seongwon97/%EC%8B%B1%EA%B8%80%ED%86%A4Singleton-%ED%8C%A8%ED%84%B4%EC%9D%B4%EB%9E%80

 