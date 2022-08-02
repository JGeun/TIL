옵저버 패턴은 데이터의 변경이 발생했을 경우 상대 클래스나 객체에 의존하지 않으면서 데이터 변경을 통보하고자할 때 유용합니다.

상적을 출력하는 기능 있다고 할 때 입력된 점수를 저장하는 ScoreRecord 클래스와 점수를 목록의 형태로 출력하는 DataSheetView 클래스가 필요하다. 성적이 입력된 경우, ScoreRecord 클래스의
addScore 메서드가 실행될 때 성적을 출력하려면 ScoreRecord 클래스는 DataSheetView를 참조해야 합니다.

```java
import java.util.ArrayList;
import java.util.List;

public class ScoreRecord {
    private List<Integer> scores = new ArrayList<>();
    private DataSheetView dataSheetView;

    public void setDataSheetView(DataSheetView dataSheetView) {
        this.dataSheetView = dataSheetView;
    }

    public void addScore(int score) {
        scores.add(score);
        dataSheetView.update();
    }

    public List<Integer> getScoreRecord() {
        return scores;
    }
}

class DataSheetView {
    private ScoreRecord scoreRecord;
    private int viewCount;

    public DataSheetView(ScoreRecord scoreRecord, int viewCount) {
        this.scoreRecord = scoreRecord;
        this.viewCount = viewCount;
    }

    public void update() {
        List<Integer> record = scoreRecord.getScoreRecord();
        displayScores(record, viewCount);
    }

    private void displayScores(List<Integer> record, int viewCount) {
        System.out.println("List of " + viewCount + " entries: ");
        for (int i = 0; i < viewCount && i < record.size(); i++) {
            System.out.println(record.get(i) + " ");
        }
        System.out.println();
    }
}

class Client {
    public static void main(String[] args) {
        ScoreRecord scoreRecord = new ScoreRecord();

        DataSheetView dataSheetView = new DataSheetView(scoreRecord, 3);

        scoreRecord.setDataSheetView(dataSheetView);

        for (int i = 1; i <= 5; i++) {
            int score = i * 10;
            System.out.println("Adding " + score);

            scoreRecord.addScore(score);
        }
    }
}
```

이 코드의 문제점은 성적을 다른 형태로 출력 방식을 바꾸거나 요구사항의 변동이 있을 경우 코드를 수정해야 합니다.

예를 들어 DataSheetView가 아니라 MinMaxView라는 최솟값과 최댓값을 보여주는 View 클래스를 이용한다면 다음과 같이 DataSheetView를 제거하고 MinMaxView를 넣어줘야하기에 OCP에
위배됩니다.

```java
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ScoreRecord {
    private List<Integer> scores = new ArrayList<>();
    private MinMaxView minMaxView;

    public void setMinMaxView(MinMaxView minMaxView) {
        this.minMaxView = minMaxView;
    }

    public void addScore(int score) {
        scores.add(score);
        minMaxView.update();
    }

    public List<Integer> getScoreRecord() {
        return scores;
    }
}

class MinMaxView {
    private ScoreRecord scoreRecord;

    public MinMaxView(ScoreRecord scoreRecord) {
        this.scoreRecord = scoreRecord;
    }

    public void update() {
        List<Integer> record = scoreRecord.getScoreRecord();
        displayMinMax(record);
    }

    private void displayMinMax(List<Integer> record) {
        int min = Collections.min(record, null);
        int max = Collections.max(record, null);
        System.out.println("Min: " + min + " Max: " + max);
    }
}

class Client {
    public static void main(String[] args) {
        ScoreRecord scoreRecord = new ScoreRecord();
        MinMaxView minMaxView = new MinMaxView(scoreRecord);

        scoreRecord.setMinMaxView(minMaxView);

        for (int i = 1; i <= 5; i++) {
            int score = i * 10;
            System.out.println("Adding " + score);

            scoreRecord.addScore(score);
        }
    }
}
```

이러한 문제들의 해결책은 성적 통보 대상이 변경되더라도 ScoreRecord 클래스를 그대로 재사용할 수 있어야 한다는 점입니다.

Observer 객체를 해당 View들이 implementation 받고, Subject 객체를 점수를 갱신하는 ScoreRecord가 받음으로서

ScoreRecord는 모든 View들에 대해 notify를 날리면 알아서 update 되는 형식의 코드를 짤 수 있습니다.

```java
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

interface Observer {
    public abstract void update();
}

abstract class Subject {
    private List<Observer> observers = new ArrayList<>();

    public void attach(Observer observer) {
        observers.add(observer);
    }

    public void detach(Observer observer) {
        observers.remove(observer);
    }

    public void notifyObservers() {
        for (Observer o : observers) {
            o.update();
        }
    }
}

public class ScoreRecord extends Subject {
    private List<Integer> scores = new ArrayList<>();

    public void addScore(int score) {
        scores.add(score);
        notifyObservers();
    }

    public List<Integer> getScoreRecord() {
        return scores;
    }
}

class DataSheetView implements Observer {
    private ScoreRecord scoreRecord;
    private int viewCount;

    public DataSheetView(ScoreRecord scoreRecord, int viewCount) {
        this.scoreRecord = scoreRecord;
        this.viewCount = viewCount;
    }

    public void update() {
        List<Integer> record = scoreRecord.getScoreRecord();
        displayScores(record, viewCount);
    }

    private void displayScores(List<Integer> record, int viewCount) {
        System.out.println("List of " + viewCount + " entries: ");
        for (int i = 0; i < viewCount && i < record.size(); i++) {
            System.out.println(record.get(i) + " ");
        }
        System.out.println();
    }
}

class MinMaxView implements Observer {
    private ScoreRecord scoreRecord;

    public MinMaxView(ScoreRecord scoreRecord) {
        this.scoreRecord = scoreRecord;
    }

    public void update() {
        List<Integer> record = scoreRecord.getScoreRecord();
        displayMinMax(record);
    }

    private void displayMinMax(List<Integer> record) {
        int min = Collections.min(record, null);
        int max = Collections.max(record, null);
        System.out.println("Min: " + min + " Max: " + max);
    }
}

class Client {
    public static void main(String[] args) {
        ScoreRecord scoreRecord = new ScoreRecord();
        DataSheetView dataSheetView3 = new DataSheetView(scoreRecord, 3);
        DataSheetView dataSheetView5 = new DataSheetView(scoreRecord, 5);
        MinMaxView minMaxView = new MinMaxView(scoreRecord);

        scoreRecord.attach(dataSheetView3);

        scoreRecord.attach(dataSheetView5);

        scoreRecord.attach(minMaxView);

        for (int i = 1; i <= 5; i++) {
            int score = i * 10;
            System.out.println("Adding " + score);
            scoreRecord.addScore(score);
        }
    }
}
```