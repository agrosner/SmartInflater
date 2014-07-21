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
