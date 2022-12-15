# 클린아키텍처를 위해서
최근 안드로이드 앱을 개발하기 위해 수많은 웹사이트랑 블로그를 돌아다니면서 클린아키텍처에 대해서 공부하려고 했으나 정말 찾기가 힘들었고 또 찾았다고 하더라도 아직 뭔가 초보인 나로써는 적용하기가 너무나 어려웠다.
그러는 중에 찾은 유튜브 영상하나가 많은 도움이 되어서 나만의 방식으로 클린아키텍처를 적용한 보일러플레이트 프로젝트 생성 과정을 공유하려한다.

## 어디서 부터 시작해야
보통 앱을 개발한다고 하면 ==Screen 개발== -> ==Viewmodel== -> ==Domain, Repository 개발==  순으로 해야한다고 생각했다. 왜냐하면 우선 UI 가 어느정도 완성이 되어야 그 아래 Viewmodel, Domain, Repository 같은 것들이 자연스럽게 개발이 될 거라고 생각했기 때문이다.

##### 하지만...
내가 찾은 유튜브 영상에서는 완전히 반대로 개발을 하고 있었다.
그래서 우선 나의 가설이 맞는지 검증을 하기 위해서 이 글에서는 ==Screen 개발== -> ==Viewmodel== -> ==Domain, Repository 개발== 순서대로 개발을 해 보고 어떤 문제가 있는지 검증해 보려고 한다.

## 클린아키텍처

![[Pasted image 20221215014243.png]]

클린아키텍처에 대해서 한 번이라도 관심을 가져본 적이 있다면 위에 그림을 많이 접했을 거라고 생각한다. 자세한 내용은 아래 정리된 글을 참고해보자.

```ad-info

https://techblog.woowahan.com/2647/

Robert C Martin 블로그의 글에서는 대부분의 아키텍처는 세부적인 차이는 있어도 공통적인 목표는 계층을 분리하여 관심사의 분리하는 것이라고 말하는데, 이런 아키텍처가 동작하기 위해서는 의존성 규칙을 지켜야 한다고 합니다.

의존성 규칙은 모든 소스코드 의존성은 반드시 외부에서 내부로, 고수준 정책을 향해야 한다고 말합니다. 즉 업무의 업무 로직을 담당하는 코드들이 DB 또는 Web 같이 구체적인 세부 사항에 의존하지 않아야 합니다. 이를 통해 **업무 로직(고수준 정책)은 세부 사항들(저수준 정책)의 변경에 영향을 받지 않도록 할 수 있습니다.**
```

그리고 이 개념은 정확히 [안드로이드 개발자 가이드](https://developer.android.com/topic/architecture?hl=ko)에도 나와있다.


## 만들어야 할 앱은

![[Pasted image 20221215020230.png|300]]
대충 이렇게 생겼다. 사진이 하나 있고 아래는 좋은 글귀가 있고 위로 스크롤하면 다른 글 보이고...
근데 여기다가 플로팅버튼을 하나 추가해서 사진링크랑 글귀도 입력하고 뭐 삭제도 하고 수정도 하는 그런 앱을 한 번 만들어볼까 한다.
조금더 덧붙이자면 이 앱은 [안드로이드 코드랩](https://developer.android.com/codelabs/basic-android-compose-training-add-scrollable-list#0)에서 소개한 앱이다.

## 그럼 화면부터 그려보자
간단하게 `LazyColumn` 을 이용해서 리스트를 만들어본다. #LazyColumn 에 대한 자세한 내용은 [여기](https://developer.android.com/reference/kotlin/androidx/compose/foundation/lazy/package-summary?hl=ko#LazyColumn(androidx.compose.ui.Modifier,androidx.compose.foundation.lazy.LazyListState,androidx.compose.foundation.layout.PaddingValues,kotlin.Boolean,androidx.compose.foundation.layout.Arrangement.Vertical,androidx.compose.ui.Alignment.Horizontal,androidx.compose.foundation.gestures.FlingBehavior,kotlin.Boolean,kotlin.Function1))를 참고하자.

- MainActivity.kt
```kotlin
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CleanarchitectureboilerplateTheme {
                AffirmationListScreen()
            }
        }
    }
}

@Composable
fun AffirmationListScreen(
    modifier: Modifier = Modifier,

) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(
            affirmationList,
            key = { affirmationListItem -> affirmationListItem.id }
        ) {
            AffirmationCard(it)
        }
    }
}
```

MainActivity 에 위와 같이 작성을 해 보았다. 우선 `affirmationList` 라는 것이 들어온다고 가정하고, 들어온 데이터를 `AffirmationCard` 에 보내주면 된다. 여기서 [데이터는 UI Layer 에서 바로 가져오는 것이 클린아키텍처에서 권장하지 않으므로](https://developer.android.com/topic/architecture?hl=ko#ui-layer) `ViewModel` 을 추가해 줘야 한다.

`ViewModel` 은 일단 무시하고 UI 를 우선 완성시키기 위해서 `AffirmationCard` 를 작성해 보자.

```kotlin
@Composable
fun AffirmationCard(affirmation: Affirmation, modifier: Modifier = Modifier) {
    Card(modifier = Modifier.padding(8.dp), elevation = 4.dp) {
        Column {
            Image(
                painter = painterResource(id = affirmation.imageResourceId),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(194.dp),
                contentScale = ContentScale.Crop
            )

            Text(
                text = affirmation.statement,
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.h6
            )
        }
    }
}
```

우선 UI를 먼저 보기 위해서 가짜 `affirmationList` 를 만들어서 전달해 보겠다. 가짜라서 이미지같은건 우선 가짜 이미지 하나 쓰면 될 것 같고 텍스트도 임의로 작성해서 3~4개 정도의 리스트로 만들어서 화면을 보는걸로 한다.

```kotlin
data class Affirmation(
    val statement: String,
    val id:String,
    @DrawableRes val imageResourceId: Int
)

fun affirmationList(): List<Affirmation> {
    return listOf(
        Affirmation("be good","1", R.drawable.image1),
        Affirmation("be good","2", R.drawable.image1),
        Affirmation("be good","3", R.drawable.image1),
        Affirmation("be good","4", R.drawable.image1),
        Affirmation("be good","5", R.drawable.image1),
    )
}
```

이렇게 가짜 리스트를 만들어주고,

```kotlin
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CleanarchitectureboilerplateTheme {
                val affirmations = affirmationList()
                AffirmationListScreen(affirmationList = affirmations)
            }
        }
    }
}
```

MainActivity 에서도 마치 리스트를 어디서 가지고 온 것 처럼 넘겨준다.

여기까지 하고 나서 앱을 구동해보면,

![[Pasted image 20221215031043.png|300]]
잘 나온다. 스크롤도 되고.

## 가짜데이터에서 진짜데이터로

UI 입장에서 진짜데이터라고 느낄려면 `ViewModel` 에서 데이터를 넘겨줘야 한다. 그래서 우선 `ViewModel` 을 만들었다고 가정하고 `AffirmationListScreen` 에 `ViewModel` 을 넘겨주자.

```kotlin
@Composable
fun AffirmationListScreen(
    modifier: Modifier = Modifier,
    affirmationViewModel: AffirmationViewModel
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(
            affirmationViewModel.affirmationList,
            key = { affirmationListItem -> affirmationListItem.id }
        ) {
            AffirmationCard(it)
        }
    }
}
```

이렇게 넘겨받아야 하는데 그럼 저 Viewmodel 을 또 MainActivity 에서 넘겨줘야 하는 문제가 생긴다. 그래서 `Hilt` 를 사용해서 의존성주입을 해주면 간편하게 구현을 할 수 있다. 그러기 위해서 우선 `Hilt` 를 셋업해주자.

### Hilt setup
[Android Developer](https://developer.android.com/training/dependency-injection/hilt-android) 에서 제공하는 가이드를 따라 설정해보자. Hilt 를 사용하는 이유는
![[Pasted image 20221215111520.png]]
안드로이드에서 권장하고 있다. 물론 강하게 권장되는 것은 매뉴얼하게 DI 를 구성하는 것이다. 이건 안드로이드에서 [코드랩](https://developer.android.com/codelabs/basic-android-kotlin-compose-add-repository?hl=ko#0)으로 제공하고 있으니 참고하면 된다. 다만 앱이 충분히 복잡한 경우 Hilt 를 사용하는 것이 권장되니 복잡해질거라고 가정하고 Hilt 로 셋업해본다.

![[Pasted image 20221215112410.png|500]]

```ad-warning
`Project Files` 모드로 바꿔서 보면 프로젝트 수준의 build.gradle 파일에 아래 내용으로 수정해야 한다. (`Android` 모드에서는 두 개가 보이는데 햇갈릴 수 있음)
```

- build.gradle
```groovy
buildscript {
    ext {
        compose_ui_version = '1.1.1'
    }
}// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id 'com.android.application' version '7.3.1' apply false
    id 'com.android.library' version '7.3.1' apply false
    id 'org.jetbrains.kotlin.android' version '1.6.10' apply false
    id 'com.google.dagger.hilt.android' version '2.44' apply false // 이 부분 추가
}
```

- app/build.gradle
```groovy
plugins {  
    id 'com.android.application'  
    id 'org.jetbrains.kotlin.android'  
    id 'kotlin-kapt'  // 이 부분 추가
    id 'com.google.dagger.hilt.android'  // 이 부분 추가
}  
  
...
  
dependencies {  
  
    ...
  
    implementation "com.google.dagger:hilt-android:2.44" // 이 부분 추가 
    kapt "com.google.dagger:hilt-compiler:2.44"  // 이 부분 추가
}

// Allow references to generated code  
kapt {  
    correctErrorTypes true  // 이 부분 추가
}
```

추가 하고 보니 새로운 버전이 나왔다면서 버전 업그레이드를 권장한다. 그래서 버전을 통일 시켜준다. (작성시점은 2022.11.15)

- build.gradle
```groovy
buildscript {  
    ext {  
        compose_ui_version = '1.1.1'  
        hilt_version = '2.44.2'  // 이 부분 추가
    }  
}// Top-level build file where you can add configuration options common to all sub-projects/modules.  
plugins {  
    ...
    id 'com.google.dagger.hilt.android' version '2.44.2' apply false  // 이렇게 수정해 줌
}
```

- app/build.gradle
```groovy
...
  
dependencies {  
  
    ...

	// 아래 두 줄 처럼 수정
    implementation "com.google.dagger:hilt-android:$hilt_version"  
	kapt "com.google.dagger:hilt-compiler:$hilt_version" 
}

...
```

수정이 끝난 후에는 Sync Now.

Hilt 를 사용하기 위해서는 Base Application 클래스에 `@HiltAndroidApp` 어노테이션을 붙여줘야 한다고 함. 그래서 아래 파일을 추가해야 한다.
- app/App.kt
```kotlin
@HiltAndroidApp  
class App: Application() {  
}
```

그리고 [Android Developer 가이드](https://developer.android.com/training/dependency-injection/hilt-android#application-class)에는 없는데 `AndroidManifest.xml` 에 app 을 추가해 줘야 한다.

- app/manifests/AndroidManifest.xml
```xml
<application  
    android:name=".App"  <-- 여기 추가해 줌
    android:allowBackup="true"  
    ...
```

그리고 아래와 같이 수정
- MainActivity.kt
```kotlin
@AndroidEntryPoint  // 여기 추가해 줌
class MainActivity : ComponentActivity() {
```

### ViewModel 추가해주기

이제 Viewmodel 을 추가할 준비가 다 됐다. 동영상 강의에서는 패키지를 core 와 feature 들로 분리를 했는데 꽤나 복잡해보인다.

![[Pasted image 20221215120007.png|300]]
이런식의 구조이고 feature 가 추가되면 `xxx_feature` 이런식으로 패키지를 추가하는 형식이다. 그리고 기본 패키지아래는 data, di, domain, presentation 의 형태로 하위패키지를 구성한다.

core 같은 경우는 클린아키텍처 과녁그림에서 Entity 영역에 해당하는 부분이다. Database, Retrofi 같은 친구들이 들어가면 된다. 아직 그렇게 까지 정의가 된 것은 없으니 feature 부터 구성해보자.

개인적으로 `_feature` 라고 명시해주는게 별로인 것 같아서 그냥 feature 의 명칭만으로 패키지를 생성한다.

![[Pasted image 20221215121639.png|300]]

이렇게 하고 나서 MainActivity.kt 에 있는 Composable 부분을 AffirmationScreen.kt 로 옮겨준다.

- AffirmationScreen.kt
```kotlin
package io.play.clean_architecture_boilerplate.affirmation.presentation

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import io.play.clean_architecture_boilerplate.Affirmation

@Composable
fun AffirmationScreen(
    modifier: Modifier = Modifier,
    affirmationViewModel: AffirmationViewModel
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(
            affirmationViewModel.affirmationList,
            key = { affirmationListItem -> affirmationListItem.id }
        ) {
            AffirmationCard(it)
        }
    }
}

@Composable
fun AffirmationCard(affirmation: Affirmation, modifier: Modifier = Modifier) {
    Card(modifier = Modifier.padding(8.dp), elevation = 4.dp) {
        Column {
            Image(
                painter = painterResource(id = affirmation.imageResourceId),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(194.dp),
                contentScale = ContentScale.Crop
            )

            Text(
                text = affirmation.statement,
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.h6
            )
        }
    }
}
```

- MainActivity.kt
```kotlin
package io.play.clean_architecture_boilerplate  
  
import android.os.Bundle  
import androidx.activity.ComponentActivity  
import androidx.activity.compose.setContent  
import androidx.annotation.DrawableRes  
import dagger.hilt.android.AndroidEntryPoint  
import io.play.clean_architecture_boilerplate.affirmation.presentation.AffirmationScreen  
import io.play.clean_architecture_boilerplate.ui.theme.CleanarchitectureboilerplateTheme  
  
@AndroidEntryPoint  
class MainActivity : ComponentActivity() {  
    override fun onCreate(savedInstanceState: Bundle?) {  
        super.onCreate(savedInstanceState)  
        setContent {  
            CleanarchitectureboilerplateTheme {  
                val affirmations = affirmationList()  
                AffirmationScreen()  
            }  
        }    }  
}  
  
data class Affirmation(  
    val statement: String,  
    val id:String,  
    @DrawableRes val imageResourceId: Int  
)  
  
fun affirmationList(): List<Affirmation> {  
    return listOf(  
        Affirmation("be good","1", R.drawable.image1),  
        Affirmation("be good","2", R.drawable.image1),  
        Affirmation("be good","3", R.drawable.image1),  
        Affirmation("be good","4", R.drawable.image1),  
        Affirmation("be good","5", R.drawable.image1),  
    )  
}
```

오류가 많이 뜨지만 우선 이렇게 해두고 AffirmationViewModel 을 만들어보자.

![[Pasted image 20221215122446.png|500]]

- AffirmationViewModel.kt
```kotlin
package io.play.clean_architecture_boilerplate.affirmation.presentation  
  
import androidx.lifecycle.ViewModel  
import dagger.hilt.android.lifecycle.HiltViewModel  
import javax.inject.Inject  
  
@HiltViewModel  
class AffirmationViewModel @Inject constructor(): ViewModel() {  
}
```

우선 깡통 ViewModel 을 만들었다. 그리고 viewmodel 을 추가해 주기 위해서 `app/build.gradle` 파일에 아래 라인을 추가해 주어야 한다. 자세한 내용은 [가이드](https://developer.android.com/kotlin/ktx?hl=ko#viewmodel)를 참고하자.

```groovy
dependencies {  
  
    ... 
    implementation "androidx.lifecycle:lifecycle-viewmodel-compose:2.5.1"  
    ...
}
```

그리고 AffirmationScreen 에 깡통 ViewModel 을 전달해준다. 자세한 내용은 [가이드](https://developer.android.com/jetpack/compose/libraries?hl=ko#hilt)를 참고하기 바란다.


- AffirmationScreen.kt
```kotlin
@Composable
fun AffirmationScreen(
    modifier: Modifier = Modifier,
    affirmationViewModel: AffirmationViewModel = viewModel() // 이렇게 수정
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(
            affirmationViewModel.affirmationList,
            key = { affirmationListItem -> affirmationListItem.id }
        ) {
            AffirmationCard(it)
        }
    }
}
```

이제 items 함수 내부에 있는 `affirmationViewModel.affirmationList` 이 부분을 구현해줘야 하겠다.

- AffirmationViewModel.kt
```kotlin
@HiltViewModel  
class AffirmationViewModel @Inject constructor(): ViewModel() {  
    val affirmationList = affirmationList()  
}  

// 아래 클래스와 함수를 MainActivity.kt 에서 가져온다
data class Affirmation(  
    val statement: String,  
    val id:String,  
    @DrawableRes val imageResourceId: Int  
)  
  
fun affirmationList(): List<Affirmation> {  
    return listOf(  
        Affirmation("be good","1", R.drawable.image1),  
        Affirmation("be good","2", R.drawable.image1),  
        Affirmation("be good","3", R.drawable.image1),  
        Affirmation("be good","4", R.drawable.image1),  
        Affirmation("be good","5", R.drawable.image1),  
    )  
}
```

- MainActivity.kt
```kotlin
@AndroidEntryPoint  
class MainActivity : ComponentActivity() {  
    override fun onCreate(savedInstanceState: Bundle?) {  
        super.onCreate(savedInstanceState)  
        setContent {  
            CleanarchitectureboilerplateTheme {  
                AffirmationScreen()  
            }  
        }    
	}  
}
```

이제 모든 오류가 사라졌다. 다시 앱을 실행시켜보자.

![[Pasted image 20221215134147.png|300]]

다시 실행이 잘 된다.

그럼 처음에 말했던 ==Screen 개발== -> ==Viewmodel== -> ==Domain, Repository 개발== 이 순서에서 ==Screen 개발== 은 끝났다고 볼 수 있다. 이제 본격적으로 ==Viewmodel== -> ==Domain, Repository 개발== 이 구간을 더 살펴보자.


### 데이터베이스에서 데이터 가져와보기
지금까지는 데이터를 임의로 생성해서 UI 에 표현했으나 실제 프로덕트는 이렇게 하지 않는다. 보통 특정 api 를 호출해서 데이터를 가져와서 화면에 보여줄 것이다.

App 에서 데이터 요청 -> 서버에서 데이터를 App 으로 전송 -> App 은 데이터를 받고 -> 받은 데이터를 캐쉬하고 -> 화면에 보여줌

이런 과정으로 데이터를 보여줄 것이다. 물론 중간에 캐쉬하는 부분은 필요가 없을 수 있다. 하지만 App 에서 데이터를 캐쉬하는 부분은 [장점](https://developer.android.com/training/data-storage/room?hl=ko)이 많다.

![[Pasted image 20221215141827.png]]

### Room database setup

- build.gradle
```groovy
buildscript {  
    ext {  
        ...
        room_version = '2.4.3'  // 이렇게 추가
    }  
}
```

- app/build.gradle
```groovy
dependencies {  
  
    ...
    // 아래 라인들 추가
    //Room  
    implementation "androidx.room:room-runtime:$room_version"  
    implementation "androidx.room:room-ktx:$room_version"  
    annotationProcessor "androidx.room:room-compiler:$room_version"  
    kapt "androidx.room:room-compiler:$room_version"
    testImplementation "androidx.room:room-testing:$room_version"
}
```

[가이드](https://developer.android.com/training/data-storage/room?hl=ko)에서는 RxJava 같은걸 추가하라고 되어 있으나 아직 그걸 사용하진 않을 것이므로 위의 내용만 추가한다. ([Room 의 기본 구성요소](https://developer.android.com/training/data-storage/room?hl=ko#components))

Room database 는 App 전역에서 사용할 수 있으므로 core 영역이라고 볼 수 있다. 그래서 우선 패키지들을 만들어보자.

![[Pasted image 20221215145717.png|300]]

AffrimationEntity.kt 파일까지 추가하고 데이터의 구조를 정의해야한다.

| **ID** | **STATEMENT**        | **IMAGE_URL**     |
| ------ | -------------------- | ----------------- |
| 1      | I am strong.         | https://url.com/1 |
| 2      | I believe in myself. | https://url.com/2 |
| 3         | Each day is a new opportunity to grow and be a better version of myself. | https://url.com/3   |

데이터 타입은 아래와 같이 정의하면 된다.
- ID : String
- STATEMENT : String
- IMAGE_URL : String

정의가 되었으니 아래 내용을 작성한다. ([가이드](https://developer.android.com/training/data-storage/room?hl=ko#data-entity))

- AffirmationEntity.kt
```kotlin
package io.play.clean_architecture_boilerplate.core.data.local.entities  
  
import androidx.room.Entity  
import androidx.room.PrimaryKey  
  
@Entity  
data class AffirmationEntity(  
    @PrimaryKey val id: String,  
    val statement: String,  
    val imageUrl: String  
)
```

이제 이 데이터를 조회할 Dao 를 정의한다. CRUD 가 가능하도록 작성하면 된다.
먼저 인터페이스를 하나 추가해주고,

![[Pasted image 20221215150417.png|300]]

아래와 같이 작성해준다.([가이드](https://developer.android.com/training/data-storage/room?hl=ko#dao))

- AffirmationDao.kt
```kotlin
package io.play.clean_architecture_boilerplate.core.data.local  
  
import androidx.room.Dao  
import androidx.room.Delete  
import androidx.room.Insert  
import androidx.room.OnConflictStrategy  
import androidx.room.Query  
import androidx.room.Update  
import io.play.clean_architecture_boilerplate.core.data.local.entities.AffirmationEntity  
  
@Dao  
interface AffirmationDao {  
    @Query("select * from AffirmationEntity")  
    suspend fun getAll(): List<AffirmationEntity>  
  
    @Insert(onConflict = OnConflictStrategy.REPLACE)  
    suspend fun insert(affirmationEntity: AffirmationEntity)  
  
    @Delete  
    suspend fun delete(affirmationEntity: AffirmationEntity)  
  
    @Update  
    suspend fun update(affirmationEntity: AffirmationEntity)  
}
```

마지막으로 데이터베이스를 작성해준다.([가이드](https://developer.android.com/training/data-storage/room?hl=ko#database))

- AppDatabase.kt
```kotlin
package io.play.clean_architecture_boilerplate.core.data.local  
  
import androidx.room.Database  
import androidx.room.RoomDatabase  
import io.play.clean_architecture_boilerplate.core.data.local.entities.AffirmationEntity  
  
@Database(entities = [AffirmationEntity::class], version = 1)  
abstract class AppDatabase: RoomDatabase() {  
    abstract fun affirmationDao(): AffirmationDao  
}
```

여기까지 됐다면 이제 데이터베이스를 사용할 수 있도록 모듈로 만들어줘야 한다. 이 [가이드](https://developer.android.com/training/dependency-injection/hilt-android?hl=ko#hilt-modules)에는
Hilt 모듈에 대한 설명이 있지만 Room database 에 관련된 내용이 없어서 동영상을 참고해 작성해 보겠다.

우선 아래와 같이 패키지랑 파일을 생성해 주고,
![[Pasted image 20221215152153.png|300]]

AppModule.kt 파일에 아래와 같이 작성해 준다.
```kotlin
package io.play.clean_architecture_boilerplate.core.di  
  
import android.content.Context  
import androidx.room.Room  
import dagger.Module  
import dagger.Provides  
import dagger.hilt.InstallIn  
import dagger.hilt.android.qualifiers.ApplicationContext  
import dagger.hilt.components.SingletonComponent  
import io.play.clean_architecture_boilerplate.core.data.local.AffirmationDao  
import io.play.clean_architecture_boilerplate.core.data.local.AppDatabase  
import javax.inject.Singleton  
  
@Module  
@InstallIn(SingletonComponent::class)  
object AppModule {  
      
    @Provides  
    @Singleton    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {  
        return Room.databaseBuilder(  
            context,  
            AppDatabase::class.java,  
            "app_db"  
        ).build()  
    }  
      
    @Provides  
    @Singleton    fun provideAffirmationDao(appDatabase: AppDatabase): AffirmationDao {  
        return appDatabase.affirmationDao()  
    }  
}
```

여기까지 됐다면 셋업은 마무리가 됐다. 마지막으로 앱을 한 번 실행시켜보고 잘 동작하는지 확인한다.

### ViewModel 과 Dao 연결해 주기
우선 dao 가 잘 작동하는지 확인하기 위해서 임시 데이터를 넣어준다. AffirmationViewModel 이 초기화 될 때 하나의 아이템을 넣어보고 잘 동작하는지 살펴본다.

- AffirmationViewModel.kt
```kotlin
@HiltViewModel  
class AffirmationViewModel @Inject constructor(  
    private val affirmationDao: AffirmationDao  
) : ViewModel() {  
    var affirmationList by mutableStateOf<List<AffirmationEntity>>(emptyList())  
        private set  
  
    init {  
        viewModelScope.launch {  
            affirmationList = affirmationDao.getAll()  
            affirmationDao.insert(  
                AffirmationEntity(  
                    "1", "I am strong.", "https://darebee.com/images/fitness/muscles-stronger.jpg"  
                )  
            )  
        }  
    }  
}
```

이렇게 수정해 주면 AffirmationScreen.kt 에서 오류가 발생한다. 왜냐하면 AffirmationCard 는 AffirmationEntity 객체가 아닌 Affirmation(아까 만든 가짜 객체) 을 파라미터로 받기 때문이다. 그래서 아래와 같이 수정해 줘야 한다.

```ad-note
이미지 같은 경우는 처음엔 PC에 저장되어 있던 이미지를 사용했으나 지금은 외부에서 가지고 오는 이미지를 사용하기 때문에 `Coil` 이라는 [라이브러리](https://coil-kt.github.io/coil/)를 사용해줘야 한다.
```

- build.gradle
```groovy
buildscript {  
    ext {  
        ...
        coil_version = '2.2.2'  // 여기에 추가
    }  
}
```

- app/build.gradle
```groovy
dependencies {  
  
    ...
    // 아래 내용 추가
    //Coil  
    implementation "io.coil-kt:coil:$coil_version"  
    implementation "io.coil-kt:coil-compose:$coil_version"  
}
```

- AffirmationScreen.kt
```kotlin
@Composable
fun AffirmationScreen(
    modifier: Modifier = Modifier,
    affirmationViewModel: AffirmationViewModel = viewModel()
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(
            affirmationViewModel.affirmationList,
            key = { affirmationListItem -> affirmationListItem.id }
        ) {
            AffirmationCard(it)
        }
    }
}

@Composable
fun AffirmationCard(affirmationEntity: AffirmationEntity, modifier: Modifier = Modifier) {
    Card(modifier = Modifier.padding(8.dp), elevation = 4.dp) {
        Column {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(affirmationEntity.imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(194.dp)
            )

            Text(
                text = affirmationEntity.statement,
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.h6
            )
        }
    }
}
```

여기까지 마쳤다면 다시 앱이 잘 구동되는지 확인해보자. 처음에 데이터를 넣어줘야 하기 때문에 한 번 구동한 이후에 insert 하는 부분을 주석으로 처리하고 다시 구동해본다.

처음에는 잘 구동되었으나 두 번째에서 오류가 발생한다.

```
java.lang.SecurityException: Permission denied (missing INTERNET permission?)
```

인터넷 접근권한을 안줘서 그런것이니 권한을 부여하자.

- AndroidManifest.xml
```xml
<?xml version="1.0" encoding="utf-8"?>  
<manifest xmlns:android="http://schemas.android.com/apk/res/android"  
    xmlns:tools="http://schemas.android.com/tools">  
  
    <uses-permission android:name="android.permission.INTERNET" />  <!-- 이 부분 추가 -->
  
    <application        android:name=".App"
```

그리고 다시 구동.
![[Pasted image 20221215162219.png|300]]

잘 구동된다.



지금까지 완전 단순한 앱이지만 어느정도는 클린한 아키텍처가 구현되었다. 하지만 앱이 조금 더 복잡해진다고 가정했을 때는 지금까지 구현한 정도로는 클린하다고 얘기하기는 어렵다. 아키텍처를 조금 더 고도화 해보자.

## 새로운 기능을 추가해보자

사용자가 Affirmation 을 직접 입력할 수 있는 기능을 추가해 보자.

### 신규기능 정의

- "+" 버튼을 누르면 affirmation 입력창이 뜬다.
- 입력창에서 affirmation 과 오늘의 기분을 입력할 수 있다.
- 사용자는 affirmation statement 만 입력하면 랜덤사진과 입력한 날짜가 자동을 입력된다.
- affirmation 카드를 클릭하면 affirmation 의 상세 정보를 볼 수가 있다.

화면에 대한 상세 내용은 아래를 참고하자

- 버튼 추가
  ![[IMG_0044.jpg|200]]
- 입력 다이얼로그
  ![[Pasted image 20221215170550.png|200]]
- affirmation 상세 화면
  ![[Pasted image 20221215171153.png|200]]

### 개발계획
- 먼저 입력창과 상세화면에 진입하기 위해서 navigation 이 필요하다.
- 랜덤사진을 얻기 위해서 https://picsum.photos/ 을 이용한다

### Navigation 추가

navigation 같은 경우는 전체 앱에서 상태를 관리해야 하므로 core 영역에 들어가는 것이 좋겠다. 따라서 아래와 같이 패키지를 추가해 준다.

![[Pasted image 20221215172057.png|300]]

그리고 navigation 을 이용하기 위해서 몇 가지 의존성을 설치해줘야 한다. ([가이드](https://developer.android.com/guide/navigation?hl=ko) 참고)

- app/build.gradle
```kotlin
dependencies {  
  
    ...
    implementation "androidx.lifecycle:lifecycle-viewmodel-compose:2.5.1"  <-- 제거
  ...
    //Hilt  
    implementation "com.google.dagger:hilt-android:$hilt_version"  
    implementation 'androidx.hilt:hilt-navigation-compose:1.0.0' <-- 추가  
    kapt "com.google.dagger:hilt-compiler:$hilt_version"  
    kapt "androidx.hilt:hilt-compiler:1.0.0"  <-- 추가
  ...
    //Navigation  
    implementation "androidx.navigation:navigation-compose:2.5.3"  <-- 추가
}
```

Navigation.kt 에는 아래와 같이 작성해 준다.

```kotlin
package io.play.clean_architecture_boilerplate.core.presentation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.play.clean_architecture_boilerplate.affirmation.presentation.AffirmationScreen

@Composable
fun Navigation() {
    val navController = rememberNavController()
    
    NavHost(navController = navController, startDestination = ScreenRoutes.AffirmationScreen.route) {
        composable(ScreenRoutes.AffirmationScreen.route) {
            AffirmationScreen(navController)
        }
    }
}

sealed class ScreenRoutes(val route:String) {
    object AffirmationScreen:ScreenRoutes("affirmation_screen")
    object AffirmationDetailScreen:ScreenRoutes("affirmation_detail_screen")
}
```

AffirmationScreen 에서 오류가 발생하는 것을 볼 수 있다. 그럼 AffirmationScreen 으로 가서 navController 를 받을 수 있도록 설정해 준다.

- AffirmationScreen.kt
```kotlin
@Composable  
fun AffirmationScreen(  
    navController: NavController,  // 이렇게 추가, modifier 는 필요가 없어서 삭제
    affirmationViewModel: AffirmationViewModel = hiltViewModel(),  
) {
```

Navigation 설정이 되었다. 이제 MainActivity 로 가서 Navigation 을 onCreate 시에 열 수 있도록 바꿔주자.

- MainActivity.kt
```kotlin
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CleanarchitectureboilerplateTheme {
                Navigation() // 이렇게 수정
            }
        }
    }
}
```

앱이 잘 구동되는지 한 번 확인해보자.

이제 AffirmationDetailScreen 을 navigation 에 추가해 주면 된다. AffirmationDetailScreen 이 없어서 오류가 뜬다. 만들어주자.

![[Pasted image 20221215174420.png|300]]

- AffirmationDetailScreen.kt
```kotlin
package io.play.clean_architecture_boilerplate.affirmation.presentation  
  
import androidx.compose.runtime.Composable  
  
@Composable  
fun AffirmationDetailScreen() {  
  
}
```

그리고 Navigation 으로 가서 방금 생성한 AffirmationDetailScreen 을 임포트해준다.
다시 앱이 잘 구동되는지 확인해보자.

이번엔 AffirmationScreen 에서 Affirmation 을 클릭했을 때 AffirmationDetailScreen 으로 넘어가게 해주자.

- AffirmationScreen.kt
```kotlin
@Composable
fun AffirmationScreen(
    navController: NavController,
    onAffirmationCardClicked: () -> Unit, // 카드를 클릭했을 때 실행할 함수 추가
    affirmationViewModel: AffirmationViewModel = hiltViewModel(),
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(
            affirmationViewModel.affirmationList,
            key = { affirmationListItem -> affirmationListItem.id }
        ) {
            AffirmationCard(it, onAffirmationCardClicked) // 이렇게 수정
        }
    }
}

@Composable
fun AffirmationCard(
    affirmationEntity: AffirmationEntity,
    onAffirmationCardClicked: () -> Unit, // 이렇게 추가
    modifier: Modifier = Modifier
) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .clickable { onAffirmationCardClicked()  // 카드를 클릭하면 전달 받은 함수 실행, 여기에 나중에는 affirmation id 같은걸 전달해 주면 클릭한 카드의 detail screen 으로 이동한다.
        elevation = 4.dp
    ) {
        Column {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(affirmationEntity.imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(194.dp)
            )

            Text(
                text = affirmationEntity.statement,
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.h6
            )
        }
    }
}
```

- AffirmationDetailScreen.kt
```kotlin
@Composable  
fun AffirmationDetailScreen() {  
    Text(text = "This is AffirmationDetailScreen")  
}
```

일단은 이렇게 텍스트만 나오게 하고 앱을 구동해 보자.
![[ezgif.com-gif-maker.gif|200]]

Navigation 은 이제 어느정도 된 것 같으니까 affirmation 추가 버튼을 달아보자.

### 새로운 Affirmation 추가하기
Floating 버튼을 추가하기 위해서는 지금 Base composable 로 되어 있는 LazyColumn 을 Scaffold 하위로 가져가야 한다.

- AffirmationScreen.kt
```kotlin
@Composable
fun AffirmationScreen(
    navController: NavController,
    onAffirmationCardClicked: () -> Unit,
    affirmationViewModel: AffirmationViewModel = hiltViewModel(),
) {
    val scaffoldState = rememberScaffoldState()

    Scaffold(
        scaffoldState = scaffoldState,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /*TODO*/ },
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
            }
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(
                affirmationViewModel.affirmationList,
                key = { affirmationListItem -> affirmationListItem.id }
            ) {
                AffirmationCard(it, onAffirmationCardClicked)
            }
        }
    }
}
```

이렇게 변경을 해주고 다시 앱을 구동해 보자.
![[Pasted image 20221215181738.png|200]]
의도치 않게 색깔이 바껴버렸다. 테스트폰이 다크모드로 설정이 되어 있어서 저런 색깔이 나오고 있다. 디자인에 대한 부분은 추후에 다시 고민하기로 하고 계속 진행하기로 한다. (별도로 색깔같은걸 지정해주지 않으면 Material 의 기본 테마가 적용된다.)

이번엔 "+" 버튼을 누르면 새로운 Affirmation 을 입력할 수 있는 다이얼로그가 떠야한다. 그 부분을 추가해 보자.

![[Pasted image 20221215230959.png|300]]

- NewAffirmationDialog.kt
```kotlin
package io.play.clean_architecture_boilerplate.affirmation.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun NewAffirmationDialog(
    onDismiss: () -> Unit,
    onClose: () -> Unit
) {
    Dialog(
        onDismissRequest = { onDismiss() }
    ) {
        Card(
            modifier = Modifier.fillMaxHeight(0.8f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(
                        onClick = { onClose() }
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = null)
                    }
                }
            }
        }
    }
}
```

Close 버튼만 있는 단순한 다이얼로그를 추가 하였다. 다이얼로그는 AffirmationScreen 에서 열려야 하므로 AffirmationScreen 을 수정해 주자.

- AffirmationScreen.kt
```kotlin
@Composable
fun AffirmationScreen(
    navController: NavController,
    onAffirmationCardClicked: () -> Unit,
    affirmationViewModel: AffirmationViewModel = hiltViewModel(),
) {
    val scaffoldState = rememberScaffoldState()

    Scaffold(
        scaffoldState = scaffoldState,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { affirmationViewModel.onNewAffirmationClick() }, // 플로팅 버튼을 눌렀을 때 행동을 정의
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
            }
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(
                affirmationViewModel.affirmationList,
                key = { affirmationListItem -> affirmationListItem.id }
            ) {
                AffirmationCard(it, onAffirmationCardClicked)
            }
        }
    }

    if (affirmationViewModel.isNewAffirmationDialogShown) { // isNewAffirmationDialogShown 값에 따라 다이얼로그가 열림
        NewAffirmationDialog(
            onDismiss = {/*TODO*/},
            onClose = { affirmationViewModel.onNewAffirmationClose() }
        )
    }
}
```

마지막으로 ViewModel 에서 다이얼로그 열림/닫힘을 처리할 부분을 넣어준다.

- AffirmationViewModel.kt
```kotlin
@HiltViewModel
class AffirmationViewModel @Inject constructor(
    private val affirmationDao: AffirmationDao
) : ViewModel() {
    var affirmationList by mutableStateOf<List<AffirmationEntity>>(emptyList())
        private set
    var isNewAffirmationDialogShown by mutableStateOf(false)
        private set

    init {
        viewModelScope.launch {
            affirmationList = affirmationDao.getAll()
//            affirmationDao.insert(
//                AffirmationEntity(
//                    "1", "I am strong.", "https://darebee.com/images/fitness/muscles-stronger.jpg"
//                )
//            )
        }
    }

    fun onNewAffirmationClick() {
        isNewAffirmationDialogShown = true
    }

    fun onNewAffirmationClose() {
        isNewAffirmationDialogShown = false
    }
}

// 아래쪽에 필요없는 내용들 삭제
```

이정도로 해놓고 앱을 한 번 구동해본다.

![[ezgif.com-gif-maker (1).gif|200]]

열림과 닫힘 모두 잘 작동한다. 그럼 이제 데이터를 입력받는 부분을 작성해보자.

```kotlin
@Composable
fun NewAffirmationDialog(
    onDismiss: () -> Unit, onClose: () -> Unit
) {
    var affirmationStatement by remember {
        mutableStateOf("")
    }
    var todayFeeling by remember {
        mutableStateOf("")
    }

    Dialog(onDismissRequest = { onDismiss() }) {
        Card(
            modifier = Modifier
                .fillMaxHeight(0.8f)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Howdy!!", fontWeight = FontWeight.Bold, fontSize = 24.sp)
                    IconButton(onClick = { onClose() }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = null)
                    }
                }
                Spacer(Modifier.size(18.dp))
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    TextField(
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text("Enter your affirmation")
                        },
                        value = affirmationStatement,
                        onValueChange = { affirmationStatement = it })
                    TextField(
                        modifier = Modifier
                            .defaultMinSize(minHeight = 200.dp)
                            .fillMaxWidth(),
                        placeholder = {
                            Text(text = "How's your feeling today?")
                        },
                        value = todayFeeling,
                        onValueChange = { todayFeeling = it },
                        singleLine = false,
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 36.dp),
                    ) {
                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { /*TODO*/ }
                        ) {
                            Text(text = "Dismiss")
                        }
                    }
                }
            }
        }
    }
}
```

우선 이렇게 작성하고 앱을 다시 구동시켜 보자.

![[Pasted image 20221216005030.png|200]]

잘 작동하는 모습을 볼 수 있다. 그럼 이제 Dismiss 버튼을 클릭했을 때의 동작을 작성해야 한다.

- NewAffirmationDialog.kt
```kotlin
@Composable
fun NewAffirmationDialog(
    onDismissButtonClicked: (String, String, String, String) -> Unit, onClose: () -> Unit // Dismiss 버튼을 클릭했을 때 동작
) {
    var affirmationStatement by remember {
        mutableStateOf("")
    }
    var todayFeeling by remember {
        mutableStateOf("")
    }

    Dialog(onDismissRequest = { onClose() }) { // 이 부분은 다이얼로그의 바깥쪽을 클릭했을 때 작동하는 부분이므로 onClose() 로 변경함
        Card(
            modifier = Modifier
                .fillMaxHeight(0.8f)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Howdy!!", fontWeight = FontWeight.Bold, fontSize = 24.sp) // 윗 부분이 너무 허전해서 추가함
                    IconButton(onClick = { onClose() }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = null)
                    }
                }
                Spacer(Modifier.size(18.dp))
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    TextField(
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text("Enter your affirmation")
                        },
                        value = affirmationStatement,
                        onValueChange = { affirmationStatement = it })
                    TextField(
                        modifier = Modifier
                            .defaultMinSize(minHeight = 200.dp)
                            .fillMaxWidth(),
                        placeholder = {
                            Text(text = "How's your feeling today?")
                        },
                        value = todayFeeling,
                        onValueChange = { todayFeeling = it },
                        singleLine = false,
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 36.dp),
                    ) {
                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                onDismissButtonClicked(
                                    affirmationStatement,
                                    todayFeeling,
                                    LocalDate.now().toString(),
                                    "https://picsum.photos/300/200"
                                )
                            } // Dismiss 버튼을 클릭하면 parent compose 에 입력한 값들을 넘겨준다.
                        ) {
                            Text(text = "Dismiss")
                        }
                    }
                }
            }
        }
    }
}
```

위와 같이 작성해 주고 AffirmationScreen 에서 입력받은 값들을 처리해 주면 된다.

- AffirmationScreen.kt
```kotlin
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AffirmationScreen(
    navController: NavController,
    onAffirmationCardClicked: () -> Unit,
    affirmationViewModel: AffirmationViewModel = hiltViewModel(),
) {
    ...

    if (affirmationViewModel.isNewAffirmationDialogShown) {
        NewAffirmationDialog(
            onDismissButtonClicked = { affirmationStatement, todayFeeling, date, imageUrl ->
                affirmationViewModel.onDismissButtonClicked(
                    affirmationStatement,
                    todayFeeling,
                    date,
                    imageUrl
                )
            },
            onClose = { affirmationViewModel.onNewAffirmationClose() }
        )
    }
}
```

`affirmationViewModel.onDismissButtonClicked` 부분의 오류를 처리해 주자.

- AffirmationViewModel.kt
```kotlin
fun onDismissButtonClicked(  
    affirmationStatement: String,  
    todayFeeling: String,  
    date: String,  
    imageUrl: String  
) {  
    onNewAffirmationClose()  
    viewModelScope.launch {  
        affirmationDao.insert(  
            AffirmationEntity(  
                "2", affirmationStatement, imageUrl  
            )  
        )  
    }  
}
```

이렇게 우선 함수를 작성해 주고 앱을 실행시켜보자.
![[Pasted image 20221216012937.png|200]] ![[Pasted image 20221215181738.png|200]]

다이얼로그 창이 닫히고 다시 AffirmationScreen 으로 돌아가는 것 까지는 잘 됐지만 입력한 리스트가 나오지 않는다. 입력후에는 affirmationList 를 갱신시켜 줘야 한다. 따라서,

- AffirmationViewModel.kt
```kotlin
fun onDismissButtonClicked(  
    affirmationStatement: String,  
    todayFeeling: String,  
    date: String,  
    imageUrl: String  
) {  
    onNewAffirmationClose()  
    viewModelScope.launch {  
        affirmationDao.insert(  
            AffirmationEntity(  
                "2", affirmationStatement, imageUrl  
            )  
        )  
        affirmationList = affirmationDao.getAll()  // 이렇게 추가
    }  
}
```

이렇게 변경해 준다. 하지만 아직 문제가 있다. 아이디가 "2" 로 고정이 되어 있고, `todayFeeling` 과 입력날짜를 입력할 수 없는 상태이다. AffirmationEntity 에 해당 값들을 입력할 수 있도록 수정하자.

- AffirmationEntity.kt
```kotlin
@Entity  
data class AffirmationEntity(  
    val statement: String,  
    val todayFeeling: String,  
    val createdAt: String,  
    val imageUrl: String,  
) {  
    @PrimaryKey(autoGenerate = true) var id: Int = 0  
}
```

아이디는 자동으로 생성될 수 있도록 변경했고, 두 개의 멤버를 추가했다. 그리고 AffirmationViewModel 에도 받은 값들을 넘겨줄 수 있게 수정한다.

- AffirmationViewModel.kt
```kotlin
fun onDismissButtonClicked(  
    affirmationStatement: String,  
    todayFeeling: String,  
    date: String,  
    imageUrl: String  
) {  
    onNewAffirmationClose()  
    viewModelScope.launch {  
        affirmationDao.insert(  
            AffirmationEntity(  
                 affirmationStatement, todayFeeling, date, imageUrl  
            )  
        )  
        affirmationList = affirmationDao.getAll()  
    }  
}
```

다시 앱을 실행해서 값을 입력해 보자.
처음 앱을 실행하면 오류가 발생하는데 기존의 아이디 타입이 String 이여서 생기는 오류로 추정이 된다. 앱을 삭제하고 다시 실행시켜보면 정상동작 하는 것을 확인할 수 있다.

![[Pasted image 20221216015631.png|200]]

#### 조금만 더 개선하기

입력할 때마다 사진이 계속 같은것만 나오고 있다. 그리고 최근 입력한 Affirmation 이 먼저 나왔으면 좋겠다.

- NewAffirmationDialog.kt
```kotlin
Button(  
    modifier = Modifier.fillMaxWidth(),  
    onClick = {  
        onDismissButtonClicked(  
            affirmationStatement,  
            todayFeeling,  
            LocalDateTime.now().toString(),  // 날짜만 있으면 정렬이 되지 않기때문에 시간까지 추가
            "https://picsum.photos/id/${(1..999).random()}/300/200" // 사진이 랜덤하게 나올 수 있도록 수정  
        )  
    }  
) {  
    Text(text = "Dismiss")  
}
```

- AffirmationDao.kt
```kotlin
@Query("select * from AffirmationEntity order by createdAt desc") // 생성한 날짜를 내림차순으로 정렬
suspend fun getAll(): List<AffirmationEntity>
```

이렇게 수정하고 다시 앱을 구동시켜 보자.

![[KakaoTalk_Photo_2022-12-16-02-05-57.jpeg|200]]![[Pasted image 20221216020631.png|200]]

실행이 잘 된다. 하지만 Dismiss 버튼을 누르면 스크롤이 제일 위로 이동을 하지 않는다. 이 부분만 더 추가를 해보겠다.

- AffirmationScreen.kt
```kotlin
@Composable
fun AffirmationScreen(
    navController: NavController,
    onAffirmationCardClicked: () -> Unit,
    affirmationViewModel: AffirmationViewModel = hiltViewModel(),
) {
    val scaffoldState = rememberScaffoldState()
    val scrollState = rememberLazyListState() // 이 부분 추가
    val coroutineScope = rememberCoroutineScope()  // 이 부분 추가

    Scaffold(
        ...
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            state = scrollState // 이 부분 추가
        ) {
            items(
                ...
            ) {
                AffirmationCard(it, onAffirmationCardClicked)
            }
            coroutineScope.launch { scrollState.animateScrollToItem(0)} // 이 부분 추가
        }
    }

    ...
}
```

이렇게 해 주면 Affirmation 을 추가할 때 새로 리스트가 갱신되고 바로 스크롤이 제일 위의 아이템쪽으로 이동한다.

### Affirmation detail 보기
