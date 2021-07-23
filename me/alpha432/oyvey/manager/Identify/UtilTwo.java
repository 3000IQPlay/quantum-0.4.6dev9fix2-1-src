/*
 * Decompiled with CFR 0.151.
 */
package me.alpha432.oyvey.manager.Identify;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.Flushable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.security.AccessController;
import java.security.GeneralSecurityException;
import java.security.PrivilegedAction;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.zip.GZIPInputStream;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class UtilTwo {
    public static final String CHARSET_UTF8 = "UTF-8";
    public static final String CONTENT_TYPE_FORM = "application/x-www-form-urlencoded";
    public static final String CONTENT_TYPE_JSON = "application/json";
    public static final String ENCODING_GZIP = "gzip";
    public static final String HEADER_ACCEPT = "Accept";
    public static final String HEADER_ACCEPT_CHARSET = "Accept-Charset";
    public static final String HEADER_ACCEPT_ENCODING = "Accept-Encoding";
    public static final String HEADER_AUTHORIZATION = "Authorization";
    public static final String HEADER_CACHE_CONTROL = "Cache-Control";
    public static final String HEADER_CONTENT_ENCODING = "Content-Encoding";
    public static final String HEADER_CONTENT_LENGTH = "Content-Length";
    public static final String HEADER_CONTENT_TYPE = "Content-Type";
    public static final String HEADER_DATE = "Date";
    public static final String HEADER_ETAG = "ETag";
    public static final String HEADER_EXPIRES = "Expires";
    public static final String HEADER_IF_NONE_MATCH = "If-None-Match";
    public static final String HEADER_LAST_MODIFIED = "Last-Modified";
    public static final String HEADER_LOCATION = "Location";
    public static final String HEADER_PROXY_AUTHORIZATION = "Proxy-Authorization";
    public static final String HEADER_REFERER = "Referer";
    public static final String HEADER_SERVER = "Server";
    public static final String HEADER_USER_AGENT = "User-Agent";
    public static final String METHOD_DELETE = "DELETE";
    public static final String METHOD_GET = "GET";
    public static final String METHOD_HEAD = "HEAD";
    public static final String METHOD_OPTIONS = "OPTIONS";
    public static final String METHOD_POST = "POST";
    public static final String METHOD_PATCH = "PATCH";
    public static final String METHOD_PUT = "PUT";
    public static final String METHOD_TRACE = "TRACE";
    public static final String PARAM_CHARSET = "charset";
    private static final String BOUNDARY = "00content0boundary00";
    private static final String CONTENT_TYPE_MULTIPART = "multipart/form-data; boundary=00content0boundary00";
    private static final String CRLF = "\r\n";
    private static final String[] EMPTY_STRINGS = new String[0];
    private static SSLSocketFactory TRUSTED_FACTORY;
    private static HostnameVerifier TRUSTED_VERIFIER;
    private static ConnectionFactory CONNECTION_FACTORY;
    private HttpURLConnection connection = null;
    private final URL url;
    private final String requestMethod;
    private RequestOutputStream output;
    private boolean multipart;
    private boolean form;
    private boolean ignoreCloseExceptions = true;
    private boolean uncompress = false;
    private int bufferSize = 8192;
    private long totalSize = -1L;
    private long totalWritten = 0L;
    private String httpProxyHost;
    private int httpProxyPort;
    private UploadProgress progress = UploadProgress.DEFAULT;

    private static String getValidCharset(String charset) {
        if (charset != null && charset.length() > 0) {
            return charset;
        }
        return CHARSET_UTF8;
    }

    private static SSLSocketFactory getTrustedFactory() throws HttpRequestException {
        if (TRUSTED_FACTORY == null) {
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager(){

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }

                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType) {
                }
            }};
            try {
                SSLContext context = SSLContext.getInstance("TLS");
                context.init(null, trustAllCerts, new SecureRandom());
                TRUSTED_FACTORY = context.getSocketFactory();
            }
            catch (GeneralSecurityException e) {
                IOException ioException = new IOException("Security exception configuring SSL context");
                ioException.initCause(e);
                throw new HttpRequestException(ioException);
            }
        }
        return TRUSTED_FACTORY;
    }

    private static HostnameVerifier getTrustedVerifier() {
        if (TRUSTED_VERIFIER == null) {
            TRUSTED_VERIFIER = new HostnameVerifier(){

                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };
        }
        return TRUSTED_VERIFIER;
    }

    private static StringBuilder addPathSeparator(String baseUrl, StringBuilder result) {
        if (baseUrl.indexOf(58) + 2 == baseUrl.lastIndexOf(47)) {
            result.append('/');
        }
        return result;
    }

    private static StringBuilder addParamPrefix(String baseUrl, StringBuilder result) {
        int queryStart = baseUrl.indexOf(63);
        int lastChar = result.length() - 1;
        if (queryStart == -1) {
            result.append('?');
        } else if (queryStart < lastChar && baseUrl.charAt(lastChar) != '&') {
            result.append('&');
        }
        return result;
    }

    private static StringBuilder addParam(Object key, Object value, StringBuilder result) {
        if (value != null && value.getClass().isArray()) {
            value = UtilTwo.arrayToList(value);
        }
        if (value instanceof Iterable) {
            Iterator iterator = ((Iterable)value).iterator();
            while (iterator.hasNext()) {
                result.append(key);
                result.append("[]=");
                Object element = iterator.next();
                if (element != null) {
                    result.append(element);
                }
                if (!iterator.hasNext()) continue;
                result.append("&");
            }
        } else {
            result.append(key);
            result.append("=");
            if (value != null) {
                result.append(value);
            }
        }
        return result;
    }

    public static void setConnectionFactory(ConnectionFactory connectionFactory) {
        CONNECTION_FACTORY = connectionFactory == null ? ConnectionFactory.DEFAULT : connectionFactory;
    }

    private static List<Object> arrayToList(Object array) {
        ArrayList<Object> result;
        block10: {
            block16: {
                block15: {
                    block14: {
                        block13: {
                            block12: {
                                block11: {
                                    block9: {
                                        if (array instanceof Object[]) {
                                            return Arrays.asList((Object[])array);
                                        }
                                        result = new ArrayList<Object>();
                                        if (!(array instanceof int[])) break block9;
                                        for (int value : (int[])array) {
                                            result.add(value);
                                        }
                                        break block10;
                                    }
                                    if (!(array instanceof boolean[])) break block11;
                                    for (boolean value : (boolean[])array) {
                                        result.add(value);
                                    }
                                    break block10;
                                }
                                if (!(array instanceof long[])) break block12;
                                for (long value : (long[])array) {
                                    result.add(value);
                                }
                                break block10;
                            }
                            if (!(array instanceof float[])) break block13;
                            for (float value : (float[])array) {
                                result.add(Float.valueOf(value));
                            }
                            break block10;
                        }
                        if (!(array instanceof double[])) break block14;
                        for (double value : (double[])array) {
                            result.add(value);
                        }
                        break block10;
                    }
                    if (!(array instanceof short[])) break block15;
                    for (short value : (short[])array) {
                        result.add(value);
                    }
                    break block10;
                }
                if (!(array instanceof byte[])) break block16;
                for (byte value : (byte[])array) {
                    result.add(value);
                }
                break block10;
            }
            if (!(array instanceof char[])) break block10;
            for (char value : (char[])array) {
                result.add(Character.valueOf(value));
            }
        }
        return result;
    }

    public static String encode(CharSequence url) throws HttpRequestException {
        URL parsed;
        try {
            parsed = new URL(url.toString());
        }
        catch (IOException e) {
            throw new HttpRequestException(e);
        }
        String host = parsed.getHost();
        int port = parsed.getPort();
        if (port != -1) {
            host = host + ':' + Integer.toString(port);
        }
        try {
            String encoded = new URI(parsed.getProtocol(), host, parsed.getPath(), parsed.getQuery(), null).toASCIIString();
            int paramsStart = encoded.indexOf(63);
            if (paramsStart > 0 && paramsStart + 1 < encoded.length()) {
                encoded = encoded.substring(0, paramsStart + 1) + encoded.substring(paramsStart + 1).replace("+", "%2B");
            }
            return encoded;
        }
        catch (URISyntaxException e) {
            IOException io = new IOException("Parsing URI failed");
            io.initCause(e);
            throw new HttpRequestException(io);
        }
    }

    public static String append(CharSequence url, Map<?, ?> params) {
        String baseUrl = url.toString();
        if (params == null || params.isEmpty()) {
            return baseUrl;
        }
        StringBuilder result = new StringBuilder(baseUrl);
        UtilTwo.addPathSeparator(baseUrl, result);
        UtilTwo.addParamPrefix(baseUrl, result);
        Iterator<Map.Entry<?, ?>> iterator = params.entrySet().iterator();
        Map.Entry<?, ?> entry = iterator.next();
        UtilTwo.addParam(entry.getKey().toString(), entry.getValue(), result);
        while (iterator.hasNext()) {
            result.append('&');
            entry = iterator.next();
            UtilTwo.addParam(entry.getKey().toString(), entry.getValue(), result);
        }
        return result.toString();
    }

    public static String append(CharSequence url, Object ... params) {
        String baseUrl = url.toString();
        if (params == null || params.length == 0) {
            return baseUrl;
        }
        if (params.length % 2 != 0) {
            throw new IllegalArgumentException("Must specify an even number of parameter names/values");
        }
        StringBuilder result = new StringBuilder(baseUrl);
        UtilTwo.addPathSeparator(baseUrl, result);
        UtilTwo.addParamPrefix(baseUrl, result);
        UtilTwo.addParam(params[0], params[1], result);
        for (int i = 2; i < params.length; i += 2) {
            result.append('&');
            UtilTwo.addParam(params[i], params[i + 1], result);
        }
        return result.toString();
    }

    public static UtilTwo get(CharSequence url) throws HttpRequestException {
        return new UtilTwo(url, METHOD_GET);
    }

    public static UtilTwo get(URL url) throws HttpRequestException {
        return new UtilTwo(url, METHOD_GET);
    }

    public static UtilTwo get(CharSequence baseUrl, Map<?, ?> params, boolean encode) {
        String url = UtilTwo.append(baseUrl, params);
        return UtilTwo.get(encode ? UtilTwo.encode(url) : url);
    }

    public static UtilTwo get(CharSequence baseUrl, boolean encode, Object ... params) {
        String url = UtilTwo.append(baseUrl, params);
        return UtilTwo.get(encode ? UtilTwo.encode(url) : url);
    }

    public static UtilTwo post(CharSequence url) throws HttpRequestException {
        return new UtilTwo(url, METHOD_POST);
    }

    public static UtilTwo post(URL url) throws HttpRequestException {
        return new UtilTwo(url, METHOD_POST);
    }

    public static UtilTwo post(CharSequence baseUrl, Map<?, ?> params, boolean encode) {
        String url = UtilTwo.append(baseUrl, params);
        return UtilTwo.post(encode ? UtilTwo.encode(url) : url);
    }

    public static UtilTwo post(CharSequence baseUrl, boolean encode, Object ... params) {
        String url = UtilTwo.append(baseUrl, params);
        return UtilTwo.post(encode ? UtilTwo.encode(url) : url);
    }

    public static UtilTwo patch(CharSequence url) throws HttpRequestException {
        return new UtilTwo(url, METHOD_PATCH);
    }

    public static UtilTwo put(CharSequence url) throws HttpRequestException {
        return new UtilTwo(url, METHOD_PUT);
    }

    public static UtilTwo put(URL url) throws HttpRequestException {
        return new UtilTwo(url, METHOD_PUT);
    }

    public static UtilTwo put(CharSequence baseUrl, Map<?, ?> params, boolean encode) {
        String url = UtilTwo.append(baseUrl, params);
        return UtilTwo.put(encode ? UtilTwo.encode(url) : url);
    }

    public static UtilTwo put(CharSequence baseUrl, boolean encode, Object ... params) {
        String url = UtilTwo.append(baseUrl, params);
        return UtilTwo.put(encode ? UtilTwo.encode(url) : url);
    }

    public static UtilTwo delete(CharSequence url) throws HttpRequestException {
        return new UtilTwo(url, METHOD_DELETE);
    }

    public static UtilTwo delete(URL url) throws HttpRequestException {
        return new UtilTwo(url, METHOD_DELETE);
    }

    public static UtilTwo delete(CharSequence baseUrl, Map<?, ?> params, boolean encode) {
        String url = UtilTwo.append(baseUrl, params);
        return UtilTwo.delete(encode ? UtilTwo.encode(url) : url);
    }

    public static UtilTwo delete(CharSequence baseUrl, boolean encode, Object ... params) {
        String url = UtilTwo.append(baseUrl, params);
        return UtilTwo.delete(encode ? UtilTwo.encode(url) : url);
    }

    public static UtilTwo head(CharSequence url) throws HttpRequestException {
        return new UtilTwo(url, METHOD_HEAD);
    }

    public static UtilTwo head(URL url) throws HttpRequestException {
        return new UtilTwo(url, METHOD_HEAD);
    }

    public static UtilTwo head(CharSequence baseUrl, Map<?, ?> params, boolean encode) {
        String url = UtilTwo.append(baseUrl, params);
        return UtilTwo.head(encode ? UtilTwo.encode(url) : url);
    }

    public static UtilTwo head(CharSequence baseUrl, boolean encode, Object ... params) {
        String url = UtilTwo.append(baseUrl, params);
        return UtilTwo.head(encode ? UtilTwo.encode(url) : url);
    }

    public static UtilTwo options(CharSequence url) throws HttpRequestException {
        return new UtilTwo(url, METHOD_OPTIONS);
    }

    public static UtilTwo options(URL url) throws HttpRequestException {
        return new UtilTwo(url, METHOD_OPTIONS);
    }

    public static UtilTwo trace(CharSequence url) throws HttpRequestException {
        return new UtilTwo(url, METHOD_TRACE);
    }

    public static UtilTwo trace(URL url) throws HttpRequestException {
        return new UtilTwo(url, METHOD_TRACE);
    }

    public static void keepAlive(boolean keepAlive) {
        UtilTwo.setProperty("http.keepAlive", Boolean.toString(keepAlive));
    }

    public static void maxConnections(int maxConnections) {
        UtilTwo.setProperty("http.maxConnections", Integer.toString(maxConnections));
    }

    public static void proxyHost(String host) {
        UtilTwo.setProperty("http.proxyHost", host);
        UtilTwo.setProperty("https.proxyHost", host);
    }

    public static void proxyPort(int port) {
        String portValue = Integer.toString(port);
        UtilTwo.setProperty("http.proxyPort", portValue);
        UtilTwo.setProperty("https.proxyPort", portValue);
    }

    public static void nonProxyHosts(String ... hosts) {
        if (hosts != null && hosts.length > 0) {
            StringBuilder separated = new StringBuilder();
            int last = hosts.length - 1;
            for (int i = 0; i < last; ++i) {
                separated.append(hosts[i]).append('|');
            }
            separated.append(hosts[last]);
            UtilTwo.setProperty("http.nonProxyHosts", separated.toString());
        } else {
            UtilTwo.setProperty("http.nonProxyHosts", null);
        }
    }

    private static String setProperty(final String name, final String value) {
        PrivilegedAction<String> action = value != null ? new PrivilegedAction<String>(){

            @Override
            public String run() {
                return System.setProperty(name, value);
            }
        } : new PrivilegedAction<String>(){

            @Override
            public String run() {
                return System.clearProperty(name);
            }
        };
        return AccessController.doPrivileged(action);
    }

    public UtilTwo(CharSequence url, String method) throws HttpRequestException {
        try {
            this.url = new URL(url.toString());
        }
        catch (MalformedURLException e) {
            throw new HttpRequestException(e);
        }
        this.requestMethod = method;
    }

    public UtilTwo(URL url, String method) throws HttpRequestException {
        this.url = url;
        this.requestMethod = method;
    }

    private Proxy createProxy() {
        return new Proxy(Proxy.Type.HTTP, new InetSocketAddress(this.httpProxyHost, this.httpProxyPort));
    }

    private HttpURLConnection createConnection() {
        try {
            HttpURLConnection connection = this.httpProxyHost != null ? CONNECTION_FACTORY.create(this.url, this.createProxy()) : CONNECTION_FACTORY.create(this.url);
            connection.setRequestMethod(this.requestMethod);
            return connection;
        }
        catch (IOException e) {
            throw new HttpRequestException(e);
        }
    }

    public String toString() {
        return this.method() + ' ' + this.url();
    }

    public HttpURLConnection getConnection() {
        if (this.connection == null) {
            this.connection = this.createConnection();
        }
        return this.connection;
    }

    public UtilTwo ignoreCloseExceptions(boolean ignore) {
        this.ignoreCloseExceptions = ignore;
        return this;
    }

    public boolean ignoreCloseExceptions() {
        return this.ignoreCloseExceptions;
    }

    public int code() throws HttpRequestException {
        try {
            this.closeOutput();
            return this.getConnection().getResponseCode();
        }
        catch (IOException e) {
            throw new HttpRequestException(e);
        }
    }

    public UtilTwo code(AtomicInteger output) throws HttpRequestException {
        output.set(this.code());
        return this;
    }

    public boolean ok() throws HttpRequestException {
        return 200 == this.code();
    }

    public boolean created() throws HttpRequestException {
        return 201 == this.code();
    }

    public boolean noContent() throws HttpRequestException {
        return 204 == this.code();
    }

    public boolean serverError() throws HttpRequestException {
        return 500 == this.code();
    }

    public boolean badRequest() throws HttpRequestException {
        return 400 == this.code();
    }

    public boolean notFound() throws HttpRequestException {
        return 404 == this.code();
    }

    public boolean notModified() throws HttpRequestException {
        return 304 == this.code();
    }

    public String message() throws HttpRequestException {
        try {
            this.closeOutput();
            return this.getConnection().getResponseMessage();
        }
        catch (IOException e) {
            throw new HttpRequestException(e);
        }
    }

    public UtilTwo disconnect() {
        this.getConnection().disconnect();
        return this;
    }

    public UtilTwo chunk(int size) {
        this.getConnection().setChunkedStreamingMode(size);
        return this;
    }

    public UtilTwo bufferSize(int size) {
        if (size < 1) {
            throw new IllegalArgumentException("Size must be greater than zero");
        }
        this.bufferSize = size;
        return this;
    }

    public int bufferSize() {
        return this.bufferSize;
    }

    public UtilTwo uncompress(boolean uncompress) {
        this.uncompress = uncompress;
        return this;
    }

    protected ByteArrayOutputStream byteStream() {
        int size = this.contentLength();
        if (size > 0) {
            return new ByteArrayOutputStream(size);
        }
        return new ByteArrayOutputStream();
    }

    public String body(String charset) throws HttpRequestException {
        ByteArrayOutputStream output = this.byteStream();
        try {
            this.copy(this.buffer(), output);
            return output.toString(UtilTwo.getValidCharset(charset));
        }
        catch (IOException e) {
            throw new HttpRequestException(e);
        }
    }

    public String body() throws HttpRequestException {
        return this.body(this.charset());
    }

    public UtilTwo body(AtomicReference<String> output) throws HttpRequestException {
        output.set(this.body());
        return this;
    }

    public UtilTwo body(AtomicReference<String> output, String charset) throws HttpRequestException {
        output.set(this.body(charset));
        return this;
    }

    public boolean isBodyEmpty() throws HttpRequestException {
        return this.contentLength() == 0;
    }

    public byte[] bytes() throws HttpRequestException {
        ByteArrayOutputStream output = this.byteStream();
        try {
            this.copy(this.buffer(), output);
        }
        catch (IOException e) {
            throw new HttpRequestException(e);
        }
        return output.toByteArray();
    }

    public BufferedInputStream buffer() throws HttpRequestException {
        return new BufferedInputStream(this.stream(), this.bufferSize);
    }

    public InputStream stream() throws HttpRequestException {
        InputStream stream;
        if (this.code() < 400) {
            try {
                stream = this.getConnection().getInputStream();
            }
            catch (IOException e) {
                throw new HttpRequestException(e);
            }
        }
        stream = this.getConnection().getErrorStream();
        if (stream == null) {
            try {
                stream = this.getConnection().getInputStream();
            }
            catch (IOException e) {
                if (this.contentLength() > 0) {
                    throw new HttpRequestException(e);
                }
                stream = new ByteArrayInputStream(new byte[0]);
            }
        }
        if (!this.uncompress || !ENCODING_GZIP.equals(this.contentEncoding())) {
            return stream;
        }
        try {
            return new GZIPInputStream(stream);
        }
        catch (IOException e) {
            throw new HttpRequestException(e);
        }
    }

    public InputStreamReader reader(String charset) throws HttpRequestException {
        try {
            return new InputStreamReader(this.stream(), UtilTwo.getValidCharset(charset));
        }
        catch (UnsupportedEncodingException e) {
            throw new HttpRequestException(e);
        }
    }

    public InputStreamReader reader() throws HttpRequestException {
        return this.reader(this.charset());
    }

    public BufferedReader bufferedReader(String charset) throws HttpRequestException {
        return new BufferedReader(this.reader(charset), this.bufferSize);
    }

    public BufferedReader bufferedReader() throws HttpRequestException {
        return this.bufferedReader(this.charset());
    }

    public UtilTwo receive(File file) throws HttpRequestException {
        BufferedOutputStream output;
        try {
            output = new BufferedOutputStream(new FileOutputStream(file), this.bufferSize);
        }
        catch (FileNotFoundException e) {
            throw new HttpRequestException(e);
        }
        return (UtilTwo)new CloseOperation<UtilTwo>((Closeable)output, this.ignoreCloseExceptions){

            @Override
            protected UtilTwo run() throws HttpRequestException, IOException {
                return UtilTwo.this.receive(output);
            }
        }.call();
    }

    public UtilTwo receive(OutputStream output) throws HttpRequestException {
        try {
            return this.copy(this.buffer(), output);
        }
        catch (IOException e) {
            throw new HttpRequestException(e);
        }
    }

    public UtilTwo receive(PrintStream output) throws HttpRequestException {
        return this.receive((OutputStream)output);
    }

    public UtilTwo receive(final Appendable appendable) throws HttpRequestException {
        final BufferedReader reader = this.bufferedReader();
        return (UtilTwo)new CloseOperation<UtilTwo>((Closeable)reader, this.ignoreCloseExceptions){

            @Override
            public UtilTwo run() throws IOException {
                int read;
                CharBuffer buffer = CharBuffer.allocate(UtilTwo.this.bufferSize);
                while ((read = reader.read(buffer)) != -1) {
                    buffer.rewind();
                    appendable.append(buffer, 0, read);
                    buffer.rewind();
                }
                return UtilTwo.this;
            }
        }.call();
    }

    public UtilTwo receive(final Writer writer) throws HttpRequestException {
        final BufferedReader reader = this.bufferedReader();
        return (UtilTwo)new CloseOperation<UtilTwo>((Closeable)reader, this.ignoreCloseExceptions){

            @Override
            public UtilTwo run() throws IOException {
                return UtilTwo.this.copy(reader, writer);
            }
        }.call();
    }

    public UtilTwo readTimeout(int timeout) {
        this.getConnection().setReadTimeout(timeout);
        return this;
    }

    public UtilTwo connectTimeout(int timeout) {
        this.getConnection().setConnectTimeout(timeout);
        return this;
    }

    public UtilTwo header(String name, String value) {
        this.getConnection().setRequestProperty(name, value);
        return this;
    }

    public UtilTwo header(String name, Number value) {
        return this.header(name, value != null ? value.toString() : null);
    }

    public UtilTwo headers(Map<String, String> headers) {
        if (!headers.isEmpty()) {
            for (Map.Entry<String, String> header : headers.entrySet()) {
                this.header(header);
            }
        }
        return this;
    }

    public UtilTwo header(Map.Entry<String, String> header) {
        return this.header(header.getKey(), header.getValue());
    }

    public String header(String name) throws HttpRequestException {
        this.closeOutputQuietly();
        return this.getConnection().getHeaderField(name);
    }

    public Map<String, List<String>> headers() throws HttpRequestException {
        this.closeOutputQuietly();
        return this.getConnection().getHeaderFields();
    }

    public long dateHeader(String name) throws HttpRequestException {
        return this.dateHeader(name, -1L);
    }

    public long dateHeader(String name, long defaultValue) throws HttpRequestException {
        this.closeOutputQuietly();
        return this.getConnection().getHeaderFieldDate(name, defaultValue);
    }

    public int intHeader(String name) throws HttpRequestException {
        return this.intHeader(name, -1);
    }

    public int intHeader(String name, int defaultValue) throws HttpRequestException {
        this.closeOutputQuietly();
        return this.getConnection().getHeaderFieldInt(name, defaultValue);
    }

    public String[] headers(String name) {
        Map<String, List<String>> headers = this.headers();
        if (headers == null || headers.isEmpty()) {
            return EMPTY_STRINGS;
        }
        List<String> values = headers.get(name);
        if (values != null && !values.isEmpty()) {
            return values.toArray(new String[values.size()]);
        }
        return EMPTY_STRINGS;
    }

    public String parameter(String headerName, String paramName) {
        return this.getParam(this.header(headerName), paramName);
    }

    public Map<String, String> parameters(String headerName) {
        return this.getParams(this.header(headerName));
    }

    protected Map<String, String> getParams(String header) {
        if (header == null || header.length() == 0) {
            return Collections.emptyMap();
        }
        int headerLength = header.length();
        int start = header.indexOf(59) + 1;
        if (start == 0 || start == headerLength) {
            return Collections.emptyMap();
        }
        int end = header.indexOf(59, start);
        if (end == -1) {
            end = headerLength;
        }
        LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
        while (start < end) {
            String value;
            int length;
            String name;
            int nameEnd = header.indexOf(61, start);
            if (nameEnd != -1 && nameEnd < end && (name = header.substring(start, nameEnd).trim()).length() > 0 && (length = (value = header.substring(nameEnd + 1, end).trim()).length()) != 0) {
                if (length > 2 && '\"' == value.charAt(0) && '\"' == value.charAt(length - 1)) {
                    params.put(name, value.substring(1, length - 1));
                } else {
                    params.put(name, value);
                }
            }
            if ((end = header.indexOf(59, start = end + 1)) != -1) continue;
            end = headerLength;
        }
        return params;
    }

    protected String getParam(String value, String paramName) {
        if (value == null || value.length() == 0) {
            return null;
        }
        int length = value.length();
        int start = value.indexOf(59) + 1;
        if (start == 0 || start == length) {
            return null;
        }
        int end = value.indexOf(59, start);
        if (end == -1) {
            end = length;
        }
        while (start < end) {
            String paramValue;
            int valueLength;
            int nameEnd = value.indexOf(61, start);
            if (nameEnd != -1 && nameEnd < end && paramName.equals(value.substring(start, nameEnd).trim()) && (valueLength = (paramValue = value.substring(nameEnd + 1, end).trim()).length()) != 0) {
                if (valueLength > 2 && '\"' == paramValue.charAt(0) && '\"' == paramValue.charAt(valueLength - 1)) {
                    return paramValue.substring(1, valueLength - 1);
                }
                return paramValue;
            }
            start = end + 1;
            if ((end = value.indexOf(59, start)) != -1) continue;
            end = length;
        }
        return null;
    }

    public String charset() {
        return this.parameter(HEADER_CONTENT_TYPE, PARAM_CHARSET);
    }

    public UtilTwo userAgent(String userAgent) {
        return this.header(HEADER_USER_AGENT, userAgent);
    }

    public UtilTwo referer(String referer) {
        return this.header(HEADER_REFERER, referer);
    }

    public UtilTwo useCaches(boolean useCaches) {
        this.getConnection().setUseCaches(useCaches);
        return this;
    }

    public UtilTwo acceptEncoding(String acceptEncoding) {
        return this.header(HEADER_ACCEPT_ENCODING, acceptEncoding);
    }

    public UtilTwo acceptGzipEncoding() {
        return this.acceptEncoding(ENCODING_GZIP);
    }

    public UtilTwo acceptCharset(String acceptCharset) {
        return this.header(HEADER_ACCEPT_CHARSET, acceptCharset);
    }

    public String contentEncoding() {
        return this.header(HEADER_CONTENT_ENCODING);
    }

    public String server() {
        return this.header(HEADER_SERVER);
    }

    public long date() {
        return this.dateHeader(HEADER_DATE);
    }

    public String cacheControl() {
        return this.header(HEADER_CACHE_CONTROL);
    }

    public String eTag() {
        return this.header(HEADER_ETAG);
    }

    public long expires() {
        return this.dateHeader(HEADER_EXPIRES);
    }

    public long lastModified() {
        return this.dateHeader(HEADER_LAST_MODIFIED);
    }

    public String location() {
        return this.header(HEADER_LOCATION);
    }

    public UtilTwo authorization(String authorization) {
        return this.header(HEADER_AUTHORIZATION, authorization);
    }

    public UtilTwo proxyAuthorization(String proxyAuthorization) {
        return this.header(HEADER_PROXY_AUTHORIZATION, proxyAuthorization);
    }

    public UtilTwo basic(String name, String password) {
        return this.authorization("Basic " + Base64.encode(name + ':' + password));
    }

    public UtilTwo proxyBasic(String name, String password) {
        return this.proxyAuthorization("Basic " + Base64.encode(name + ':' + password));
    }

    public UtilTwo ifModifiedSince(long ifModifiedSince) {
        this.getConnection().setIfModifiedSince(ifModifiedSince);
        return this;
    }

    public UtilTwo ifNoneMatch(String ifNoneMatch) {
        return this.header(HEADER_IF_NONE_MATCH, ifNoneMatch);
    }

    public UtilTwo contentType(String contentType) {
        return this.contentType(contentType, null);
    }

    public UtilTwo contentType(String contentType, String charset) {
        if (charset != null && charset.length() > 0) {
            String separator = "; charset=";
            return this.header(HEADER_CONTENT_TYPE, contentType + "; charset=" + charset);
        }
        return this.header(HEADER_CONTENT_TYPE, contentType);
    }

    public String contentType() {
        return this.header(HEADER_CONTENT_TYPE);
    }

    public int contentLength() {
        return this.intHeader(HEADER_CONTENT_LENGTH);
    }

    public UtilTwo contentLength(String contentLength) {
        return this.contentLength(Integer.parseInt(contentLength));
    }

    public UtilTwo contentLength(int contentLength) {
        this.getConnection().setFixedLengthStreamingMode(contentLength);
        return this;
    }

    public UtilTwo accept(String accept) {
        return this.header(HEADER_ACCEPT, accept);
    }

    public UtilTwo acceptJson() {
        return this.accept(CONTENT_TYPE_JSON);
    }

    protected UtilTwo copy(final InputStream input, final OutputStream output) throws IOException {
        return (UtilTwo)new CloseOperation<UtilTwo>((Closeable)input, this.ignoreCloseExceptions){

            @Override
            public UtilTwo run() throws IOException {
                int read;
                byte[] buffer = new byte[UtilTwo.this.bufferSize];
                while ((read = input.read(buffer)) != -1) {
                    output.write(buffer, 0, read);
                    UtilTwo.this.totalWritten = UtilTwo.this.totalWritten + (long)read;
                    UtilTwo.this.progress.onUpload(UtilTwo.this.totalWritten, UtilTwo.this.totalSize);
                }
                return UtilTwo.this;
            }
        }.call();
    }

    protected UtilTwo copy(final Reader input, final Writer output) throws IOException {
        return (UtilTwo)new CloseOperation<UtilTwo>((Closeable)input, this.ignoreCloseExceptions){

            @Override
            public UtilTwo run() throws IOException {
                int read;
                char[] buffer = new char[UtilTwo.this.bufferSize];
                while ((read = input.read(buffer)) != -1) {
                    output.write(buffer, 0, read);
                    UtilTwo.this.totalWritten = UtilTwo.this.totalWritten + (long)read;
                    UtilTwo.this.progress.onUpload(UtilTwo.this.totalWritten, -1L);
                }
                return UtilTwo.this;
            }
        }.call();
    }

    public UtilTwo progress(UploadProgress callback) {
        this.progress = callback == null ? UploadProgress.DEFAULT : callback;
        return this;
    }

    private UtilTwo incrementTotalSize(long size) {
        if (this.totalSize == -1L) {
            this.totalSize = 0L;
        }
        this.totalSize += size;
        return this;
    }

    protected UtilTwo closeOutput() throws IOException {
        this.progress(null);
        if (this.output == null) {
            return this;
        }
        if (this.multipart) {
            this.output.write("\r\n--00content0boundary00--\r\n");
        }
        if (this.ignoreCloseExceptions) {
            try {
                this.output.close();
            }
            catch (IOException iOException) {}
        } else {
            this.output.close();
        }
        this.output = null;
        return this;
    }

    protected UtilTwo closeOutputQuietly() throws HttpRequestException {
        try {
            return this.closeOutput();
        }
        catch (IOException e) {
            throw new HttpRequestException(e);
        }
    }

    protected UtilTwo openOutput() throws IOException {
        if (this.output != null) {
            return this;
        }
        this.getConnection().setDoOutput(true);
        String charset = this.getParam(this.getConnection().getRequestProperty(HEADER_CONTENT_TYPE), PARAM_CHARSET);
        this.output = new RequestOutputStream(this.getConnection().getOutputStream(), charset, this.bufferSize);
        return this;
    }

    protected UtilTwo startPart() throws IOException {
        if (!this.multipart) {
            this.multipart = true;
            this.contentType(CONTENT_TYPE_MULTIPART).openOutput();
            this.output.write("--00content0boundary00\r\n");
        } else {
            this.output.write("\r\n--00content0boundary00\r\n");
        }
        return this;
    }

    protected UtilTwo writePartHeader(String name, String filename) throws IOException {
        return this.writePartHeader(name, filename, null);
    }

    protected UtilTwo writePartHeader(String name, String filename, String contentType) throws IOException {
        StringBuilder partBuffer = new StringBuilder();
        partBuffer.append("form-data; name=\"").append(name);
        if (filename != null) {
            partBuffer.append("\"; filename=\"").append(filename);
        }
        partBuffer.append('\"');
        this.partHeader("Content-Disposition", partBuffer.toString());
        if (contentType != null) {
            this.partHeader(HEADER_CONTENT_TYPE, contentType);
        }
        return this.send(CRLF);
    }

    public UtilTwo part(String name, String part) {
        return this.part(name, null, part);
    }

    public UtilTwo part(String name, String filename, String part) throws HttpRequestException {
        return this.part(name, filename, null, part);
    }

    public UtilTwo part(String name, String filename, String contentType, String part) throws HttpRequestException {
        try {
            this.startPart();
            this.writePartHeader(name, filename, contentType);
            this.output.write(part);
        }
        catch (IOException e) {
            throw new HttpRequestException(e);
        }
        return this;
    }

    public UtilTwo part(String name, Number part) throws HttpRequestException {
        return this.part(name, null, part);
    }

    public UtilTwo part(String name, String filename, Number part) throws HttpRequestException {
        return this.part(name, filename, part != null ? part.toString() : null);
    }

    public UtilTwo part(String name, File part) throws HttpRequestException {
        return this.part(name, null, part);
    }

    public UtilTwo part(String name, String filename, File part) throws HttpRequestException {
        return this.part(name, filename, null, part);
    }

    public UtilTwo part(String name, String filename, String contentType, File part) throws HttpRequestException {
        BufferedInputStream stream;
        try {
            stream = new BufferedInputStream(new FileInputStream(part));
            this.incrementTotalSize(part.length());
        }
        catch (IOException e) {
            throw new HttpRequestException(e);
        }
        return this.part(name, filename, contentType, stream);
    }

    public UtilTwo part(String name, InputStream part) throws HttpRequestException {
        return this.part(name, null, null, part);
    }

    public UtilTwo part(String name, String filename, String contentType, InputStream part) throws HttpRequestException {
        try {
            this.startPart();
            this.writePartHeader(name, filename, contentType);
            this.copy(part, this.output);
        }
        catch (IOException e) {
            throw new HttpRequestException(e);
        }
        return this;
    }

    public UtilTwo partHeader(String name, String value) throws HttpRequestException {
        return this.send(name).send(": ").send(value).send(CRLF);
    }

    public UtilTwo send(File input) throws HttpRequestException {
        BufferedInputStream stream;
        try {
            stream = new BufferedInputStream(new FileInputStream(input));
            this.incrementTotalSize(input.length());
        }
        catch (FileNotFoundException e) {
            throw new HttpRequestException(e);
        }
        return this.send(stream);
    }

    public UtilTwo send(byte[] input) throws HttpRequestException {
        if (input != null) {
            this.incrementTotalSize(input.length);
        }
        return this.send(new ByteArrayInputStream(input));
    }

    public UtilTwo send(InputStream input) throws HttpRequestException {
        try {
            this.openOutput();
            this.copy(input, this.output);
        }
        catch (IOException e) {
            throw new HttpRequestException(e);
        }
        return this;
    }

    public UtilTwo send(final Reader input) throws HttpRequestException {
        try {
            this.openOutput();
        }
        catch (IOException e) {
            throw new HttpRequestException(e);
        }
        final OutputStreamWriter writer = new OutputStreamWriter((OutputStream)this.output, this.output.encoder.charset());
        return (UtilTwo)new FlushOperation<UtilTwo>((Flushable)writer){

            @Override
            protected UtilTwo run() throws IOException {
                return UtilTwo.this.copy(input, writer);
            }
        }.call();
    }

    public UtilTwo send(CharSequence value) throws HttpRequestException {
        try {
            this.openOutput();
            this.output.write(value.toString());
        }
        catch (IOException e) {
            throw new HttpRequestException(e);
        }
        return this;
    }

    public OutputStreamWriter writer() throws HttpRequestException {
        try {
            this.openOutput();
            return new OutputStreamWriter((OutputStream)this.output, this.output.encoder.charset());
        }
        catch (IOException e) {
            throw new HttpRequestException(e);
        }
    }

    public UtilTwo form(Map<?, ?> values) throws HttpRequestException {
        return this.form(values, CHARSET_UTF8);
    }

    public UtilTwo form(Map.Entry<?, ?> entry) throws HttpRequestException {
        return this.form(entry, CHARSET_UTF8);
    }

    public UtilTwo form(Map.Entry<?, ?> entry, String charset) throws HttpRequestException {
        return this.form(entry.getKey(), entry.getValue(), charset);
    }

    public UtilTwo form(Object name, Object value) throws HttpRequestException {
        return this.form(name, value, CHARSET_UTF8);
    }

    public UtilTwo form(Object name, Object value, String charset) throws HttpRequestException {
        boolean first;
        boolean bl = first = !this.form;
        if (first) {
            this.contentType(CONTENT_TYPE_FORM, charset);
            this.form = true;
        }
        charset = UtilTwo.getValidCharset(charset);
        try {
            this.openOutput();
            if (!first) {
                this.output.write(38);
            }
            this.output.write(URLEncoder.encode(name.toString(), charset));
            this.output.write(61);
            if (value != null) {
                this.output.write(URLEncoder.encode(value.toString(), charset));
            }
        }
        catch (IOException e) {
            throw new HttpRequestException(e);
        }
        return this;
    }

    public UtilTwo form(Map<?, ?> values, String charset) throws HttpRequestException {
        if (!values.isEmpty()) {
            for (Map.Entry<?, ?> entry : values.entrySet()) {
                this.form(entry, charset);
            }
        }
        return this;
    }

    public UtilTwo trustAllCerts() throws HttpRequestException {
        HttpURLConnection connection = this.getConnection();
        if (connection instanceof HttpsURLConnection) {
            ((HttpsURLConnection)connection).setSSLSocketFactory(UtilTwo.getTrustedFactory());
        }
        return this;
    }

    public UtilTwo trustAllHosts() {
        HttpURLConnection connection = this.getConnection();
        if (connection instanceof HttpsURLConnection) {
            ((HttpsURLConnection)connection).setHostnameVerifier(UtilTwo.getTrustedVerifier());
        }
        return this;
    }

    public URL url() {
        return this.getConnection().getURL();
    }

    public String method() {
        return this.getConnection().getRequestMethod();
    }

    public UtilTwo useProxy(String proxyHost, int proxyPort) {
        if (this.connection != null) {
            throw new IllegalStateException("The connection has already been created. This method must be called before reading or writing to the request.");
        }
        this.httpProxyHost = proxyHost;
        this.httpProxyPort = proxyPort;
        return this;
    }

    public UtilTwo followRedirects(boolean followRedirects) {
        this.getConnection().setInstanceFollowRedirects(followRedirects);
        return this;
    }

    static {
        CONNECTION_FACTORY = ConnectionFactory.DEFAULT;
    }

    public static class RequestOutputStream
    extends BufferedOutputStream {
        private final CharsetEncoder encoder;

        public RequestOutputStream(OutputStream stream, String charset, int bufferSize) {
            super(stream, bufferSize);
            this.encoder = Charset.forName(UtilTwo.getValidCharset(charset)).newEncoder();
        }

        public RequestOutputStream write(String value) throws IOException {
            ByteBuffer bytes = this.encoder.encode(CharBuffer.wrap(value));
            super.write(bytes.array(), 0, bytes.limit());
            return this;
        }
    }

    protected static abstract class FlushOperation<V>
    extends Operation<V> {
        private final Flushable flushable;

        protected FlushOperation(Flushable flushable) {
            this.flushable = flushable;
        }

        @Override
        protected void done() throws IOException {
            this.flushable.flush();
        }
    }

    protected static abstract class CloseOperation<V>
    extends Operation<V> {
        private final Closeable closeable;
        private final boolean ignoreCloseExceptions;

        protected CloseOperation(Closeable closeable, boolean ignoreCloseExceptions) {
            this.closeable = closeable;
            this.ignoreCloseExceptions = ignoreCloseExceptions;
        }

        @Override
        protected void done() throws IOException {
            if (this.closeable instanceof Flushable) {
                ((Flushable)((Object)this.closeable)).flush();
            }
            if (this.ignoreCloseExceptions) {
                try {
                    this.closeable.close();
                }
                catch (IOException iOException) {}
            } else {
                this.closeable.close();
            }
        }
    }

    protected static abstract class Operation<V>
    implements Callable<V> {
        protected Operation() {
        }

        protected abstract V run() throws HttpRequestException, IOException;

        protected abstract void done() throws IOException;

        @Override
        public V call() throws HttpRequestException {
            boolean thrown = false;
            try {
                V v = this.run();
                return v;
            }
            catch (HttpRequestException e) {
                thrown = true;
                throw e;
            }
            catch (IOException e) {
                thrown = true;
                throw new HttpRequestException(e);
            }
            finally {
                block10: {
                    try {
                        this.done();
                    }
                    catch (IOException e) {
                        if (thrown) break block10;
                        throw new HttpRequestException(e);
                    }
                }
            }
        }
    }

    public static class HttpRequestException
    extends RuntimeException {
        private static final long serialVersionUID = -1170466989781746231L;

        public HttpRequestException(IOException cause) {
            super(cause);
        }

        @Override
        public IOException getCause() {
            return (IOException)super.getCause();
        }
    }

    public static class Base64 {
        private static final byte EQUALS_SIGN = 61;
        private static final String PREFERRED_ENCODING = "US-ASCII";
        private static final byte[] _STANDARD_ALPHABET = new byte[]{65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 43, 47};

        private Base64() {
        }

        private static byte[] encode3to4(byte[] source, int srcOffset, int numSigBytes, byte[] destination, int destOffset) {
            byte[] ALPHABET = _STANDARD_ALPHABET;
            int inBuff = (numSigBytes > 0 ? source[srcOffset] << 24 >>> 8 : 0) | (numSigBytes > 1 ? source[srcOffset + 1] << 24 >>> 16 : 0) | (numSigBytes > 2 ? source[srcOffset + 2] << 24 >>> 24 : 0);
            switch (numSigBytes) {
                case 3: {
                    destination[destOffset] = ALPHABET[inBuff >>> 18];
                    destination[destOffset + 1] = ALPHABET[inBuff >>> 12 & 0x3F];
                    destination[destOffset + 2] = ALPHABET[inBuff >>> 6 & 0x3F];
                    destination[destOffset + 3] = ALPHABET[inBuff & 0x3F];
                    return destination;
                }
                case 2: {
                    destination[destOffset] = ALPHABET[inBuff >>> 18];
                    destination[destOffset + 1] = ALPHABET[inBuff >>> 12 & 0x3F];
                    destination[destOffset + 2] = ALPHABET[inBuff >>> 6 & 0x3F];
                    destination[destOffset + 3] = 61;
                    return destination;
                }
                case 1: {
                    destination[destOffset] = ALPHABET[inBuff >>> 18];
                    destination[destOffset + 1] = ALPHABET[inBuff >>> 12 & 0x3F];
                    destination[destOffset + 2] = 61;
                    destination[destOffset + 3] = 61;
                    return destination;
                }
            }
            return destination;
        }

        public static String encode(String string) {
            byte[] bytes;
            try {
                bytes = string.getBytes(PREFERRED_ENCODING);
            }
            catch (UnsupportedEncodingException e) {
                bytes = string.getBytes();
            }
            return Base64.encodeBytes(bytes);
        }

        public static String encodeBytes(byte[] source) {
            return Base64.encodeBytes(source, 0, source.length);
        }

        public static String encodeBytes(byte[] source, int off, int len) {
            byte[] encoded = Base64.encodeBytesToBytes(source, off, len);
            try {
                return new String(encoded, PREFERRED_ENCODING);
            }
            catch (UnsupportedEncodingException uue) {
                return new String(encoded);
            }
        }

        public static byte[] encodeBytesToBytes(byte[] source, int off, int len) {
            if (source == null) {
                throw new NullPointerException("Cannot serialize a null array.");
            }
            if (off < 0) {
                throw new IllegalArgumentException("Cannot have negative offset: " + off);
            }
            if (len < 0) {
                throw new IllegalArgumentException("Cannot have length offset: " + len);
            }
            if (off + len > source.length) {
                throw new IllegalArgumentException(String.format("Cannot have offset of %d and length of %d with array of length %d", off, len, source.length));
            }
            int encLen = len / 3 * 4 + (len % 3 > 0 ? 4 : 0);
            byte[] outBuff = new byte[encLen];
            int d = 0;
            int e = 0;
            int len2 = len - 2;
            while (d < len2) {
                Base64.encode3to4(source, d + off, 3, outBuff, e);
                d += 3;
                e += 4;
            }
            if (d < len) {
                Base64.encode3to4(source, d + off, len - d, outBuff, e);
                e += 4;
            }
            if (e <= outBuff.length - 1) {
                byte[] finalOut = new byte[e];
                System.arraycopy(outBuff, 0, finalOut, 0, e);
                return finalOut;
            }
            return outBuff;
        }
    }

    public static interface UploadProgress {
        public static final UploadProgress DEFAULT = new UploadProgress(){

            @Override
            public void onUpload(long uploaded, long total) {
            }
        };

        public void onUpload(long var1, long var3);
    }

    public static interface ConnectionFactory {
        public static final ConnectionFactory DEFAULT = new ConnectionFactory(){

            @Override
            public HttpURLConnection create(URL url) throws IOException {
                return (HttpURLConnection)url.openConnection();
            }

            @Override
            public HttpURLConnection create(URL url, Proxy proxy) throws IOException {
                return (HttpURLConnection)url.openConnection(proxy);
            }
        };

        public HttpURLConnection create(URL var1) throws IOException;

        public HttpURLConnection create(URL var1, Proxy var2) throws IOException;
    }
}

