package com.dag.news.feeds;

import com.dag.news.bo.TempFeed;
import com.dag.news.bo.TempNew;
import com.dag.news.model.Feed;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import org.apache.http.Header;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.net.ssl.SSLContext;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.XMLEvent;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Component
public class BasicRssReaderImpl implements BasicRssReader {

    @Value("${net.timeout:20000}")
    int timeout = 20000;

    static private Logger logger = LoggerFactory.getLogger(BasicRssReaderImpl.class);

    static TrustStrategy trustStrategy = new TrustStrategy() {

        public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            if (logger.isDebugEnabled()) {
                for (X509Certificate cert : chain) {
                    logger.debug("certification " + cert);
                }
            }
            return true;
        }

    };

    @Override
    public SyndFeed readFeed(Feed _feed) throws Exception {
        SyndFeed feed = null;
        CloseableHttpClient httpclient = null;

        try {
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, trustStrategy).build();
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext, new NoopHostnameVerifier());

            httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).build();

            HttpGet httpget = new HttpGet(_feed.getUrl());

            httpget.setHeader("Accept", "text/html, application/xhtml+xml, image/jxr, */*");
            httpget.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/52.0.2743.116 Safari/537.36 Edge/15.15063");

            httpget.setHeader("Accept-Encoding", "gzip, deflate");
            httpget.setHeader("Accept-Language", "en-GB, en; q=0.8, es-ES; q=0.6, es; q=0.4, ca; q=0.2");

            httpget.setConfig(RequestConfig.custom().setSocketTimeout(timeout).setConnectionRequestTimeout(timeout)
                    .setConnectTimeout(timeout).setCircularRedirectsAllowed(true).setRedirectsEnabled(true).build());

            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    try {
                        if (httpget != null) {
                            httpget.abort();
                        }
                    } catch (Exception ex) {

                    }
                }
            };
            Timer time = new Timer(true);
            time.schedule(task, timeout);

            logger.debug("Executing request " + httpget.getRequestLine());

            CloseableHttpResponse response = httpclient.execute(httpget);
            time.cancel();
            Header _contentType = null;
            try {
                _contentType = response.getFirstHeader("Content-Type");

                if (logger.isDebugEnabled())
                    logger.debug("get " + response.getStatusLine() + " " + _contentType);

                if (response.getStatusLine().getStatusCode() == 200) {
                    SyndFeedInput input = new SyndFeedInput();
                    feed = input.build(new XmlReader(response.getEntity().getContent()));
                } else {
                    throw new RuntimeException("network error [" + response.getStatusLine().getStatusCode() + "]");
                }
            } finally {
                response.close();
            }
        } finally {
            try {
                httpclient.close();
            } catch (IOException e) {

            }
        }

        return feed;
    }

    static public String extractGoogleLink(String url) {
        if (url.startsWith("http://news.google") || url.startsWith("https://news.google")) {

            Pattern pattern = Pattern.compile("(&?url=)([^&]+)");
            Matcher matcher = pattern.matcher(url);
            // check all occurance
            if (matcher.find() && matcher.groupCount() == 2) {
                url = matcher.group(2);
            }
        }
        return url;
    }


    public static void main(String[] a) throws Exception {
        String u = "http://ineverycrea.net/comunidad/ineverycrea/mi-perfil/inicio";
        u = "http://www.larazon.es/rss/internacional.xml";
        u = "http://rss.elconfidencial.com/espana/";
        u = "http://estaticos.expansion.com/rss/portada.xml";
        u = "http://newsrss.bbc.co.uk/rss/on_this_day/front_page/rss.xml";
        u = "http://estaticos.elmundo.es/elmundo/rss/ciencia.xml";
        u = "http://www.bloglines.com/";
        u = "http://news.google.gr/?output=rss";
        u="http://www.elpuntavui.cat/societat.feed?type=rss";
        // u= "https://tienda.abc.es/";
        // u = "http://thescotsman.scotsman.com/index.cfm?format=rss";
        // u="http://www.20minutos.es/rss/";
        // u="http://www.larazon.es/rss/local/madrid.xml";
        // u="http://elpais.com/tag/rss/cine/a/";
        // URL feedUrl = new URL(args[0]);
        //

        // http://news.google.com/news/url?sa=t&fd=R&ct2=el_gr&usg=AFQjCNGaR8lUoAnnzj7gfkcVOdmwqm3Dbw&clid=c3a7d30bb8a4878e06b80cf16b898331&cid=52779995241293&ei=O3hSV_D1EsavzAae7ryIAw&url=http://www.zougla.gr/kosmos/article/meksiko-vre8ike-mazikos-tafos-me-117-nekrous
        try {
            BasicRssReaderImpl kk = new BasicRssReaderImpl();
            SyndFeed feedx = kk.readFeed(new Feed(u));

            System.out.println(feedx.getPublishedDate().toGMTString());
            System.out.println(feedx.getTitle());
            for (SyndEntry s : feedx.getEntries()) {

                System.out.println(s.getTitle());

                String url = s.getLink();

                System.out.println(extractGoogleLink(url));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        // // URL url = new URL(u);
        // // InputStream in = url.openStream();
        // // in.read(new byte[1024], 0, 1024);
        //
        // // HttpGet get = new HttpGet(u);
        // SyndFeed feed = null;
        // CloseableHttpClient httpclient = null;
        // try {
        // HttpGet httpget = new HttpGet(u);
        // httpget.setConfig(RequestConfig.custom().setSocketTimeout(5000).setConnectionRequestTimeout(5000)
        // .setConnectTimeout(5000).setCircularRedirectsAllowed(true).build());
        //
        // SSLContext sslContext = new
        // SSLContextBuilder().loadTrustMaterial(null, trustStrategy).build();
        // SSLConnectionSocketFactory sslsf = new
        // SSLConnectionSocketFactory(sslContext, new NoopHostnameVerifier());
        //
        // httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
        //
        // System.out.println("Executing request " + httpget.getRequestLine());
        // CloseableHttpResponse response = httpclient.execute(httpget);
        // try {
        //
        // System.out.println("----------------------------------------");
        // System.out.println(response.getStatusLine());
        // //System.out.println(EntityUtils.toString(response.getEntity()));
        //
        //
        // //response.getHeaders("Content-type")
        //
        //
        // SyndFeedInput input = new SyndFeedInput();
        // feed = input.build(new XmlReader(response.getEntity().getContent()));
        //
        //// feed = input.build(new XmlReader(new URL(u)));
        //
        // } finally {
        // response.close();
        // }
        // } catch (Exception ex) {
        // ex.printStackTrace();
        // } finally {
        // httpclient.close();
        // }
        // SyndFeedInput input = new SyndFeedInput();
        //
        // feed = input.build(new XmlReader(new URL(u)));
        // System.out.println(feed);
    }
}

