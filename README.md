# Cosse

Cosse is a lightweight low level Android library for Server Side-Events (SSE). Cosse is written in Kotlin and takes advantage of Kotlin's coroutines by starting a coroutine everytime a new message is recieved. For custom functionality upon message receipt inside the coroutine, this project should be forked and adapted to suit the project's needs.

## Getting Started

Add the following line to your gradle file:

```
dependencies {
    implementation 'com.github.charmas3r:cosse:v0.8.2-alpha'
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

Create your request by setting URL and connect timeout

```
val request = HttpRequest("https://myurl.com", 40000)
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
        Log.i(TAG, response.headers)
    }

    override fun onMessage(line: String) {
        Log.i(TAG, line)
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
    public void onMessage(@NotNull String s) {

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
