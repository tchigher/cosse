# Cosse

Cosse is a lightweight low level Android library for Server Side-Events (SSE). Cosse is written in Kotlin and takes advantage of Kotlin's coroutines by starting a coroutine everytime a new message is recieved. For custom functionality upon message receipt inside the coroutine, this project should be forked and adapted to suit the project's needs.

## Getting Started

Add the following line to your gradle file:

```
dependencies {
    implementation 'com.github.charmas3r:cosse:0.9.2'
}
```

Add in your root build.gradle:

```
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```

### Usage

#### Request

Create your request by setting URL and channel timeout. The channel timeout is triggered after the provided amount of time and will close the channel after it is triggered. Note, the channel timeout is different from HttpUrlConnection's connect timeout which is a timeout based on the length of time it takes to establish the connection.

```
//This sets the request with a channel timeout of 10 minutes
val request = HttpRequest("https://myurl.com", 600000)
```
The channel timeout can also be omitted if this is only being handled server side:

```
val request = HttpRequest("https://myurl.com")
```

#### Headers

Add custom headers to your request

```
request.headers.put("Authorization", "123345679")
```

#### Callback

Assign the callback

```
val test = SseClient(request, object : SseListener {
    override fun onOpen(response: HttpResponse) {
         response.headers.forEach {
             Log.d(TAG, "Response Header: ${it.key}, Value: ${it.value}")
         }
    }

    override fun onMessage(line: String, obj: JSONObject) {
        //if your SSE returns JSON the object will be passed back
        text_response.setText(obj.toString())
    }

    override fun onTimeout() {
        Log.i(TAG, "SSE Timed out")
    }

    override fun onError(status: Int) {
        Log.i(TAG, "Error response code: " + status);
    }

    override fun onClose() {
        Log.i(TAG, "SSE Closed")
    }
});

test.execute()
```

#### Close

You should close the channel if it is no longer in use.

```
test.close()
```

### Java Usage

```
HttpRequest request = new HttpRequest("https://myurl.com", 40000);

Map<String, String> headers = new HashMap<>();
headers.put("Authorization", "12345678");
request.setHeaders(headers);

SseClient test = new SseClient(request, new SseListener() {
    @Override
    public void onOpen(@NotNull HttpResponse httpResponse) {

    }

    @Override
    public void onMessage(@NotNull String s, @NotNull JSONObject obj) {
	//if JSON is not return obj will return empyty object "{}"
    }

    @Override
    public void onTimeout() {

    }

    @Override
    public void onError(int i) {

    }

    @Override
    public void onClose() {

    }
});

test.execute();
```

## License

Copyright 2019 Evan Smith

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
