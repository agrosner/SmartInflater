SmartInflater
=============

Why do we bother with ```findViewById(R.id.someView)``` and the simply set an ```OnClickListener```? Why do we need to find the view and store it into a variable in our class each and every time? SmartInflater is a simple, yet powerful way to inject views, provide **Handlers** that allow for special methods that will be called for specific events.

## Usage

### Prerequisites

1. MinSdk is 5
2. Compiled on SDK 20 with buildToolsVersion 20
3. Has no dependencies (yet)
4. Java 7 (you should be using it now anyways)

### Including It in your project

1. Clone this repo
2. Go to your top-level build.gradle file

```groovy

dependencies{
  //locally in the a folder named "Libraries"
  compile project(':Libraries:SmartInflater');
}


```

NOTE: MavenCentral coming soon

### Getting Started

#### Initialize the instance.

In an Activity or Application's ``` onCreate() ``` method:

```java

SmartInflater.initialize(this);

```

Note: SmartInflater holds on a **WeakReference** to the Context, so you do not need to worry about memory leaks!

<br />

#### Inflate Your Layout!

In a view:

``` java

public void myInitializationMethod(){

  //note this will automatically attach to root in a ViewGroup class!
  SmartInflater.inflate(this, R.layout.my_layout);

}


```

In your base fragment:

```java


 @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(mLayout==-1){
            throw new RuntimeException("You must define a layout for: " + getClass().getSimpleName());
        }

        return SmartInflater.inflate(this, mLayout);
    }

```

In your activity:

```java 


@Override
protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(SmartInflater.inflate(this, R.layout.activity_my_activity));
		...
}

```

It's *that* easy.

### SResource

*SResource* is an annotation that specifies we want to fill this View with its corresponding XML view when inflated by SmartInflater. By default, these fields are **not** optional, meaning that if at runtime, SmartInflater does not find the view specified by the field, it will throw an exception.
<br />

Supported Properties:
  1. id() - specify a different id than the field name, or for easy navigation to the id it corresponds to
  2. optional() = throws an exception if the corresponding view is not found. **False** by default.

```xml

...

<TextView
  android:id="@+id/title"
  ...
/>

...

````

```java

@SResource private TextView title;

//or

@SResource(id = R.id.title)
private TextView my_textview_with_another_name;

```

### SMethod and SHandlers

*SHandler* is an interface that provides a standard callback for handling view operations. It enables limitless additions to this library by overriding ``` handleView(Method method, View view) ```. A SHandler has a method prefix that SmartInflater uses to associate each Method name you declare in your class to an **SHandler**. Each **SHandler** must have a **unique method prefix**. 
<br />
*SMethod* is an annotation that tells SmartInflater we want to associate this method with some SHandler. We do this by taking the method prefix and seeing if the **SGlobalHandlerList** has a SHandler with the method prefix.
 
<br />
Out-of-the-box-handlers include:
  1. OnCheckedChangedHandler
  2. OnChildClickHandler
  3. OnClickHandler
  4. OnCreateHandler (called after View is first injected)
  5. OnGroupClickHandler
  6. OnItemClickHandler
  7. OnItemSelectedHandler
  8. OnLongClickHandler
  9. OnTouchHandler
  10. Your own SHandler implementation (more on this later)


```java
//any visibility is allowed
// parameter must be baser-than-or-equal-to the corresponding XML view
@SMethod
private void onClickTitle(TextView title){
  //do something here
}

//id() option exits here too!
@SMethod(id=R.id.title)
private void onClickSuperLongTitleMethod(TextView title){
  //do something else here
}


```

#### Custom SHandler example

Say we use the support library's OnRefreshListener a lot. We want to make it available in SmartInflater. How do we do it?
<br />
1. Create class that implements SHandler.
2. Register the SHandler in the SGlobalHandlerList in your Application
3. Call the method in your class where you want it!

##### 1
```java

public class OnRefreshHandler extends OnRefreshListener implements SHandler {

  private Method mMethod;
  
  //don't worry about calling this. SGlobalHandlerList will take care of it.
  public OnRefreshHandler(Object inObject){
    super(inObject);
  }
  
  @Override
  public void onRefresh(){
  //invoke our method here
    try{
      mMethod.setAccessble(true);
      mMethod.invoke(inObject);
    } catch(Exception e){
      throw new RuntimeException(e);
    }
  }
  
  @Override
  public void handleView(Method method, View view){
    mMethod = method;
    ((SwipeRefreshLayout)view).setOnRefreshListener(this);
  }
  
  @Override
  public String getMethodPrefix(){
    return "onRefresh";
  }

```

##### 2

```java

public class MyApp extends Application{

  @Override
  public void onCreate(){
    super();
  
    SGlobalHandlerList.addHandler(OnRefreshHandler.class);
  }

}

```

##### 3

```java

@SMethod
public void onRefreshMyRefresh(){

}

```
