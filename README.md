GifImageView
============

Android ImageView that handles Animated GIF images

### Usage

In your ``build.gradle`` file:

```groovy
repositories {
    maven { url 'https://github.com/felipecsl/m2repository/raw/master/' }
}

dependencies {
    compile 'com.felipecsl:gifimageview:1.0.+'
}
```

In your Activity class:

```java
@Override
protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    gifView = new GifImageView(context);
    gifView.setBytes(bitmapData);
    setContentView(gifView);
}

@Override
protected void onStart() {
    super.onStart();
    gifView.startAnimation();
}

@Override
protected void onStop() {
    super.onStop();
    gifView.stopAnimation();
}
```

### Contributing

* Check out the latest master to make sure the feature hasn't been implemented or the bug hasn't been fixed yet
* Check out the issue tracker to make sure someone already hasn't requested it and/or contributed it
* Fork the project
* Start a feature/bugfix branch
* Commit and push until you are happy with your contribution
* Make sure to add tests for it. This is important so I don't break it in a future version unintentionally.

### Copyright and license

Code and documentation copyright 2011-2014 Felipe Lima.
Code released under the [MIT license](https://github.com/felipecsl/GifImageView/blob/master/LICENSE.txt).
