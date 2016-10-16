package server.bios.asbserver.server;

import android.text.TextUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import server.bios.asbserver.server._interface._Response;

/**
 * Created by BIOS on 10/5/2016.
 */

public class HttpResponse {
    private OkHttpClient client;

    public HttpResponse() {
        client = new OkHttpClient.Builder().build();
    }

    public HttpResponse(int connection_timeout, int read_timeout) {
        client = new OkHttpClient.Builder()
                .connectTimeout(connection_timeout, TimeUnit.SECONDS)
                .readTimeout(read_timeout, TimeUnit.SECONDS).build();
    }

    public ServerResponse getResponse(URL url) throws IOException {
        return new ServerResponse(url);
    }

    private class ServerResponse implements _Response {
        private Response response;

        public ServerResponse(URL url) throws IOException {
            if (client == null) throw new NullPointerException("client == null");
            Request request = new Request.Builder().url(url).build();
            response = client.newCall(request).execute();
        }

        public StringBuilder getHeaderResponce() {
            if (response == null) throw new NullPointerException("response == null");
            StringBuilder stringBuilder = new StringBuilder();

            stringBuilder.append(parseProtocol(response.protocol()).concat(" ")
                    .concat(String.valueOf(response.code())).concat(" ")
                    .concat(response.message()).concat("\r\n"));

            Map<String, List<String>> headers = response.headers().toMultimap();
            for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
                String key = entry.getKey();
                String value = TextUtils.join(",", entry.getValue());
                stringBuilder.append(key.concat(": ").concat(value).concat("\r\n"));
            }
            stringBuilder.append("\r\n");
            return stringBuilder;
        }

        public InputStream byteStream() {
            if (response == null) throw new NullPointerException("response == null");
            return response.body().byteStream();
        }

        public String getString() throws IOException {
            if (response == null) throw new NullPointerException("response == null");
            return response.body().string();
        }

        private String parseProtocol(final Protocol p) {
            switch (p) {
                case HTTP_1_0:
                    return "HTTP/1.0";
                case HTTP_1_1:
                    return "HTTP/1.1";
                case SPDY_3:
                    return "SPDY/3.1";
                case HTTP_2:
                    return "HTTP/2.0";
            }

            throw new IllegalAccessError("Unkwown protocol");
        }

        public void close() {
            if (response == null) throw new NullPointerException("response == null");
            response.body().close();
        }
    }
}
