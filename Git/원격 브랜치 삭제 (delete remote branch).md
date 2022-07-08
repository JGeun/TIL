<h3>배경</h3>

작업을 하면서 브랜치를 지우기 위해
```shell
git branch -d 브랜치명
```

을 입력했는데 브랜치는 사라지지만 origin/(브랜치) 는 사라지지 않았습니다. 이런 부분까지 지우고 싶어서 정리하게 되었습니다



<h3>해결 방법</h3>

```shell
git push origin --delete (브랜치명)

or

git branch -d 브랜치명
git push origin 브랜치명
```

저는 개인적으로 1번이 훨씬 더 편했습니다!

