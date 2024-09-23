[![Android CI - Build Apk](https://github.com/abdnezar/SpinViewLibrary/actions/workflows/android.yml/badge.svg)](https://github.com/abdnezar/SpinViewLibrary/actions/workflows/android.yml)
[![](https://jitpack.io/v/abdnezar/SpinViewLibrary.svg)](https://jitpack.io/#abdnezar/SpinViewLibrary)

# SpinView
Easiest way to add Spin and Win on Android. LibSpin offers an Interactive Gamified Interface with lots of Customization options.

# Add to your app
```gradle

// force androidx.appcompat:appcompat:1.6.1 if using another version
configurations.configureEach {
    resolutionStrategy {
        force 'androidx.appcompat:appcompat:1.6.1'
    }
}

dependencies {
    ...

    implementation 'com.github.abdnezar:SpinViewLibrary:1.0.1'
}


```

## Usage
Include SpinWheel in your layout :

```xml

<com.abdnezar.spinview.SpinWheel
    android:id="@+id/spinWheel"
    android:layout_width="300dp"
    android:layout_height="300dp"
    app:spnwTopTextColor="#263238"
    app:spnwBackgroundColor="#FF9800"
    app:spnwCursor="@drawable/ic_cursor"
    app:spnwCenterImage="@drawable/ic_center_image_1" />

```

And Initialize the SpinWheel :

```java

private var data: MutableList<SpinItem> = ArrayList()

// Creating Spin Items
private fun getSpinItems(): MutableList<SpinItem> {
        val item1 = SpinItem()
        item1.topText = "0"
        item1.icon = R.drawable.ic_gold
        item1.color = -0xc20
        data.add(item1)

        #add more items
        ...
        ...
}

binding.spinWheel.setData(data)
binding.spinWheel.setRound(5)
binding.spinWheel.isTouchEnabled = true


binding.spinWheel.setSpinWheelRoundItemSelectedListener { index ->
  Toast.makeText(this, "Selected index: $index", Toast.LENGTH_SHORT).show()
}

binding.spinButton.setOnClickListener {
  binding.spinWheel.startSpinWheelWithTargetIndex(0)
}
```

## Example

For an example, please check the ExampleActivity class in the provided Sample App. [See Example](https://github.com/abdnezar/SpinViewLibrary/blob/master/app/src/main/java/com/abdnezar/spinview/ExampleActivity.kt)

## License
```
MIT License

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
