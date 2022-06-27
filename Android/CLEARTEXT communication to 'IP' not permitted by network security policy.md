안드로이드 OS 9.0 Pie부터 네트워크 보안 정책이 변경되어 "모든 네트워크 트래픽에서 http 대신 https를 사용하도록 강제하는 정책"입니다. 

http 연결은 명시적을 사용을 선언한 경우에만 사용할 수 있게 되었습니다.

#### <해결 방법>

방법1.

AndroidManifest에서 android:usesCleartextTraffic의 Flag값을 True로 변경해줍니다
```xml
<application
    ...
    android:usesCleartextTraffic="true"/>
```

<br/>

방법2.

1 - 1) network_security_config 파일 생성합니다
res/xml/network_security_config.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<network-security-config>
    <domain-config cleartextTrafficPermitted="true">
        <domain includeSubdomains="true">api_url</domain>
    </domain-config>
</network-security-config>
```

1 - 2) 위의 방법은 특정 주소를 허용하도록 설정하지만 그렇지 않은 상황인 경우도 있습니다. ex) 특정 사이트가 http인 경우

이런 경우 전체 http url을 허용해주는 방식을 사용합니다

```xml
<?xml version="1.0" encoding="utf-8"?>
<network-security-config>
    <base-config cleartextTrafficPermitted="true"/>
</network-security-config>
```

2)Android Manifest에 등록합니다
```xml
<application
....
android:networkSecurityConfig="@xml/network_security_config">
```