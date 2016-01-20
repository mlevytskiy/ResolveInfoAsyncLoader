ResolveInfoAsyncLoader
================

[![Join the chat at https://gitter.im/mlevytskiy/ResolveInfoAsyncLoader](https://badges.gitter.im/mlevytskiy/ResolveInfoAsyncLoader.svg)](https://gitter.im/mlevytskiy/ResolveInfoAsyncLoader?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

Help to show [ResolveInfo](http://developer.android.com/intl/es/reference/android/content/pm/ResolveInfo.html) label and icon asynchronously. (less blocking UI thread).

##Including in your project

Gradle

````groovy
compile 'com.funakoshi.resolveInfoAsyncLoader:ResolveInfoAsyncLoaderLib:1.0.1'
````

##How use

Use LabelTextView instead of TextView for show app name.
Use IconImageView instead of ImageView for show app icon.

````
<com.funakoshi.resolveInfoAsyncLoader.IconImageView
        android:id="@+id/image_view"
        android:layout_width="50dp"
        android:layout_height="50dp"/>
        
<com.funakoshi.resolveInfoAsyncLoader.LabelTextView
        android:id="@+id/text_view"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content" />
 ````
        
````java
label.setResolveInfo(resolveInfo);
icon.setResolveInfo(resolveInfo);
 ````

or

````java
label.setPackageName(packageName);
icon.setPackageName(packageName);
 ````


##License

The MIT License (MIT)

Copyright (c) 2016 Levytskyi Maksym

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.




