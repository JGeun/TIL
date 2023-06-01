commit()

### 주석 설명

> Commit your preferences changes back from this Editor to the SharedPreferences object it is editing.
> 이 편집기에서 편집 중인 SharedPreferences Object로 preferences 변경사항을 다시 commit합니다.
>
>This atomically performs the requested modifications, replacing whatever is currently in the SharedPreferences. 이렇게 하면 요청된 수정 작업이 원자적으로 수행되어 현재 SharedPreferences에 있는 모든 수정 작업을 대체할 수 있습니다.
>
>Note that when two editors are modifying preferences at the same time, the last one to call commit wins.
> 두 편집자가 동시에 preferences를 수정할 때 commit을 호출하는 마지막 편집자가 승리합니다.
>
>If you don't care about the return value and you're using this from your application's main thread, consider using {@link #apply} instead.
> 반환 값이 중요하지 않고 application의 메인 스레드로부터 사용한다면 apply를 대신 사용하는 것이 좋습니다.
>
>@return Returns true if the new values were successfully written to persistent storage. @return 새 값이 영구 스토리지에 성공적으로 기록된 경우 true를 반환합니다.

### 소스 코드

```java
@Override
public boolean commit(){
        long startTime=0;

        if(DEBUG){
        startTime=System.currentTimeMillis();
        }

        MemoryCommitResult mcr=commitToMemory();
        SharedPreferencesImpl.this.enqueueDiskWrite(
        mcr,null /* sync write on this thread okay */
        );
        try{
        mcr.writtenToDiskLatch.await();
        }catch(InterruptedException e){
        return false;
        }finally{
        if(DEBUG){
        Log.d(TAG,mFile.getName()+":"+mcr.memoryStateGeneration
        +" committed after "+(System.currentTimeMillis()-startTime)
        +" ms");
        }
        }
        notifyListeners(mcr);
        return mcr.writeToDiskResult;
        }
```

commit 코드를 보면 호출된 스레드에서 실행되는 것을 확인할 수 있습니다.

### apply()

#### 주석 설명

> Commit your preferences changes back from this Editor to the SharedPreferences object it is editing.
>
> 이 편집기에서 편집 중인 SharedPreferences Object로 preferences 변경사항을 다시 커밋합니다.
>
>This atomically performs the requested modifications, replacing whatever is currently in the SharedPreferences.
>
> 이렇게 하면 요청된 수정 작업이 원자적으로 수행되어 현재 SharedPreferences에 있는 모든 수정 작업을 대체할 수 있습니다.
>
>Note that when two editors are modifying preferences at the same time, the last one to call apply wins.
>
>두 명의 편집자가 동시에 preferences를 수정할 때 마지막으로 적용을 호출한 편집자가 승리합니다.
>
>Unlike commit, which writes its preferences out to persistent storage synchronously, apply commits its changes to the in-memory SharedPreferences immediately but starts an asynchronous commit to disk and you won't be notified of any failures.
>
> 영구 스토리지에 preferences를 동기적으로 기록하는 commit과는 달리, apply는 메모리 내 SharedPreferenecs에 변경 사항을 즉시 commit하지만 디스크에 대한 비동기 commit을 시작하면 장애에 대한 알림이 표시되지 않습니다.
>
> If another editor on this SharedPreferences does a regular commit while a apply is still outstanding, the commit will block until all async commits are completed as well as the commit itself.
> 적용이 아직 완료되지 않은 상태에서 SharedPreferences의 다른 편집기가 정기적으로 commit을 한다면, commit 뿐만 아니라 모든 commit들이 완료될 때까지 commit이 차단됩니다.
>
>As SharedPreferences instances are singletons within a process, it's safe to replace any instance of commit with apply if you were already ignoring the return value. SharedPreferences
> 인스턴스는 프로세스 내에서 싱글톤이기 때문에 이미 반환값을 무시하고 있었다면 commit 인스턴스를 apply로 바꾸는 것이 안전합니다.
>
>You don't need to worry about Android component lifecycles and their interaction with apply() writing to disk. Android 컴포넌트 생명주기들 dist에 기록하는 apply()와의 상호작용에 대해 걱정할 필요가 없습니다.
>
>The framework makes sure in-flight disk writes from apply() complete before switching states.
> 이 프레임워크는 상태를 전환하기 전에 apply()의 실행 중인 디스크 쓰기를 완료하도록 합니다.
>
> The SharedPreferences.Editor interface isn't expected to be implemented directly.
>
> SharedPreferences.Editor 인터페이스는 즉시 시행되지는 않을 것으로 예상됩니다.
>
> However, if you previously did implement it and are now getting errors about missing apply(), you can simply call commit from apply().
>
> 그러나 이전에 구현한 경우 apply() 누락에 대한 에러가 발생하면, apply()에서 commit을 호출하면 됩니다.

#### 소스코드

```java
@Override public void apply(){
    final long startTime=System.currentTimeMillis();
    final MemoryCommitResult mcr = commitToMemory();
    final Runnable awaitCommit = new Runnable(){
        @Override 
        public void run(){
            try{
                mcr.writtenToDiskLatch.await();
            } catch(InterruptedException ignored){
        }
    if(DEBUG&&mcr.wasWritten){
        Log.d(TAG, mFile.getName()+":"+mcr.memoryStateGeneration
            +" applied after "+(System.currentTimeMillis()-startTime)
            +" ms");}}};QueuedWork.addFinisher(awaitCommit);
    Runnable postWriteRunnable=new Runnable(){
        @Override 
        public void run(){
            awaitCommit.run();
            QueuedWork.removeFinisher(awaitCommit);
        }
    };
    SharedPreferencesImpl.this.enqueueDiskWrite(mcr,postWriteRunnable); 
    // Okay to notify the listeners before it's hitdisk 
    // because the listeners should always get the same 
    // SharedPreferences instance back, which has the 
    // changesreflected in memory.notifyListeners(mcr);}
```

apply의 경우엔 commit과는 달리 Runnable을 설정해서 실행하는 것을 볼 수 있습니다. 따라서 메인 스레드에서 실행하는 것이라면 commit 대신 apply를 쓰는 것을 추천하는 것입니다.