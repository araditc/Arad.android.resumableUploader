# Arad.android.resumableUploader

A power full resumable upload library for Android

![](https://i.ibb.co/3mFZdMH/Webp-net-resizeimage-6.png)

Download
--------

```groovy
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
	        implementation 'com.github.araditc:Arad.android.resumableUploader:Tag'
}
```

How do I use Arad Arad uploader?
-------------------

Simple use cases will look something like this:

```java
// For a simple view:
        AradUploader.newInstance().upload(token, filePath, type, uploadId);


          AradUploader.setProgressUploadListener(new UploadWorker.UploadResult() {
            @Override
            public void onUploadComplete(int i, MediaResponse mediaResponse) {
                
            }

            @Override
            public void onUploadProgress(int index, int fileId, int total, int value, int percent) {

            }

            @Override
            public void onErrorUploadProgress(int index, int fileId, int total, int value, int percent) {

            }

            @Override
            public void onUploadFailed(String s) {

            }
        });


//      unregister your call back

        AradUploader.unRegisterProgressUploadListener();
       
```

License
--------

  
  Copyright 2020 Arad-Itc.
 
  Licensed under the Arad License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at
 
  https://arad-itc.com/
 
  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.


