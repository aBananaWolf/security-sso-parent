package cn.com.oauth2;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;

/**
 * @author wyl
 * @create 2020-07-17 20:12
 */
public class MyHttpServletRequestWrapper extends HttpServletRequestWrapper {
    private HashMap<String,String> customizeHeader;

    public MyHttpServletRequestWrapper(HttpServletRequest request) {
        super(request);
        this.customizeHeader = new HashMap<>();
    }

    public void putHeader(String header, String value) {
        customizeHeader.put(header, value);
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        HashSet<String> headers = new HashSet<>(customizeHeader.keySet());
        Enumeration<String> originHeaders = super.getHeaderNames();
        while (originHeaders.hasMoreElements()) {
            headers.add(originHeaders.nextElement());
        }
        return Collections.enumeration(headers);
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        String header = customizeHeader.get(name);
        Enumeration<String> headers;
        if (header == null) {
            headers = super.getHeaders(name);
        } else {
            headers = Collections.enumeration(Collections.singletonList(header));
        }
        return headers;
    }

    @Override
    public String getHeader(String name) {
        String header = customizeHeader.get(name);
        if (header != null)
            return header;
        return super.getHeader(name);
    }
}
